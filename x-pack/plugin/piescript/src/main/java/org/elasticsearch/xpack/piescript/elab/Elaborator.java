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
import org.elasticsearch.xpack.piescript.core.CoreExpr;
import org.elasticsearch.xpack.piescript.core.CoreFree;
import org.elasticsearch.xpack.piescript.core.CoreLit;
import org.elasticsearch.xpack.piescript.core.CorePrimOp;
import org.elasticsearch.xpack.piescript.core.CoreRecord;
import org.elasticsearch.xpack.piescript.core.CoreVar;
import org.elasticsearch.xpack.piescript.parser.PiescriptAntlrParser;
import org.elasticsearch.xpack.piescript.types.LitVal;
import org.elasticsearch.xpack.piescript.types.MonoType;
import org.elasticsearch.xpack.piescript.types.Op;
import org.elasticsearch.xpack.piescript.types.RowType;
import org.elasticsearch.xpack.piescript.types.TypeScheme;

import java.util.List;
import java.util.Map;

/**
 * Bidirectional type-checker and desugarer. Walks the ANTLR parse tree via
 * recursive descent (not the generated visitor), producing fully-typed
 * {@link CoreExpr} nodes. Carries an immutable {@link ElaborationContext}
 * as a parameter and a shared mutable {@link ElaborationState}.
 *
 * <p>Elaboration of individual CST forms is split across handler classes in
 * the same package: {@link Let}, {@link Lambda}, {@link Applications},
 * {@link Operators}, {@link Records}, {@link Blocks}, {@link TypeAnnotations}. Polymorphism
 * machinery (generalization, instantiation, ∀-CHECK) lives in
 * {@link Polymorphism}. This class holds the main dispatch, shared
 * infrastructure, and small inline cases.
 *
 * <p>Current limitations:
 * <ul>
 *   <li>No if/then/else — deferred to Phase 1e (pattern matching).</li>
 * </ul>
 */
public final class Elaborator {

    static final MonoType INTEGER = new MonoType.TCon("Integer");
    static final MonoType LONG = new MonoType.TCon("Long");
    static final MonoType DOUBLE = new MonoType.TCon("Double");
    static final MonoType KEYWORD = new MonoType.TCon("Keyword");
    static final MonoType BOOLEAN = new MonoType.TCon("Boolean");
    static final MonoType NULL_TYPE = new MonoType.TCon("Null");

    static final MonoType LIST = new MonoType.TCon("List");
    static final MonoType CHANNEL = new MonoType.TCon("Channel");
    static final MonoType DATETIME = new MonoType.TCon("DateTime");
    static final MonoType UNSIGNED_LONG = new MonoType.TCon("UnsignedLong");
    static final MonoType IP = new MonoType.TCon("Ip");
    static final MonoType VERSION = new MonoType.TCon("Version");
    static final MonoType GEO_POINT = new MonoType.TCon("GeoPoint");
    static final MonoType CARTESIAN_POINT = new MonoType.TCon("CartesianPoint");
    static final MonoType GEO_SHAPE = new MonoType.TCon("GeoShape");
    static final MonoType CARTESIAN_SHAPE = new MonoType.TCon("CartesianShape");
    static final MonoType UNSUPPORTED = new MonoType.TCon("Unsupported");

    static final Map<String, MonoType> KNOWN_TYPES = Map.ofEntries(
        Map.entry("Integer", INTEGER),
        Map.entry("Long", LONG),
        Map.entry("Double", DOUBLE),
        Map.entry("Keyword", KEYWORD),
        Map.entry("Boolean", BOOLEAN),
        Map.entry("Null", NULL_TYPE),
        Map.entry("DateTime", DATETIME),
        Map.entry("UnsignedLong", UNSIGNED_LONG),
        Map.entry("Ip", IP),
        Map.entry("Version", VERSION),
        Map.entry("GeoPoint", GEO_POINT),
        Map.entry("CartesianPoint", CARTESIAN_POINT),
        Map.entry("GeoShape", GEO_SHAPE),
        Map.entry("CartesianShape", CARTESIAN_SHAPE),
        Map.entry("Unsupported", UNSUPPORTED),
        Map.entry("List", LIST),
        Map.entry("Channel", CHANNEL)
    );

    final ElaborationState state;
    private int constraintsSolvedUpTo;

    public Elaborator(ElaborationState state) {
        this.state = state;
        this.constraintsSolvedUpTo = 0;
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
        var ctx = ElaborationContext.withModule(Prelude.MODULE);
        var topBindings = program.topBinding();
        var finalExpr = program.expr();

        CoreExpr result;
        if (topBindings.isEmpty()) {
            result = elaborate(finalExpr, ctx);
        } else {
            result = Let.topBindings(this, topBindings, 0, finalExpr, ctx);
        }

        solveConstraints();
        return result;
    }

    // ──── Main dispatch ────

