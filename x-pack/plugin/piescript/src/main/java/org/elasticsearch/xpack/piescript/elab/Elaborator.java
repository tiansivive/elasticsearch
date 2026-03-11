/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.elab;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.lucene.util.BytesRef;
import org.elasticsearch.xpack.esql.core.tree.Location;
import org.elasticsearch.xpack.esql.core.tree.Source;
import org.elasticsearch.xpack.piescript.core.CoreApp;
import org.elasticsearch.xpack.piescript.core.CoreExpr;
import org.elasticsearch.xpack.piescript.core.CoreLam;
import org.elasticsearch.xpack.piescript.core.CoreLet;
import org.elasticsearch.xpack.piescript.core.CoreLit;
import org.elasticsearch.xpack.piescript.core.CorePrimOp;
import org.elasticsearch.xpack.piescript.core.CoreProject;
import org.elasticsearch.xpack.piescript.core.CoreRecord;
import org.elasticsearch.xpack.piescript.core.CoreUpdate;
import org.elasticsearch.xpack.piescript.core.CoreVar;
import org.elasticsearch.xpack.piescript.parser.PiescriptAntlrParser;
import org.elasticsearch.xpack.piescript.types.LitVal;
import org.elasticsearch.xpack.piescript.types.MonoType;
import org.elasticsearch.xpack.piescript.types.Op;
import org.elasticsearch.xpack.piescript.types.RowType;
import org.elasticsearch.xpack.piescript.types.TypeScheme;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Bidirectional type-checker and desugarer. Walks the ANTLR parse tree via
 * recursive descent (not the generated visitor), producing fully-typed
 * {@link CoreExpr} nodes. Carries an immutable {@link ElaborationContext}
 * as a parameter and a shared mutable {@link ElaborationState}.
 *
 * <p>Phase 1 limitations:
 * <ul>
 *   <li>No open-row unification — projection/update use direct field lookup on closed rows.</li>
 *   <li>No if/then/else — deferred to Phase 1d (pattern matching).</li>
 *   <li>No numeric widening/coercion — primops have fixed signatures per concrete type.</li>
 * </ul>
 */
public final class Elaborator {

    private static final MonoType INTEGER = new MonoType.TCon("Integer");
    private static final MonoType LONG = new MonoType.TCon("Long");
    private static final MonoType DOUBLE = new MonoType.TCon("Double");
    private static final MonoType KEYWORD = new MonoType.TCon("Keyword");
    private static final MonoType BOOLEAN = new MonoType.TCon("Boolean");
    private static final MonoType NULL_TYPE = new MonoType.TCon("Null");

    private static final Map<String, MonoType> KNOWN_TYPES = Map.of(
        "Integer",
        INTEGER,
        "Long",
        LONG,
        "Double",
        DOUBLE,
        "Keyword",
        KEYWORD,
        "Boolean",
        BOOLEAN,
        "Null",
        NULL_TYPE
    );

    private final ElaborationState state;

    public Elaborator(ElaborationState state) {
        this.state = state;
    }

    /**
     * Bundles an ESQL {@link Source} (for Core IR nodes) with raw line/column
     * (for error messages). We avoid calling methods on {@code Source} at
     * compile time because it implements {@code WarningSourceLocation} from
     * the compute module, which is not on piescript's compile classpath.
     */
    record Src(Source source, int line, int column) {}

    /**
     * Elaborate a full program: top-level bindings desugared to nested lets
     * wrapping the final expression.
     */
    public CoreExpr elaborateProgram(PiescriptAntlrParser.ProgramContext program) {
        var ctx = ElaborationContext.EMPTY;
        var topBindings = program.topBinding();
        var finalExpr = program.expr();

        if (topBindings.isEmpty()) {
            return elaborate(finalExpr, ctx);
        }

        return elaborateTopBindings(topBindings, 0, finalExpr, ctx);
    }

