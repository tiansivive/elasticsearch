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
import org.elasticsearch.indices.IndicesExpressionGrouper;
import org.elasticsearch.transport.TransportService;
import org.elasticsearch.xpack.esql.session.IndexResolver;
import org.elasticsearch.xpack.piescript.parser.PiescriptAntlrParser;
import org.elasticsearch.xpack.piescript.parser.PiescriptAntlrParserBaseVisitor;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.elasticsearch.TransportVersion.current;

/**
 * Index resolution pre-pass that runs between parsing and elaboration.
 *
 * <ol>
 *   <li>Walks the ANTLR CST to collect all {@code QueryExpr} nodes and
 *       {@code use} declarations, extracting their index patterns.</li>
 *   <li>For each unique index pattern, asynchronously calls
 *       {@link IndexResolver#resolveMainIndicesVersioned} to obtain the
 *       merged field capabilities mapping.</li>
 *   <li>Produces a {@code Map<String, ResolvedMapping>} keyed by index
 *       pattern, ready for the elaborator to use when typing query
 *       expressions and {@code use} bindings.</li>
 * </ol>
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
     */
    public static IndexResolutionPrePass create(Client client, TransportService transportService) {
        return new IndexResolutionPrePass(new IndexResolver(client), transportService.getRemoteClusterService());
    }

    /**
     * Information extracted from a single {@code QueryExpr} CST node.
     *
     * @param ctx the ANTLR parse tree node for the query expression
     * @param parsedBody the parsed ESQL body with extracted index pattern
     */
    public record QueryInfo(PiescriptAntlrParser.QueryExprContext ctx, EsqlBodyParser.ParsedEsqlBody parsedBody) {}

    /**
     * Walk the CST and collect all {@code QueryExpr} nodes, extracting
     * their ESQL body and index patterns.
     *
     * @param cst the root of the parse tree
     * @return list of query info records; empty if the program has no queries
     */
    public static List<QueryInfo> collectQueries(PiescriptAntlrParser.ProgramContext cst) {
        List<QueryInfo> queries = new ArrayList<>();
        cst.accept(new PiescriptAntlrParserBaseVisitor<Void>() {
            @Override
            public Void visitQueryExpr(PiescriptAntlrParser.QueryExprContext ctx) {
                String esqlBody = ctx.ESQL_BODY().getText();
                var parsed = EsqlBodyParser.parse(esqlBody);
                queries.add(new QueryInfo(ctx, parsed));
                return null;
            }
        });
        return queries;
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
     * Resolve all unique index patterns from queries and {@code use} declarations.
     * Fan-out resolution via {@link RefCountingListener}: one field caps
     * request per unique pattern, all in parallel.
     *
     * @param queries the query info list from {@link #collectQueries}
     * @param useIndexNames index names from {@link #collectUseDeclarations}
     * @param listener receives the resolved mappings keyed by index pattern/name,
     *                 or failure if any resolution fails
     */
    public void resolve(List<QueryInfo> queries, Set<String> useIndexNames, ActionListener<Map<String, ResolvedMapping>> listener) {
        Set<String> uniquePatterns = new LinkedHashSet<>();
        for (var q : queries) {
            uniquePatterns.add(q.parsedBody().indexPattern());
        }
        uniquePatterns.addAll(useIndexNames);

        if (uniquePatterns.isEmpty()) {
            listener.onResponse(Map.of());
            return;
        }

        var results = new ConcurrentHashMap<String, ResolvedMapping>();
        try (var refs = new RefCountingListener(listener.map(ignored -> Map.copyOf(results)))) {
            for (String pattern : uniquePatterns) {
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
            indicesExpressionGrouper,
            listener.delegateFailureAndWrap((l, versioned) -> {
                var resolution = versioned.inner();
                if (resolution.isValid() == false) {
                    l.onFailure(new IllegalArgumentException("failed to resolve index pattern [" + indexPattern + "]"));
                    return;
                }
                var esIndex = resolution.get();
                l.onResponse(new ResolvedMapping(indexPattern, esIndex.mapping(), esIndex.partiallyUnmappedFields()));
            })
        );
    }
}
