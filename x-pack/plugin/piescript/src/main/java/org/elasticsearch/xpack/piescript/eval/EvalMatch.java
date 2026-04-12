/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.eval;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.xpack.piescript.core.Alternative;
import org.elasticsearch.xpack.piescript.core.CoreMatch;
import org.elasticsearch.xpack.piescript.core.Pattern;
import org.elasticsearch.xpack.piescript.types.LitVal;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Evaluates {@link CoreMatch} expressions.
 */
final class EvalMatch {

    private EvalMatch() {}

    /**
     * Evaluates a match expression. The scrutinee is evaluated first, then
     * the arms are tried top-to-bottom. The first matching arm binds its
     * variables in the environment and evaluates its body.
     */
    static void evaluateMatch(CoreMatch match, Value[] env, Evaluator eval, ActionListener<Value> listener) {
        eval.evaluate(match.scrutinee(), env, ActionListener.wrap(
            scrutineeVal -> match.arms().stream()
                .map(arm -> tryMatch(arm.pattern(), scrutineeVal).map(bindings -> Map.entry(arm, bindings)))
                .flatMap(Optional::stream)
                .findFirst()
                .ifPresentOrElse(
                    matchResult -> {
                        Value[] newEnv = extendEnv(env, matchResult.getValue());
                        eval.evaluate(matchResult.getKey().body(), newEnv, listener);
                    },
                    () -> listener.onFailure(new EvaluationException("No match for value: " + scrutineeVal))
                ),
            listener::onFailure
        ));
    }

    /**
     * Attempts to match a pattern against a value.
     *
     * @return an Optional containing the bound values in the order they were bound,
     *         or empty if the match fails.
     */
    private static Optional<List<Value>> tryMatch(Pattern pattern, Value value) {
        List<Value> bindings = new ArrayList<>();
        return matchRecursive(pattern, value, bindings) ? Optional.of(bindings) : Optional.empty();
    }

    private static boolean matchRecursive(Pattern pattern, Value value, List<Value> bindings) {
        return switch (pattern) {
            case Pattern.LitPat litPat -> matchLiteral(litPat, value);
            
            case Pattern.VarPat varPat -> {
                bindings.add(value);
                yield true;
            }
            
            case Pattern.WildcardPat wildcardPat -> true;
            
            case Pattern.RecordPat recordPat -> {
                if (!(value instanceof Value.RecordVal recordVal)) {
                    yield false;
                }
                
                var recordFields = recordVal.fields();
                
                // Record patterns bind fields in alphabetical order of field names
                var sortedFields = new TreeMap<>(recordPat.fields());
                
                boolean allMatch = sortedFields.entrySet().stream().allMatch(entry -> {
                    Value fieldValue = recordFields.get(entry.getKey());
                    return fieldValue != null && matchRecursive(entry.getValue(), fieldValue, bindings);
                });
                
                if (!allMatch) {
                    yield false;
                }
                
                if (recordPat.hasTail()) {
                    // Extract the remaining fields into a new RecordVal
                    var tailFields = recordFields.entrySet().stream()
                        .filter(e -> !recordPat.fields().containsKey(e.getKey()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
                    bindings.add(new Value.RecordVal(tailFields));
                }
                
                yield true;
            }
            
            case Pattern.ListPat listPat -> {
                if (!(value instanceof Value.ListVal listVal)) {
                    yield false;
                }
                
                var elements = listVal.elements();
                var patElements = listPat.elements();
                
                if (elements.size() != patElements.size()) {
                    yield false;
                }
                
                yield IntStream.range(0, elements.size())
                    .allMatch(i -> matchRecursive(patElements.get(i), elements.get(i), bindings));
            }
            
            case Pattern.ConsListPat consPat -> {
                if (!(value instanceof Value.ListVal listVal) || listVal.elements().isEmpty()) {
                    yield false;
                }
                
                var elements = listVal.elements();
                if (!matchRecursive(consPat.head(), elements.get(0), bindings)) {
                    yield false;
                }
                
                var tailVal = new Value.ListVal(elements.subList(1, elements.size()));
                yield matchRecursive(consPat.tail(), tailVal, bindings);
            }
        };
    }

    private static boolean matchLiteral(Pattern.LitPat litPat, Value value) {
        return switch (litPat.value()) {
            case LitVal.IntegerLit i when value instanceof Value.DoubleVal d -> d.value() == i.value();
            case LitVal.LongLit l when value instanceof Value.DoubleVal d -> d.value() == l.value();
            case LitVal.DoubleLit dec when value instanceof Value.DoubleVal d -> d.value() == dec.value();
            case LitVal.KeywordLit k when value instanceof Value.KeywordVal kv -> kv.value().equals(k.value().utf8ToString());
            case LitVal.BooleanLit b when value instanceof Value.BooleanVal bv -> bv.value() == b.value();
            case LitVal.NullLit n when value instanceof Value.NullVal nv -> true;
            default -> false;
        };
    }

    private static Value[] extendEnv(Value[] env, List<Value> bindings) {
        // The bindings are collected in left-to-right order.
        // Prepending them one by one left-to-right naturally puts the rightmost
        // binding at index 0, which matches de Bruijn indexing semantics.
        return bindings.stream().reduce(env, (e, v) -> Evaluator.prepend(v, e), (e1, e2) -> e1);
    }
}
