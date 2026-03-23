/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.eval;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.ActionListenerResponseHandler;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.action.ActionRunnable;
import org.elasticsearch.action.support.SubscribableListener;
import org.elasticsearch.cluster.metadata.ProjectId;
import org.elasticsearch.xpack.esql.action.EsqlQueryAction;
import org.elasticsearch.xpack.esql.action.EsqlQueryRequest;
import org.elasticsearch.xpack.piescript.PiescriptSendAction;
import org.elasticsearch.xpack.piescript.PiescriptSendRequest;
import org.elasticsearch.xpack.piescript.core.CoreApp;
import org.elasticsearch.xpack.piescript.core.CoreExpr;
import org.elasticsearch.xpack.piescript.core.CoreField;
import org.elasticsearch.xpack.piescript.core.CoreFree;
import org.elasticsearch.xpack.piescript.core.CoreLam;
import org.elasticsearch.xpack.piescript.core.CoreLet;
import org.elasticsearch.xpack.piescript.core.CoreList;
import org.elasticsearch.xpack.piescript.core.CoreLit;
import org.elasticsearch.xpack.piescript.core.CorePrimOp;
import org.elasticsearch.xpack.piescript.core.CoreProject;
import org.elasticsearch.xpack.piescript.core.CoreQueryExec;
import org.elasticsearch.xpack.piescript.core.CoreRecord;
import org.elasticsearch.xpack.piescript.core.CoreSend;
import org.elasticsearch.xpack.piescript.core.CoreSpawn;
import org.elasticsearch.xpack.piescript.core.CoreTypeAbs;
import org.elasticsearch.xpack.piescript.core.CoreTypeApp;
import org.elasticsearch.xpack.piescript.core.CoreUpdate;
import org.elasticsearch.xpack.piescript.core.CoreVar;
import org.elasticsearch.xpack.piescript.core.CoreWhen;
import org.elasticsearch.xpack.piescript.elab.Prelude;
import org.elasticsearch.xpack.piescript.types.LitVal;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Uniformly asynchronous tree-walking evaluator for well-typed Core IR.
 * Every {@code evaluate} call takes an {@link ActionListener} — for pure
 * expressions the listener fires immediately on the calling thread; for
 * coordination primitives ({@code spawn}/{@code when}) and queries the
 * listener fires asynchronously. See D-041.
 *
 * <p>Uses a de Bruijn environment machine: the environment is a {@code Value[]}
 * indexed by de Bruijn index, with position 0 being the most recently bound
 * variable (D-024). Environment arrays are immutable by convention — {@code prepend}
 * always allocates a new array.
 *
 * <p>The evaluator trusts the type checker (D-025): where a specific
 * {@link Value} variant is required (closure for application, record for
 * projection/update), a non-matching value is an internal invariant violation
 * ({@link AssertionError}), not a user error.
 *
 * <p>Evaluation of individual expression forms is split across handler classes
 * in the same package: {@link EvalPrimOps}, {@link EvalBuiltins},
 * {@link EvalCoordination}. This class holds the main dispatch, structural
 * forms, and shared infrastructure.
 */
public final class Evaluator {

    private static final Value[] EMPTY_ENV = new Value[0];

    final EvalDependencies deps;

    public Evaluator(EvalDependencies deps) {
        this.deps = deps;
    }

    public void evaluate(CoreExpr expr, ActionListener<Value> listener) {
        evaluate(expr, EMPTY_ENV, listener);
    }

