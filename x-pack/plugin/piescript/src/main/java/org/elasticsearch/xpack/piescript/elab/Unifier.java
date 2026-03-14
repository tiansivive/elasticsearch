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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Robinson unification over {@link MonoType}. Reads and writes the zonker on
 * {@link ElaborationState}. Returns {@code Optional.empty()} on success,
 * {@code Optional.of(TypeError)} on failure.
 *
 * <p>Handles {@code TCon}, {@code Arrow}, {@code RecordType} (closed and open
 * rows via Leijen-style unification, D-030), {@code AppType}, {@code Meta},
 * {@code Rigid}, and null-as-bottom (D1.11).
 */
public final class Unifier {

    private static final String NULL_TYPE = "Null";

    private Unifier() {}

    /**
     * Unify two types, writing solutions to the zonker. Both sides are resolved
     * through the zonker before dispatch.
     */
    public static Optional<TypeError> unify(MonoType a, MonoType b, ElaborationState state) {
        var ra = state.zonkOrKeep(a);
        var rb = state.zonkOrKeep(b);

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
     * Leijen-style open-row unification (D-030). Flattens both rows through the
     * zonker, unifies common fields pairwise, then dispatches on tails:
     * <ul>
     *   <li>Both closed, no excess fields → success</li>
     *   <li>Both closed, excess on either side → MissingFields error</li>
     *   <li>One open, one closed → solve tail to closed row of excess fields</li>
     *   <li>Both open → fresh row meta; solve both tails</li>
     * </ul>
     */
    private static Optional<TypeError> unifyRows(RowType rawA, RowType rawB, ElaborationState state) {
        var a = state.resolveRow(rawA);
        var b = state.resolveRow(rawB);

        var commonLabels = new HashSet<>(a.fields().keySet());
        commonLabels.retainAll(b.fields().keySet());
        for (var label : commonLabels) {
            var error = unify(a.fields().get(label), b.fields().get(label), state);
            if (error.isPresent()) {
                return Optional.of(new TypeError.FieldMismatch(label, error.get()));
            }
        }

        var onlyA = filterKeys(a.fields(), commonLabels);
        var onlyB = filterKeys(b.fields(), commonLabels);

        var tailA = a.rowVar();
        var tailB = b.rowVar();

        if (tailA.isEmpty() && tailB.isEmpty()) {
            if (onlyA.isEmpty() == false) return Optional.of(new TypeError.MissingFields(onlyA.keySet(), new MonoType.RecordType(b)));
            if (onlyB.isEmpty() == false) return Optional.of(new TypeError.MissingFields(onlyB.keySet(), new MonoType.RecordType(a)));
            return Optional.empty();
        }

        if (tailA.isPresent() && tailB.isEmpty()) {
            if (onlyA.isEmpty() == false) return Optional.of(new TypeError.MissingFields(onlyA.keySet(), new MonoType.RecordType(b)));
            return solveRowTail(tailA.get(), onlyB, Optional.empty(), state);
        }

        if (tailA.isEmpty() && tailB.isPresent()) {
            if (onlyB.isEmpty() == false) return Optional.of(new TypeError.MissingFields(onlyB.keySet(), new MonoType.RecordType(a)));
            return solveRowTail(tailB.get(), onlyA, Optional.empty(), state);
        }

        var freshTail = state.freshRow(Math.min(tailA.get().bindingLevel(), tailB.get().bindingLevel()));
        var errA = solveRowTail(tailA.get(), onlyB, Optional.of(freshTail), state);
        if (errA.isPresent()) return errA;
        return solveRowTail(tailB.get(), onlyA, Optional.of(freshTail), state);
    }

    private static Optional<TypeError> solveRowTail(
        MonoType.Meta tail,
        Map<String, MonoType> extraFields,
        Optional<MonoType.Meta> newTail,
        ElaborationState state
    ) {
        var row = new RowType(extraFields, newTail);
        for (var fieldType : extraFields.values()) {
            if (occursIn(tail.id(), fieldType, state)) {
                return Optional.of(new TypeError.InfiniteType(tail, new MonoType.RecordType(row)));
            }
        }
        state.solve(tail.id(), row);
        return Optional.empty();
    }

    private static Map<String, MonoType> filterKeys(Map<String, MonoType> map, Set<String> exclude) {
        var result = new LinkedHashMap<String, MonoType>();
        for (var entry : map.entrySet()) {
            if (exclude.contains(entry.getKey()) == false) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    /**
     * Occurs check: does {@code metaId} appear anywhere in {@code type}
     * (after resolving through the zonker)?
     */
    private static boolean occursIn(int metaId, MonoType type, ElaborationState state) {
        return switch (state.zonkOrKeep(type)) {
            case MonoType.Meta(var id, var lvl, var kind) -> id == metaId;
            case MonoType.TCon t -> false;
            case MonoType.Rigid r -> false;
            case MonoType.Arrow(var param, var result) -> occursIn(metaId, param, state) || occursIn(metaId, result, state);
            case MonoType.RecordType(var row) -> occursInRow(metaId, row, state);
            case MonoType.AppType(var ctor, var arg) -> occursIn(metaId, ctor, state) || occursIn(metaId, arg, state);
        };
    }

    private static boolean occursInRow(int metaId, RowType rawRow, ElaborationState state) {
        var row = state.resolveRow(rawRow);
        for (var fieldType : row.fields().values()) {
            if (occursIn(metaId, fieldType, state)) return true;
        }
        return row.rowVar().map(rv -> rv.id() == metaId).orElse(false);
    }
}
