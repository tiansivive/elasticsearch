/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript;

import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.common.collect.Iterators;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.xcontent.ChunkedToXContentObject;
import org.elasticsearch.core.Nullable;
import org.elasticsearch.xcontent.ToXContent;
import org.elasticsearch.xcontent.XContentBuilder;
import org.elasticsearch.xpack.piescript.eval.Value;
import org.elasticsearch.xpack.piescript.eval.ValueSerialization;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Response from the piescript eval and dev endpoints.
 *
 * <p>In normal mode, carries a {@link Value} and its type string.
 * In dev mode, carries the full pipeline debug output (tree, core IR,
 * constraints, zonker, diagnostics) with graceful error reporting at
 * each stage.
 *
 * <p>TODO: merge {@code /_piescript/eval} and {@code /_piescript/dev}
 * into a single endpoint with {@code ?dev} query parameter.
 */
public class PiescriptResponse extends ActionResponse implements ChunkedToXContentObject {

    @Nullable
    private final Value value;
    @Nullable
    private final String typeString;
    @Nullable
    private final DevInfo devInfo;

    /**
     * Debug information collected during the piescript pipeline.
     * Each field is nullable — a null value means that stage was not reached
     * (e.g. if parsing failed, only {@code parseError} is populated).
     */
    public record DevInfo(
        @Nullable String tree,
        @Nullable String core,
        @Nullable String coreRaw,
        @Nullable String type,
        @Nullable String constraints,
        @Nullable String zonker,
        List<String> diagnostics,
        @Nullable String eval,
        @Nullable String evalError,
        @Nullable String parseError,
        @Nullable String typeError
    ) {}

    private PiescriptResponse(Value value, String typeString) {
        this.value = value;
        this.typeString = typeString;
        this.devInfo = null;
    }

    private PiescriptResponse(DevInfo devInfo) {
        this.value = null;
        this.typeString = null;
        this.devInfo = devInfo;
    }

    public static PiescriptResponse fromValue(Value value, String typeString) {
        return new PiescriptResponse(value, typeString);
    }

    public static PiescriptResponse fromDev(DevInfo devInfo) {
        return new PiescriptResponse(devInfo);
    }

    // ── Serialization ──

    private static final byte EVAL_RESULT = 0;
    private static final byte DEV_RESULT = 1;

    public PiescriptResponse(StreamInput in) throws IOException {
        byte tag = in.readByte();
        if (tag == EVAL_RESULT) {
            this.typeString = in.readString();
            this.value = readValue(in);
            this.devInfo = null;
        } else {
            this.value = null;
            this.typeString = null;
            this.devInfo = new DevInfo(
                in.readOptionalString(),
                in.readOptionalString(),
                in.readOptionalString(),
                in.readOptionalString(),
                in.readOptionalString(),
                in.readOptionalString(),
                in.readStringCollectionAsList(),
                in.readOptionalString(),
                in.readOptionalString(),
                in.readOptionalString(),
                in.readOptionalString()
            );
        }
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        if (devInfo != null) {
            out.writeByte(DEV_RESULT);
            out.writeOptionalString(devInfo.tree);
            out.writeOptionalString(devInfo.core);
            out.writeOptionalString(devInfo.coreRaw);
            out.writeOptionalString(devInfo.type);
            out.writeOptionalString(devInfo.constraints);
            out.writeOptionalString(devInfo.zonker);
            out.writeStringCollection(devInfo.diagnostics);
            out.writeOptionalString(devInfo.eval);
            out.writeOptionalString(devInfo.evalError);
            out.writeOptionalString(devInfo.parseError);
            out.writeOptionalString(devInfo.typeError);
        } else {
            out.writeByte(EVAL_RESULT);
            out.writeString(typeString);
            writeValue(out, value);
        }
    }

    // ── XContent rendering ──

