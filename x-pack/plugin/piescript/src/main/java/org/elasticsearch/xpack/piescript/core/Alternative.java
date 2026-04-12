/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.core;

/**
 * A single arm in a {@link CoreMatch} expression.
 *
 * @param pattern the pattern to match against the scrutinee
 * @param body    the expression to evaluate if the pattern matches
 */
public record Alternative(Pattern pattern, CoreExpr body) {}
