/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.core;

import org.elasticsearch.xpack.esql.core.tree.NodeInfo;
import org.elasticsearch.xpack.esql.core.tree.Source;
import org.elasticsearch.xpack.piescript.types.MonoType;

import java.util.List;
import java.util.Objects;

/**
 * An ESQL query embedded in a piescript program. Carries the full ESQL query
 * string (for execution via {@code EsqlQueryAction}) and the extracted index
 * pattern (for diagnostics and index-resolution keying).
 *
 * <p>The type is {@code AppType(TCon("Stream"), RecordType(rho))} where
 * {@code rho} is a row derived from the resolved index mapping.
 *
 * <p>This is a pragmatic {@link CoreExpr} variant for Phase 2. It migrates to
 * the {@code CoreProcess} hierarchy in Phase 3 when the two-layer IR split is
 * introduced.
 */
public final class CoreQuery extends CoreExpr {

    private final String esqlQuery;
    private final String indexPattern;
    private final MonoType type;

    public CoreQuery(Source source, String esqlQuery, String indexPattern, MonoType type) {
        super(source, List.of());
        this.esqlQuery = esqlQuery;
        this.indexPattern = indexPattern;
        this.type = type;
    }

    /** The full ESQL query string, ready to pass to {@code EsqlQueryAction}. */
    public String esqlQuery() {
        return esqlQuery;
    }

    /** The extracted index pattern (e.g. {@code "logs-*"}) for diagnostics and pre-pass keying. */
    public String indexPattern() {
        return indexPattern;
    }

    @Override
    public MonoType type() {
        return type;
    }

    @Override
    protected NodeInfo<CoreQuery> info() {
        return NodeInfo.create(this, CoreQuery::new, esqlQuery, indexPattern, type);
    }

    @Override
    public CoreExpr replaceChildren(List<CoreExpr> newChildren) {
        throw new UnsupportedOperationException("leaf node has no children to replace");
    }

    @Override
    public int hashCode() {
        return Objects.hash(esqlQuery, indexPattern, type);
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj) == false) return false;
        CoreQuery other = (CoreQuery) obj;
        return Objects.equals(esqlQuery, other.esqlQuery)
            && Objects.equals(indexPattern, other.indexPattern)
            && Objects.equals(type, other.type);
    }
}
