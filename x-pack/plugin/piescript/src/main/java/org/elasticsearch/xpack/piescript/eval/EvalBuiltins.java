/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.eval;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.support.SubscribableListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Built-in function application: list processing ({@code map}, {@code filter},
 * {@code reduce}), math functions ({@code abs}, {@code floor}, {@code sqrt}, etc.),
 * and cluster topology lookups. List element iteration is expressed as a
 * {@link SubscribableListener} chain — each element becomes one step in the chain,
 * and the infrastructure handles both synchronous inline completion and genuinely
 * async suspension. See D-041.
 *
 * <p>The chain approach allocates O(n) listeners upfront. An iterative while-loop
 * (ThrottledIterator-style) would achieve O(1) outstanding listeners; worth
 * revisiting if list sizes grow large enough for the allocation to matter.
 */
final class EvalBuiltins {

    private EvalBuiltins() {}

    static void applyBuiltin(Evaluator eval, Value.BuiltinVal builtin, Value arg, ActionListener<Value> listener) {
        var args = new ArrayList<>(builtin.partialArgs());
        args.add(arg);
        if (args.size() < builtin.arity()) {
            listener.onResponse(new Value.BuiltinVal(builtin.name(), builtin.arity(), List.copyOf(args)));
        } else {
            executeBuiltin(eval, builtin.name(), args, listener);
        }
    }

