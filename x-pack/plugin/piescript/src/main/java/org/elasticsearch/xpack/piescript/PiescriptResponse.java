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
import org.elasticsearch.core.Releasable;
import org.elasticsearch.xcontent.ToXContent;
import org.elasticsearch.xcontent.XContentBuilder;
import org.elasticsearch.xpack.esql.action.EsqlQueryResponse;
import org.elasticsearch.xpack.piescript.eval.Value;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * Response from the piescript eval endpoint. Wraps either an expression
 * evaluation result ({@link Value} + type string) or an {@link EsqlQueryResponse}
 * from the query passthrough path (D-023).
 */
public class PiescriptResponse extends ActionResponse implements ChunkedToXContentObject, Releasable {

    private static final byte EXPRESSION_RESULT = 0;
    private static final byte QUERY_RESULT = 1;

    @Nullable
    private final Value value;
    @Nullable
    private final String typeString;
    @Nullable
    private final EsqlQueryResponse esqlResponse;

    private PiescriptResponse(Value value, String typeString) {
        this.value = value;
        this.typeString = typeString;
        this.esqlResponse = null;
    }

    private PiescriptResponse(EsqlQueryResponse esqlResponse) {
        this.value = null;
        this.typeString = null;
        this.esqlResponse = esqlResponse;
    }

    public static PiescriptResponse fromValue(Value value, String typeString) {
        return new PiescriptResponse(value, typeString);
    }

    public static PiescriptResponse fromEsqlResponse(EsqlQueryResponse esqlResponse) {
        return new PiescriptResponse(esqlResponse);
    }

    public PiescriptResponse(StreamInput in) throws IOException {
        byte discriminator = in.readByte();
        if (discriminator == EXPRESSION_RESULT) {
            this.typeString = in.readString();
            this.value = readValue(in);
            this.esqlResponse = null;
        } else {
            throw new UnsupportedOperationException("query result deserialization from stream not supported in Phase 1c");
        }
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        if (esqlResponse != null) {
            out.writeByte(QUERY_RESULT);
            esqlResponse.writeTo(out);
        } else {
            out.writeByte(EXPRESSION_RESULT);
            out.writeString(typeString);
            writeValue(out, value);
        }
    }

    @Override
    public Iterator<? extends ToXContent> toXContentChunked(ToXContent.Params params) {
        if (esqlResponse != null) {
            return esqlResponse.toXContentChunked(params);
        }
        return Iterators.single((builder, p) -> {
            builder.startObject();
            builder.field("type", typeString);
            writeValueToXContent(builder, "result", value);
            builder.endObject();
            return builder;
        });
    }

    @Override
    public void close() {
        if (esqlResponse != null) {
            esqlResponse.close();
        }
    }

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
            case Value.ClosureVal ignored -> builder.field(fieldName, "<function>");
        }
    }

    private static void writeValue(StreamOutput out, Value val) throws IOException {
        switch (val) {
            case Value.IntegerVal v -> {
                out.writeByte((byte) 0);
                out.writeInt(v.value());
            }
            case Value.LongVal v -> {
                out.writeByte((byte) 1);
                out.writeLong(v.value());
            }
            case Value.DoubleVal v -> {
                out.writeByte((byte) 2);
                out.writeDouble(v.value());
            }
            case Value.KeywordVal v -> {
                out.writeByte((byte) 3);
                out.writeString(v.value());
            }
            case Value.BooleanVal v -> {
                out.writeByte((byte) 4);
                out.writeBoolean(v.value());
            }
            case Value.NullVal ignored -> out.writeByte((byte) 5);
            case Value.RecordVal v -> {
                out.writeByte((byte) 6);
                out.writeMap(v.fields(), (o, value) -> writeValue(o, value));
            }
            case Value.ClosureVal ignored -> out.writeByte((byte) 7);
        }
    }

    private static Value readValue(StreamInput in) throws IOException {
        byte tag = in.readByte();
        return switch (tag) {
            case 0 -> new Value.IntegerVal(in.readInt());
            case 1 -> new Value.LongVal(in.readLong());
            case 2 -> new Value.DoubleVal(in.readDouble());
            case 3 -> new Value.KeywordVal(in.readString());
            case 4 -> new Value.BooleanVal(in.readBoolean());
            case 5 -> new Value.NullVal();
            case 6 -> new Value.RecordVal(in.readMap(PiescriptResponse::readValue));
            case 7 -> new Value.ClosureVal(null, null);
            default -> throw new IOException("unknown Value tag: " + tag);
        };
    }
}
