/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.eval;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.common.util.concurrent.AtomicArray;
import org.elasticsearch.common.util.concurrent.CountDown;
import org.elasticsearch.xpack.piescript.core.CoreWhen;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Coordination primitive evaluation: {@code when} (positional channel collector).
 * See D-041.
 */
final class EvalCoordination {

    private EvalCoordination() {}

    static void evaluateWhen(Evaluator eval, CoreWhen when, Value[] env, ActionListener<Value> listener) {
        var bindings = when.bindings();
        int n = bindings.size();

        var collector = new PositionalCollector<Value>(
            n,
            listener.delegateFailureAndWrap((l, values) -> eval.evaluate(when.body(), extendEnv(env, values), l))
        );

        for (int i = 0; i < n; i++) {
            resolveChannel(eval, bindings.get(i), env, collector.listenerForSlot(i));
        }
    }

    /**
     * Evaluate a {@code when} binding's channel expression, then subscribe to the resulting channel.
     * Flattens the two-phase "eval binding → unwrap SpawnVal → subscribe" into a single async step.
     */
    private static void resolveChannel(Evaluator eval, CoreWhen.WhenBinding binding, Value[] env, ActionListener<Value> listener) {
        eval.evaluate(
            binding.channel(),
            env,
            listener.delegateFailureAndWrap((l, chanVal) -> ((Value.SpawnVal) chanVal).channel().addListener(l))
        );
    }

    /**
     * Extend the de Bruijn environment with channel results. Each {@code prepend}
     * pushes a value to the innermost position (index 0), so we iterate forward:
     * the last binding prepended becomes the innermost (index 0), and the first
     * binding ends up outermost — matching how the elaborator's {@code foldContext}
     * binds left-to-right with each new name shadowing at the innermost position.
     */
    private static Value[] extendEnv(Value[] base, List<Value> values) {
        var env = base;
        for (int i = 0; i < values.size(); i++) {
            env = Evaluator.prepend(values.get(i), env);
        }
        return env;
    }

    /**
     * Collects exactly {@code size} values into positional slots, then completes
     * a delegate listener with the collected list. Like {@code GroupedActionListener}
     * but preserves binding order instead of arrival order. Thread-safe.
     */
    private static class PositionalCollector<T> {
        private final AtomicArray<T> results;
        private final CountDown countdown;
        private final AtomicReference<Exception> failure = new AtomicReference<>();
        private final ActionListener<List<T>> delegate;

        PositionalCollector(int size, ActionListener<List<T>> delegate) {
            this.results = new AtomicArray<>(size);
            this.countdown = new CountDown(size);
            this.delegate = delegate;
        }

        ActionListener<T> listenerForSlot(int slot) {
            return ActionListener.wrap(value -> {
                results.setOnce(slot, value);
                tryComplete();
            }, e -> {
                failure.compareAndSet(null, e);
                tryComplete();
            });
        }

        private void tryComplete() {
            if (countdown.countDown()) {
                var f = failure.get();
                if (f != null) {
                    delegate.onFailure(f);
                } else {
                    delegate.onResponse(results.asList());
                }
            }
        }
    }
}
