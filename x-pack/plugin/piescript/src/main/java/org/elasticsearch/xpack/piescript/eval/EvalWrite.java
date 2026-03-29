/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.eval;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.support.SubscribableListener;
import org.elasticsearch.cluster.metadata.ProjectId;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.core.Nullable;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.index.VersionType;
import org.elasticsearch.index.engine.Engine;
import org.elasticsearch.index.mapper.SourceToParse;
import org.elasticsearch.index.shard.IndexShard;
import org.elasticsearch.xcontent.XContentBuilder;
import org.elasticsearch.xcontent.XContentFactory;
import org.elasticsearch.xcontent.XContentType;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.elasticsearch.index.seqno.SequenceNumbers.UNASSIGNED_PRIMARY_TERM;
import static org.elasticsearch.index.seqno.SequenceNumbers.UNASSIGNED_SEQ_NO;

/**
 * Runtime implementation of write builtins: {@code Shard.writer}, {@code Shard.write},
 * {@code Shard.refresh}, {@code Shard.globalCheckpoint}, and {@code Index.bulk}.
 * See D-051.
 */
final class EvalWrite {

    private EvalWrite() {}

    // ──── Shard.writer ────

    /**
     * Implement {@code Shard.writer}: acquire a write context on a primary shard.
     * Validates that the shard is a started primary on this node. Returns a
     * {@link Value.WriterVal} via a channel, mirroring {@code Shard.open}.
     */
    static void writer(Evaluator eval, Value.IndexVal indexVal, Value.RecordVal shardRecord, ActionListener<Value> listener) {
        var deps = eval.deps;
        if (deps.indicesService() == null) {
            listener.onFailure(new EvaluationException("Shard.writer requires IndicesService"));
            return;
        }
        if (deps.clusterService() == null) {
            listener.onFailure(new EvaluationException("Shard.writer requires ClusterService"));
            return;
        }

        var channelId = deps.channelRegistry().nextChannelId();
        var channelListener = new SubscribableListener<Value>();
        deps.channelRegistry().register(channelId, channelListener);

        deps.executor().execute(() -> {
            try {
                var writerVal = doWriter(deps, indexVal, shardRecord);
                channelListener.onResponse(writerVal);
            } catch (Exception e) {
                channelListener.onFailure(e);
            }
        });

        listener.onResponse(new Value.ChannelVal(deps.localNodeId(), channelId));
    }

    private static Value.WriterVal doWriter(EvalDependencies deps, Value.IndexVal indexVal, Value.RecordVal shardRecord) {
        var project = deps.clusterService().state().metadata().getProject(ProjectId.DEFAULT);
        var indexMetadata = project.index(indexVal.name());
        if (indexMetadata == null) {
            throw new EvaluationException("Shard.writer: index [" + indexVal.name() + "] not found in cluster state");
        }
        var index = indexMetadata.getIndex();
        int shardId = (int) EvalBuiltins.requireDouble(shardRecord.fields().get("shard_id"), "Shard.writer");

        var indexService = deps.indicesService().indexServiceSafe(index);
        IndexShard indexShard;
        try {
            indexShard = indexService.getShard(shardId);
        } catch (IndexNotFoundException e) {
            throw new EvaluationException(
                "Shard.writer: index ["
                    + indexVal.name()
                    + "] shard ["
                    + shardId
                    + "] is not hosted on this node ["
                    + deps.localNodeId()
                    + "]. Route Shard.writer to the node that holds the primary shard."
            );
        }

        if (indexShard.routingEntry().primary() == false) {
            throw new EvaluationException(
                "Shard.writer: shard ["
                    + indexVal.name()
                    + "]["
                    + shardId
                    + "] is not a primary on this node. Only primary shards can be written to."
            );
        }

        return new Value.WriterVal(new WriterState(indexShard, indexService));
    }

    // ──── Shard.write ────