    private CoreExpr elaborateTopBindings(
        List<PiescriptAntlrParser.TopBindingContext> bindings,
        int index,
        PiescriptAntlrParser.ExprContext finalExpr,
        ElaborationContext ctx
    ) {
        if (index >= bindings.size()) {
            return elaborate(finalExpr, ctx);
        }

        var binding = bindings.get(index);
        var src = source(binding);
        var name = binding.IDENTIFIER().getText();

        var letCtx = ctx.enterBindingLevel();
        CoreExpr rhs = elaborate(binding.expr(), letCtx);
        if (binding.type() != null) {
            var annotatedType = resolveTypeAnnotation(binding.type());
            unifyOrThrow(rhs.type(), annotatedType, src);
        }

        var scheme = generalize(rhs.type(), letCtx.bindingLevel());
        var bodyCtx = ctx.bind(name, scheme);
        var body = elaborateTopBindings(bindings, index + 1, finalExpr, bodyCtx);

        return new CoreLet(src.source, name, rhs.type(), rhs, body, body.type());
    }

    // ──── Main dispatch ────

    private CoreExpr elaborate(ParseTree node, ElaborationContext ctx) {
        return switch (node) {
            case PiescriptAntlrParser.LetExprContext let -> elaborateLet(let, ctx);
            case PiescriptAntlrParser.LambdaExprContext lam -> elaborateLambda(lam, ctx);
            case PiescriptAntlrParser.ExprPipeContext e -> elaborate(e.pipeExpr(), ctx);

            case PiescriptAntlrParser.PipePassthroughContext p -> elaborate(p.orExpr(), ctx);
            case PiescriptAntlrParser.PipeOpContext p -> {
                var arg = elaborate(p.pipeExpr(), ctx);
                var fn = elaborate(p.orExpr(), ctx);
                var s = source(p);
                var resultType = state.freshType(ctx.bindingLevel());
                unifyOrThrow(fn.type(), new MonoType.Arrow(arg.type(), resultType), s);
                yield new CoreApp(s.source, fn, arg, resultType);
            }

            case PiescriptAntlrParser.OrPassthroughContext p -> elaborate(p.andExpr(), ctx);
            case PiescriptAntlrParser.OrOpContext p -> elaborateBinaryOp(p, p.orExpr(), p.andExpr(), Op.OR, ctx);
            case PiescriptAntlrParser.AndPassthroughContext p -> elaborate(p.eqExpr(), ctx);
            case PiescriptAntlrParser.AndOpContext p -> elaborateBinaryOp(p, p.andExpr(), p.eqExpr(), Op.AND, ctx);
            case PiescriptAntlrParser.EqPassthroughContext p -> elaborate(p.cmpExpr(), ctx);
            case PiescriptAntlrParser.EqualityOpContext p -> {
                var op = p.op.getType() == PiescriptAntlrParser.EQ ? Op.EQ : Op.NEQ;
                yield elaborateBinaryOp(p, p.cmpExpr(0), p.cmpExpr(1), op, ctx);
            }
            case PiescriptAntlrParser.CmpPassthroughContext p -> elaborate(p.addExpr(), ctx);
            case PiescriptAntlrParser.ComparisonOpContext p -> {
                var op = switch (p.op.getType()) {
                    case PiescriptAntlrParser.LT -> Op.LT;
                    case PiescriptAntlrParser.GT -> Op.GT;
                    case PiescriptAntlrParser.LTE -> Op.LTE;
                    case PiescriptAntlrParser.GTE -> Op.GTE;
                    default -> throw error(source(p), "unknown comparison operator");
                };
                yield elaborateBinaryOp(p, p.addExpr(0), p.addExpr(1), op, ctx);
            }
            case PiescriptAntlrParser.AddPassthroughContext p -> elaborate(p.mulExpr(), ctx);
            case PiescriptAntlrParser.AdditiveOpContext p -> {
                var op = p.op.getType() == PiescriptAntlrParser.PLUS ? Op.ADD : Op.SUB;
                yield elaborateBinaryOp(p, p.addExpr(), p.mulExpr(), op, ctx);
            }
            case PiescriptAntlrParser.MulPassthroughContext p -> elaborate(p.unaryExpr(), ctx);
            case PiescriptAntlrParser.MultiplicativeOpContext p -> {
                var op = switch (p.op.getType()) {
                    case PiescriptAntlrParser.ASTERISK -> Op.MUL;
                    case PiescriptAntlrParser.SLASH -> Op.DIV;
                    case PiescriptAntlrParser.PERCENT -> Op.MOD;
                    default -> throw error(source(p), "unknown multiplicative operator");
                };
                yield elaborateBinaryOp(p, p.mulExpr(), p.unaryExpr(), op, ctx);
            }

            case PiescriptAntlrParser.UnaryPassthroughContext p -> elaborate(p.appExpr(), ctx);
            case PiescriptAntlrParser.UnaryOpContext p -> {
                var operand = elaborate(p.unaryExpr(), ctx);
                var s = source(p);
                if (p.op.getType() == PiescriptAntlrParser.BANG) {
                    unifyOrThrow(operand.type(), BOOLEAN, s);
                    yield new CorePrimOp(s.source, Op.NOT, List.of(operand), BOOLEAN);
                } else {
                    unifyOrThrow(operand.type(), INTEGER, s);
                    yield new CorePrimOp(s.source, Op.NEG, List.of(operand), INTEGER);
                }
            }

            case PiescriptAntlrParser.AppPassthroughContext p -> elaborate(p.primary(), ctx);
            case PiescriptAntlrParser.ApplicationContext p -> {
                var fn = elaborate(p.appExpr(), ctx);
                var arg = elaborate(p.primary(), ctx);
                var s = source(p);
                var fnType = state.resolveType(fn.type());
                MonoType resultType;
                if (fnType instanceof MonoType.Arrow(var paramType, var retType)) {
                    unifyOrThrow(arg.type(), paramType, s);
                    resultType = retType;
                } else if (fnType instanceof MonoType.Meta) {
                    var alpha = state.freshType(ctx.bindingLevel());
                    var beta = state.freshType(ctx.bindingLevel());
                    unifyOrThrow(fnType, new MonoType.Arrow(alpha, beta), s);
                    unifyOrThrow(arg.type(), alpha, s);
                    resultType = beta;
                } else {
                    throw error(s, new TypeError.Mismatch(new MonoType.Arrow(arg.type(), NULL_TYPE), fnType), "not a function");
                }
                yield new CoreApp(s.source, fn, arg, resultType);
            }

            case PiescriptAntlrParser.VariableContext v -> elaborateVar(v, ctx);
            case PiescriptAntlrParser.IntegerLiteralContext lit -> elaborateIntegerLiteral(lit);
            case PiescriptAntlrParser.DecimalLiteralContext lit -> {
                var value = Double.parseDouble(lit.DECIMAL_LITERAL().getText());
                yield new CoreLit(source(lit).source, new LitVal.DoubleLit(value), DOUBLE);
            }
            case PiescriptAntlrParser.StringLiteralContext lit -> {
                var raw = lit.QUOTED_STRING().getText();
                var unescaped = unescapeString(raw.substring(1, raw.length() - 1));
                yield new CoreLit(source(lit).source, new LitVal.KeywordLit(new BytesRef(unescaped)), KEYWORD);
            }
            case PiescriptAntlrParser.TrueLiteralContext t -> new CoreLit(source(t).source, new LitVal.BooleanLit(true), BOOLEAN);
            case PiescriptAntlrParser.FalseLiteralContext f -> new CoreLit(source(f).source, new LitVal.BooleanLit(false), BOOLEAN);
            case PiescriptAntlrParser.NullLiteralContext n -> new CoreLit(source(n).source, new LitVal.NullLit(), NULL_TYPE);
            case PiescriptAntlrParser.ParenExprContext p -> elaborate(p.expr(), ctx);
            case PiescriptAntlrParser.AscriptionContext a -> {
                var annotatedType = resolveTypeAnnotation(a.type());
                var expr = elaborate(a.expr(), ctx);
                unifyOrThrow(expr.type(), annotatedType, source(a));
                yield expr;
            }

            case PiescriptAntlrParser.EmptyRecordContext e -> {
                var row = RowType.closed(Map.of());
                yield new CoreRecord(source(e).source, List.of(), List.of(), new MonoType.RecordType(row));
            }
            case PiescriptAntlrParser.RecordLiteralContext r -> elaborateRecord(r, ctx);
            case PiescriptAntlrParser.ProjectionContext p -> elaborateProjection(p, ctx);
            case PiescriptAntlrParser.AccessorContext a -> elaborateAccessor(a, ctx);
            case PiescriptAntlrParser.RecordUpdateExprContext u -> elaborateUpdate(u, ctx);
            case PiescriptAntlrParser.UpdateSugarContext u -> elaborateUpdateSugar(u, ctx);

            case PiescriptAntlrParser.BlockExprContext b -> elaborate(b.block(), ctx);
            case PiescriptAntlrParser.BlockContext b -> elaborateBlock(b, ctx);

            case PiescriptAntlrParser.IfExprContext e -> throw error(source(e), "if/then/else is not yet supported (Phase 1d)");

            default -> throw new ElaborationException(0, 0, "unexpected parse node: " + node.getClass().getSimpleName());
        };
    }

