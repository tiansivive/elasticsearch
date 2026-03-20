/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.eval;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.SortedNumericDocValues;
import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreMode;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;
import org.apache.lucene.util.NumericUtils;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.support.SubscribableListener;
import org.elasticsearch.cluster.metadata.ProjectId;
import org.elasticsearch.index.IndexService;
import org.elasticsearch.index.mapper.MappedFieldType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Runtime implementation of {@code Shard.open}, {@code Shard.consume}, and
 * {@code Shard.read} builtins. Provides pull-based interaction with Lucene
 * via the three-step protocol: open a searcher, consume document references,
 * read field values from individual documents. See D-050.
 */
final class EvalShard {

    private EvalShard() {}

    /**
     * Implement {@code Shard.open}: acquire an Engine.Searcher, compile the query,
     * build per-segment scorers, and complete a channel with the resulting SearcherVal.
     *
     * <p>The channel is returned immediately; the searcher acquisition and query
     * compilation run asynchronously on the executor.
     */
    static void open(
        Evaluator eval,
        Value.IndexVal indexVal,
        Value.RecordVal shardRecord,
        Value.RecordVal queryRecord,
        ActionListener<Value> listener
    ) {
        var deps = eval.deps;
        if (deps.indicesService() == null) {
            listener.onFailure(new EvaluationException("Shard.open requires IndicesService"));
            return;
        }
        if (deps.clusterService() == null) {
            listener.onFailure(new EvaluationException("Shard.open requires ClusterService"));
            return;
        }

        var channelId = deps.channelRegistry().nextChannelId();
        var channelListener = new SubscribableListener<Value>();
        deps.channelRegistry().register(channelId, channelListener);

        deps.executor().execute(() -> {
            try {
                var searcherVal = doOpen(deps, indexVal, shardRecord, queryRecord);
                channelListener.onResponse(searcherVal);
            } catch (Exception e) {
                channelListener.onFailure(e);
            }
        });

        listener.onResponse(new Value.ChannelVal(deps.localNodeId(), channelId));
    }

    private static Value.SearcherVal doOpen(
        EvalDependencies deps,
        Value.IndexVal indexVal,
        Value.RecordVal shardRecord,
        Value.RecordVal queryRecord
    ) throws Exception {
        var project = deps.clusterService().state().metadata().getProject(ProjectId.DEFAULT);
        var indexMetadata = project.index(indexVal.name());
        if (indexMetadata == null) {
            throw new EvaluationException("Shard.open: index [" + indexVal.name() + "] not found in cluster state");
        }
        var index = indexMetadata.getIndex();
        IndexService indexService = deps.indicesService().indexServiceSafe(index);

        int shardId = (int) EvalBuiltins.requireDouble(shardRecord.fields().get("shard_id"), "Shard.open");
        var indexShard = indexService.getShard(shardId);
        var engineSearcher = indexShard.acquireSearcher("piescript_open");

        try {
            Query luceneQuery = convertQuery(queryRecord);
            Weight weight = engineSearcher.createWeight(luceneQuery, ScoreMode.COMPLETE_NO_SCORES, 1.0f);

            var matchingLeaves = new ArrayList<LeafReaderContext>();
            var scorers = new ArrayList<Scorer>();
            for (var leaf : engineSearcher.getIndexReader().leaves()) {
                Scorer scorer = weight.scorer(leaf);
                if (scorer != null) {
                    matchingLeaves.add(leaf);
                    scorers.add(scorer);
                }
            }

            var state = new SearcherState(engineSearcher, weight, matchingLeaves, scorers, indexService);
            return new Value.SearcherVal(state);
        } catch (Exception e) {
            engineSearcher.close();
            throw e;
        }
    }

    /**
     * Convert a piescript query record to a Lucene Query.
     * For the vertical slice, supports {@code { match_all: true }}.
     */
    private static Query convertQuery(Value.RecordVal queryRecord) {
        Map<String, Value> fields = queryRecord.fields();
        if (fields.containsKey("match_all")) {
            return new MatchAllDocsQuery();
        }
        throw new EvaluationException(
            "Shard.open: unsupported query record — expected { match_all: true }, got fields: " + fields.keySet()
        );
    }

    // ──── Shard.consume ────

    /**
     * Implement {@code Shard.consume}: advance the searcher cursor by up to N documents,
     * returning a {@code ListVal} of {@code DocRefVal}s. Subsequent calls resume where
     * the previous call left off. Returns an empty list when exhausted.
     */
    static void consume(Value.SearcherVal searcherVal, int n, ActionListener<Value> listener) {
        try {
            var state = searcherVal.state();
            var docs = new ArrayList<Value>(Math.min(n, 64));

            while (docs.size() < n && state.currentIterator != null) {
                int docId = state.currentIterator.nextDoc();
                if (docId == DocIdSetIterator.NO_MORE_DOCS) {
                    state.segmentIndex++;
                    if (state.segmentIndex < state.scorers.size()) {
                        state.currentIterator = state.scorers.get(state.segmentIndex).iterator();
                    } else {
                        state.currentIterator = null;
                    }
                } else {
                    docs.add(new Value.DocRefVal(state.matchingLeaves.get(state.segmentIndex), docId, state));
                }
            }

            listener.onResponse(new Value.ListVal(docs));
        } catch (Exception e) {
            listener.onFailure(new EvaluationException("Shard.consume failed", e));
        }
    }

