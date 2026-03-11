/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.elab;

import org.elasticsearch.xpack.piescript.types.MonoType;
import org.elasticsearch.xpack.piescript.types.RowType;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Robinson unification over {@link MonoType}. Reads and writes the zonker on
 * {@link ElaborationState}. Returns {@code Optional.empty()} on success,
 * {@code Optional.of(TypeError)} on failure.
 *
 * <p>Phase 1 handles: {@code TCon}, {@code Arrow}, {@code RecordType} (closed
 * rows only), {@code AppType}, {@code Meta}, and null-as-bottom (D1.11).
 */
public final class Unifier {

    private static final String NULL_TYPE = "Null";

    private Unifier() {}

    /**
     * Unify two types, writing solutions to the zonker. Both sides are resolved
     * through the zonker before dispatch.
     */
    public static Optional<TypeError> unify(MonoType a, MonoType b, ElaborationState state) {
        var ra = state.resolveType(a);
        var rb = state.resolveType(b);

        if (ra.equals(rb)) return Optional.empty();
        if (ra instanceof MonoType.TCon(var n) && n.equals(NULL_TYPE)) return Optional.empty();
        if (rb instanceof MonoType.TCon(var n) && n.equals(NULL_TYPE)) return Optional.empty();
        if (ra instanceof MonoType.Meta ma) return solveMeta(ma, rb, state);
        if (rb instanceof MonoType.Meta mb) return solveMeta(mb, ra, state);

        return switch (ra) {
            case MonoType.TCon(var na) when rb instanceof MonoType.TCon(var nb) && na.equals(nb) -> Optional.empty();
            case MonoType.Arrow(var p1, var r1) when rb instanceof MonoType.Arrow(var p2, var r2) -> unify(p1, p2, state).or(
                () -> unify(r1, r2, state)
            );
            case MonoType.RecordType(var row1) when rb instanceof MonoType.RecordType(var row2) -> unifyRows(row1, row2, state);
            case MonoType.AppType(var c1, var a1) when rb instanceof MonoType.AppType(var c2, var a2) -> unify(c1, c2, state).or(
                () -> unify(a1, a2, state)
            );
            default -> Optional.of(new TypeError.Mismatch(ra, rb));
        };
    }

    private static Optional<TypeError> solveMeta(MonoType.Meta meta, MonoType type, ElaborationState state) {
        if (occursIn(meta.id(), type, state)) {
            return Optional.of(new TypeError.InfiniteType(meta, type));
        }
        state.solve(meta.id(), type);
        return Optional.empty();
    }

    /**
     * Closed-row unification: field sets must match exactly, each pair unified.
     * Open rows (Phase 2) are not handled.
     */
    private static Optional<TypeError> unifyRows(RowType a, RowType b, ElaborationState state) {
        var onlyInA = difference(a.fields().keySet(), b.fields().keySet());
        if (onlyInA.isEmpty() == false) {
            return Optional.of(new TypeError.MissingFields(onlyInA, new MonoType.RecordType(b)));
        }

        var onlyInB = difference(b.fields().keySet(), a.fields().keySet());
        if (onlyInB.isEmpty() == false) {
            return Optional.of(new TypeError.MissingFields(onlyInB, new MonoType.RecordType(a)));
        }

        for (var label : a.fields().keySet()) {
            var error = unify(a.fields().get(label), b.fields().get(label), state);
            if (error.isPresent()) {
                return Optional.of(new TypeError.FieldMismatch(label, error.get()));
            }
        }
        return Optional.empty();
    }

    private static Set<String> difference(Set<String> left, Set<String> right) {
        var result = new HashSet<>(left);
        result.removeAll(right);
        return result;
    }

    /**
     * Occurs check: does {@code metaId} appear anywhere in {@code type}
     * (after resolving through the zonker)?
     */
    private static boolean occursIn(int metaId, MonoType type, ElaborationState state) {
        return switch (state.resolveType(type)) {
            case MonoType.Meta(var id, var lvl, var kind) -> id == metaId;
            case MonoType.TCon t -> false;
            case MonoType.Arrow(var param, var result) -> occursIn(metaId, param, state) || occursIn(metaId, result, state);
            case MonoType.RecordType(var row) -> occursInRow(metaId, row, state);
            case MonoType.AppType(var ctor, var arg) -> occursIn(metaId, ctor, state) || occursIn(metaId, arg, state);
        };
    }

    private static boolean occursInRow(int metaId, RowType row, ElaborationState state) {
        return row.fields().values().stream().anyMatch(fieldType -> occursIn(metaId, fieldType, state))
            || row.rowVar().map(rv -> rv.id() == metaId).orElse(false);
    }
}