    /**
     * Implement {@code Shard.write}: write a single document to a primary shard
     * via {@code IndexShard.applyIndexOperationOnPrimary}. Returns a
     * {@code WriteResult} record with seq_no, version, and result status.
     *
     * <p>The document ID is a separate argument (not part of the record body)
     * because the type parameter {@code r} in {@code Writer r} has kind {@code Type}
     * (a full record type), not kind {@code Row}. With row-kinded type parameters,
     * this would be {@code Shard.write : Writer r → { _id: Keyword | r } → WriteResult}.
     * See D-050 deviation §5.
     *
     * <p>This is a primary-only write — replication is not triggered. Replicas
     * catch up via the translog (ES background). See D-051.
     */
    static void write(Value.WriterVal writerVal, String docId, Value.RecordVal document, ActionListener<Value> listener) {
        try {
            var state = writerVal.state();
            XContentBuilder source = recordToXContent(document);

            var sourceToParse = new SourceToParse(docId, BytesReference.bytes(source), XContentType.JSON);

            Engine.IndexResult result = state.indexShard.applyIndexOperationOnPrimary(
                org.elasticsearch.common.lucene.uid.Versions.MATCH_ANY,
                VersionType.INTERNAL,
                sourceToParse,
                UNASSIGNED_SEQ_NO,
                UNASSIGNED_PRIMARY_TERM,
                -1, // user-provided ID: engine uses INDEX semantics (upsert), not CREATE
                false
            );

            if (result.getResultType() == Engine.Result.Type.MAPPING_UPDATE_REQUIRED) {
                listener.onFailure(
                    new EvaluationException(
                        "Shard.write: mapping update required for index ["
                            + state.indexShard.shardId().getIndexName()
                            + "]. Ensure the index mappings include all fields being written."
                    )
                );
                return;
            }
            if (result.getResultType() == Engine.Result.Type.FAILURE) {
                listener.onFailure(new EvaluationException("Shard.write failed: " + result.getFailure().getMessage(), result.getFailure()));
                return;
            }

            var writeResult = new Value.RecordVal(
                Map.of(
                    "seq_no",
                    new Value.DoubleVal(result.getSeqNo()),
                    "version",
                    new Value.DoubleVal(result.getVersion()),
                    "result",
                    new Value.KeywordVal(result.getResultType().name())
                )
            );
            listener.onResponse(writeResult);
        } catch (Exception e) {
            listener.onFailure(new EvaluationException("Shard.write failed", e));
        }
    }

    // ──── Shard.refresh ────

    /**
     * Implement {@code Shard.refresh}: trigger an index refresh on the shard so that
     * recently written documents become visible to subsequent {@code Shard.open} readers.
     * Returns a {@code RefreshResult} record via a channel.
     */
    static void refresh(Evaluator eval, Value.WriterVal writerVal, ActionListener<Value> listener) {
        var deps = eval.deps;
        var channelId = deps.channelRegistry().nextChannelId();
        var channelListener = new SubscribableListener<Value>();
        deps.channelRegistry().register(channelId, channelListener);

        deps.executor().execute(() -> {
            try {
                var state = writerVal.state();
                Engine.RefreshResult refreshResult = state.indexShard.refresh("piescript");
                boolean refreshed = refreshResult.generation() != Engine.RefreshResult.NO_REFRESH.generation();
                channelListener.onResponse(new Value.RecordVal(Map.of("refreshed", new Value.BooleanVal(refreshed))));
            } catch (Exception e) {
                channelListener.onFailure(new EvaluationException("Shard.refresh failed", e));
            }
        });

        listener.onResponse(new Value.ChannelVal(deps.localNodeId(), channelId));
    }

    // ──── Shard.globalCheckpoint ────

