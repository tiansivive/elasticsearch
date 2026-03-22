/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.eval;

import org.elasticsearch.index.IndexService;
import org.elasticsearch.index.shard.IndexShard;

/**
 * State backing a {@link Value.WriterVal}. Holds references to the primary
 * {@link IndexShard} and its parent {@link IndexService} for document writes.
 *
 * <p>Node-local, non-serializable. Created by {@code Shard.writer}, consumed
 * by {@code Shard.write} and {@code Shard.refresh}. Unlike {@link SearcherState},
 * there is no mutable cursor — each write is independent.
 *
 * <p>Primary-only: the shard must be a started primary at acquisition time.
 * Replication is not handled at this level (see D-051).
 */
final class WriterState {
    final IndexShard indexShard;
    final IndexService indexService;

    WriterState(IndexShard indexShard, IndexService indexService) {
        this.indexShard = indexShard;
        this.indexService = indexService;
    }
}
