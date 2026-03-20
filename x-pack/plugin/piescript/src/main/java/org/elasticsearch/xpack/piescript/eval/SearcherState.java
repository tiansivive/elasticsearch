/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.eval;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;
import org.elasticsearch.index.IndexService;
import org.elasticsearch.index.engine.Engine;

import java.io.IOException;
import java.util.List;

/**
 * Mutable state backing a {@link Value.SearcherVal}. Holds the Lucene searcher,
 * compiled query weight, per-segment scorers, and a consumption cursor that
 * tracks the current position across segments.
 *
 * <p>Node-local, non-serializable. Created by {@code Shard.open}, consumed
 * by {@code Shard.consume}, released when exhausted or on error.
 *
 * <p>The cursor ({@code segmentIndex} + {@code currentIterator}) is advanced
 * by {@code Shard.consume}. Each call to consume picks up where the last left off.
 */
final class SearcherState {
    final Engine.Searcher engineSearcher;
    final Weight weight;
    final List<LeafReaderContext> matchingLeaves;
    final List<Scorer> scorers;
    final IndexService indexService;

    int segmentIndex;
    DocIdSetIterator currentIterator;

    SearcherState(
        Engine.Searcher engineSearcher,
        Weight weight,
        List<LeafReaderContext> matchingLeaves,
        List<Scorer> scorers,
        IndexService indexService
    ) {
        this.engineSearcher = engineSearcher;
        this.weight = weight;
        this.matchingLeaves = matchingLeaves;
        this.scorers = scorers;
        this.indexService = indexService;
        this.segmentIndex = 0;
        this.currentIterator = scorers.isEmpty() ? null : scorers.getFirst().iterator();
    }

    /**
     * Release the underlying Engine.Searcher. Safe to call multiple times.
     */
    void release() throws IOException {
        engineSearcher.close();
    }
}
