/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.eval;

import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.xpack.piescript.core.CoreExprSerialization;

import java.io.IOException;

/**
 * Full-fidelity wire serialization for the {@link Value} sealed hierarchy (11 variants).
 * Unlike the partial serialization in {@code PiescriptResponse} (which loses closure/builtin
 * state for display purposes), this preserves all data — including {@code ClosureVal} bodies
 * and environments — for cross-node closure shipping.
 *
 * <p>Tag assignments are stable and compatible with {@code PiescriptResponse}'s existing tags.
 *
 * <p>Depends on {@link CoreExprSerialization} for {@code ClosureVal.body}.
 */
public final class ValueSerialization {

    private ValueSerialization() {}

    private static final byte TAG_INTEGER = 0;
    private static final byte TAG_LONG = 1;
    private static final byte TAG_DOUBLE = 2;
    private static final byte TAG_KEYWORD = 3;
    private static final byte TAG_BOOLEAN = 4;
    private static final byte TAG_NULL = 5;
    private static final byte TAG_RECORD = 6;
    private static final byte TAG_CLOSURE = 7;
    private static final byte TAG_BUILTIN = 8;
    private static final byte TAG_LIST = 9;
    private static final byte TAG_CHANNEL = 10;
    private static final byte TAG_INDEX = 11;
    private static final byte TAG_EXCHANGE = 12;

    public static void writeValue(StreamOutput out, Value value) throws IOException {
        switch (value) {
            case Value.IntegerVal v -> {
                out.writeByte(TAG_INTEGER);
                out.writeInt(v.value());
            }
            case Value.LongVal v -> {
                out.writeByte(TAG_LONG);
                out.writeLong(v.value());
            }
            case Value.DoubleVal v -> {
                out.writeByte(TAG_DOUBLE);
                out.writeDouble(v.value());
            }
            case Value.KeywordVal v -> {
                out.writeByte(TAG_KEYWORD);
                out.writeString(v.value());
            }
            case Value.BooleanVal v -> {
                out.writeByte(TAG_BOOLEAN);
                out.writeBoolean(v.value());
            }
            case Value.NullVal ignored -> out.writeByte(TAG_NULL);
            case Value.RecordVal v -> {
                out.writeByte(TAG_RECORD);
                out.writeMap(v.fields(), (o, val) -> writeValue(o, val));
            }
            case Value.ClosureVal v -> {
                out.writeByte(TAG_CLOSURE);
                CoreExprSerialization.writeCoreExpr(out, v.body());
                out.writeVInt(v.env().length);
                for (Value envVal : v.env()) {
                    writeValue(out, envVal);
                }
            }
            case Value.BuiltinVal v -> {
                out.writeByte(TAG_BUILTIN);
                out.writeString(v.name());
                out.writeVInt(v.arity());
                out.writeCollection(v.partialArgs(), (o, arg) -> writeValue(o, arg));
            }
            case Value.ListVal v -> {
                out.writeByte(TAG_LIST);
                out.writeCollection(v.elements(), (o, elem) -> writeValue(o, elem));
            }
            case Value.ChannelVal v -> {
                out.writeByte(TAG_CHANNEL);
                out.writeString(v.nodeId());
                out.writeString(v.channelId());
            }
            case Value.IndexVal v -> {
                out.writeByte(TAG_INDEX);
                out.writeString(v.name());
                out.writeString(v.uuid());
                out.writeMap(v.fieldTypes(), StreamOutput::writeString);
            }
            case Value.ExchangeVal v -> {
                out.writeByte(TAG_EXCHANGE);
                out.writeString(v.nodeId());
                out.writeString(v.exchangeId());
                out.writeStringCollection(v.columnNames());
                out.writeVInt(v.bufferSize());
            }
            case Value.SearcherVal ignored -> throw new IOException("SearcherVal is not serializable (node-local only)");
            case Value.DocRefVal ignored -> throw new IOException("DocRefVal is not serializable (node-local only)");
            case Value.WriterVal ignored -> throw new IOException("WriterVal is not serializable (node-local only)");
            case Value.Symbol ignored -> throw new IOException("Symbol is not serializable (ephemeral ESQL compilation)");
            case Value.PageVal ignored -> throw new IOException("PageVal is not serializable (node-local only)");
            case Value.ExchangeSinkVal ignored -> throw new IOException("ExchangeSinkVal is not serializable (node-local only)");
            case Value.ExchangeSourceVal ignored -> throw new IOException("ExchangeSourceVal is not serializable (node-local only)");
        }
    }

    public static Value readValue(StreamInput in) throws IOException {
        byte tag = in.readByte();
        return switch (tag) {
            case TAG_INTEGER -> new Value.IntegerVal(in.readInt());
            case TAG_LONG -> new Value.LongVal(in.readLong());
            case TAG_DOUBLE -> new Value.DoubleVal(in.readDouble());
            case TAG_KEYWORD -> new Value.KeywordVal(in.readString());
            case TAG_BOOLEAN -> new Value.BooleanVal(in.readBoolean());
            case TAG_NULL -> new Value.NullVal();
            case TAG_RECORD -> new Value.RecordVal(in.readMap(ValueSerialization::readValue));
            case TAG_CLOSURE -> {
                var body = CoreExprSerialization.readCoreExpr(in);
                int envLen = in.readVInt();
                var env = new Value[envLen];
                for (int i = 0; i < envLen; i++) {
                    env[i] = readValue(in);
                }
                yield new Value.ClosureVal(body, env);
            }
            case TAG_BUILTIN -> {
                var name = in.readString();
                var arity = in.readVInt();
                var partialArgs = in.readCollectionAsList(ValueSerialization::readValue);
                yield new Value.BuiltinVal(name, arity, partialArgs);
            }
            case TAG_LIST -> new Value.ListVal(in.readCollectionAsList(ValueSerialization::readValue));
            case TAG_CHANNEL -> new Value.ChannelVal(in.readString(), in.readString());
            case TAG_INDEX -> {
                var name = in.readString();
                var uuid = in.readString();
                var fieldTypes = in.readMap(StreamInput::readString);
                yield new Value.IndexVal(name, uuid, fieldTypes);
            }
            case TAG_EXCHANGE -> {
                var nodeId = in.readString();
                var exchangeId = in.readString();
                var columnNames = in.readCollectionAsList(StreamInput::readString);
                var bufferSize = in.readVInt();
                yield new Value.ExchangeVal(nodeId, exchangeId, columnNames, bufferSize);
            }
            default -> throw new IOException("unknown Value tag: " + tag);
        };
    }
}
