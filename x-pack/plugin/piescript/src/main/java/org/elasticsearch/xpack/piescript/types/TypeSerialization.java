/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.types;

import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;

import java.io.IOException;
import java.util.LinkedHashMap;

/**
 * Wire serialization for piescript type-system structures: {@link MonoType},
 * {@link RowType}, {@link LitVal}, and {@link Op}.
 *
 * <p>All methods use a leading byte tag to distinguish sealed-hierarchy variants.
 * Tag assignments are stable across versions (append-only).
 */
public final class TypeSerialization {

    private TypeSerialization() {}

    // ──── MonoType ────

    private static final byte MONO_TCON = 0;
    private static final byte MONO_ARROW = 1;
    private static final byte MONO_RECORD = 2;
    private static final byte MONO_APP = 3;
    private static final byte MONO_META = 4;
    private static final byte MONO_RIGID = 5;
    private static final byte MONO_ROW = 6;

    public static void writeMonoType(StreamOutput out, MonoType type) throws IOException {
        switch (type) {
            case MonoType.TCon t -> {
                out.writeByte(MONO_TCON);
                out.writeString(t.name());
            }
            case MonoType.Arrow a -> {
                out.writeByte(MONO_ARROW);
                writeMonoType(out, a.param());
                writeMonoType(out, a.result());
            }
            case MonoType.RecordType r -> {
                out.writeByte(MONO_RECORD);
                writeMonoType(out, r.row());
            }
            case RowType row -> {
                out.writeByte(MONO_ROW);
                writeRowType(out, row);
            }
            case MonoType.AppType a -> {
                out.writeByte(MONO_APP);
                writeMonoType(out, a.constructor());
                writeMonoType(out, a.argument());
            }
            case MonoType.Meta m -> {
                out.writeByte(MONO_META);
                out.writeVInt(m.id());
                out.writeVInt(m.bindingLevel());
                writeMonoType(out, m.kind());
            }
            case MonoType.Rigid r -> {
                out.writeByte(MONO_RIGID);
                out.writeVInt(r.id());
                writeMonoType(out, r.kind());
            }
        }
    }

    public static MonoType readMonoType(StreamInput in) throws IOException {
        byte tag = in.readByte();
        return switch (tag) {
            case MONO_TCON -> new MonoType.TCon(in.readString());
            case MONO_ARROW -> new MonoType.Arrow(readMonoType(in), readMonoType(in));
            case MONO_RECORD -> new MonoType.RecordType(readMonoType(in));
            case MONO_ROW -> readRowType(in);
            case MONO_APP -> new MonoType.AppType(readMonoType(in), readMonoType(in));
            case MONO_META -> new MonoType.Meta(in.readVInt(), in.readVInt(), readMonoType(in));
            case MONO_RIGID -> new MonoType.Rigid(in.readVInt(), readMonoType(in));
            default -> throw new IOException("unknown MonoType tag: " + tag);
        };
    }

    // ──── RowType ────

    public static void writeRowType(StreamOutput out, RowType row) throws IOException {
        out.writeVInt(row.fields().size());
        for (var entry : row.fields().entrySet()) {
            out.writeString(entry.getKey());
            writeMonoType(out, entry.getValue());
        }
        out.writeBoolean(row.tail().isPresent());
        if (row.tail().isPresent()) {
            writeMonoType(out, row.tail().get());
        }
    }

    public static RowType readRowType(StreamInput in) throws IOException {
        int size = in.readVInt();
        var fields = new LinkedHashMap<String, MonoType>(size);
        for (int i = 0; i < size; i++) {
            fields.put(in.readString(), readMonoType(in));
        }
        boolean hasTail = in.readBoolean();
        if (hasTail) {
            return RowType.open(fields, readMonoType(in));
        }
        return RowType.closed(fields);
    }

    // ──── LitVal ────

    private static final byte LIT_INTEGER = 0;
    private static final byte LIT_LONG = 1;
    private static final byte LIT_DOUBLE = 2;
    private static final byte LIT_KEYWORD = 3;
    private static final byte LIT_BOOLEAN = 4;
    private static final byte LIT_NULL = 5;
    private static final byte LIT_INDEX = 6;

    public static void writeLitVal(StreamOutput out, LitVal lit) throws IOException {
        switch (lit) {
            case LitVal.IntegerLit v -> {
                out.writeByte(LIT_INTEGER);
                out.writeInt(v.value());
            }
            case LitVal.LongLit v -> {
                out.writeByte(LIT_LONG);
                out.writeLong(v.value());
            }
            case LitVal.DoubleLit v -> {
                out.writeByte(LIT_DOUBLE);
                out.writeDouble(v.value());
            }
            case LitVal.KeywordLit v -> {
                out.writeByte(LIT_KEYWORD);
                out.writeBytesRef(v.value());
            }
            case LitVal.BooleanLit v -> {
                out.writeByte(LIT_BOOLEAN);
                out.writeBoolean(v.value());
            }
            case LitVal.NullLit ignored -> out.writeByte(LIT_NULL);
            case LitVal.IndexLit v -> {
                out.writeByte(LIT_INDEX);
                out.writeString(v.name());
                out.writeMap(v.fieldTypes(), StreamOutput::writeString);
            }
        }
    }

    public static LitVal readLitVal(StreamInput in) throws IOException {
        byte tag = in.readByte();
        return switch (tag) {
            case LIT_INTEGER -> new LitVal.IntegerLit(in.readInt());
            case LIT_LONG -> new LitVal.LongLit(in.readLong());
            case LIT_DOUBLE -> new LitVal.DoubleLit(in.readDouble());
            case LIT_KEYWORD -> new LitVal.KeywordLit(in.readBytesRef());
            case LIT_BOOLEAN -> new LitVal.BooleanLit(in.readBoolean());
            case LIT_NULL -> new LitVal.NullLit();
            case LIT_INDEX -> {
                var name = in.readString();
                var fieldTypes = in.readMap(StreamInput::readString);
                yield new LitVal.IndexLit(name, fieldTypes);
            }
            default -> throw new IOException("unknown LitVal tag: " + tag);
        };
    }

    // ──── Op ────

    public static void writeOp(StreamOutput out, Op op) throws IOException {
        out.writeByte((byte) op.ordinal());
    }

    public static Op readOp(StreamInput in) throws IOException {
        byte ordinal = in.readByte();
        return Op.values()[ordinal];
    }
}