    // ──── Let ────

    private CoreExpr elaborateLet(PiescriptAntlrParser.LetExprContext let, ElaborationContext ctx) {
        var src = source(let);
        var name = let.IDENTIFIER().getText();
        var letCtx = ctx.enterBindingLevel();

        CoreExpr rhs;
        if (let.type() != null) {
            var annotatedType = resolveTypeAnnotation(let.type());
            rhs = elaborate(let.expr(0), letCtx);
            unifyOrThrow(rhs.type(), annotatedType, src);
        } else {
            rhs = elaborate(let.expr(0), letCtx);
        }

        var scheme = generalize(rhs.type(), letCtx.bindingLevel());
        var bodyCtx = ctx.bind(name, scheme);
        var body = elaborate(let.expr(1), bodyCtx);

        return new CoreLet(src.source, name, rhs.type(), rhs, body, body.type());
    }

    // ──── Lambda ────

    private CoreExpr elaborateLambda(PiescriptAntlrParser.LambdaExprContext lam, ElaborationContext ctx) {
        return elaborateLambdaParams(lam.param(), 0, lam.expr(), ctx, source(lam));
    }

    private CoreExpr elaborateLambdaParams(
        List<PiescriptAntlrParser.ParamContext> params,
        int index,
        PiescriptAntlrParser.ExprContext bodyExpr,
        ElaborationContext ctx,
        Src lamSource
    ) {
        if (index >= params.size()) {
            return elaborate(bodyExpr, ctx);
        }

        var param = params.get(index);
        String name;
        MonoType paramType;

        switch (param) {
            case PiescriptAntlrParser.UntypedParamContext u -> {
                name = u.IDENTIFIER().getText();
                paramType = state.freshType(ctx.bindingLevel());
            }
            case PiescriptAntlrParser.TypedParamContext t -> {
                name = t.IDENTIFIER().getText();
                paramType = resolveTypeAnnotation(t.type());
            }
            default -> throw error(source(param), "unexpected parameter form");
        }

        var innerCtx = ctx.bind(name, TypeScheme.mono(paramType));
        var body = elaborateLambdaParams(params, index + 1, bodyExpr, innerCtx, lamSource);
        var arrowType = new MonoType.Arrow(paramType, body.type());
        var nodeSrc = index == 0 ? lamSource : source(param);

        return new CoreLam(nodeSrc.source, name, paramType, body, arrowType);
    }