    void evaluate(CoreExpr expr, Value[] env, ActionListener<Value> listener) {
        switch (expr) {
            case CoreLit lit -> listener.onResponse(litToValue(lit.value()));

            case CoreVar var -> listener.onResponse(env[var.index()]);

            case CoreFree free -> {
                var arity = Prelude.ARITY.get(free.name());
                if (arity == null) {
                    listener.onFailure(new EvaluationException("unknown built-in: " + free.name()));
                    return;
                }
                listener.onResponse(new Value.BuiltinVal(free.name(), arity, List.of()));
            }

            case CoreLam lam -> listener.onResponse(new Value.ClosureVal(lam.body(), env.clone()));

            case CoreApp app -> evaluate(
                app.fn(),
                env,
                listener.delegateFailureAndWrap(
                    (l1, fn) -> evaluate(app.arg(), env, l1.delegateFailureAndWrap((l2, arg) -> applyFunction(fn, arg, l2)))
                )
            );

            case CoreLet let -> evaluate(
                let.rhs(),
                env,
                listener.delegateFailureAndWrap((l, rhsVal) -> evaluate(let.body(), prepend(rhsVal, env), l))
            );

            case CoreRecord rec -> evaluateRecord(rec, env, listener);

            case CoreList list -> evaluateList(list, env, listener);

            case CoreProject proj -> evaluate(proj.expr(), env, listener.delegateFailureAndWrap((l, record) -> {
                if (record instanceof Value.Symbol sym) {
                    var prefix = sym.esql().isEmpty() ? "" : sym.esql() + ".";
                    l.onResponse(new Value.Symbol(prefix + proj.label()));
                } else {
                    var recVal = switch (record) {
                        case Value.RecordVal r -> r;
                        default -> throw new AssertionError("type checker bug: expected record, got " + record);
                    };
                    l.onResponse(recVal.fields().get(proj.label()));
                }
            }));

            case CoreUpdate upd -> evaluate(upd.expr(), env, listener.delegateFailureAndWrap((l, base) -> {
                var baseRec = switch (base) {
                    case Value.RecordVal r -> r;
                    default -> throw new AssertionError("type checker bug: expected record, got " + base);
                };
                evaluateUpdateFields(upd.updates(), 0, env, new LinkedHashMap<>(baseRec.fields()), l);
            }));

            case CoreTypeAbs typeAbs -> evaluate(typeAbs.body(), env, listener);

            case CoreTypeApp typeApp -> evaluate(typeApp.polyExpr(), env, listener);

            case CorePrimOp primOp -> EvalPrimOps.evaluate(this, primOp, env, listener);

            case CoreSpawn spawn -> {
                var channelId = deps.channelRegistry().nextChannelId();
                var channelListener = new SubscribableListener<Value>();
                deps.channelRegistry().register(channelId, channelListener);
                if (spawn.body() != null) {
                    deps.executor().execute(ActionRunnable.wrap(channelListener, l -> evaluate(spawn.body(), env, l)));
                }
                listener.onResponse(new Value.ChannelVal(deps.localNodeId(), channelId));
            }

            // Three-way dispatch for send (D-045, D-047):
            // 1. Inbox — always via transport, even for the local node. The inbox is not in the
            // ChannelRegistry because it is reusable (not a single-shot SubscribableListener),
            // and keeping it out prevents `when` from accidentally subscribing to it. Inbox
            // handling (validate ClosureVal, fork evaluation) lives in TransportPiescriptSendAction.
            // 2. Local regular channel — direct registry completion, no transport overhead.
            // 3. Remote regular channel — serialized via transport to the owning node.
            // This separation goes away when multi-value channels replace the current model.
            case CoreSend send -> evaluate(send.channel(), env, listener.delegateFailureAndWrap((l1, chanVal) -> {
                var ch = (Value.ChannelVal) chanVal;
                evaluate(send.value(), env, l1.delegateFailureAndWrap((l2, value) -> {
                    if (PiescriptSendRequest.INBOX_CHANNEL_ID.equals(ch.channelId())) {
                        sendRemote(ch, value, l2);
                    } else if (ch.nodeId() != null && ch.nodeId().equals(deps.localNodeId())) {
                        deps.channelRegistry().complete(ch.channelId(), value);
                        l2.onResponse(new Value.NullVal());
                    } else {
                        sendRemote(ch, value, l2);
                    }
                }));
            }));

            case CoreWhen when -> EvalCoordination.evaluateWhen(this, when, env, listener);

            case CoreQueryExec qe -> evaluate(qe.plan(), env, listener.delegateFailureAndWrap((l, result) -> {
                if (result instanceof Value.Symbol sym) {
                    if (deps.client() == null) {
                        l.onFailure(new EvaluationException("query evaluation requires a client"));
                        return;
                    }
                    var request = EsqlQueryRequest.syncEsqlQueryRequest(sym.esql());
                    deps.client()
                        .execute(
                            EsqlQueryAction.INSTANCE,
                            request,
                            l.delegateFailureAndWrap((l2, response) -> l2.onResponse(EsqlValueConverter.convertResponse(response)))
                        );
                } else {
                    l.onFailure(new EvaluationException("query expression did not produce an ESQL Symbol"));
                }
            }));
        }
    }

    // ──── Remote send (D-045) ────

