/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.elab;

import org.elasticsearch.xpack.piescript.types.Kind;
import org.elasticsearch.xpack.piescript.types.MonoType;
import org.elasticsearch.xpack.piescript.types.RowType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
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
    private final List<Constraint> constraints;
    private Map<String, ResolvedMapping> resolvedMappings;
    private final List<String> diagnostics;

    public ElaborationState() {
        this.metaSupply = 0;
        this.zonker = new HashMap<>();
        this.constraints = new ArrayList<>();
        this.resolvedMappings = Map.of();
        this.diagnostics = new ArrayList<>();
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
     * Allocate a fresh rigid type variable. Shares the ID space with metas
     * so that IDs are globally unique across both Rigids and Metas.
     */
    public MonoType.Rigid freshRigid(Kind kind) {
        return new MonoType.Rigid(metaSupply++, kind);
    }

    /**
     * Resolve a MonoType by following any meta chains in the zonker.
     * If the type is a solved meta, returns the resolved solution.
     * Otherwise returns the type unchanged (including unsolved metas).
     */
    public MonoType zonkOrKeep(MonoType type) {
        return switch (type) {
            case MonoType.Meta meta -> resolve(meta.id()).filter(MonoType.class::isInstance)
                .map(MonoType.class::cast)
                .map(this::zonkOrKeep)
                .orElse(type);
            default -> type;
        };
    }

    /**
     * Flatten a row by following its tail through the zonker. If the row var is
     * solved to a {@link RowType}, merge the tail's fields into the parent and recurse.
     */
    public RowType resolveRow(RowType row) {
        if (row.rowVar().isEmpty()) return row;
        var meta = row.rowVar().get();
        var resolved = zonkOrKeep(meta);
        if (resolved instanceof MonoType.Meta m && !m.equals(meta)) {
            return new RowType(row.fields(), Optional.of(m));
        }
        var solution = resolve(meta.id());
        if (solution.isPresent() && solution.get() instanceof RowType tailRow) {
            var merged = new LinkedHashMap<>(row.fields());
            merged.putAll(tailRow.fields());
            return resolveRow(new RowType(merged, tailRow.rowVar()));
        }
        return row;
    }

    /** Read-only view of the zonker for inspection (e.g., generalization). */
    public Map<Integer, Object> zonker() {
        return Collections.unmodifiableMap(zonker);
    }

    // ──── Constraint accumulator ────

    /** Record a deferred type equality constraint to be solved post-elaboration. */
    public void emitConstraint(MonoType left, MonoType right, int line, int column) {
        constraints.add(new Constraint(left, right, line, column));
    }

    /** The accumulated constraints, in emission order. */
    public List<Constraint> constraints() {
        return Collections.unmodifiableList(constraints);
    }

    // ──── Resolved index mappings (Phase 2) ────

    /**
     * Install the resolved index mappings from the pre-pass. Must be called
     * before elaboration begins if the program contains query expressions.
     */
    public void setResolvedMappings(Map<String, ResolvedMapping> mappings) {
        this.resolvedMappings = mappings;
    }

    /**
     * Look up a resolved mapping by index pattern. Returns {@code null} if
     * the pattern was not resolved (no query uses it, or the pre-pass was
     * not run).
     */
    public ResolvedMapping resolvedMapping(String indexPattern) {
        return resolvedMappings.get(indexPattern);
    }

    // ──── Diagnostics (warnings, conflict reports) ────

    /**
     * Record a non-fatal diagnostic (e.g., mapping conflict on a field).
     * Diagnostics do not abort elaboration but are surfaced to the user.
     */
    public void addDiagnostic(String message) {
        diagnostics.add(message);
    }

    /** The accumulated diagnostics, in emission order. */
    public List<String> diagnostics() {
        return Collections.unmodifiableList(diagnostics);
    }
}
