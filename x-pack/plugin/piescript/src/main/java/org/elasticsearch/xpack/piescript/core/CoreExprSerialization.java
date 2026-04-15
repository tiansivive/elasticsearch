/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.core;

import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.xpack.esql.core.tree.Location;
import org.elasticsearch.xpack.esql.core.tree.Source;
import org.elasticsearch.xpack.piescript.types.TypeSerialization;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Wire serialization for the {@link CoreExpr} sealed hierarchy (21 variants).
 * Used to ship closure bodies across nodes as part of {@code ClosureVal} serialization.
 *
 * <p>Each variant is identified by a stable byte tag. Source location is not serialized;
 * deserialized nodes use a synthetic empty source since the original source text is not
 * meaningful on the receiving node.
 *
 * <p>Depends on {@link TypeSerialization} for {@code MonoType}, {@code LitVal}, and {@code Op}.
 */
public final class CoreExprSerialization {

    private static final Source WIRE_SOURCE = new Source(new Location(0, 0), "<wire>");

    private CoreExprSerialization() {}

    private static final byte TAG_VAR = 0;
    private static final byte TAG_FREE = 1;
    private static final byte TAG_LIT = 2;
    private static final byte TAG_LAM = 3;
    private static final byte TAG_APP = 4;
    private static final byte TAG_LET = 5;
    private static final byte TAG_RECORD = 6;
    private static final byte TAG_PROJECT = 7;
    private static final byte TAG_UPDATE = 8;
    private static final byte TAG_PRIMOP = 9;
    private static final byte TAG_TYPE_ABS = 10;
    private static final byte TAG_TYPE_APP = 11;
    private static final byte TAG_QUERY = 12;
    private static final byte TAG_SPAWN = 13;
    private static final byte TAG_WHEN = 14;
    private static final byte TAG_SEND = 15;
    private static final byte TAG_LIST = 16;
    private static final byte TAG_QUERY_EXEC = 17;
    private static final byte TAG_MATCH = 18;
    private static final byte TAG_LOOP = 19;
    private static final byte TAG_REPEAT = 20;

