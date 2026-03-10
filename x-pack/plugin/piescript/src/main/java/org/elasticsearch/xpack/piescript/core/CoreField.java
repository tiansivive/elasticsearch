/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.core;

/**
 * A labeled field in a record expression or record update. Pairs a field name
 * with its value expression. Used as a convenience for constructing and
 * inspecting {@link CoreRecord} and {@link CoreUpdate} nodes.
 *
 * <p>Internally, the Node classes store labels and values separately (labels
 * as properties, values as children) to integrate with the {@code Node<T>}
 * tree infrastructure. This record provides the ergonomic combined view.
 */
public record CoreField(String label, CoreExpr value) {}
