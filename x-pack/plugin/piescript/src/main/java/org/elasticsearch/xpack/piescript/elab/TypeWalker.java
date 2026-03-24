/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.elab;

import org.elasticsearch.xpack.piescript.types.MonoType;
import org.elasticsearch.xpack.piescript.types.RowType;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Type-level utilities: deep resolution through the zonker, and meta
 * collection for generalization.
 */
public final class TypeWalker {

    private TypeWalker() {}

    /**
     * Recursively resolve all solved metas in a type, producing a fully
     * grounded type (or as grounded as the zonker allows).
     */
    public static MonoType resolveDeep(MonoType type, ElaborationState state) {
        return switch (type) {
            case MonoType.Meta meta -> {
                var resolved = state.force(meta);
                yield resolved instanceof MonoType.Meta ? resolved : resolveDeep(resolved, state);
            }
            case MonoType.TCon t -> t;
            case MonoType.Rigid r -> r;
            case MonoType.Arrow(var param, var result) -> new MonoType.Arrow(resolveDeep(param, state), resolveDeep(result, state));
            case MonoType.RecordType(var row) when row instanceof RowType rowType -> {
                var flattened = state.resolveRow(rowType);
                var newFields = new LinkedHashMap<String, MonoType>();
                for (var entry : flattened.fields().entrySet()) {
                    newFields.put(entry.getKey(), resolveDeep(entry.getValue(), state));
                }
                yield new MonoType.RecordType(new RowType(newFields, flattened.tail()));
            }
            case MonoType.RecordType(var row) -> new MonoType.RecordType(resolveDeep(row, state));
            case RowType row -> {
                var flattened = state.resolveRow(row);
                var newFields = new LinkedHashMap<String, MonoType>();
                for (var entry : flattened.fields().entrySet()) {
                    newFields.put(entry.getKey(), resolveDeep(entry.getValue(), state));
                }
                yield new RowType(newFields, flattened.tail());
            }
            case MonoType.AppType(var ctor, var arg) -> new MonoType.AppType(resolveDeep(ctor, state), resolveDeep(arg, state));
        };
    }

    /**
     * Collect unsolved metas at or above the given binding level. Follows
     * the zonker so that solved metas are traversed into their solutions.
     */
    static void collectMetas(MonoType type, int bindingLevel, ElaborationState state, Map<Integer, MonoType> acc) {
        switch (state.force(type)) {
            case MonoType.Meta(var id, var lvl, var kind) -> {
                if (lvl >= bindingLevel && state.isSolved(id) == false) {
                    acc.put(id, kind);
                }
            }
            case MonoType.TCon ignored -> {
            }
            case MonoType.Rigid ignored -> {
            }
            case MonoType.Arrow(var param, var result) -> {
                collectMetas(param, bindingLevel, state, acc);
                collectMetas(result, bindingLevel, state, acc);
            }
            case MonoType.RecordType(var row) -> collectMetasInRow(row, bindingLevel, state, acc);
            case RowType row -> collectMetasInRow(row, bindingLevel, state, acc);
            case MonoType.AppType(var ctor, var arg) -> {
                collectMetas(ctor, bindingLevel, state, acc);
                collectMetas(arg, bindingLevel, state, acc);
            }
        }
    }

    private static void collectMetasInRow(MonoType row, int bindingLevel, ElaborationState state, Map<Integer, MonoType> acc) {
        if (row instanceof RowType rowType) {
            var flat = state.resolveRow(rowType);
            for (var fieldType : flat.fields().values()) {
                collectMetas(fieldType, bindingLevel, state, acc);
            }
            flat.tail().ifPresent(t -> {
                if (t instanceof MonoType.Meta rv && rv.bindingLevel() >= bindingLevel && state.isSolved(rv.id()) == false) {
                    acc.put(rv.id(), rv.kind());
                }
            });
        } else {
            collectMetas(row, bindingLevel, state, acc);
        }
    }
}
