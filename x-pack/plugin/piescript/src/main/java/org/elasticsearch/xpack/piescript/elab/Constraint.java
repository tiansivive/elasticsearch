/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.elab;

import org.elasticsearch.xpack.piescript.types.MonoType;

/**
 * A deferred type equality constraint: {@code left ~ right}. Accumulated
 * during elaboration and solved post-hoc by the constraint solver.
 * Line and column are retained for error reporting if the constraint
 * is unsatisfiable.
 */
public record Constraint(MonoType left, MonoType right, int line, int column) {}