    public static void writeCoreExpr(StreamOutput out, CoreExpr expr) throws IOException {
        switch (expr) {
            case CoreVar v -> {
                out.writeByte(TAG_VAR);
                out.writeVInt(v.index());
                out.writeOptionalString(v.debugName());
                TypeSerialization.writeMonoType(out, v.type());
            }
            case CoreFree f -> {
                out.writeByte(TAG_FREE);
                out.writeString(f.name());
                TypeSerialization.writeMonoType(out, f.type());
            }
            case CoreLit lit -> {
                out.writeByte(TAG_LIT);
                TypeSerialization.writeLitVal(out, lit.value());
                TypeSerialization.writeMonoType(out, lit.type());
            }
            case CoreLam lam -> {
                out.writeByte(TAG_LAM);
                out.writeOptionalString(lam.debugName());
                TypeSerialization.writeMonoType(out, lam.paramType());
                writeCoreExpr(out, lam.body());
                TypeSerialization.writeMonoType(out, lam.type());
            }
            case CoreApp app -> {
                out.writeByte(TAG_APP);
                writeCoreExpr(out, app.fn());
                writeCoreExpr(out, app.arg());
                TypeSerialization.writeMonoType(out, app.type());
            }
            case CoreLet let -> {
                out.writeByte(TAG_LET);
                out.writeOptionalString(let.debugName());
                TypeSerialization.writeMonoType(out, let.bindType());
                writeCoreExpr(out, let.rhs());
                writeCoreExpr(out, let.body());
                TypeSerialization.writeMonoType(out, let.type());
            }
            case CoreRecord rec -> {
                out.writeByte(TAG_RECORD);
                out.writeStringCollection(rec.labels());
                out.writeCollection(rec.children(), (o, child) -> writeCoreExpr(o, child));
                TypeSerialization.writeMonoType(out, rec.type());
            }
            case CoreProject proj -> {
                out.writeByte(TAG_PROJECT);
                writeCoreExpr(out, proj.expr());
                out.writeString(proj.label());
                TypeSerialization.writeMonoType(out, proj.type());
            }
            case CoreUpdate upd -> {
                out.writeByte(TAG_UPDATE);
                out.writeStringCollection(upd.labels());
                out.writeCollection(upd.children(), (o, child) -> writeCoreExpr(o, child));
                TypeSerialization.writeMonoType(out, upd.type());
            }
            case CorePrimOp primOp -> {
                out.writeByte(TAG_PRIMOP);
                TypeSerialization.writeOp(out, primOp.op());
                out.writeCollection(primOp.args(), (o, arg) -> writeCoreExpr(o, arg));
                TypeSerialization.writeMonoType(out, primOp.type());
            }
            case CoreTypeAbs ta -> {
                out.writeByte(TAG_TYPE_ABS);
                out.writeVInt(ta.rigidId());
                TypeSerialization.writeMonoType(out, ta.kind());
                writeCoreExpr(out, ta.body());
                TypeSerialization.writeMonoType(out, ta.type());
            }
            case CoreTypeApp tapp -> {
                out.writeByte(TAG_TYPE_APP);
                writeCoreExpr(out, tapp.polyExpr());
                TypeSerialization.writeMonoType(out, tapp.typeArg());
                TypeSerialization.writeMonoType(out, tapp.type());
            }
            case CoreSpawn sp -> {
                out.writeByte(TAG_SPAWN);
                boolean hasBody = sp.body() != null;
                out.writeBoolean(hasBody);
                if (hasBody) {
                    writeCoreExpr(out, sp.body());
                }
                TypeSerialization.writeMonoType(out, sp.type());
            }
            case CoreWhen when -> {
                out.writeByte(TAG_WHEN);
                var bindings = when.bindings();
                out.writeVInt(bindings.size());
                for (var b : bindings) {
                    writeCoreExpr(out, b.channel());
                    out.writeOptionalString(b.debugName());
                }
                writeCoreExpr(out, when.body());
                TypeSerialization.writeMonoType(out, when.type());
            }
            case CoreSend send -> {
                out.writeByte(TAG_SEND);
                writeCoreExpr(out, send.channel());
                writeCoreExpr(out, send.value());
                TypeSerialization.writeMonoType(out, send.type());
            }
            case CoreList list -> {
                out.writeByte(TAG_LIST);
                out.writeCollection(list.elements(), (o, child) -> writeCoreExpr(o, child));
                TypeSerialization.writeMonoType(out, list.type());
            }
            case CoreQueryExec qe -> {
                out.writeByte(TAG_QUERY_EXEC);
                writeCoreExpr(out, qe.plan());
                TypeSerialization.writeMonoType(out, qe.type());
            }
            case CoreMatch match -> {
                out.writeByte(TAG_MATCH);
                writeCoreExpr(out, match.scrutinee());
                var arms = match.arms();
                out.writeVInt(arms.size());
                for (var arm : arms) {
                    writePattern(out, arm.pattern());
                    writeCoreExpr(out, arm.body());
                }
                TypeSerialization.writeMonoType(out, match.type());
            }
            case CoreLoop loop -> {
                out.writeByte(TAG_LOOP);
                writeCoreExpr(out, loop.init());
                var arms = loop.arms();
                out.writeVInt(arms.size());
                for (var arm : arms) {
                    writePattern(out, arm.pattern());
                    writeCoreExpr(out, arm.body());
                }
                TypeSerialization.writeMonoType(out, loop.type());
            }
            case CoreRepeat repeat -> {
                out.writeByte(TAG_REPEAT);
                writeCoreExpr(out, repeat.expr());
                TypeSerialization.writeMonoType(out, repeat.type());
            }
        }
    }