    private static void executeBuiltin(Evaluator eval, String name, List<Value> args, ActionListener<Value> listener) {
        switch (name) {
            case "List.map" -> mapList(eval, args.get(0), requireList(args.get(1), name).elements(), listener);
            case "List.filter" -> filterList(eval, args.get(0), requireList(args.get(1), name).elements(), listener);
            case "List.reduce" -> reduceList(eval, args.get(0), args.get(1), requireList(args.get(2), name).elements(), listener);
            case "List.head" -> {
                var elems = requireList(args.get(0), name).elements();
                if (elems.isEmpty()) {
                    listener.onFailure(new EvaluationException("List.head: empty list"));
                } else {
                    listener.onResponse(elems.getFirst());
                }
            }
            case "List.tail" -> {
                var elems = requireList(args.get(0), name).elements();
                if (elems.isEmpty()) {
                    listener.onFailure(new EvaluationException("List.tail: empty list"));
                } else {
                    listener.onResponse(new Value.ListVal(elems.subList(1, elems.size())));
                }
            }
            case "List.length" -> listener.onResponse(new Value.DoubleVal(requireList(args.get(0), name).elements().size()));
            case "List.isEmpty" -> listener.onResponse(new Value.BooleanVal(requireList(args.get(0), name).elements().isEmpty()));
            case "List.at" -> {
                int idx = (int) requireDouble(args.get(0), name);
                var elems = requireList(args.get(1), name).elements();
                if (idx < 0 || idx >= elems.size()) {
                    listener.onFailure(
                        new EvaluationException("List.at: index " + idx + " out of bounds for list of size " + elems.size())
                    );
                } else {
                    listener.onResponse(elems.get(idx));
                }
            }
            case "Math.abs" -> listener.onResponse(new Value.DoubleVal(Math.abs(requireDouble(args.get(0), name))));
            case "Math.floor" -> listener.onResponse(new Value.DoubleVal(Math.floor(requireDouble(args.get(0), name))));
            case "Math.ceil" -> listener.onResponse(new Value.DoubleVal(Math.ceil(requireDouble(args.get(0), name))));
            case "Math.round" -> listener.onResponse(new Value.DoubleVal(Math.round(requireDouble(args.get(0), name))));
            case "Math.sqrt" -> listener.onResponse(new Value.DoubleVal(Math.sqrt(requireDouble(args.get(0), name))));
            case "Math.log" -> listener.onResponse(new Value.DoubleVal(Math.log(requireDouble(args.get(0), name))));
            case "Math.min" -> listener.onResponse(
                new Value.DoubleVal(Math.min(requireDouble(args.get(0), name), requireDouble(args.get(1), name)))
            );
            case "Math.max" -> listener.onResponse(
                new Value.DoubleVal(Math.max(requireDouble(args.get(0), name), requireDouble(args.get(1), name)))
            );
            case "Math.pow" -> listener.onResponse(
                new Value.DoubleVal(Math.pow(requireDouble(args.get(0), name), requireDouble(args.get(1), name)))
            );
            case "Math.toInt" -> listener.onResponse(new Value.DoubleVal((long) requireDouble(args.get(0), name)));
            case "Cluster.topology" -> EvalTopology.resolveClusterTopology(eval, listener);
            case "Index.routing" -> EvalTopology.resolveRouting(eval, args.get(0), listener);
            case "Index.shards" -> EvalTopology.resolveRouting(eval, args.get(0), listener.map(v -> {
                var rec = (Value.RecordVal) v;
                return rec.fields().get("shards");
            }));
            case "Index.nodes" -> EvalTopology.resolveRouting(eval, args.get(0), listener.map(v -> {
                var rec = (Value.RecordVal) v;
                return rec.fields().get("nodes");
            }));
            case "Shard.open" -> EvalShard.open(
                eval,
                requireIndexVal(args.get(0), name),
                requireRecord(args.get(1), name),
                requireRecord(args.get(2), name),
                listener
            );
            case "Shard.consume" -> EvalShard.consume(
                requireSearcherVal(args.get(1), name),
                (int) requireDouble(args.get(0), name),
                listener
            );
            case "Shard.read" -> EvalShard.read(requireDocRefVal(args.get(0), name), listener);
            case "Shard.writer" -> EvalWrite.writer(eval, requireIndexVal(args.get(0), name), requireRecord(args.get(1), name), listener);
            case "Shard.write" -> {
                var docId = switch (args.get(1)) {
                    case Value.KeywordVal k -> k.value();
                    default -> throw new AssertionError("type checker bug: expected Keyword for Shard.write _id, got " + args.get(1));
                };
                EvalWrite.write(requireWriterVal(args.get(0), name), docId, requireRecord(args.get(2), name), listener);
            }
            case "Shard.refresh" -> EvalWrite.refresh(eval, requireWriterVal(args.get(0), name), listener);
            case "Shard.globalCheckpoint" -> EvalWrite.globalCheckpoint(
                eval,
                requireIndexVal(args.get(0), name),
                requireRecord(args.get(1), name),
                listener
            );
            case "Index.bulk" -> {
                var indexName = switch (args.get(0)) {
                    case Value.KeywordVal k -> k.value();
                    default -> throw new AssertionError("type checker bug: expected Keyword for Index.bulk, got " + args.get(0));
                };
                EvalWrite.bulk(eval, indexName, requireList(args.get(1), name), listener);
            }
            // ──── ESQL.* builtins (Block F — D-052) ────
            // NbE-style: closures are partially evaluated with a Symbol("") row,
            // producing a Symbol carrying the compiled ESQL fragment.
            case "ESQL.from" -> {
                var idx = requireIndexVal(args.get(0), name);
                listener.onResponse(new Value.Symbol("FROM " + idx.name()));
            }
            case "ESQL.where" -> esqlClosureCommand(eval, "WHERE", args, listener);
            case "ESQL.eval" -> esqlEvalCommand(eval, args, listener);
            case "ESQL.keep" -> {
                var fields = requireList(args.get(0), name).elements().stream().map(v -> ((Value.KeywordVal) v).value()).toList();
                var pipeline = requireSymbol(args.get(1), name);
                listener.onResponse(new Value.Symbol(pipeline.esql() + " | KEEP " + String.join(", ", fields)));
            }
            case "ESQL.drop" -> {
                var fields = requireList(args.get(0), name).elements().stream().map(v -> ((Value.KeywordVal) v).value()).toList();
                var pipeline = requireSymbol(args.get(1), name);
                listener.onResponse(new Value.Symbol(pipeline.esql() + " | DROP " + String.join(", ", fields)));
            }
            case "ESQL.limit" -> {
                int count = (int) requireDouble(args.get(0), name);
                var pipeline = requireSymbol(args.get(1), name);
                listener.onResponse(new Value.Symbol(pipeline.esql() + " | LIMIT " + count));
            }
            case "ESQL.sort" -> esqlSortCommand(eval, false, args, listener);
            case "ESQL.sortDesc" -> esqlSortCommand(eval, true, args, listener);
            case "ESQL.rename" -> {
                var entries = requireList(args.get(0), name).elements();
                var parts = new java.util.ArrayList<String>();
                for (var entry : entries) {
                    var rec = requireRecord(entry, name);
                    parts.add(
                        ((Value.KeywordVal) rec.fields().get("from")).value() + " AS " + ((Value.KeywordVal) rec.fields().get("to")).value()
                    );
                }
                var pipeline = requireSymbol(args.get(1), name);
                listener.onResponse(new Value.Symbol(pipeline.esql() + " | RENAME " + String.join(", ", parts)));
            }
            case "ESQL.explain" -> {
                var pipeline = requireSymbol(args.get(0), name);
                listener.onResponse(new Value.KeywordVal(pipeline.esql()));
            }
            default -> listener.onFailure(new EvaluationException("unknown built-in: " + name));
        }
    }