    /**
     * Checking mode (D-036): elaborate {@code node} against an expected {@link TypeScheme}.
     *
     * <p>If the scheme has quantifiers (a universal type), delegates to
     * {@link Polymorphism#forallCheck} which introduces {@code CoreTypeAbs} nodes
     * for each quantified variable and recursively checks against the monomorphic
     * body. This means form-specific check handlers (e.g., {@link Lambda#check})
     * never see polymorphic types — they receive a concrete monotype.
     *
     * <p>For monomorphic schemes, dispatches to form-specific checking rules where
     * available (lambda against arrow), otherwise falls back to synthesize + constrain.
     */
    CoreExpr check(ParseTree node, TypeScheme expected, ElaborationContext ctx, Src src) {
        if (expected.quantified().isEmpty() == false) {
            return Polymorphism.forallCheck(this, node, expected, ctx, src);
        }

        return switch (node) {
            case PiescriptAntlrParser.LambdaExprContext lam -> Lambda.check(this, lam, expected.body(), ctx);
            case PiescriptAntlrParser.RecordLiteralContext r -> Records.check(this, r, expected.body(), ctx);
            case PiescriptAntlrParser.LetExprContext let -> Let.checkLet(this, let, expected.body(), ctx);
            case PiescriptAntlrParser.BlockExprContext b -> check(b.block(), expected, ctx, src);
            case PiescriptAntlrParser.BlockContext b -> Blocks.checkBlock(this, b, expected.body(), ctx);
            default -> {
                CoreExpr result = elaborate(node, ctx);
                emitConstraint(result.type(), expected.body(), src);
                yield result;
            }
        };
    }