    public static CoreExpr readCoreExpr(StreamInput in) throws IOException {
        byte tag = in.readByte();
        return switch (tag) {
            case TAG_VAR -> new CoreVar(WIRE_SOURCE, in.readVInt(), in.readOptionalString(), TypeSerialization.readMonoType(in));
            case TAG_FREE -> new CoreFree(WIRE_SOURCE, in.readString(), TypeSerialization.readMonoType(in));
            case TAG_LIT -> new CoreLit(WIRE_SOURCE, TypeSerialization.readLitVal(in), TypeSerialization.readMonoType(in));
            case TAG_LAM -> {
                var debugName = in.readOptionalString();
                var paramType = TypeSerialization.readMonoType(in);
                var body = readCoreExpr(in);
                var type = TypeSerialization.readMonoType(in);
                yield new CoreLam(WIRE_SOURCE, debugName, paramType, body, type);
            }
            case TAG_APP -> {
                var fn = readCoreExpr(in);
                var arg = readCoreExpr(in);
                var type = TypeSerialization.readMonoType(in);
                yield new CoreApp(WIRE_SOURCE, fn, arg, type);
            }
            case TAG_LET -> {
                var debugName = in.readOptionalString();
                var bindType = TypeSerialization.readMonoType(in);
                var rhs = readCoreExpr(in);
                var body = readCoreExpr(in);
                var type = TypeSerialization.readMonoType(in);
                yield new CoreLet(WIRE_SOURCE, debugName, bindType, rhs, body, type);
            }
            case TAG_RECORD -> {
                var labels = in.readStringCollectionAsList();
                var values = in.readCollectionAsList(CoreExprSerialization::readCoreExpr);
                var type = TypeSerialization.readMonoType(in);
                yield new CoreRecord(WIRE_SOURCE, labels, values, type);
            }
            case TAG_PROJECT -> {
                var expr = readCoreExpr(in);
                var label = in.readString();
                var type = TypeSerialization.readMonoType(in);
                yield new CoreProject(WIRE_SOURCE, expr, label, type);
            }
            case TAG_UPDATE -> {
                var labels = in.readStringCollectionAsList();
                var children = in.readCollectionAsList(CoreExprSerialization::readCoreExpr);
                var type = TypeSerialization.readMonoType(in);
                yield new CoreUpdate(WIRE_SOURCE, labels, children, type);
            }
            case TAG_PRIMOP -> {
                var op = TypeSerialization.readOp(in);
                var args = in.readCollectionAsList(CoreExprSerialization::readCoreExpr);
                var type = TypeSerialization.readMonoType(in);
                yield new CorePrimOp(WIRE_SOURCE, op, args, type);
            }
            case TAG_TYPE_ABS -> {
                var rigidId = in.readVInt();
                var kind = TypeSerialization.readMonoType(in);
                var body = readCoreExpr(in);
                var type = TypeSerialization.readMonoType(in);
                yield new CoreTypeAbs(WIRE_SOURCE, rigidId, kind, body, type);
            }
            case TAG_TYPE_APP -> {
                var polyExpr = readCoreExpr(in);
                var typeArg = TypeSerialization.readMonoType(in);
                var type = TypeSerialization.readMonoType(in);
                yield new CoreTypeApp(WIRE_SOURCE, polyExpr, typeArg, type);
            }
            case TAG_SPAWN -> {
                boolean hasBody = in.readBoolean();
                var body = hasBody ? readCoreExpr(in) : null;
                var type = TypeSerialization.readMonoType(in);
                yield new CoreSpawn(WIRE_SOURCE, body, type);
            }
            case TAG_WHEN -> {
                int bindingCount = in.readVInt();
                var bindings = new ArrayList<CoreWhen.WhenBinding>(bindingCount);
                for (int i = 0; i < bindingCount; i++) {
                    var channel = readCoreExpr(in);
                    var debugName = in.readOptionalString();
                    bindings.add(new CoreWhen.WhenBinding(channel, debugName));
                }
                var body = readCoreExpr(in);
                var type = TypeSerialization.readMonoType(in);
                yield new CoreWhen(WIRE_SOURCE, bindings, body, type);
            }
            case TAG_SEND -> {
                var channel = readCoreExpr(in);
                var value = readCoreExpr(in);
                var type = TypeSerialization.readMonoType(in);
                yield new CoreSend(WIRE_SOURCE, channel, value, type);
            }
            case TAG_LIST -> {
                var elements = in.readCollectionAsList(CoreExprSerialization::readCoreExpr);
                var type = TypeSerialization.readMonoType(in);
                yield new CoreList(WIRE_SOURCE, elements, type);
            }
            case TAG_QUERY_EXEC -> {
                var plan = readCoreExpr(in);
                var type = TypeSerialization.readMonoType(in);
                yield new CoreQueryExec(WIRE_SOURCE, plan, type);
            }
            case TAG_MATCH -> {
                var scrutinee = readCoreExpr(in);
                int armCount = in.readVInt();
                var arms = new ArrayList<Alternative>(armCount);
                for (int i = 0; i < armCount; i++) {
                    var pat = readPattern(in);
                    var body = readCoreExpr(in);
                    arms.add(new Alternative(pat, body));
                }
                var type = TypeSerialization.readMonoType(in);
                yield new CoreMatch(WIRE_SOURCE, scrutinee, arms, type);
            }
            case TAG_LOOP -> {
                var init = readCoreExpr(in);
                int armCount = in.readVInt();
                var arms = new ArrayList<Alternative>(armCount);
                for (int i = 0; i < armCount; i++) {
                    var pat = readPattern(in);
                    var body = readCoreExpr(in);
                    arms.add(new Alternative(pat, body));
                }
                var type = TypeSerialization.readMonoType(in);
                yield new CoreLoop(WIRE_SOURCE, init, arms, type);
            }
            case TAG_REPEAT -> {
                var expr = readCoreExpr(in);
                var type = TypeSerialization.readMonoType(in);
                yield new CoreRepeat(WIRE_SOURCE, expr, type);
            }
            default -> throw new IOException("unknown CoreExpr tag: " + tag);
        };
    }

