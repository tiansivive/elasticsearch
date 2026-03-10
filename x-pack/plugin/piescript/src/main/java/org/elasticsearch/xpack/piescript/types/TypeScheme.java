/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.types;

import java.util.Set;

/**
 * Polymorphic type scheme: {@code ∀{α₁..αₙ}.body}. The {@code quantified} set contains
 * meta IDs that are universally quantified. At each use site, the elaborator instantiates
 * the scheme by allocating fresh metas for the quantified variables.
 *
 * <p>Monomorphic types are represented as schemes with an empty quantified set.
 */
public record TypeScheme(Set<Integer> quantified, MonoType body) {

    /** Wrap a monomorphic type as a trivial scheme with no quantified variables. */
    public static TypeScheme mono(MonoType type) {
        return new TypeScheme(Set.of(), type);
    }
}