    // ──── Variable ────

    private CoreExpr elaborateVar(PiescriptAntlrParser.VariableContext v, ElaborationContext ctx) {
        var src = source(v);
        var name = v.IDENTIFIER().getText();
        var lookup = ctx.lookup(name).orElseThrow(() -> error(src, "unbound variable: " + name));
        var instantiated = instantiate(lookup.scheme(), ctx);
        return new CoreVar(src.source, lookup.index(), name, instantiated);
    }

    // ──── Integer literal ────

    private CoreExpr elaborateIntegerLiteral(PiescriptAntlrParser.IntegerLiteralContext lit) {
        var src = source(lit);
        var text = lit.INTEGER_LITERAL().getText();
        try {
            long wide = Long.parseLong(text);
            if (Integer.MIN_VALUE <= wide && wide <= Integer.MAX_VALUE) {
                return new CoreLit(src.source, new LitVal.IntegerLit((int) wide), INTEGER);
            }
            return new CoreLit(src.source, new LitVal.LongLit(wide), LONG);
        } catch (NumberFormatException e) {
            throw error(src, "integer literal out of range: " + text);
        }
    }

    // ──── Binary operators (PrimOp as typed function) ────

    private CoreExpr elaborateBinaryOp(ParserRuleContext node, ParseTree left, ParseTree right, Op op, ElaborationContext ctx) {
        var lhs = elaborate(left, ctx);
        var rhs = elaborate(right, ctx);
        var s = source(node);

        var sig = primOpSignature(op);
        unifyOrThrow(lhs.type(), sig.param, s);
        unifyOrThrow(rhs.type(), sig.param, s);
        return new CorePrimOp(s.source, op, List.of(lhs, rhs), sig.result);
    }