    @Override
    public Iterator<? extends ToXContent> toXContentChunked(ToXContent.Params params) {
        if (devInfo != null) {
            return Iterators.single((builder, p) -> {
                builder.startObject();
                writeOptionalField(builder, "tree", devInfo.tree);
                writeOptionalField(builder, "parse_error", devInfo.parseError);
                writeOptionalField(builder, "core", devInfo.core);
                writeOptionalField(builder, "core_raw", devInfo.coreRaw);
                writeOptionalField(builder, "type", devInfo.type);
                writeOptionalField(builder, "constraints", devInfo.constraints);
                writeOptionalField(builder, "zonker", devInfo.zonker);
                if (devInfo.diagnostics.isEmpty() == false) {
                    builder.field("diagnostics", devInfo.diagnostics);
                }
                writeOptionalField(builder, "type_error", devInfo.typeError);
                writeOptionalField(builder, "eval", devInfo.eval);
                writeOptionalField(builder, "eval_error", devInfo.evalError);
                builder.endObject();
                return builder;
            });
        }
        return Iterators.single((builder, p) -> {
            builder.startObject();
            builder.field("type", typeString);
            writeValueToXContent(builder, "result", value);
            builder.endObject();
            return builder;
        });
    }

    private static void writeOptionalField(XContentBuilder builder, String name, @Nullable String value) throws IOException {
        if (value != null) {
            builder.field(name, value);
        }
    }

    // ── Value serialization helpers ──

    private static void writeValueToXContent(XContentBuilder builder, String fieldName, Value val) throws IOException {
        switch (val) {
            case Value.IntegerVal v -> builder.field(fieldName, v.value());
            case Value.LongVal v -> builder.field(fieldName, v.value());
            case Value.DoubleVal v -> builder.field(fieldName, v.value());
            case Value.KeywordVal v -> builder.field(fieldName, v.value());
            case Value.BooleanVal v -> builder.field(fieldName, v.value());
            case Value.NullVal ignored -> builder.nullField(fieldName);
            case Value.RecordVal v -> {
                builder.startObject(fieldName);
                for (Map.Entry<String, Value> entry : v.fields().entrySet()) {
                    writeValueToXContent(builder, entry.getKey(), entry.getValue());
                }
                builder.endObject();
            }
            case Value.ListVal s -> {
                builder.startArray(fieldName);
                for (var element : s.elements()) {
                    writeValueAsArrayElement(builder, element);
                }
                builder.endArray();
            }
            case Value.ClosureVal ignored -> builder.field(fieldName, "<function>");
            case Value.BuiltinVal b -> builder.field(fieldName, "<builtin:" + b.name() + ">");
            case Value.ChannelVal ignored -> builder.field(fieldName, "<channel>");
        }
    }

    private static void writeValueAsArrayElement(XContentBuilder builder, Value val) throws IOException {
        switch (val) {
            case Value.IntegerVal v -> builder.value(v.value());
            case Value.LongVal v -> builder.value(v.value());
            case Value.DoubleVal v -> builder.value(v.value());
            case Value.KeywordVal v -> builder.value(v.value());
            case Value.BooleanVal v -> builder.value(v.value());
            case Value.NullVal ignored -> builder.nullValue();
            case Value.RecordVal v -> {
                builder.startObject();
                for (Map.Entry<String, Value> entry : v.fields().entrySet()) {
                    writeValueToXContent(builder, entry.getKey(), entry.getValue());
                }
                builder.endObject();
            }
            case Value.ListVal s -> {
                builder.startArray();
                for (var element : s.elements()) {
                    writeValueAsArrayElement(builder, element);
                }
                builder.endArray();
            }
            case Value.ClosureVal ignored -> builder.value("<function>");
            case Value.BuiltinVal b -> builder.value("<builtin:" + b.name() + ">");
            case Value.ChannelVal ignored -> builder.value("<channel>");
        }
    }

    private static void writeValue(StreamOutput out, Value val) throws IOException {
        ValueSerialization.writeValue(out, val);
    }

    private static Value readValue(StreamInput in) throws IOException {
        return ValueSerialization.readValue(in);
    }
}
