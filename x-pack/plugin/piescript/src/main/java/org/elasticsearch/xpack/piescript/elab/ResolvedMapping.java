/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.elab;

import org.elasticsearch.xpack.esql.core.type.EsField;

import java.util.Map;
import java.util.Set;

/**
 * A resolved index mapping produced by the index resolution pre-pass.
 * Wraps the data extracted from ESQL's {@code IndexResolution} / {@code EsIndex}
 * so that the elaborator can convert field mappings to piescript types.
 *
 * <p>Fields whose type differs across concrete indices are represented as
 * {@link org.elasticsearch.xpack.esql.core.type.InvalidMappedField} entries
 * in the {@link #fieldMap}. The elaborator (T2.5/T2.6) inspects these to
 * report type conflicts.
 *
 * @param indexPattern the original index pattern string (e.g., {@code "logs-*"})
 * @param fieldMap merged field mapping from {@code EsIndex.mapping()};
 *                 keys are top-level field names, values contain type info
 *                 and nested properties
 * @param partiallyUnmappedFields fields that exist in some but not all concrete
 *                                indices matching the pattern
 */
public record ResolvedMapping(String indexPattern, Map<String, EsField> fieldMap, Set<String> partiallyUnmappedFields) {}