    private record PrimSig(MonoType param, MonoType result) {}

    /**
     * Concrete primop signatures. Phase 1: arithmetic is {@code Integer -> Integer -> Integer},
     * comparison is {@code Integer -> Integer -> Boolean}, boolean ops are
     * {@code Boolean -> Boolean -> Boolean}. Long/Double support deferred to coercion phase.
     */
    private static PrimSig primOpSignature(Op op) {
        return switch (op) {
            case ADD, SUB, MUL, DIV, MOD -> new PrimSig(INTEGER, INTEGER);
            case EQ, NEQ, LT, GT, LTE, GTE -> new PrimSig(INTEGER, BOOLEAN);
            case AND, OR -> new PrimSig(BOOLEAN, BOOLEAN);
            case NOT, NEG -> throw new IllegalStateException("unary ops should not use binary dispatch");
        };
    }

    // ──── Records ────

    private CoreExpr elaborateRecord(PiescriptAntlrParser.RecordLiteralContext r, ElaborationContext ctx) {
        var s = source(r);
        var labels = new ArrayList<String>();
        var values = new ArrayList<CoreExpr>();
        var fieldTypes = new LinkedHashMap<String, MonoType>();

        for (var field : r.recordField()) {
            var label = field.IDENTIFIER().getText();
            if (fieldTypes.containsKey(label)) {
                throw error(source(field), "duplicate field: " + label);
            }
            var value = elaborate(field.expr(), ctx);
            labels.add(label);
            values.add(value);
            fieldTypes.put(label, value.type());
        }

        var row = RowType.closed(fieldTypes);
        return new CoreRecord(s.source, labels, values, new MonoType.RecordType(row));
    }

    private CoreExpr elaborateProjection(PiescriptAntlrParser.ProjectionContext p, ElaborationContext ctx) {
        var s = source(p);
        var label = p.IDENTIFIER().getText();
        var expr = elaborate(p.primary(), ctx);
        var exprType = state.resolveType(expr.type());

        return switch (exprType) {
            case MonoType.RecordType(var row) -> {
                var fieldType = row.fields().get(label);
                if (fieldType == null) {
                    throw error(s, "record has no field '" + label + "'");
                }
                yield new CoreProject(s.source, expr, label, fieldType);
            }
            default -> throw error(s, "projection requires a record type, got " + exprType);
        };
    }

    /**
     * {@code .field} desugars to {@code fn $acc -> $acc.field}.
     */
    private CoreExpr elaborateAccessor(PiescriptAntlrParser.AccessorContext a, ElaborationContext ctx) {
        var s = source(a);
        var label = a.IDENTIFIER().getText();
        var resultType = state.freshType(ctx.bindingLevel());
        var paramType = new MonoType.RecordType(RowType.closed(Map.of(label, resultType)));

        var varExpr = new CoreVar(s.source, 0, "$acc", paramType);
        var project = new CoreProject(s.source, varExpr, label, resultType);
        return new CoreLam(s.source, "$acc", paramType, project, new MonoType.Arrow(paramType, resultType));
    }