    private void sendRemote(Value.ChannelVal target, Value value, ActionListener<Value> listener) {
        if (deps.transportService() == null) {
            listener.onFailure(new EvaluationException("remote send requires transport service"));
            return;
        }
        var targetNode = deps.clusterService().state().nodes().get(target.nodeId());
        if (targetNode == null) {
            listener.onFailure(new EvaluationException("target node [" + target.nodeId() + "] not found in cluster state"));
            return;
        }
        var request = new PiescriptSendRequest(target.channelId(), value);
        deps.transportService()
            .sendRequest(
                targetNode,
                PiescriptSendAction.NAME,
                request,
                new ActionListenerResponseHandler<>(
                    listener.delegateFailureAndWrap((l, ignored) -> l.onResponse(new Value.NullVal())),
                    in -> ActionResponse.Empty.INSTANCE,
                    deps.executor()
                )
            );
    }

    // ──── Record construction (sequential field evaluation) ────

    private void evaluateRecord(CoreRecord rec, Value[] env, ActionListener<Value> listener) {
        var labels = rec.labels();
        var values = rec.children();
        var fields = new LinkedHashMap<String, Value>(labels.size());
        evaluateRecordFields(labels, values, 0, env, fields, listener);
    }

    private void evaluateRecordFields(
        List<String> labels,
        List<CoreExpr> values,
        int index,
        Value[] env,
        LinkedHashMap<String, Value> fields,
        ActionListener<Value> listener
    ) {
        if (index >= labels.size()) {
            listener.onResponse(new Value.RecordVal(fields));
            return;
        }
        evaluate(values.get(index), env, listener.delegateFailureAndWrap((l, val) -> {
            fields.put(labels.get(index), val);
            evaluateRecordFields(labels, values, index + 1, env, fields, l);
        }));
    }

    private void evaluateList(CoreList list, Value[] env, ActionListener<Value> listener) {
        var elements = list.elements();
        var results = new java.util.ArrayList<Value>(elements.size());
        evaluateListElements(elements, 0, env, results, listener);
    }

    private void evaluateListElements(
        java.util.List<CoreExpr> elements,
        int index,
        Value[] env,
        java.util.ArrayList<Value> results,
        ActionListener<Value> listener
    ) {
        if (index >= elements.size()) {
            listener.onResponse(new Value.ListVal(results));
            return;
        }
        evaluate(elements.get(index), env, listener.delegateFailureAndWrap((l, val) -> {
            results.add(val);
            evaluateListElements(elements, index + 1, env, results, l);
        }));
    }

    private void evaluateUpdateFields(
        List<CoreField> updates,
        int index,
        Value[] env,
        LinkedHashMap<String, Value> fields,
        ActionListener<Value> listener
    ) {
        if (index >= updates.size()) {
            listener.onResponse(new Value.RecordVal(fields));
            return;
        }
        var field = updates.get(index);
        evaluate(field.value(), env, listener.delegateFailureAndWrap((l, val) -> {
            fields.put(field.label(), val);
            evaluateUpdateFields(updates, index + 1, env, fields, l);
        }));
    }

    // ──── Literals ────

    private Value litToValue(LitVal lit) {
        return switch (lit) {
            case LitVal.IntegerLit v -> new Value.IntegerVal(v.value());
            case LitVal.LongLit v -> new Value.LongVal(v.value());
            case LitVal.DoubleLit v -> new Value.DoubleVal(v.value());
            case LitVal.KeywordLit v -> new Value.KeywordVal(v.value().utf8ToString());
            case LitVal.BooleanLit v -> new Value.BooleanVal(v.value());
            case LitVal.NullLit v -> new Value.NullVal();
            case LitVal.IndexLit v -> resolveIndex(v);
        };
    }

    private Value.IndexVal resolveIndex(LitVal.IndexLit lit) {
        String uuid = "";
        if (deps.clusterService() != null) {
            var project = deps.clusterService().state().metadata().getProject(ProjectId.DEFAULT);
            var indexMetadata = project.index(lit.name());
            if (indexMetadata != null) {
                uuid = indexMetadata.getIndex().getUUID();
            }
        }
        return new Value.IndexVal(lit.name(), uuid, lit.fieldTypes());
    }

    // ──── Function application (shared by CoreApp dispatch and EvalBuiltins) ────

    public void applyFunction(Value fn, Value arg, ActionListener<Value> listener) {
        switch (fn) {
            case Value.ClosureVal closure -> evaluate(closure.body(), prepend(arg, closure.env()), listener);
            case Value.BuiltinVal builtin -> EvalBuiltins.applyBuiltin(this, builtin, arg, listener);
            default -> listener.onFailure(new IllegalStateException("type checker bug: expected callable, got " + fn));
        }
    }

    // ──── Environment ────

    static Value[] prepend(Value value, Value[] env) {
        var newEnv = new Value[env.length + 1];
        newEnv[0] = value;
        System.arraycopy(env, 0, newEnv, 1, env.length);
        return newEnv;
    }
}
