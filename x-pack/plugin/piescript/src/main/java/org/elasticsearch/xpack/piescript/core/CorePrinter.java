/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.core;

import org.elasticsearch.xpack.piescript.elab.ElaborationState;
import org.elasticsearch.xpack.piescript.elab.TypeWalker;
import org.elasticsearch.xpack.piescript.types.LitVal;
import org.elasticsearch.xpack.piescript.types.MonoType;
import org.elasticsearch.xpack.piescript.types.RowType;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Pretty-printer for Core IR expressions and types. Produces a compact,
 * human-readable S-expression-like format suitable for dev/debug endpoints.
 * All types are resolved through the zonker before printing.
 */
public final class CorePrinter {

    private CorePrinter() {}

    /** Print the Core IR with all types resolved through the zonker. */
    public static String printExpr(CoreExpr expr, ElaborationState state) {
        var sb = new StringBuilder();
        writeExpr(expr, state, sb);
        return sb.toString();
    }

    /** Print the Core IR with bare metas/rigids — no zonker lookups. */
    public static String printExprRaw(CoreExpr expr) {
        var sb = new StringBuilder();
        writeExpr(expr, null, sb);
        return sb.toString();
    }

    /** Print a type resolved through the zonker. */
    public static String printType(MonoType type, ElaborationState state) {
        return writeType(TypeWalker.resolveDeep(type, state));
    }

    /** Print the accumulated constraints as {@code left ~ right} pairs. */
    public static String printConstraints(ElaborationState state) {
        var sb = new StringBuilder();
        int i = 0;
        for (var c : state.constraints()) {
            sb.append("C").append(i++).append(": ");
            sb.append(writeType(c.left()));
            sb.append(" ~ ");
            sb.append(writeType(c.right()));
            sb.append("  [").append(c.line()).append(':').append(c.column()).append("]\n");
        }
        return sb.toString();
    }