    /**
     * Implement {@code Shard.globalCheckpoint}: read the global checkpoint for a shard.
     * The global checkpoint is the highest seq_no for which all in-sync shard copies
     * have acknowledged. This is the same checkpoint system that Transforms use.
     */
    static void globalCheckpoint(Evaluator eval, Value.IndexVal indexVal, Value.RecordVal shardRecord, ActionListener<Value> listener) {
        var deps = eval.deps;
        if (deps.indicesService() == null) {
            listener.onFailure(new EvaluationException("Shard.globalCheckpoint requires IndicesService"));
            return;
        }
        if (deps.clusterService() == null) {
            listener.onFailure(new EvaluationException("Shard.globalCheckpoint requires ClusterService"));
            return;
        }

        try {
            var project = deps.clusterService().state().metadata().getProject(ProjectId.DEFAULT);
            var indexMetadata = project.index(indexVal.name());
            if (indexMetadata == null) {
                listener.onFailure(
                    new EvaluationException("Shard.globalCheckpoint: index [" + indexVal.name() + "] not found in cluster state")
                );
                return;
            }
            var index = indexMetadata.getIndex();
            int shardId = (int) EvalBuiltins.requireDouble(shardRecord.fields().get("shard_id"), "Shard.globalCheckpoint");

            var indexService = deps.indicesService().indexServiceSafe(index);
            var indexShard = indexService.getShard(shardId);
            long globalCheckpoint = indexShard.seqNoStats().getGlobalCheckpoint();
            listener.onResponse(new Value.DoubleVal(globalCheckpoint));
        } catch (Exception e) {
            listener.onFailure(new EvaluationException("Shard.globalCheckpoint failed", e));
        }
    }

    // ──── Index.bulk ────

    /**
     * Implement {@code Index.bulk}: write a list of records to an index via the
     * Bulk API. Goes through the full transport stack — handles routing, replication,
     * ingest pipelines, index auto-creation.
     */
    static void bulk(Evaluator eval, String indexName, Value.ListVal records, ActionListener<Value> listener) {
        var deps = eval.deps;
        if (deps.client() == null) {
            listener.onFailure(new EvaluationException("Index.bulk requires a client"));
            return;
        }

        var channelId = deps.channelRegistry().nextChannelId();
        var channelListener = new SubscribableListener<Value>();
        deps.channelRegistry().register(channelId, channelListener);

        deps.executor().execute(() -> {
            try {
                var bulkRequest = new org.elasticsearch.action.bulk.BulkRequest();
                for (Value element : records.elements()) {
                    if (element instanceof Value.RecordVal rec == false) {
                        channelListener.onFailure(
                            new EvaluationException("Index.bulk: expected record elements, got " + element.getClass().getSimpleName())
                        );
                        return;
                    }
                    var rec = (Value.RecordVal) element;
                    String id = extractId(rec);
                    XContentBuilder source = recordToXContent(rec);
                    var indexRequest = new org.elasticsearch.action.index.IndexRequest(indexName).id(id)
                        .source(BytesReference.bytes(source), XContentType.JSON);
                    bulkRequest.add(indexRequest);
                }

                deps.client()
                    .execute(
                        org.elasticsearch.action.bulk.TransportBulkAction.TYPE,
                        bulkRequest,
                        channelListener.delegateFailureAndWrap((l, bulkResponse) -> {
                            int written = 0;
                            int failed = 0;
                            for (var item : bulkResponse.getItems()) {
                                if (item.isFailed()) {
                                    failed++;
                                } else {
                                    written++;
                                }
                            }
                            l.onResponse(
                                new Value.RecordVal(
                                    Map.of(
                                        "total",
                                        new Value.DoubleVal(bulkResponse.getItems().length),
                                        "written",
                                        new Value.DoubleVal(written),
                                        "failed",
                                        new Value.DoubleVal(failed)
                                    )
                                )
                            );
                        })
                    );
            } catch (Exception e) {
                channelListener.onFailure(new EvaluationException("Index.bulk failed", e));
            }
        });

        listener.onResponse(new Value.ChannelVal(deps.localNodeId(), channelId));
    }

    // ──── RecordVal → XContent conversion ────