    private CoreExpr elaborateUpdate(PiescriptAntlrParser.RecordUpdateExprContext u, ElaborationContext ctx) {
        var s = source(u);
        var baseExpr = elaborate(u.expr(), ctx);
        var baseType = state.resolveType(baseExpr.type());

        var existingFields = switch (baseType) {
            case MonoType.RecordType(var row) -> row.fields();
            default -> throw error(s, "record update requires a record type, got " + baseType);
        };

        var labels = new ArrayList<String>();
        var updateValues = new ArrayList<CoreExpr>();
        var newFields = new LinkedHashMap<>(existingFields);

        for (var update : u.recordUpdate()) {
            var label = update.IDENTIFIER().getText();
            var value = elaborate(update.expr(), ctx);
            labels.add(label);
            updateValues.add(value);
            newFields.put(label, value.type());
        }

        var children = new ArrayList<CoreExpr>();
        children.add(baseExpr);
        children.addAll(updateValues);
        return new CoreUpdate(s.source, labels, children, new MonoType.RecordType(RowType.closed(newFields)));
    }

    /**
     * {@code { _ | field = expr }} desugars to {@code fn $upd -> { $upd | field = expr }}.
     */
    private CoreExpr elaborateUpdateSugar(PiescriptAntlrParser.UpdateSugarContext u, ElaborationContext ctx) {
        var s = source(u);
        var paramFields = new LinkedHashMap<String, MonoType>();
        for (var update : u.recordUpdate()) {
            paramFields.put(update.IDENTIFIER().getText(), state.freshType(ctx.bindingLevel()));
        }
        var paramType = new MonoType.RecordType(RowType.closed(paramFields));
        var innerCtx = ctx.bind("$upd", TypeScheme.mono(paramType));

        var baseVar = new CoreVar(s.source, 0, "$upd", paramType);
        var labels = new ArrayList<String>();
        var updateValues = new ArrayList<CoreExpr>();
        var resultFields = new LinkedHashMap<String, MonoType>();

        for (var update : u.recordUpdate()) {
            var label = update.IDENTIFIER().getText();
            var value = elaborate(update.expr(), innerCtx);
            labels.add(label);
            updateValues.add(value);
            resultFields.put(label, value.type());
        }

        var resultType = new MonoType.RecordType(RowType.closed(resultFields));
        var children = new ArrayList<CoreExpr>();
        children.add(baseVar);
        children.addAll(updateValues);
        var updateExpr = new CoreUpdate(s.source, labels, children, resultType);
        return new CoreLam(s.source, "$upd", paramType, updateExpr, new MonoType.Arrow(paramType, resultType));
    }

    // ──── Blocks ────

    private CoreExpr elaborateBlock(PiescriptAntlrParser.BlockContext block, ElaborationContext ctx) {
        return elaborateBlockStmts(block.blockStmt(), 0, block.expr(), ctx);
    }

    private CoreExpr elaborateBlockStmts(
        List<PiescriptAntlrParser.BlockStmtContext> stmts,
        int index,
        PiescriptAntlrParser.ExprContext finalExpr,
        ElaborationContext ctx
    ) {
        if (index >= stmts.size()) {
            return elaborate(finalExpr, ctx);
        }

        var stmt = stmts.get(index);
        return switch (stmt) {
            case PiescriptAntlrParser.BlockLetContext let -> {
                var s = source(let);
                var name = let.IDENTIFIER().getText();
                var letCtx = ctx.enterBindingLevel();

                CoreExpr rhs;
                if (let.type() != null) {
                    var annotatedType = resolveTypeAnnotation(let.type());
                    rhs = elaborate(let.expr(), letCtx);
                    unifyOrThrow(rhs.type(), annotatedType, s);
                } else {
                    rhs = elaborate(let.expr(), letCtx);
                }

                var scheme = generalize(rhs.type(), letCtx.bindingLevel());
                var bodyCtx = ctx.bind(name, scheme);
                var body = elaborateBlockStmts(stmts, index + 1, finalExpr, bodyCtx);
                yield new CoreLet(s.source, name, rhs.type(), rhs, body, body.type());
            }
            case PiescriptAntlrParser.BlockExprStmtContext exprStmt -> {
                elaborate(exprStmt.expr(), ctx);
                yield elaborateBlockStmts(stmts, index + 1, finalExpr, ctx);
            }
            default -> throw error(source(stmt), "unexpected block statement");
        };
    }

