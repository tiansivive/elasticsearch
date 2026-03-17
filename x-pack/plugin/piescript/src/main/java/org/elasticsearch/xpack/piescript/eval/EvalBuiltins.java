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
 * Built-in function application and list processing ({@code map}, {@code filter},
 * {@code reduce}). List element iteration is expressed as a {@link SubscribableListener}
 * chain — each element becomes one step in the chain, and the infrastructure handles
 * both synchronous inline completion and genuinely async suspension. See D-041.
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
            case "map" -> mapList(eval, args.get(0), requireList(args.get(1), name).elements(), listener);
            case "filter" -> filterList(eval, args.get(0), requireList(args.get(1), name).elements(), listener);
            case "reduce" -> reduceList(eval, args.get(0), args.get(1), requireList(args.get(2), name).elements(), listener);
            case "head" -> {
                var elems = requireList(args.get(0), name).elements();
                if (elems.isEmpty()) {
                    listener.onFailure(new EvaluationException("head: empty list"));
                } else {
                    listener.onResponse(elems.getFirst());
                }
            }
            case "tail" -> {
                var elems = requireList(args.get(0), name).elements();
                if (elems.isEmpty()) {
                    listener.onFailure(new EvaluationException("tail: empty list"));
                } else {
                    listener.onResponse(new Value.ListVal(elems.subList(1, elems.size())));
                }
            }
            case "length" -> listener.onResponse(new Value.IntegerVal(requireList(args.get(0), name).elements().size()));
            case "isEmpty" -> listener.onResponse(new Value.BooleanVal(requireList(args.get(0), name).elements().isEmpty()));
            case "topology" -> EvalTopology.resolveTopology(eval, args.get(0), listener);
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
}
