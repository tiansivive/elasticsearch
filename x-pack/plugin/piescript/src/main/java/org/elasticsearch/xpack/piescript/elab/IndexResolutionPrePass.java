/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.elab;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.support.RefCountingListener;
import org.elasticsearch.client.internal.Client;
import org.elasticsearch.cluster.service.ClusterService;
import org.elasticsearch.indices.IndicesExpressionGrouper;
import org.elasticsearch.transport.TransportService;
import org.elasticsearch.xpack.esql.plugin.EsqlPlugin;
import org.elasticsearch.xpack.esql.session.IndexResolver;
import org.elasticsearch.xpack.piescript.parser.PiescriptAntlrParser;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.elasticsearch.TransportVersion.current;

/**
 * Index resolution pre-pass that runs between parsing and elaboration.
 *
 * <p>Walks the ANTLR CST to collect {@code use} declarations, extracts index
 * names, and asynchronously resolves field capabilities via
 * {@link IndexResolver}. Produces a {@code Map<String, ResolvedMapping>}
 * keyed by index name, ready for the elaborator to use when typing
 * {@code use} bindings.
 *
 * <p>The pre-pass is asynchronous (field caps is a cluster action), but
 * its output is consumed synchronously by the elaborator. The async
 * boundary is managed by {@code TransportPiescriptAction} using
 * {@link org.elasticsearch.action.support.SubscribableListener}.
 */
public final class IndexResolutionPrePass {

    private final IndexResolver indexResolver;
    private final IndicesExpressionGrouper indicesExpressionGrouper;

    public IndexResolutionPrePass(IndexResolver indexResolver, IndicesExpressionGrouper indicesExpressionGrouper) {
        this.indexResolver = indexResolver;
        this.indicesExpressionGrouper = indicesExpressionGrouper;
    }

    /**
     * Create a pre-pass using the remote cluster service from a transport service
     * for full CCS support.
     *
     * <p>{@link IndexResolver}'s treatment of flattened fields follows ESQL's dynamic
     * {@code esql.query.flattened_enabled} setting. The flag is wired exactly as
     * {@code EsqlPlugin} wires it (initial value + settings-update consumer) so the
     * pre-pass sees the same view of index schemas as ESQL query execution.
     */
    public static IndexResolutionPrePass create(Client client, TransportService transportService, ClusterService clusterService) {
        AtomicBoolean flattenedDataTypeEnabled = new AtomicBoolean(EsqlPlugin.FLATTENED_ENABLED.get(clusterService.getSettings()));
        clusterService.getClusterSettings().addSettingsUpdateConsumer(EsqlPlugin.FLATTENED_ENABLED, flattenedDataTypeEnabled::set);
        return new IndexResolutionPrePass(
            new IndexResolver(client, flattenedDataTypeEnabled::get),
            transportService.getRemoteClusterService()
        );
    }

    /**
     * Collect index names from top-level {@code use "index" as idx} declarations.
     *
     * @param cst the root of the parse tree
     * @return set of unique index names; empty if the program has no {@code use} declarations
     */
    public static Set<String> collectUseDeclarations(PiescriptAntlrParser.ProgramContext cst) {
        Set<String> indexNames = new LinkedHashSet<>();
        for (var binding : cst.topBinding()) {
            if (binding instanceof PiescriptAntlrParser.TopUseContext use) {
                String quoted = use.QUOTED_STRING().getText();
                indexNames.add(quoted.substring(1, quoted.length() - 1));
            }
        }
        return indexNames;
    }

    /**
     * Resolve all unique index names from {@code use} declarations.
     * Fan-out resolution via {@link RefCountingListener}: one field caps
     * request per unique name, all in parallel.
     *
     * @param useIndexNames index names from {@link #collectUseDeclarations}
     * @param listener receives the resolved mappings keyed by index name,
     *                 or failure if any resolution fails
     */
    public void resolve(Set<String> useIndexNames, ActionListener<Map<String, ResolvedMapping>> listener) {
        if (useIndexNames.isEmpty()) {
            listener.onResponse(Map.of());
            return;
        }

        var results = new ConcurrentHashMap<String, ResolvedMapping>();
        try (var refs = new RefCountingListener(listener.map(ignored -> Map.copyOf(results)))) {
            for (String pattern : useIndexNames) {
                var ref = refs.acquire();
                resolvePattern(pattern, ActionListener.wrap(mapping -> {
                    results.put(pattern, mapping);
                    ref.onResponse(null);
                }, ref::onFailure));
            }
        }
    }

    private void resolvePattern(String indexPattern, ActionListener<ResolvedMapping> listener) {
        indexResolver.resolveMainIndicesVersioned(
            indexPattern,
            IndexResolver.ALL_FIELDS,
            null,
            false,
            current(),
            false,
            false,
            false,
            false,
            indicesExpressionGrouper,
            listener.delegateFailureAndWrap((l, versioned) -> {
                var resolution = versioned.inner();
                if (resolution.isValid() == false) {
                    l.onFailure(new IllegalArgumentException("failed to resolve index pattern [" + indexPattern + "]"));
                    return;
                }
                var esIndex = resolution.get();
                l.onResponse(new ResolvedMapping(indexPattern, esIndex.mapping()));
            })
        );
    }
}