    CoreExpr elaborate(ParseTree node, ElaborationContext ctx) {
        return switch (node) {
            case PiescriptAntlrParser.LetExprContext let -> Let.let(this, let, ctx);
            case PiescriptAntlrParser.LambdaExprContext lam -> Lambda.infer(this, lam, ctx);
            case PiescriptAntlrParser.ExprPipeContext e -> elaborate(e.pipeExpr(), ctx);

            case PiescriptAntlrParser.PipePassthroughContext p -> elaborate(p.orExpr(), ctx);
            case PiescriptAntlrParser.PipeOpContext p -> Applications.pipe(this, p, ctx);

            case PiescriptAntlrParser.OrPassthroughContext p -> elaborate(p.andExpr(), ctx);
            case PiescriptAntlrParser.OrOpContext p -> Operators.binary(this, p, p.orExpr(), p.andExpr(), Op.OR, ctx);
            case PiescriptAntlrParser.AndPassthroughContext p -> elaborate(p.eqExpr(), ctx);
            case PiescriptAntlrParser.AndOpContext p -> Operators.binary(this, p, p.andExpr(), p.eqExpr(), Op.AND, ctx);
            case PiescriptAntlrParser.EqPassthroughContext p -> elaborate(p.cmpExpr(), ctx);
            case PiescriptAntlrParser.EqualityOpContext p -> {
                var op = p.op.getType() == PiescriptAntlrParser.EQ ? Op.EQ : Op.NEQ;
                yield Operators.binary(this, p, p.cmpExpr(0), p.cmpExpr(1), op, ctx);
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
                yield Operators.binary(this, p, p.addExpr(0), p.addExpr(1), op, ctx);
            }
            case PiescriptAntlrParser.AddPassthroughContext p -> elaborate(p.mulExpr(), ctx);
            case PiescriptAntlrParser.AdditiveOpContext p -> {
                var op = p.op.getType() == PiescriptAntlrParser.PLUS ? Op.ADD : Op.SUB;
                yield Operators.binary(this, p, p.addExpr(), p.mulExpr(), op, ctx);
            }
            case PiescriptAntlrParser.MulPassthroughContext p -> elaborate(p.unaryExpr(), ctx);
            case PiescriptAntlrParser.MultiplicativeOpContext p -> {
                var op = switch (p.op.getType()) {
                    case PiescriptAntlrParser.ASTERISK -> Op.MUL;
                    case PiescriptAntlrParser.SLASH -> Op.DIV;
                    case PiescriptAntlrParser.PERCENT -> Op.MOD;
                    default -> throw error(source(p), "unknown multiplicative operator");
                };
                yield Operators.binary(this, p, p.mulExpr(), p.unaryExpr(), op, ctx);
            }

            case PiescriptAntlrParser.UnaryPassthroughContext p -> elaborate(p.appExpr(), ctx);
            case PiescriptAntlrParser.UnaryOpContext p -> {
                var operand = elaborate(p.unaryExpr(), ctx);
                var s = source(p);
                if (p.op.getType() == PiescriptAntlrParser.BANG) {
                    emitConstraint(operand.type(), BOOLEAN, s);
                    yield new CorePrimOp(s.source, Op.NOT, List.of(operand), BOOLEAN);
                } else {
                    emitConstraint(operand.type(), DOUBLE, s);
                    yield new CorePrimOp(s.source, Op.NEG, List.of(operand), DOUBLE);
                }
            }

            case PiescriptAntlrParser.AppPassthroughContext p -> elaborate(p.primary(), ctx);
            case PiescriptAntlrParser.ApplicationContext p -> Applications.application(this, p, ctx);

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
                var scheme = TypeAnnotations.toTypeScheme(this, a.type());
                yield check(a.expr(), scheme, ctx, source(a));
            }

            case PiescriptAntlrParser.EmptyRecordContext e -> {
                var row = RowType.closed(Map.of());
                yield new CoreRecord(source(e).source, List.of(), List.of(), new MonoType.RecordType(row));
            }
            case PiescriptAntlrParser.RecordLiteralContext r -> Records.record(this, r, ctx);
            case PiescriptAntlrParser.ProjectionContext p -> Records.projection(this, p, ctx);
            case PiescriptAntlrParser.AccessorContext a -> Records.accessor(this, a, ctx);
            case PiescriptAntlrParser.RecordUpdateExprContext u -> Records.update(this, u, ctx);
            case PiescriptAntlrParser.UpdateSugarContext u -> Records.updateSugar(this, u, ctx);

            case PiescriptAntlrParser.BlockExprContext b -> elaborate(b.block(), ctx);
            case PiescriptAntlrParser.BlockContext b -> Blocks.block(this, b, ctx);

            case PiescriptAntlrParser.IfExprContext e -> throw error(source(e), "if/then/else is not yet supported (Phase 1e)");
            case PiescriptAntlrParser.QueryExprContext q -> Queries.query(this, q, ctx);
            case PiescriptAntlrParser.SpawnExprContext s -> Spawns.spawn(this, s, ctx);
            case PiescriptAntlrParser.SpawnBangExprContext s -> Spawns.spawnBang(this, s, ctx);
            case PiescriptAntlrParser.SendExprContext s -> Sends.send(this, s, ctx);
            case PiescriptAntlrParser.WhenExprContext w -> Whens.when_(this, w, ctx);

            default -> throw new ElaborationException(0, 0, "unexpected parse node: " + node.getClass().getSimpleName());
        };
    }

    // ──── Variable ────

    private CoreExpr elaborateVar(PiescriptAntlrParser.VariableContext v, ElaborationContext ctx) {
        var src = source(v);
        var name = v.ident().getText();

        var localLookup = ctx.lookup(name);
        if (localLookup.isPresent()) {
            var lookup = localLookup.get();
            var scheme = lookup.scheme();
            if (scheme.quantified().isEmpty()) {
                return new CoreVar(src.source, lookup.index(), name, scheme.body());
            }
            return Polymorphism.instantiateAndWrap(
                this,
                type -> new CoreVar(src.source, lookup.index(), name, type),
                scheme,
                ctx,
                src.source
            );
        }

        var moduleLookup = ctx.lookupModule(name);
        if (moduleLookup.isPresent()) {
            var scheme = moduleLookup.get();
            if (scheme.quantified().isEmpty()) {
                return new CoreFree(src.source, name, scheme.body());
            }
            return Polymorphism.instantiateAndWrap(this, type -> new CoreFree(src.source, name, type), scheme, ctx, src.source);
        }

        throw error(src, "unbound variable: " + name);
    }

    // ──── Integer literal ────

    private CoreExpr elaborateIntegerLiteral(PiescriptAntlrParser.IntegerLiteralContext lit) {
        var src = source(lit);
        var text = lit.INTEGER_LITERAL().getText();
        try {
            double value = Double.parseDouble(text);
            return new CoreLit(src.source, new LitVal.DoubleLit(value), DOUBLE);
        } catch (NumberFormatException e) {
            throw error(src, "numeric literal out of range: " + text);
        }
    }

    // ──── Generalize / Instantiate (delegated to Polymorphism) ────

    TypeScheme generalize(MonoType type, int bindingLevel) {
        return Polymorphism.generalize(this, type, bindingLevel);
    }

    // ──── Constraint emission + solving ────

    void emitConstraint(MonoType a, MonoType b, Src src) {
        state.emitConstraint(a, b, src.line, src.column);
    }

    /**
     * Solve accumulated constraints by feeding them to the unifier. Tracks
     * progress so repeated calls only process newly emitted constraints.
     * Called before each generalization point and once at the end.
     */
    void solveConstraints() {
        var constraints = state.constraints();
        while (constraintsSolvedUpTo < constraints.size()) {
            var c = constraints.get(constraintsSolvedUpTo++);
            Unifier.unify(c.left(), c.right(), state)
                .ifPresent(err -> { throw new ElaborationException(c.line(), c.column(), formatTypeError(err)); });
        }
    }

    private static String formatTypeError(TypeError err) {
        return switch (err) {
            case TypeError.Mismatch(var expected, var actual) -> "type mismatch: expected " + expected + ", got " + actual;
            case TypeError.InfiniteType(var meta, var type) -> "infinite type: " + meta + " occurs in " + type;
            case TypeError.FieldMismatch(var label, var cause) -> "field '" + label + "': " + formatTypeError(cause);
            case TypeError.MissingFields(var missing, var inType) -> "missing fields " + missing + " in " + inType;
        };
    }

    static ElaborationException error(Src src, TypeError typeError, String message) {
        return new ElaborationException(src.line, src.column, typeError, message);
    }

    static ElaborationException error(Src src, String message) {
        return new ElaborationException(src.line, src.column, message);
    }

    // ──── Source extraction ────

    static Src source(ParserRuleContext ctx) {
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
