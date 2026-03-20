// Generated from /Users/t.vilaverde/Workspace/Elastic/elasticsearch/x-pack/plugin/piescript/src/main/antlr/PiescriptAntlrParser.g4 by ANTLR 4.13.1
package org.elasticsearch.xpack.piescript.parser;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link PiescriptAntlrParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface PiescriptAntlrParserVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link PiescriptAntlrParser#ident}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitIdent(PiescriptAntlrParser.IdentContext ctx);

    /**
     * Visit a parse tree produced by {@link PiescriptAntlrParser#program}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitProgram(PiescriptAntlrParser.ProgramContext ctx);

    /**
     * Visit a parse tree produced by the {@code TopLet}
     * labeled alternative in {@link PiescriptAntlrParser#topBinding}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitTopLet(PiescriptAntlrParser.TopLetContext ctx);

    /**
     * Visit a parse tree produced by the {@code TopUse}
     * labeled alternative in {@link PiescriptAntlrParser#topBinding}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitTopUse(PiescriptAntlrParser.TopUseContext ctx);

    /**
     * Visit a parse tree produced by the {@code LetExpr}
     * labeled alternative in {@link PiescriptAntlrParser#expr}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLetExpr(PiescriptAntlrParser.LetExprContext ctx);

    /**
     * Visit a parse tree produced by the {@code LambdaExpr}
     * labeled alternative in {@link PiescriptAntlrParser#expr}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLambdaExpr(PiescriptAntlrParser.LambdaExprContext ctx);

    /**
     * Visit a parse tree produced by the {@code SpawnBangExpr}
     * labeled alternative in {@link PiescriptAntlrParser#expr}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSpawnBangExpr(PiescriptAntlrParser.SpawnBangExprContext ctx);

    /**
     * Visit a parse tree produced by the {@code SpawnExpr}
     * labeled alternative in {@link PiescriptAntlrParser#expr}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSpawnExpr(PiescriptAntlrParser.SpawnExprContext ctx);

    /**
     * Visit a parse tree produced by the {@code SendExpr}
     * labeled alternative in {@link PiescriptAntlrParser#expr}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSendExpr(PiescriptAntlrParser.SendExprContext ctx);

    /**
     * Visit a parse tree produced by the {@code WhenExpr}
     * labeled alternative in {@link PiescriptAntlrParser#expr}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitWhenExpr(PiescriptAntlrParser.WhenExprContext ctx);

    /**
     * Visit a parse tree produced by the {@code ExprPipe}
     * labeled alternative in {@link PiescriptAntlrParser#expr}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitExprPipe(PiescriptAntlrParser.ExprPipeContext ctx);

    /**
     * Visit a parse tree produced by {@link PiescriptAntlrParser#whenBinding}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitWhenBinding(PiescriptAntlrParser.WhenBindingContext ctx);

    /**
     * Visit a parse tree produced by the {@code PipeOp}
     * labeled alternative in {@link PiescriptAntlrParser#pipeExpr}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitPipeOp(PiescriptAntlrParser.PipeOpContext ctx);

    /**
     * Visit a parse tree produced by the {@code PipePassthrough}
     * labeled alternative in {@link PiescriptAntlrParser#pipeExpr}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitPipePassthrough(PiescriptAntlrParser.PipePassthroughContext ctx);

    /**
     * Visit a parse tree produced by the {@code OrPassthrough}
     * labeled alternative in {@link PiescriptAntlrParser#orExpr}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOrPassthrough(PiescriptAntlrParser.OrPassthroughContext ctx);

    /**
     * Visit a parse tree produced by the {@code OrOp}
     * labeled alternative in {@link PiescriptAntlrParser#orExpr}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOrOp(PiescriptAntlrParser.OrOpContext ctx);

    /**
     * Visit a parse tree produced by the {@code AndPassthrough}
     * labeled alternative in {@link PiescriptAntlrParser#andExpr}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitAndPassthrough(PiescriptAntlrParser.AndPassthroughContext ctx);

    /**
     * Visit a parse tree produced by the {@code AndOp}
     * labeled alternative in {@link PiescriptAntlrParser#andExpr}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitAndOp(PiescriptAntlrParser.AndOpContext ctx);

    /**
     * Visit a parse tree produced by the {@code EqualityOp}
     * labeled alternative in {@link PiescriptAntlrParser#eqExpr}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEqualityOp(PiescriptAntlrParser.EqualityOpContext ctx);

    /**
     * Visit a parse tree produced by the {@code EqPassthrough}
     * labeled alternative in {@link PiescriptAntlrParser#eqExpr}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEqPassthrough(PiescriptAntlrParser.EqPassthroughContext ctx);

    /**
     * Visit a parse tree produced by the {@code ComparisonOp}
     * labeled alternative in {@link PiescriptAntlrParser#cmpExpr}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitComparisonOp(PiescriptAntlrParser.ComparisonOpContext ctx);

    /**
     * Visit a parse tree produced by the {@code CmpPassthrough}
     * labeled alternative in {@link PiescriptAntlrParser#cmpExpr}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCmpPassthrough(PiescriptAntlrParser.CmpPassthroughContext ctx);

    /**
     * Visit a parse tree produced by the {@code AdditiveOp}
     * labeled alternative in {@link PiescriptAntlrParser#addExpr}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitAdditiveOp(PiescriptAntlrParser.AdditiveOpContext ctx);

    /**
     * Visit a parse tree produced by the {@code AddPassthrough}
     * labeled alternative in {@link PiescriptAntlrParser#addExpr}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitAddPassthrough(PiescriptAntlrParser.AddPassthroughContext ctx);

    /**
     * Visit a parse tree produced by the {@code MultiplicativeOp}
     * labeled alternative in {@link PiescriptAntlrParser#mulExpr}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMultiplicativeOp(PiescriptAntlrParser.MultiplicativeOpContext ctx);

    /**
     * Visit a parse tree produced by the {@code MulPassthrough}
     * labeled alternative in {@link PiescriptAntlrParser#mulExpr}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMulPassthrough(PiescriptAntlrParser.MulPassthroughContext ctx);

    /**
     * Visit a parse tree produced by the {@code UnaryOp}
     * labeled alternative in {@link PiescriptAntlrParser#unaryExpr}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitUnaryOp(PiescriptAntlrParser.UnaryOpContext ctx);

    /**
     * Visit a parse tree produced by the {@code UnaryPassthrough}
     * labeled alternative in {@link PiescriptAntlrParser#unaryExpr}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitUnaryPassthrough(PiescriptAntlrParser.UnaryPassthroughContext ctx);

    /**
     * Visit a parse tree produced by the {@code AppPassthrough}
     * labeled alternative in {@link PiescriptAntlrParser#appExpr}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitAppPassthrough(PiescriptAntlrParser.AppPassthroughContext ctx);

    /**
     * Visit a parse tree produced by the {@code Application}
     * labeled alternative in {@link PiescriptAntlrParser#appExpr}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitApplication(PiescriptAntlrParser.ApplicationContext ctx);

    /**
     * Visit a parse tree produced by the {@code EmptyRecord}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEmptyRecord(PiescriptAntlrParser.EmptyRecordContext ctx);

    /**
     * Visit a parse tree produced by the {@code RecordLiteral}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRecordLiteral(PiescriptAntlrParser.RecordLiteralContext ctx);

    /**
     * Visit a parse tree produced by the {@code IfExpr}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitIfExpr(PiescriptAntlrParser.IfExprContext ctx);

    /**
     * Visit a parse tree produced by the {@code Variable}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitVariable(PiescriptAntlrParser.VariableContext ctx);

    /**
     * Visit a parse tree produced by the {@code QueryExpr}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitQueryExpr(PiescriptAntlrParser.QueryExprContext ctx);

    /**
     * Visit a parse tree produced by the {@code Projection}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitProjection(PiescriptAntlrParser.ProjectionContext ctx);

    /**
     * Visit a parse tree produced by the {@code Accessor}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitAccessor(PiescriptAntlrParser.AccessorContext ctx);

    /**
     * Visit a parse tree produced by the {@code BlockExpr}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBlockExpr(PiescriptAntlrParser.BlockExprContext ctx);

    /**
     * Visit a parse tree produced by the {@code FalseLiteral}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFalseLiteral(PiescriptAntlrParser.FalseLiteralContext ctx);

    /**
     * Visit a parse tree produced by the {@code StringLiteral}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitStringLiteral(PiescriptAntlrParser.StringLiteralContext ctx);

    /**
     * Visit a parse tree produced by the {@code TrueLiteral}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitTrueLiteral(PiescriptAntlrParser.TrueLiteralContext ctx);

    /**
     * Visit a parse tree produced by the {@code Ascription}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitAscription(PiescriptAntlrParser.AscriptionContext ctx);

    /**
     * Visit a parse tree produced by the {@code DecimalLiteral}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDecimalLiteral(PiescriptAntlrParser.DecimalLiteralContext ctx);

    /**
     * Visit a parse tree produced by the {@code RecordUpdateExpr}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRecordUpdateExpr(PiescriptAntlrParser.RecordUpdateExprContext ctx);

    /**
     * Visit a parse tree produced by the {@code ParenExpr}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitParenExpr(PiescriptAntlrParser.ParenExprContext ctx);

    /**
     * Visit a parse tree produced by the {@code NullLiteral}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitNullLiteral(PiescriptAntlrParser.NullLiteralContext ctx);

    /**
     * Visit a parse tree produced by the {@code IntegerLiteral}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitIntegerLiteral(PiescriptAntlrParser.IntegerLiteralContext ctx);

    /**
     * Visit a parse tree produced by the {@code UpdateSugar}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitUpdateSugar(PiescriptAntlrParser.UpdateSugarContext ctx);

    /**
     * Visit a parse tree produced by {@link PiescriptAntlrParser#recordField}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRecordField(PiescriptAntlrParser.RecordFieldContext ctx);

    /**
     * Visit a parse tree produced by {@link PiescriptAntlrParser#recordUpdate}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRecordUpdate(PiescriptAntlrParser.RecordUpdateContext ctx);

    /**
     * Visit a parse tree produced by {@link PiescriptAntlrParser#block}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBlock(PiescriptAntlrParser.BlockContext ctx);

    /**
     * Visit a parse tree produced by the {@code BlockLet}
     * labeled alternative in {@link PiescriptAntlrParser#blockStmt}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBlockLet(PiescriptAntlrParser.BlockLetContext ctx);

    /**
     * Visit a parse tree produced by the {@code BlockExprStmt}
     * labeled alternative in {@link PiescriptAntlrParser#blockStmt}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBlockExprStmt(PiescriptAntlrParser.BlockExprStmtContext ctx);

    /**
     * Visit a parse tree produced by the {@code UntypedParam}
     * labeled alternative in {@link PiescriptAntlrParser#param}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitUntypedParam(PiescriptAntlrParser.UntypedParamContext ctx);

    /**
     * Visit a parse tree produced by the {@code TypedParam}
     * labeled alternative in {@link PiescriptAntlrParser#param}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitTypedParam(PiescriptAntlrParser.TypedParamContext ctx);

    /**
     * Visit a parse tree produced by the {@code FunctionType}
     * labeled alternative in {@link PiescriptAntlrParser#type}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFunctionType(PiescriptAntlrParser.FunctionTypeContext ctx);

    /**
     * Visit a parse tree produced by the {@code TypeNonArrow}
     * labeled alternative in {@link PiescriptAntlrParser#type}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitTypeNonArrow(PiescriptAntlrParser.TypeNonArrowContext ctx);

    /**
     * Visit a parse tree produced by the {@code TypeAppPassthrough}
     * labeled alternative in {@link PiescriptAntlrParser#typeApp}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitTypeAppPassthrough(PiescriptAntlrParser.TypeAppPassthroughContext ctx);

    /**
     * Visit a parse tree produced by the {@code TypeApplication}
     * labeled alternative in {@link PiescriptAntlrParser#typeApp}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitTypeApplication(PiescriptAntlrParser.TypeApplicationContext ctx);

    /**
     * Visit a parse tree produced by the {@code TypeCon}
     * labeled alternative in {@link PiescriptAntlrParser#typeAtom}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitTypeCon(PiescriptAntlrParser.TypeConContext ctx);

    /**
     * Visit a parse tree produced by the {@code TypeVar}
     * labeled alternative in {@link PiescriptAntlrParser#typeAtom}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitTypeVar(PiescriptAntlrParser.TypeVarContext ctx);

    /**
     * Visit a parse tree produced by the {@code RecordType}
     * labeled alternative in {@link PiescriptAntlrParser#typeAtom}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRecordType(PiescriptAntlrParser.RecordTypeContext ctx);

    /**
     * Visit a parse tree produced by the {@code ParenType}
     * labeled alternative in {@link PiescriptAntlrParser#typeAtom}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitParenType(PiescriptAntlrParser.ParenTypeContext ctx);

    /**
     * Visit a parse tree produced by {@link PiescriptAntlrParser#rowType}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRowType(PiescriptAntlrParser.RowTypeContext ctx);

    /**
     * Visit a parse tree produced by {@link PiescriptAntlrParser#rowField}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRowField(PiescriptAntlrParser.RowFieldContext ctx);
}