    /** Print the zonker substitution: each solved meta and its solution. */
    public static String printZonker(ElaborationState state) {
        var sb = new StringBuilder();
        var entries = state.zonker().entrySet().stream().sorted(Map.Entry.comparingByKey()).toList();
        for (var entry : entries) {
            sb.append('?').append(entry.getKey()).append(" |=> ");
            switch (entry.getValue()) {
                case MonoType mono -> sb.append(writeType(mono));
                case RowType row -> sb.append(writeRow(row));
                default -> sb.append(entry.getValue());
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    private static void writeExpr(CoreExpr expr, ElaborationState state, StringBuilder sb) {
        switch (expr) {
            case CoreLit lit -> writeLit(lit.value(), sb);
            case CoreVar v -> {
                if (v.debugName() != null) {
                    sb.append(v.debugName());
                } else {
                    sb.append('#').append(v.index());
                }
            }
            case CoreFree f -> sb.append(f.name());
            case CoreLam lam -> {
                sb.append("(fn ");
                sb.append(lam.debugName() != null ? lam.debugName() : "_");
                sb.append(" : ");
                sb.append(state != null ? writeType(TypeWalker.resolveDeep(lam.paramType(), state)) : writeType(lam.paramType()));
                sb.append(" -> ");
                writeExpr(lam.body(), state, sb);
                sb.append(')');
            }
            case CoreApp app -> {
                sb.append('(');
                writeExpr(app.fn(), state, sb);
                sb.append(' ');
                writeExpr(app.arg(), state, sb);
                sb.append(')');
            }
            case CoreLet let -> {
                sb.append("(let ");
                sb.append(let.debugName() != null ? let.debugName() : "_");
                sb.append(" : ");
                sb.append(state != null ? writeType(TypeWalker.resolveDeep(let.bindType(), state)) : writeType(let.bindType()));
                sb.append(" = ");
                writeExpr(let.rhs(), state, sb);
                sb.append(" in ");
                writeExpr(let.body(), state, sb);
                sb.append(')');
            }
            case CorePrimOp op -> {
                sb.append('(');
                sb.append(op.op().name());
                for (var arg : op.args()) {
                    sb.append(' ');
                    writeExpr(arg, state, sb);
                }
                sb.append(')');
            }
            case CoreRecord rec -> {
                sb.append("{ ");
                var fields = rec.fields();
                for (int i = 0; i < fields.size(); i++) {
                    if (i > 0) sb.append(", ");
                    sb.append(fields.get(i).label()).append(": ");
                    writeExpr(fields.get(i).value(), state, sb);
                }
                sb.append(" }");
            }
            case CoreProject proj -> {
                writeExpr(proj.expr(), state, sb);
                sb.append('.').append(proj.label());
            }
            case CoreUpdate upd -> {
                sb.append("{ ");
                writeExpr(upd.expr(), state, sb);
                sb.append(" | ");
                var updates = upd.updates();
                for (int i = 0; i < updates.size(); i++) {
                    if (i > 0) sb.append(", ");
                    sb.append(updates.get(i).label()).append(" = ");
                    writeExpr(updates.get(i).value(), state, sb);
                }
                sb.append(" }");
            }
            case CoreTypeAbs typeAbs -> {
                sb.append("(Λ ");
                sb.append(rigidName(typeAbs.rigidId(), typeAbs.kind()));
                sb.append(". ");
                writeExpr(typeAbs.body(), state, sb);
                sb.append(')');
            }
            case CoreTypeApp typeApp -> {
                sb.append('(');
                writeExpr(typeApp.polyExpr(), state, sb);
                sb.append(" @");
                sb.append(state != null ? writeType(TypeWalker.resolveDeep(typeApp.typeArg(), state)) : writeType(typeApp.typeArg()));
                sb.append(')');
            }
            case CoreQuery q -> {
                sb.append("(query ");
                sb.append(q.indexPattern());
                sb.append(" : ");
                sb.append(state != null ? writeType(TypeWalker.resolveDeep(q.type(), state)) : writeType(q.type()));
                sb.append(')');
            }
        }
    }

    private static void writeLit(LitVal val, StringBuilder sb) {
        switch (val) {
            case LitVal.IntegerLit(var v) -> sb.append(v);
            case LitVal.LongLit(var v) -> sb.append(v).append('L');
            case LitVal.DoubleLit(var v) -> sb.append(v);
            case LitVal.KeywordLit(var v) -> sb.append('"').append(v.utf8ToString()).append('"');
            case LitVal.BooleanLit(var v) -> sb.append(v);
            case LitVal.NullLit() -> sb.append("null");
        }
    }

    static String writeType(MonoType type) {
        return switch (type) {
            case MonoType.TCon(var name) -> name;
            case MonoType.Arrow(var param, var result) -> {
                var paramStr = param instanceof MonoType.Arrow ? "(" + writeType(param) + ")" : writeType(param);
                yield paramStr + " -> " + writeType(result);
            }
            case MonoType.RecordType(var row) -> writeRow(row);
            case MonoType.AppType(var ctor, var arg) -> writeType(ctor) + " " + writeType(arg);
            case MonoType.Meta(var id, var lvl, var kind) -> "?" + id;
            case MonoType.Rigid(var id, var kind) -> rigidName(id, kind);
        };
    }

    private static String rigidName(int id, org.elasticsearch.xpack.piescript.types.Kind kind) {
        return switch (kind) {
            case ROW -> String.valueOf((char) ('r' + (id % 3)));
            case TYPE -> String.valueOf((char) ('a' + (id % 26)));
        };
    }

    private static String writeRow(RowType row) {
        var fields = row.fields()
            .entrySet()
            .stream()
            .map(e -> e.getKey() + ": " + writeType(e.getValue()))
            .collect(Collectors.joining(", "));
        if (row.rowVar().isEmpty()) {
            return "{ " + fields + " }";
        }
        var tail = row.rowVar().get();
        return "{ " + fields + " | ?" + tail.id() + " }";
    }
}
