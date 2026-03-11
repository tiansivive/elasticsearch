/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.elab;

import org.elasticsearch.xpack.piescript.types.Kind;
import org.elasticsearch.xpack.piescript.types.MonoType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Mutable global state shared across the entire elaboration pass. Contains
 * only the parts that genuinely require shared mutation:
 * <ul>
 *   <li><b>Metavar supply</b> — monotonic counter. Each fresh meta must have
 *       a globally unique ID.</li>
 *   <li><b>Zonker</b> — maps meta IDs to their solutions. Unification writes
 *       here, and those writes must be visible across all branches of the
 *       recursive descent.</li>
 * </ul>
 *
 * <p>The typing context (Γ) and binding level are <em>not</em> here — they
 * live in the immutable {@link ElaborationContext}, passed by value through
 * the recursive descent. This split ensures that scope management is handled
 * by the call stack (no push/pop, no risk of getting out of sync) while
 * global state is explicitly isolated.
 *
 * <p>The {@code bindingLevel} parameter on {@link #freshType} and
 * {@link #freshRow} comes from the caller's {@link ElaborationContext},
 * making the dependency explicit.
 */
public final class ElaborationState {

    private int metaSupply;
    private final Map<Integer, Object> zonker;

    public ElaborationState() {
        this.metaSupply = 0;
        this.zonker = new HashMap<>();
    }

    // ──── Metavar supply ────

    /**
     * Allocate a fresh type metavariable. The {@code bindingLevel} should be
     * taken from the caller's {@link ElaborationContext#bindingLevel()}.
     */
    public MonoType.Meta freshType(int bindingLevel) {
        return new MonoType.Meta(metaSupply++, bindingLevel, Kind.TYPE);
    }

    /**
     * Allocate a fresh row metavariable. The {@code bindingLevel} should be
     * taken from the caller's {@link ElaborationContext#bindingLevel()}.
     */
    public MonoType.Meta freshRow(int bindingLevel) {
        return new MonoType.Meta(metaSupply++, bindingLevel, Kind.ROW);
    }

    /** The number of metavariables allocated so far. */
    public int metaCount() {
        return metaSupply;
    }

    // ──── Zonker ────

    /**
     * Record a solution for the given meta. The caller is responsible for
     * ensuring the solution type matches the meta's kind (MonoType for
     * Kind.TYPE, RowType for Kind.ROW).
     */
    public void solve(int metaId, Object solution) {
        zonker.put(metaId, solution);
    }

    /**
     * Look up the solution for a meta, following chains.
     *
     * <p>Chain resolution: if the solution is itself a {@link MonoType.Meta}
     * that has a solution, follow the chain recursively.
     *
     * @return the resolved solution, or empty if the meta is unsolved
     */
    public Optional<Object> resolve(int metaId) {
        return Optional.ofNullable(zonker.get(metaId)).flatMap(solution -> switch (solution) {
            case MonoType.Meta next when isSolved(next.id()) -> resolve(next.id());
            default -> Optional.of(solution);
        });
    }

    /** Whether a meta has been solved (has an entry in the zonker). */
    public boolean isSolved(int metaId) {
        return zonker.containsKey(metaId);
    }

    /**
     * Resolve a MonoType by following any meta chains in the zonker.
     * If the type is a solved meta, returns the resolved solution.
     * Otherwise returns the type unchanged.
     */
    public MonoType resolveType(MonoType type) {
        return switch (type) {
            case MonoType.Meta meta -> resolve(meta.id()).filter(MonoType.class::isInstance)
                .map(MonoType.class::cast)
                .map(this::resolveType)
                .orElse(type);
            default -> type;
        };
    }

    /** Read-only view of the zonker for inspection (e.g., generalization). */
    public Map<Integer, Object> zonker() {
        return Collections.unmodifiableMap(zonker);
    }
}