    private static final byte PAT_LIT = 0;
    private static final byte PAT_VAR = 1;
    private static final byte PAT_WILDCARD = 2;
    private static final byte PAT_RECORD = 3;
    private static final byte PAT_LIST = 4;
    private static final byte PAT_CONS = 5;

    public static void writePattern(StreamOutput out, Pattern pattern) throws IOException {
        switch (pattern) {
            case Pattern.LitPat lit -> {
                out.writeByte(PAT_LIT);
                TypeSerialization.writeLitVal(out, lit.value());
            }
            case Pattern.VarPat var -> {
                out.writeByte(PAT_VAR);
                out.writeOptionalString(var.debugName());
                TypeSerialization.writeMonoType(out, var.type());
            }
            case Pattern.WildcardPat w -> out.writeByte(PAT_WILDCARD);
            case Pattern.RecordPat rec -> {
                out.writeByte(PAT_RECORD);
                out.writeMap(rec.fields(), StreamOutput::writeString, CoreExprSerialization::writePattern);
                out.writeBoolean(rec.hasTail());
                out.writeOptionalString(rec.tailName());
            }
            case Pattern.ListPat list -> {
                out.writeByte(PAT_LIST);
                out.writeCollection(list.elements(), CoreExprSerialization::writePattern);
            }
            case Pattern.ConsListPat cons -> {
                out.writeByte(PAT_CONS);
                writePattern(out, cons.head());
                writePattern(out, cons.tail());
            }
        }
    }

    public static Pattern readPattern(StreamInput in) throws IOException {
        byte tag = in.readByte();
        return switch (tag) {
            case PAT_LIT -> new Pattern.LitPat(TypeSerialization.readLitVal(in));
            case PAT_VAR -> new Pattern.VarPat(in.readOptionalString(), TypeSerialization.readMonoType(in));
            case PAT_WILDCARD -> new Pattern.WildcardPat();
            case PAT_RECORD -> new Pattern.RecordPat(
                in.readMap(StreamInput::readString, CoreExprSerialization::readPattern),
                in.readBoolean(),
                in.readOptionalString()
            );
            case PAT_LIST -> new Pattern.ListPat(in.readCollectionAsList(CoreExprSerialization::readPattern));
            case PAT_CONS -> new Pattern.ConsListPat(readPattern(in), readPattern(in));
            default -> throw new IOException("unknown Pattern tag: " + tag);
        };
    }
}
