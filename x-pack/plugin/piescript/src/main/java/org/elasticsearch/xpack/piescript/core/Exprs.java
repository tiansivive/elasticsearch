/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.core;

import org.apache.lucene.util.BytesRef;
import org.elasticsearch.xpack.esql.core.tree.Source;
import org.elasticsearch.xpack.piescript.types.Kind;
import org.elasticsearch.xpack.piescript.types.LitVal;
import org.elasticsearch.xpack.piescript.types.MonoType;
import org.elasticsearch.xpack.piescript.types.Op;
import org.elasticsearch.xpack.piescript.types.RowType;
import org.elasticsearch.xpack.piescript.types.Types;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Static factory methods for concise {@link CoreExpr} construction. All methods
 * use {@link Source#EMPTY} and infer types where possible. Intended for
 * {@code import static} use in tests and any code that constructs IR with
 * synthetic source locations.
 *
 * <p>Example usage:
 * <pre>{@code
 * import static org.elasticsearch.xpack.piescript.core.Exprs.*;
 * import static org.elasticsearch.xpack.piescript.types.Types.*;
 *
 * // map (fn r -> r.age) stream
 * var body = proj(var(0, "r", INTEGER), "age", INTEGER);
 * var lambda = lam("r", INTEGER, body);
 * var expr = app(app(free("map", INTEGER), lambda), var(0, "stream", INTEGER));
 *
 * // { a: 1, b: true }
 * var record = rec(field("a", lit(1)), field("b", lit(true)));
 * }</pre>
 */
public final class Exprs {

    private Exprs() {}

    /** Default source for DSL-constructed nodes. */
    public static final Source SRC = Source.EMPTY;

    // ──── Literals ────

    public static CoreLit lit(int n) {
        return new CoreLit(SRC, new LitVal.DoubleLit(n), Types.DOUBLE);
    }

    public static CoreLit lit(long n) {
        return new CoreLit(SRC, new LitVal.DoubleLit(n), Types.DOUBLE);
    }

    public static CoreLit lit(double d) {
        return new CoreLit(SRC, new LitVal.DoubleLit(d), Types.DOUBLE);
    }

    public static CoreLit lit(String s) {
        return new CoreLit(SRC, new LitVal.KeywordLit(new BytesRef(s)), Types.KEYWORD);
    }

    public static CoreLit lit(boolean b) {
        return new CoreLit(SRC, new LitVal.BooleanLit(b), Types.BOOLEAN);
    }

    public static CoreLit litNull() {
        return new CoreLit(SRC, new LitVal.NullLit(), Types.NULL);
    }

    /** Literal with explicit {@link LitVal} and type (escape hatch). */
    public static CoreLit lit(LitVal value, MonoType type) {
        return new CoreLit(SRC, value, type);
    }

    // ──── Variables ────

    public static CoreVar var(int index, MonoType type) {
        return new CoreVar(SRC, index, null, type);
    }

    public static CoreVar var(int index, String debugName, MonoType type) {
        return new CoreVar(SRC, index, debugName, type);
    }

    /** Module-level free variable (builtin reference). */
    public static CoreFree free(String name, MonoType type) {
        return new CoreFree(SRC, name, type);
    }

    // ──── Lambda ────

    /** Lambda with inferred arrow type: {@code paramType → body.type()}. */
    public static CoreLam lam(String name, MonoType paramType, CoreExpr body) {
        return new CoreLam(SRC, name, paramType, body, new MonoType.Arrow(paramType, body.type()));
    }

    /** Lambda with explicit overall type. */
    public static CoreLam lam(String name, MonoType paramType, CoreExpr body, MonoType type) {
        return new CoreLam(SRC, name, paramType, body, type);
    }

    // ──── Application ────

    /**
     * Application with inferred result type. If {@code fn.type()} is an
     * {@link MonoType.Arrow}, uses the arrow's result type; otherwise falls
     * back to {@code fn.type()} (useful when types are placeholders in tests).
     */
    public static CoreApp app(CoreExpr fn, CoreExpr arg) {
        MonoType resultType = fn.type() instanceof MonoType.Arrow a ? a.result() : fn.type();
        return new CoreApp(SRC, fn, arg, resultType);
    }

    /** Application with explicit result type. */
    public static CoreApp app(CoreExpr fn, CoreExpr arg, MonoType resultType) {
        return new CoreApp(SRC, fn, arg, resultType);
    }

    // ──── Let ────

    /** Let-binding with inferred bind type ({@code rhs.type()}) and overall type ({@code body.type()}). */
    public static CoreLet let(String name, CoreExpr rhs, CoreExpr body) {
        return new CoreLet(SRC, name, rhs.type(), rhs, body, body.type());
    }

    /** Let-binding with explicit types. */
    public static CoreLet let(String name, MonoType bindType, CoreExpr rhs, CoreExpr body, MonoType type) {
        return new CoreLet(SRC, name, bindType, rhs, body, type);
    }

    // ──── Records ────

    /** Record field pair for use with {@link #rec}. */
    public static CoreField field(String label, CoreExpr value) {
        return new CoreField(label, value);
    }

    /**
     * Record literal with inferred type. Builds a closed record type from
     * each field's label and its value expression's type.
     */
    public static CoreRecord rec(CoreField... fields) {
        var labels = new ArrayList<String>(fields.length);
        var values = new ArrayList<CoreExpr>(fields.length);
        var fieldTypes = new LinkedHashMap<String, MonoType>();
        for (var f : fields) {
            labels.add(f.label());
            values.add(f.value());
            fieldTypes.put(f.label(), f.value().type());
        }
        return new CoreRecord(SRC, labels, values, new MonoType.RecordType(RowType.closed(fieldTypes)));
    }

    /** Field projection. */
    public static CoreProject proj(CoreExpr expr, String label, MonoType type) {
        return new CoreProject(SRC, expr, label, type);
    }

    /**
     * Record update with inferred type. Merges the base record's fields
     * with the update fields; update fields override existing ones.
     */
    public static CoreUpdate update(CoreExpr base, CoreField... updates) {
        var labels = new ArrayList<String>(updates.length);
        var children = new ArrayList<CoreExpr>(1 + updates.length);
        children.add(base);
        var fieldTypes = new LinkedHashMap<String, MonoType>();
        if (base.type() instanceof MonoType.RecordType rt) {
            fieldTypes.putAll(((RowType) rt.row()).fields());
        }
        for (var f : updates) {
            labels.add(f.label());
            children.add(f.value());
            fieldTypes.put(f.label(), f.value().type());
        }
        return new CoreUpdate(SRC, labels, children, new MonoType.RecordType(RowType.closed(fieldTypes)));
    }

    // ──── Primitive operations ────

    /**
     * Primitive operation with inferred result type. Comparison and boolean
     * operators produce {@link Types#BOOLEAN}; arithmetic and negation
     * produce the first argument's type.
     */
    public static CorePrimOp prim(Op op, CoreExpr... args) {
        MonoType resultType = switch (op) {
            case EQ, NEQ, LT, GT, LTE, GTE, AND, OR, NOT -> Types.BOOLEAN;
            case ADD, SUB, MUL, DIV, MOD, NEG -> args[0].type();
        };
        return new CorePrimOp(SRC, op, List.of(args), resultType);
    }

    public static CorePrimOp add(CoreExpr a, CoreExpr b) {
        return prim(Op.ADD, a, b);
    }

    public static CorePrimOp sub(CoreExpr a, CoreExpr b) {
        return prim(Op.SUB, a, b);
    }

    public static CorePrimOp mul(CoreExpr a, CoreExpr b) {
        return prim(Op.MUL, a, b);
    }

    public static CorePrimOp gt(CoreExpr a, CoreExpr b) {
        return prim(Op.GT, a, b);
    }

    public static CorePrimOp eq(CoreExpr a, CoreExpr b) {
        return prim(Op.EQ, a, b);
    }

    // ──── System F ────

    public static CoreTypeAbs typeAbs(int rigidId, Kind kind, CoreExpr body) {
        return new CoreTypeAbs(SRC, rigidId, kind, body, body.type());
    }

    public static CoreTypeApp typeApp(CoreExpr polyExpr, MonoType typeArg, MonoType resultType) {
        return new CoreTypeApp(SRC, polyExpr, typeArg, resultType);
    }

    // ──── Query ────

    public static CoreQuery query(String esql, String indexPattern, MonoType type) {
        return new CoreQuery(SRC, esql, indexPattern, type);
    }

    // ──── Coordination (Join Calculus) ────

    /** {@code spawn body} — fork computation, return {@code Channel bodyType}. */
    public static CoreSpawn spawn(CoreExpr body) {
        return new CoreSpawn(SRC, body, Types.channel(body.type()));
    }

    /** {@code spawn!} — bare channel creation with explicit inner type. */
    public static CoreSpawn spawnBang(MonoType innerType) {
        return new CoreSpawn(SRC, null, Types.channel(innerType));
    }

    /** {@code send channel value} — fire-and-forget, type is {@code Null}. */
    public static CoreSend send(CoreExpr channel, CoreExpr value) {
        return new CoreSend(SRC, channel, value, Types.NULL);
    }

    /** {@code when} binding: channel expression paired with a debug name. */
    public static CoreWhen.WhenBinding binding(CoreExpr channel, String debugName) {
        return new CoreWhen.WhenBinding(channel, debugName);
    }

    /** {@code when (bindings) -> body} with inferred type from body. */
    public static CoreWhen when(List<CoreWhen.WhenBinding> bindings, CoreExpr body) {
        return new CoreWhen(SRC, bindings, body, body.type());
    }
}