    private static void mapList(Evaluator eval, Value fn, List<Value> elements, ActionListener<Value> listener) {
        var results = new ArrayList<Value>();
        var chain = SubscribableListener.<Void>newForked(l -> l.onResponse(null));
        for (var element : elements) {
            chain = chain.<Void>andThen((l, ignored) -> eval.applyFunction(fn, element, l.map(val -> {
                results.add(val);
                return null;
            })));
        }
        chain.addListener(listener.safeMap(ignored -> new Value.ListVal(results)));
    }

    private static void filterList(Evaluator eval, Value fn, List<Value> elements, ActionListener<Value> listener) {
        var results = new ArrayList<Value>();
        var chain = SubscribableListener.<Void>newForked(l -> l.onResponse(null));
        for (var element : elements) {
            chain = chain.<Void>andThen((l, ignored) -> eval.applyFunction(fn, element, l.map(val -> {
                if (val instanceof Value.BooleanVal(var b) && b) {
                    results.add(element);
                }
                return null;
            })));
        }
        chain.addListener(listener.safeMap(ignored -> new Value.ListVal(results)));
    }

    private static void reduceList(Evaluator eval, Value fn, Value initialAcc, List<Value> elements, ActionListener<Value> listener) {
        var chain = SubscribableListener.<Value>newForked(l -> l.onResponse(initialAcc));
        for (var element : elements) {
            chain = chain.<Value>andThen(
                (l, acc) -> eval.applyFunction(fn, acc, l.delegateFailureAndWrap((l2, partial) -> eval.applyFunction(partial, element, l2)))
            );
        }
        chain.addListener(listener);
    }

    static Value.ListVal requireList(Value value, String builtinName) {
        return switch (value) {
            case Value.ListVal s -> s;
            default -> throw new AssertionError("type checker bug: expected List for " + builtinName + ", got " + value);
        };
    }

    static Value.IndexVal requireIndexVal(Value value, String builtinName) {
        return switch (value) {
            case Value.IndexVal v -> v;
            default -> throw new AssertionError("type checker bug: expected Index for " + builtinName + ", got " + value);
        };
    }

    static Value.RecordVal requireRecord(Value value, String builtinName) {
        return switch (value) {
            case Value.RecordVal v -> v;
            default -> throw new AssertionError("type checker bug: expected Record for " + builtinName + ", got " + value);
        };
    }

    static Value.SearcherVal requireSearcherVal(Value value, String builtinName) {
        return switch (value) {
            case Value.SearcherVal v -> v;
            default -> throw new AssertionError("type checker bug: expected Searcher for " + builtinName + ", got " + value);
        };
    }

    static Value.DocRefVal requireDocRefVal(Value value, String builtinName) {
        return switch (value) {
            case Value.DocRefVal v -> v;
            default -> throw new AssertionError("type checker bug: expected DocRef for " + builtinName + ", got " + value);
        };
    }

    static Value.WriterVal requireWriterVal(Value value, String builtinName) {
        return switch (value) {
            case Value.WriterVal v -> v;
            default -> throw new AssertionError("type checker bug: expected Writer for " + builtinName + ", got " + value);
        };
    }