    /**
     * Convert a {@link Value.RecordVal} to a JSON {@link XContentBuilder} suitable
     * for use as {@code IndexRequest} source. Handles the subset of {@link Value}
     * types that map to JSON: doubles, keywords, booleans, nulls, nested records
     * (objects), and lists (arrays). Non-convertible values (closures, channels,
     * builtins, etc.) throw {@link EvaluationException}.
     *
     * <p>The {@code _id} field, if present, is excluded from the source — use
     * {@link #extractId} to obtain it separately for {@code IndexRequest.id()}.
     */
    static XContentBuilder recordToXContent(Value.RecordVal record) throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        for (Map.Entry<String, Value> entry : record.fields().entrySet()) {
            if ("_id".equals(entry.getKey())) {
                continue;
            }
            writeField(builder, entry.getKey(), entry.getValue());
        }
        builder.endObject();
        return builder;
    }

    /**
     * Extract the {@code _id} field from a record, if present. Returns {@code null}
     * if absent (ES will auto-generate an ID).
     */
    @Nullable
    static String extractId(Value.RecordVal record) {
        Value idVal = record.fields().get("_id");
        if (idVal == null) {
            return null;
        }
        return switch (idVal) {
            case Value.KeywordVal k -> k.value();
            case Value.DoubleVal d -> {
                if (d.value() == Math.floor(d.value()) && Double.isFinite(d.value())) {
                    yield String.valueOf((long) d.value());
                }
                yield String.valueOf(d.value());
            }
            default -> throw new EvaluationException(
                "Shard.write: _id field must be a Keyword or Double, got " + idVal.getClass().getSimpleName()
            );
        };
    }

    /**
     * Strip the {@code _id} field from a record's fields, returning a new map
     * without it. If {@code _id} is absent, returns the original map.
     */
    static Map<String, Value> fieldsWithoutId(Value.RecordVal record) {
        if (record.fields().containsKey("_id") == false) {
            return record.fields();
        }
        var result = new LinkedHashMap<>(record.fields());
        result.remove("_id");
        return result;
    }

    private static void writeField(XContentBuilder builder, String name, Value value) throws IOException {
        switch (value) {
            case Value.DoubleVal v -> writeDoubleField(builder, name, v.value());
            case Value.IntegerVal v -> builder.field(name, v.value());
            case Value.LongVal v -> builder.field(name, v.value());
            case Value.KeywordVal v -> builder.field(name, v.value());
            case Value.BooleanVal v -> builder.field(name, v.value());
            case Value.NullVal ignored -> builder.nullField(name);
            case Value.RecordVal v -> {
                builder.startObject(name);
                for (Map.Entry<String, Value> entry : v.fields().entrySet()) {
                    writeField(builder, entry.getKey(), entry.getValue());
                }
                builder.endObject();
            }
            case Value.ListVal v -> {
                builder.startArray(name);
                for (Value element : v.elements()) {
                    writeArrayElement(builder, element);
                }
                builder.endArray();
            }
            default -> throw new EvaluationException("Cannot convert " + value.getClass().getSimpleName() + " to JSON for indexing");
        }
    }

    private static void writeArrayElement(XContentBuilder builder, Value value) throws IOException {
        switch (value) {
            case Value.DoubleVal v -> writeDoubleValue(builder, v.value());
            case Value.IntegerVal v -> builder.value(v.value());
            case Value.LongVal v -> builder.value(v.value());
            case Value.KeywordVal v -> builder.value(v.value());
            case Value.BooleanVal v -> builder.value(v.value());
            case Value.NullVal ignored -> builder.nullValue();
            case Value.RecordVal v -> {
                builder.startObject();
                for (Map.Entry<String, Value> entry : v.fields().entrySet()) {
                    writeField(builder, entry.getKey(), entry.getValue());
                }
                builder.endObject();
            }
            case Value.ListVal v -> {
                builder.startArray();
                for (Value element : v.elements()) {
                    writeArrayElement(builder, element);
                }
                builder.endArray();
            }
            default -> throw new EvaluationException("Cannot convert " + value.getClass().getSimpleName() + " to JSON for indexing");
        }
    }

    private static void writeDoubleField(XContentBuilder builder, String name, double v) throws IOException {
        if (v == Math.floor(v) && Double.isFinite(v) && Math.abs(v) <= Long.MAX_VALUE) {
            builder.field(name, (long) v);
        } else {
            builder.field(name, v);
        }
    }

    private static void writeDoubleValue(XContentBuilder builder, double v) throws IOException {
        if (v == Math.floor(v) && Double.isFinite(v) && Math.abs(v) <= Long.MAX_VALUE) {
            builder.value((long) v);
        } else {
            builder.value(v);
        }
    }
}
