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
 * Built-in function application and stream processing ({@code map}, {@code filter},
 * {@code reduce}). Stream element iteration is expressed as a {@link SubscribableListener}
 * chain — each element becomes one step in the chain, and the infrastructure handles
 * both synchronous inline completion and genuinely async suspension. See D-041.
 *
 * <p>The chain approach allocates O(n) listeners upfront. An iterative while-loop
 * (ThrottledIterator-style) would achieve O(1) outstanding listeners; worth
 * revisiting if stream sizes grow large enough for the allocation to matter.
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
            case "map" -> mapStream(eval, args.get(0), requireStream(args.get(1), name).elements(), listener);
            case "filter" -> filterStream(eval, args.get(0), requireStream(args.get(1), name).elements(), listener);
            case "reduce" -> reduceStream(eval, args.get(0), args.get(1), requireStream(args.get(2), name).elements(), listener);
            default -> listener.onFailure(new EvaluationException("unknown built-in: " + name));
        }
    }

    private static void mapStream(Evaluator eval, Value fn, List<Value> elements, ActionListener<Value> listener) {
        var results = new ArrayList<Value>();
        var chain = SubscribableListener.<Void>newForked(l -> l.onResponse(null));
        for (var element : elements) {
            chain = chain.<Void>andThen((l, ignored) -> eval.applyFunction(fn, element, l.map(val -> {
                results.add(val);
                return null;
            })));
        }
        chain.addListener(listener.safeMap(ignored -> new Value.StreamVal(results)));
    }

    private static void filterStream(Evaluator eval, Value fn, List<Value> elements, ActionListener<Value> listener) {
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
        chain.addListener(listener.safeMap(ignored -> new Value.StreamVal(results)));
    }

    private static void reduceStream(Evaluator eval, Value fn, Value initialAcc, List<Value> elements, ActionListener<Value> listener) {
        var chain = SubscribableListener.<Value>newForked(l -> l.onResponse(initialAcc));
        for (var element : elements) {
            chain = chain.<Value>andThen((l, acc) ->
                eval.applyFunction(fn, acc, l.delegateFailureAndWrap((l2, partial) -> eval.applyFunction(partial, element, l2)))
            );
        }
        chain.addListener(listener);
    }

    private static Value.StreamVal requireStream(Value value, String builtinName) {
        return switch (value) {
            case Value.StreamVal s -> s;
            default -> throw new AssertionError("type checker bug: expected Stream for " + builtinName + ", got " + value);
        };
    }
}