    // ──── Shard.read ────

    /**
     * Implement {@code Shard.read}: read all field values from a document reference,
     * returning a {@code RecordVal} whose shape matches the {@code Index r} row type.
     *
     * <p>This is a wildcard read — all mapped, non-meta fields with doc values are
     * included. Missing doc values for a mapped field are a hard error (the type
     * system guarantees the field exists).
     *
     * <p><b>Future work:</b> individual field projection requires a {@code Label} kind
     * for type-level singleton strings, enabling {@code Shard.read : ∀(r : Row)(f : Label). DocRef r → f → Project r f}.
     * See D-050 decision record.
     */
    static void read(Value.DocRefVal docRef, ActionListener<Value> listener) {
        try {
            var mapperService = docRef.searcherState().indexService.mapperService();
            var leafReader = docRef.leafContext().reader();
            int docId = docRef.docId();

            var allFields = mapperService.mappingLookup().getMatchingFieldNames("*");
            var record = new LinkedHashMap<String, Value>();
            for (String name : allFields) {
                if (name.startsWith("_")) {
                    continue;
                }
                MappedFieldType fieldType = mapperService.fieldType(name);
                if (fieldType == null || fieldType.hasDocValues() == false) {
                    continue;
                }
                Value val = readDocValue(leafReader, docId, name, fieldType.typeName());
                record.put(name, val);
            }
            listener.onResponse(new Value.RecordVal(record));
        } catch (Exception e) {
            listener.onFailure(new EvaluationException("Shard.read failed", e));
        }
    }

    private static Value readDocValue(org.apache.lucene.index.LeafReader reader, int docId, String fieldName, String typeName)
        throws IOException {
        return switch (typeName) {
            case "keyword", "text", "constant_keyword", "wildcard" -> readKeywordDocValue(reader, docId, fieldName);
            case "long", "integer", "short", "byte" -> readLongDocValue(reader, docId, fieldName);
            case "double" -> readDoubleDocValue(reader, docId, fieldName);
            case "float", "half_float" -> readFloatDocValue(reader, docId, fieldName);
            case "scaled_float" -> readLongDocValue(reader, docId, fieldName);
            case "boolean" -> readBooleanDocValue(reader, docId, fieldName);
            default -> throw new EvaluationException(
                "Shard.read: unsupported doc value type [" + typeName + "] for field [" + fieldName + "]"
            );
        };
    }

    private static Value readKeywordDocValue(org.apache.lucene.index.LeafReader reader, int docId, String fieldName) throws IOException {
        SortedSetDocValues dv = reader.getSortedSetDocValues(fieldName);
        if (dv == null || dv.advanceExact(docId) == false || dv.docValueCount() == 0) {
            throw new EvaluationException("Shard.read: missing doc value for field [" + fieldName + "] in doc " + docId);
        }
        return new Value.KeywordVal(dv.lookupOrd(dv.nextOrd()).utf8ToString());
    }

    private static Value readLongDocValue(org.apache.lucene.index.LeafReader reader, int docId, String fieldName) throws IOException {
        SortedNumericDocValues dv = reader.getSortedNumericDocValues(fieldName);
        if (dv == null || dv.advanceExact(docId) == false) {
            throw new EvaluationException("Shard.read: missing doc value for field [" + fieldName + "] in doc " + docId);
        }
        return new Value.DoubleVal(dv.nextValue());
    }

    private static Value readDoubleDocValue(org.apache.lucene.index.LeafReader reader, int docId, String fieldName) throws IOException {
        SortedNumericDocValues dv = reader.getSortedNumericDocValues(fieldName);
        if (dv == null || dv.advanceExact(docId) == false) {
            throw new EvaluationException("Shard.read: missing doc value for field [" + fieldName + "] in doc " + docId);
        }
        return new Value.DoubleVal(NumericUtils.sortableLongToDouble(dv.nextValue()));
    }

    private static Value readFloatDocValue(org.apache.lucene.index.LeafReader reader, int docId, String fieldName) throws IOException {
        SortedNumericDocValues dv = reader.getSortedNumericDocValues(fieldName);
        if (dv == null || dv.advanceExact(docId) == false) {
            throw new EvaluationException("Shard.read: missing doc value for field [" + fieldName + "] in doc " + docId);
        }
        return new Value.DoubleVal(NumericUtils.sortableIntToFloat((int) dv.nextValue()));
    }

    private static Value readBooleanDocValue(org.apache.lucene.index.LeafReader reader, int docId, String fieldName) throws IOException {
        SortedNumericDocValues dv = reader.getSortedNumericDocValues(fieldName);
        if (dv == null || dv.advanceExact(docId) == false) {
            throw new EvaluationException("Shard.read: missing doc value for field [" + fieldName + "] in doc " + docId);
        }
        return new Value.BooleanVal(dv.nextValue() != 0);
    }
}
