/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.types;

import java.util.Map;

/**
 * Polymorphic type scheme: {@code ∀{α₁..αₙ}.body}. The {@code quantified} map holds
 * Rigid IDs to their kinds (represented as {@link MonoType} values, e.g.
 * {@code TCon("Type")}, {@code TCon("Row")}). At each use site, the elaborator
 * instantiates the scheme by allocating fresh metas of the appropriate kind.
 *
 * <p>Monomorphic types are represented as schemes with an empty quantified map.
 */
public record TypeScheme(Map<Integer, MonoType> quantified, MonoType body) {

    /** Wrap a monomorphic type as a trivial scheme with no quantified variables. */
    public static TypeScheme mono(MonoType type) {
        return new TypeScheme(Map.of(), type);
    }
}