    // ──── Type annotations ────

    private MonoType resolveTypeAnnotation(PiescriptAntlrParser.TypeContext typeCtx) {
        return switch (typeCtx) {
            case PiescriptAntlrParser.FunctionTypeContext fn -> new MonoType.Arrow(
                resolveTypePrimary(fn.typePrimary()),
                resolveTypeAnnotation(fn.type())
            );
            case PiescriptAntlrParser.TypeAtomContext atom -> resolveTypePrimary(atom.typePrimary());
            default -> throw error(source(typeCtx), "unexpected type syntax");
        };
    }

    private MonoType resolveTypePrimary(PiescriptAntlrParser.TypePrimaryContext primary) {
        return switch (primary) {
            case PiescriptAntlrParser.TypeConContext tc -> {
                var name = tc.IDENTIFIER().getText();
                var type = KNOWN_TYPES.get(name);
                if (type == null) {
                    throw error(source(tc), "unknown type: " + name);
                }
                yield type;
            }
            case PiescriptAntlrParser.RecordTypeContext rt -> {
                var fields = new LinkedHashMap<String, MonoType>();
                for (var field : rt.rowType().rowField()) {
                    fields.put(field.IDENTIFIER().getText(), resolveTypeAnnotation(field.type()));
                }
                yield new MonoType.RecordType(RowType.closed(fields));
            }
            case PiescriptAntlrParser.ParenTypeContext pt -> resolveTypeAnnotation(pt.type());
            default -> throw error(source(primary), "unexpected type syntax");
        };
    }

    // ──── Type-level operations (delegated to TypeWalker) ────

    private TypeScheme generalize(MonoType type, int bindingLevel) {
        return TypeWalker.generalize(type, bindingLevel, state);
    }

    private MonoType instantiate(TypeScheme scheme, ElaborationContext ctx) {
        return TypeWalker.instantiate(scheme, ctx.bindingLevel(), state);
    }

    private MonoType resolveDeep(MonoType type) {
        return TypeWalker.resolveDeep(type, state);
    }

    // ──── Unification + error helpers ────

    private void unifyOrThrow(MonoType a, MonoType b, Src src) {
        Unifier.unify(a, b, state).ifPresent(err -> { throw error(src, err, formatTypeError(err)); });
    }

    private static String formatTypeError(TypeError err) {
        return switch (err) {
            case TypeError.Mismatch(var expected, var actual) -> "type mismatch: expected " + expected + ", got " + actual;
            case TypeError.InfiniteType(var meta, var type) -> "infinite type: " + meta + " occurs in " + type;
            case TypeError.FieldMismatch(var label, var cause) -> "field '" + label + "': " + formatTypeError(cause);
            case TypeError.MissingFields(var missing, var inType) -> "missing fields " + missing + " in " + inType;
        };
    }

    private static ElaborationException error(Src src, TypeError typeError, String message) {
        return new ElaborationException(src.line, src.column, typeError, message);
    }

    private static ElaborationException error(Src src, String message) {
        return new ElaborationException(src.line, src.column, message);
    }

    // ──── Source extraction ────

    private static Src source(ParserRuleContext ctx) {
        Token start = ctx.getStart();
        Token stop = ctx.getStop() != null ? ctx.getStop() : start;
        String text = start.getInputStream().getText(new Interval(start.getStartIndex(), stop.getStopIndex()));
        return new Src(
            new Source(new Location(start.getLine(), start.getCharPositionInLine()), text),
            start.getLine(),
            start.getCharPositionInLine() + 1
        );
    }

    // ──── String unescaping ────

    private static String unescapeString(String raw) {
        var sb = new StringBuilder(raw.length());
        for (int i = 0; i < raw.length(); i++) {
            char c = raw.charAt(i);
            if (c == '\\' && i + 1 < raw.length()) {
                char next = raw.charAt(++i);
                switch (next) {
                    case 'n' -> sb.append('\n');
                    case 'r' -> sb.append('\r');
                    case 't' -> sb.append('\t');
                    case '\\' -> sb.append('\\');
                    case '"' -> sb.append('"');
                    default -> {
                        sb.append('\\');
                        sb.append(next);
                    }
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