    static double requireDouble(Value value, String builtinName) {
        return switch (value) {
            case Value.DoubleVal v -> v.value();
            case Value.IntegerVal v -> (double) v.value();
            case Value.LongVal v -> (double) v.value();
            default -> throw new AssertionError("type checker bug: expected Double for " + builtinName + ", got " + value);
        };
    }

    private static Value.Symbol requireSymbol(Value value, String builtinName) {
        return switch (value) {
            case Value.Symbol s -> s;
            default -> throw new AssertionError("type checker bug: expected Symbol for " + builtinName + ", got " + value);
        };
    }

    /**
     * Partially evaluate a closure with {@code Symbol("")} as the row argument,
     * then append the resulting ESQL fragment as {@code | COMMAND <fragment>}.
     * Used by {@code ESQL.where}.
     */
    private static void esqlClosureCommand(Evaluator eval, String command, java.util.List<Value> args, ActionListener<Value> listener) {
        var closure = args.get(0);
        var pipeline = requireSymbol(args.get(1), "ESQL." + command.toLowerCase());
        eval.applyFunction(closure, new Value.Symbol(""), listener.delegateFailureAndWrap((l, result) -> {
            var fragment = requireSymbol(result, "ESQL." + command.toLowerCase() + " closure result");
            l.onResponse(new Value.Symbol(pipeline.esql() + " | " + command + " " + fragment.esql()));
        }));
    }

    /**
     * Partially evaluate a closure with {@code Symbol("")} as the row argument,
     * expecting a record result. Compiles each field as {@code label = fragment}.
     * Used by {@code ESQL.eval}.
     */
    private static void esqlEvalCommand(Evaluator eval, java.util.List<Value> args, ActionListener<Value> listener) {
        var closure = args.get(0);
        var pipeline = requireSymbol(args.get(1), "ESQL.eval");
        eval.applyFunction(closure, new Value.Symbol(""), listener.delegateFailureAndWrap((l, result) -> {
            if (result instanceof Value.RecordVal rec) {
                var parts = new java.util.ArrayList<String>();
                for (var entry : rec.fields().entrySet()) {
                    parts.add(entry.getKey() + " = " + compileValueToEsql(entry.getValue()));
                }
                l.onResponse(new Value.Symbol(pipeline.esql() + " | EVAL " + String.join(", ", parts)));
            } else {
                l.onFailure(new EvaluationException("ESQL.eval: closure must produce a record, got " + result.getClass().getSimpleName()));
            }
        }));
    }

    /**
     * Partially evaluate a sort key closure with {@code Symbol("")} as the row argument.
     * Used by {@code ESQL.sort} and {@code ESQL.sortDesc}.
     */
    private static void esqlSortCommand(Evaluator eval, boolean descending, java.util.List<Value> args, ActionListener<Value> listener) {
        var closure = args.get(0);
        var pipeline = requireSymbol(args.get(1), "ESQL.sort");
        eval.applyFunction(closure, new Value.Symbol(""), listener.delegateFailureAndWrap((l, result) -> {
            var fragment = requireSymbol(result, "ESQL.sort closure result");
            l.onResponse(new Value.Symbol(pipeline.esql() + " | SORT " + fragment.esql() + (descending ? " DESC" : " ASC")));
        }));
    }

    /**
     * Compile a {@link Value} to an ESQL expression fragment. Symbols pass through;
     * concrete values become ESQL literals.
     */
    static String compileValueToEsql(Value value) {
        return switch (value) {
            case Value.Symbol s -> s.esql();
            case Value.DoubleVal d -> {
                double v = d.value();
                yield v == Math.floor(v) && Double.isFinite(v) ? String.valueOf((long) v) : String.valueOf(v);
            }
            case Value.IntegerVal i -> String.valueOf(i.value());
            case Value.LongVal l -> String.valueOf(l.value());
            case Value.KeywordVal k -> "\"" + k.value().replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
            case Value.BooleanVal b -> b.value() ? "true" : "false";
            case Value.NullVal ignored -> "null";
            default -> throw new EvaluationException(
                "cannot compile to ESQL: " + value.getClass().getSimpleName() + " is not an ESQL-compatible value"
            );
        };
    }
}
