// Generated from /Users/t.vilaverde/Workspace/Elastic/elasticsearch/x-pack/plugin/piescript/src/main/antlr/PiescriptAntlrParser.g4 by ANTLR 4.13.1
package org.elasticsearch.xpack.piescript.parser;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link PiescriptAntlrParser}.
 */
public interface PiescriptAntlrParserListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link PiescriptAntlrParser#ident}.
     * @param ctx the parse tree
     */
    void enterIdent(PiescriptAntlrParser.IdentContext ctx);

    /**
     * Exit a parse tree produced by {@link PiescriptAntlrParser#ident}.
     * @param ctx the parse tree
     */
    void exitIdent(PiescriptAntlrParser.IdentContext ctx);

    /**
     * Enter a parse tree produced by {@link PiescriptAntlrParser#program}.
     * @param ctx the parse tree
     */
    void enterProgram(PiescriptAntlrParser.ProgramContext ctx);

    /**
     * Exit a parse tree produced by {@link PiescriptAntlrParser#program}.
     * @param ctx the parse tree
     */
    void exitProgram(PiescriptAntlrParser.ProgramContext ctx);

    /**
     * Enter a parse tree produced by the {@code TopLet}
     * labeled alternative in {@link PiescriptAntlrParser#topBinding}.
     * @param ctx the parse tree
     */
    void enterTopLet(PiescriptAntlrParser.TopLetContext ctx);

    /**
     * Exit a parse tree produced by the {@code TopLet}
     * labeled alternative in {@link PiescriptAntlrParser#topBinding}.
     * @param ctx the parse tree
     */
    void exitTopLet(PiescriptAntlrParser.TopLetContext ctx);

    /**
     * Enter a parse tree produced by the {@code TopUse}
     * labeled alternative in {@link PiescriptAntlrParser#topBinding}.
     * @param ctx the parse tree
     */
    void enterTopUse(PiescriptAntlrParser.TopUseContext ctx);

    /**
     * Exit a parse tree produced by the {@code TopUse}
     * labeled alternative in {@link PiescriptAntlrParser#topBinding}.
     * @param ctx the parse tree
     */
    void exitTopUse(PiescriptAntlrParser.TopUseContext ctx);

    /**
     * Enter a parse tree produced by the {@code LetExpr}
     * labeled alternative in {@link PiescriptAntlrParser#expr}.
     * @param ctx the parse tree
     */
    void enterLetExpr(PiescriptAntlrParser.LetExprContext ctx);

    /**
     * Exit a parse tree produced by the {@code LetExpr}
     * labeled alternative in {@link PiescriptAntlrParser#expr}.
     * @param ctx the parse tree
     */
    void exitLetExpr(PiescriptAntlrParser.LetExprContext ctx);

    /**
     * Enter a parse tree produced by the {@code LambdaExpr}
     * labeled alternative in {@link PiescriptAntlrParser#expr}.
     * @param ctx the parse tree
     */
    void enterLambdaExpr(PiescriptAntlrParser.LambdaExprContext ctx);

    /**
     * Exit a parse tree produced by the {@code LambdaExpr}
     * labeled alternative in {@link PiescriptAntlrParser#expr}.
     * @param ctx the parse tree
     */
    void exitLambdaExpr(PiescriptAntlrParser.LambdaExprContext ctx);

    /**
     * Enter a parse tree produced by the {@code SpawnBangExpr}
     * labeled alternative in {@link PiescriptAntlrParser#expr}.
     * @param ctx the parse tree
     */
    void enterSpawnBangExpr(PiescriptAntlrParser.SpawnBangExprContext ctx);

    /**
     * Exit a parse tree produced by the {@code SpawnBangExpr}
     * labeled alternative in {@link PiescriptAntlrParser#expr}.
     * @param ctx the parse tree
     */
    void exitSpawnBangExpr(PiescriptAntlrParser.SpawnBangExprContext ctx);

    /**
     * Enter a parse tree produced by the {@code SpawnExpr}
     * labeled alternative in {@link PiescriptAntlrParser#expr}.
     * @param ctx the parse tree
     */
    void enterSpawnExpr(PiescriptAntlrParser.SpawnExprContext ctx);

    /**
     * Exit a parse tree produced by the {@code SpawnExpr}
     * labeled alternative in {@link PiescriptAntlrParser#expr}.
     * @param ctx the parse tree
     */
    void exitSpawnExpr(PiescriptAntlrParser.SpawnExprContext ctx);

    /**
     * Enter a parse tree produced by the {@code SendExpr}
     * labeled alternative in {@link PiescriptAntlrParser#expr}.
     * @param ctx the parse tree
     */
    void enterSendExpr(PiescriptAntlrParser.SendExprContext ctx);

    /**
     * Exit a parse tree produced by the {@code SendExpr}
     * labeled alternative in {@link PiescriptAntlrParser#expr}.
     * @param ctx the parse tree
     */
    void exitSendExpr(PiescriptAntlrParser.SendExprContext ctx);

    /**
     * Enter a parse tree produced by the {@code WhenExpr}
     * labeled alternative in {@link PiescriptAntlrParser#expr}.
     * @param ctx the parse tree
     */
    void enterWhenExpr(PiescriptAntlrParser.WhenExprContext ctx);

    /**
     * Exit a parse tree produced by the {@code WhenExpr}
     * labeled alternative in {@link PiescriptAntlrParser#expr}.
     * @param ctx the parse tree
     */
    void exitWhenExpr(PiescriptAntlrParser.WhenExprContext ctx);

    /**
     * Enter a parse tree produced by the {@code ExprPipe}
     * labeled alternative in {@link PiescriptAntlrParser#expr}.
     * @param ctx the parse tree
     */
    void enterExprPipe(PiescriptAntlrParser.ExprPipeContext ctx);

    /**
     * Exit a parse tree produced by the {@code ExprPipe}
     * labeled alternative in {@link PiescriptAntlrParser#expr}.
     * @param ctx the parse tree
     */
    void exitExprPipe(PiescriptAntlrParser.ExprPipeContext ctx);

    /**
     * Enter a parse tree produced by {@link PiescriptAntlrParser#whenBinding}.
     * @param ctx the parse tree
     */
    void enterWhenBinding(PiescriptAntlrParser.WhenBindingContext ctx);

    /**
     * Exit a parse tree produced by {@link PiescriptAntlrParser#whenBinding}.
     * @param ctx the parse tree
     */
    void exitWhenBinding(PiescriptAntlrParser.WhenBindingContext ctx);

    /**
     * Enter a parse tree produced by the {@code PipeOp}
     * labeled alternative in {@link PiescriptAntlrParser#pipeExpr}.
     * @param ctx the parse tree
     */
    void enterPipeOp(PiescriptAntlrParser.PipeOpContext ctx);

    /**
     * Exit a parse tree produced by the {@code PipeOp}
     * labeled alternative in {@link PiescriptAntlrParser#pipeExpr}.
     * @param ctx the parse tree
     */
    void exitPipeOp(PiescriptAntlrParser.PipeOpContext ctx);

    /**
     * Enter a parse tree produced by the {@code PipePassthrough}
     * labeled alternative in {@link PiescriptAntlrParser#pipeExpr}.
     * @param ctx the parse tree
     */
    void enterPipePassthrough(PiescriptAntlrParser.PipePassthroughContext ctx);

    /**
     * Exit a parse tree produced by the {@code PipePassthrough}
     * labeled alternative in {@link PiescriptAntlrParser#pipeExpr}.
     * @param ctx the parse tree
     */
    void exitPipePassthrough(PiescriptAntlrParser.PipePassthroughContext ctx);

    /**
     * Enter a parse tree produced by the {@code OrPassthrough}
     * labeled alternative in {@link PiescriptAntlrParser#orExpr}.
     * @param ctx the parse tree
     */
    void enterOrPassthrough(PiescriptAntlrParser.OrPassthroughContext ctx);

    /**
     * Exit a parse tree produced by the {@code OrPassthrough}
     * labeled alternative in {@link PiescriptAntlrParser#orExpr}.
     * @param ctx the parse tree
     */
    void exitOrPassthrough(PiescriptAntlrParser.OrPassthroughContext ctx);

    /**
     * Enter a parse tree produced by the {@code OrOp}
     * labeled alternative in {@link PiescriptAntlrParser#orExpr}.
     * @param ctx the parse tree
     */
    void enterOrOp(PiescriptAntlrParser.OrOpContext ctx);

    /**
     * Exit a parse tree produced by the {@code OrOp}
     * labeled alternative in {@link PiescriptAntlrParser#orExpr}.
     * @param ctx the parse tree
     */
    void exitOrOp(PiescriptAntlrParser.OrOpContext ctx);

    /**
     * Enter a parse tree produced by the {@code AndPassthrough}
     * labeled alternative in {@link PiescriptAntlrParser#andExpr}.
     * @param ctx the parse tree
     */
    void enterAndPassthrough(PiescriptAntlrParser.AndPassthroughContext ctx);

    /**
     * Exit a parse tree produced by the {@code AndPassthrough}
     * labeled alternative in {@link PiescriptAntlrParser#andExpr}.
     * @param ctx the parse tree
     */
    void exitAndPassthrough(PiescriptAntlrParser.AndPassthroughContext ctx);

    /**
     * Enter a parse tree produced by the {@code AndOp}
     * labeled alternative in {@link PiescriptAntlrParser#andExpr}.
     * @param ctx the parse tree
     */
    void enterAndOp(PiescriptAntlrParser.AndOpContext ctx);

    /**
     * Exit a parse tree produced by the {@code AndOp}
     * labeled alternative in {@link PiescriptAntlrParser#andExpr}.
     * @param ctx the parse tree
     */
    void exitAndOp(PiescriptAntlrParser.AndOpContext ctx);

    /**
     * Enter a parse tree produced by the {@code EqualityOp}
     * labeled alternative in {@link PiescriptAntlrParser#eqExpr}.
     * @param ctx the parse tree
     */
    void enterEqualityOp(PiescriptAntlrParser.EqualityOpContext ctx);

    /**
     * Exit a parse tree produced by the {@code EqualityOp}
     * labeled alternative in {@link PiescriptAntlrParser#eqExpr}.
     * @param ctx the parse tree
     */
    void exitEqualityOp(PiescriptAntlrParser.EqualityOpContext ctx);

    /**
     * Enter a parse tree produced by the {@code EqPassthrough}
     * labeled alternative in {@link PiescriptAntlrParser#eqExpr}.
     * @param ctx the parse tree
     */
    void enterEqPassthrough(PiescriptAntlrParser.EqPassthroughContext ctx);

    /**
     * Exit a parse tree produced by the {@code EqPassthrough}
     * labeled alternative in {@link PiescriptAntlrParser#eqExpr}.
     * @param ctx the parse tree
     */
    void exitEqPassthrough(PiescriptAntlrParser.EqPassthroughContext ctx);

    /**
     * Enter a parse tree produced by the {@code ComparisonOp}
     * labeled alternative in {@link PiescriptAntlrParser#cmpExpr}.
     * @param ctx the parse tree
     */
    void enterComparisonOp(PiescriptAntlrParser.ComparisonOpContext ctx);

    /**
     * Exit a parse tree produced by the {@code ComparisonOp}
     * labeled alternative in {@link PiescriptAntlrParser#cmpExpr}.
     * @param ctx the parse tree
     */
    void exitComparisonOp(PiescriptAntlrParser.ComparisonOpContext ctx);

    /**
     * Enter a parse tree produced by the {@code CmpPassthrough}
     * labeled alternative in {@link PiescriptAntlrParser#cmpExpr}.
     * @param ctx the parse tree
     */
    void enterCmpPassthrough(PiescriptAntlrParser.CmpPassthroughContext ctx);

    /**
     * Exit a parse tree produced by the {@code CmpPassthrough}
     * labeled alternative in {@link PiescriptAntlrParser#cmpExpr}.
     * @param ctx the parse tree
     */
    void exitCmpPassthrough(PiescriptAntlrParser.CmpPassthroughContext ctx);

    /**
     * Enter a parse tree produced by the {@code AdditiveOp}
     * labeled alternative in {@link PiescriptAntlrParser#addExpr}.
     * @param ctx the parse tree
     */
    void enterAdditiveOp(PiescriptAntlrParser.AdditiveOpContext ctx);

    /**
     * Exit a parse tree produced by the {@code AdditiveOp}
     * labeled alternative in {@link PiescriptAntlrParser#addExpr}.
     * @param ctx the parse tree
     */
    void exitAdditiveOp(PiescriptAntlrParser.AdditiveOpContext ctx);

    /**
     * Enter a parse tree produced by the {@code AddPassthrough}
     * labeled alternative in {@link PiescriptAntlrParser#addExpr}.
     * @param ctx the parse tree
     */
    void enterAddPassthrough(PiescriptAntlrParser.AddPassthroughContext ctx);

    /**
     * Exit a parse tree produced by the {@code AddPassthrough}
     * labeled alternative in {@link PiescriptAntlrParser#addExpr}.
     * @param ctx the parse tree
     */
    void exitAddPassthrough(PiescriptAntlrParser.AddPassthroughContext ctx);

    /**
     * Enter a parse tree produced by the {@code MultiplicativeOp}
     * labeled alternative in {@link PiescriptAntlrParser#mulExpr}.
     * @param ctx the parse tree
     */
    void enterMultiplicativeOp(PiescriptAntlrParser.MultiplicativeOpContext ctx);

    /**
     * Exit a parse tree produced by the {@code MultiplicativeOp}
     * labeled alternative in {@link PiescriptAntlrParser#mulExpr}.
     * @param ctx the parse tree
     */
    void exitMultiplicativeOp(PiescriptAntlrParser.MultiplicativeOpContext ctx);

    /**
     * Enter a parse tree produced by the {@code MulPassthrough}
     * labeled alternative in {@link PiescriptAntlrParser#mulExpr}.
     * @param ctx the parse tree
     */
    void enterMulPassthrough(PiescriptAntlrParser.MulPassthroughContext ctx);

    /**
     * Exit a parse tree produced by the {@code MulPassthrough}
     * labeled alternative in {@link PiescriptAntlrParser#mulExpr}.
     * @param ctx the parse tree
     */
    void exitMulPassthrough(PiescriptAntlrParser.MulPassthroughContext ctx);

    /**
     * Enter a parse tree produced by the {@code UnaryOp}
     * labeled alternative in {@link PiescriptAntlrParser#unaryExpr}.
     * @param ctx the parse tree
     */
    void enterUnaryOp(PiescriptAntlrParser.UnaryOpContext ctx);

    /**
     * Exit a parse tree produced by the {@code UnaryOp}
     * labeled alternative in {@link PiescriptAntlrParser#unaryExpr}.
     * @param ctx the parse tree
     */
    void exitUnaryOp(PiescriptAntlrParser.UnaryOpContext ctx);

    /**
     * Enter a parse tree produced by the {@code UnaryPassthrough}
     * labeled alternative in {@link PiescriptAntlrParser#unaryExpr}.
     * @param ctx the parse tree
     */
    void enterUnaryPassthrough(PiescriptAntlrParser.UnaryPassthroughContext ctx);

    /**
     * Exit a parse tree produced by the {@code UnaryPassthrough}
     * labeled alternative in {@link PiescriptAntlrParser#unaryExpr}.
     * @param ctx the parse tree
     */
    void exitUnaryPassthrough(PiescriptAntlrParser.UnaryPassthroughContext ctx);

    /**
     * Enter a parse tree produced by the {@code AppPassthrough}
     * labeled alternative in {@link PiescriptAntlrParser#appExpr}.
     * @param ctx the parse tree
     */
    void enterAppPassthrough(PiescriptAntlrParser.AppPassthroughContext ctx);

    /**
     * Exit a parse tree produced by the {@code AppPassthrough}
     * labeled alternative in {@link PiescriptAntlrParser#appExpr}.
     * @param ctx the parse tree
     */
    void exitAppPassthrough(PiescriptAntlrParser.AppPassthroughContext ctx);

    /**
     * Enter a parse tree produced by the {@code Application}
     * labeled alternative in {@link PiescriptAntlrParser#appExpr}.
     * @param ctx the parse tree
     */
    void enterApplication(PiescriptAntlrParser.ApplicationContext ctx);

    /**
     * Exit a parse tree produced by the {@code Application}
     * labeled alternative in {@link PiescriptAntlrParser#appExpr}.
     * @param ctx the parse tree
     */
    void exitApplication(PiescriptAntlrParser.ApplicationContext ctx);

    /**
     * Enter a parse tree produced by the {@code EmptyRecord}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     */
    void enterEmptyRecord(PiescriptAntlrParser.EmptyRecordContext ctx);

    /**
     * Exit a parse tree produced by the {@code EmptyRecord}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     */
    void exitEmptyRecord(PiescriptAntlrParser.EmptyRecordContext ctx);

    /**
     * Enter a parse tree produced by the {@code RecordLiteral}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     */
    void enterRecordLiteral(PiescriptAntlrParser.RecordLiteralContext ctx);

    /**
     * Exit a parse tree produced by the {@code RecordLiteral}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     */
    void exitRecordLiteral(PiescriptAntlrParser.RecordLiteralContext ctx);

    /**
     * Enter a parse tree produced by the {@code IfExpr}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     */
    void enterIfExpr(PiescriptAntlrParser.IfExprContext ctx);

    /**
     * Exit a parse tree produced by the {@code IfExpr}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     */
    void exitIfExpr(PiescriptAntlrParser.IfExprContext ctx);

    /**
     * Enter a parse tree produced by the {@code Variable}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     */
    void enterVariable(PiescriptAntlrParser.VariableContext ctx);

    /**
     * Exit a parse tree produced by the {@code Variable}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     */
    void exitVariable(PiescriptAntlrParser.VariableContext ctx);

    /**
     * Enter a parse tree produced by the {@code QueryExpr}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     */
    void enterQueryExpr(PiescriptAntlrParser.QueryExprContext ctx);

    /**
     * Exit a parse tree produced by the {@code QueryExpr}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     */
    void exitQueryExpr(PiescriptAntlrParser.QueryExprContext ctx);

    /**
     * Enter a parse tree produced by the {@code Projection}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     */
    void enterProjection(PiescriptAntlrParser.ProjectionContext ctx);

    /**
     * Exit a parse tree produced by the {@code Projection}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     */
    void exitProjection(PiescriptAntlrParser.ProjectionContext ctx);

    /**
     * Enter a parse tree produced by the {@code Accessor}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     */
    void enterAccessor(PiescriptAntlrParser.AccessorContext ctx);

    /**
     * Exit a parse tree produced by the {@code Accessor}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     */
    void exitAccessor(PiescriptAntlrParser.AccessorContext ctx);

    /**
     * Enter a parse tree produced by the {@code BlockExpr}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     */
    void enterBlockExpr(PiescriptAntlrParser.BlockExprContext ctx);

    /**
     * Exit a parse tree produced by the {@code BlockExpr}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     */
    void exitBlockExpr(PiescriptAntlrParser.BlockExprContext ctx);

    /**
     * Enter a parse tree produced by the {@code FalseLiteral}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     */
    void enterFalseLiteral(PiescriptAntlrParser.FalseLiteralContext ctx);

    /**
     * Exit a parse tree produced by the {@code FalseLiteral}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     */
    void exitFalseLiteral(PiescriptAntlrParser.FalseLiteralContext ctx);

    /**
     * Enter a parse tree produced by the {@code StringLiteral}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     */
    void enterStringLiteral(PiescriptAntlrParser.StringLiteralContext ctx);

    /**
     * Exit a parse tree produced by the {@code StringLiteral}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     */
    void exitStringLiteral(PiescriptAntlrParser.StringLiteralContext ctx);

    /**
     * Enter a parse tree produced by the {@code TrueLiteral}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     */
    void enterTrueLiteral(PiescriptAntlrParser.TrueLiteralContext ctx);

    /**
     * Exit a parse tree produced by the {@code TrueLiteral}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     */
    void exitTrueLiteral(PiescriptAntlrParser.TrueLiteralContext ctx);

    /**
     * Enter a parse tree produced by the {@code Ascription}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     */
    void enterAscription(PiescriptAntlrParser.AscriptionContext ctx);

    /**
     * Exit a parse tree produced by the {@code Ascription}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     */
    void exitAscription(PiescriptAntlrParser.AscriptionContext ctx);

    /**
     * Enter a parse tree produced by the {@code DecimalLiteral}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     */
    void enterDecimalLiteral(PiescriptAntlrParser.DecimalLiteralContext ctx);

    /**
     * Exit a parse tree produced by the {@code DecimalLiteral}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     */
    void exitDecimalLiteral(PiescriptAntlrParser.DecimalLiteralContext ctx);

    /**
     * Enter a parse tree produced by the {@code RecordUpdateExpr}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     */
    void enterRecordUpdateExpr(PiescriptAntlrParser.RecordUpdateExprContext ctx);

    /**
     * Exit a parse tree produced by the {@code RecordUpdateExpr}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     */
    void exitRecordUpdateExpr(PiescriptAntlrParser.RecordUpdateExprContext ctx);

    /**
     * Enter a parse tree produced by the {@code ParenExpr}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     */
    void enterParenExpr(PiescriptAntlrParser.ParenExprContext ctx);

    /**
     * Exit a parse tree produced by the {@code ParenExpr}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     */
    void exitParenExpr(PiescriptAntlrParser.ParenExprContext ctx);

    /**
     * Enter a parse tree produced by the {@code NullLiteral}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     */
    void enterNullLiteral(PiescriptAntlrParser.NullLiteralContext ctx);

    /**
     * Exit a parse tree produced by the {@code NullLiteral}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     */
    void exitNullLiteral(PiescriptAntlrParser.NullLiteralContext ctx);

    /**
     * Enter a parse tree produced by the {@code IntegerLiteral}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     */
    void enterIntegerLiteral(PiescriptAntlrParser.IntegerLiteralContext ctx);

    /**
     * Exit a parse tree produced by the {@code IntegerLiteral}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     */
    void exitIntegerLiteral(PiescriptAntlrParser.IntegerLiteralContext ctx);

    /**
     * Enter a parse tree produced by the {@code UpdateSugar}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     */
    void enterUpdateSugar(PiescriptAntlrParser.UpdateSugarContext ctx);

    /**
     * Exit a parse tree produced by the {@code UpdateSugar}
     * labeled alternative in {@link PiescriptAntlrParser#primary}.
     * @param ctx the parse tree
     */
    void exitUpdateSugar(PiescriptAntlrParser.UpdateSugarContext ctx);

    /**
     * Enter a parse tree produced by {@link PiescriptAntlrParser#recordField}.
     * @param ctx the parse tree
     */
    void enterRecordField(PiescriptAntlrParser.RecordFieldContext ctx);

    /**
     * Exit a parse tree produced by {@link PiescriptAntlrParser#recordField}.
     * @param ctx the parse tree
     */
    void exitRecordField(PiescriptAntlrParser.RecordFieldContext ctx);

    /**
     * Enter a parse tree produced by {@link PiescriptAntlrParser#recordUpdate}.
     * @param ctx the parse tree
     */
    void enterRecordUpdate(PiescriptAntlrParser.RecordUpdateContext ctx);

    /**
     * Exit a parse tree produced by {@link PiescriptAntlrParser#recordUpdate}.
     * @param ctx the parse tree
     */
    void exitRecordUpdate(PiescriptAntlrParser.RecordUpdateContext ctx);

    /**
     * Enter a parse tree produced by {@link PiescriptAntlrParser#block}.
     * @param ctx the parse tree
     */
    void enterBlock(PiescriptAntlrParser.BlockContext ctx);

    /**
     * Exit a parse tree produced by {@link PiescriptAntlrParser#block}.
     * @param ctx the parse tree
     */
    void exitBlock(PiescriptAntlrParser.BlockContext ctx);

    /**
     * Enter a parse tree produced by the {@code BlockLet}
     * labeled alternative in {@link PiescriptAntlrParser#blockStmt}.
     * @param ctx the parse tree
     */
    void enterBlockLet(PiescriptAntlrParser.BlockLetContext ctx);

    /**
     * Exit a parse tree produced by the {@code BlockLet}
     * labeled alternative in {@link PiescriptAntlrParser#blockStmt}.
     * @param ctx the parse tree
     */
    void exitBlockLet(PiescriptAntlrParser.BlockLetContext ctx);

    /**
     * Enter a parse tree produced by the {@code BlockExprStmt}
     * labeled alternative in {@link PiescriptAntlrParser#blockStmt}.
     * @param ctx the parse tree
     */
    void enterBlockExprStmt(PiescriptAntlrParser.BlockExprStmtContext ctx);

    /**
     * Exit a parse tree produced by the {@code BlockExprStmt}
     * labeled alternative in {@link PiescriptAntlrParser#blockStmt}.
     * @param ctx the parse tree
     */
    void exitBlockExprStmt(PiescriptAntlrParser.BlockExprStmtContext ctx);

    /**
     * Enter a parse tree produced by the {@code UntypedParam}
     * labeled alternative in {@link PiescriptAntlrParser#param}.
     * @param ctx the parse tree
     */
    void enterUntypedParam(PiescriptAntlrParser.UntypedParamContext ctx);

    /**
     * Exit a parse tree produced by the {@code UntypedParam}
     * labeled alternative in {@link PiescriptAntlrParser#param}.
     * @param ctx the parse tree
     */
    void exitUntypedParam(PiescriptAntlrParser.UntypedParamContext ctx);

    /**
     * Enter a parse tree produced by the {@code TypedParam}
     * labeled alternative in {@link PiescriptAntlrParser#param}.
     * @param ctx the parse tree
     */
    void enterTypedParam(PiescriptAntlrParser.TypedParamContext ctx);

    /**
     * Exit a parse tree produced by the {@code TypedParam}
     * labeled alternative in {@link PiescriptAntlrParser#param}.
     * @param ctx the parse tree
     */
    void exitTypedParam(PiescriptAntlrParser.TypedParamContext ctx);

    /**
     * Enter a parse tree produced by the {@code FunctionType}
     * labeled alternative in {@link PiescriptAntlrParser#type}.
     * @param ctx the parse tree
     */
    void enterFunctionType(PiescriptAntlrParser.FunctionTypeContext ctx);

    /**
     * Exit a parse tree produced by the {@code FunctionType}
     * labeled alternative in {@link PiescriptAntlrParser#type}.
     * @param ctx the parse tree
     */
    void exitFunctionType(PiescriptAntlrParser.FunctionTypeContext ctx);

    /**
     * Enter a parse tree produced by the {@code TypeNonArrow}
     * labeled alternative in {@link PiescriptAntlrParser#type}.
     * @param ctx the parse tree
     */
    void enterTypeNonArrow(PiescriptAntlrParser.TypeNonArrowContext ctx);

    /**
     * Exit a parse tree produced by the {@code TypeNonArrow}
     * labeled alternative in {@link PiescriptAntlrParser#type}.
     * @param ctx the parse tree
     */
    void exitTypeNonArrow(PiescriptAntlrParser.TypeNonArrowContext ctx);

    /**
     * Enter a parse tree produced by the {@code TypeAppPassthrough}
     * labeled alternative in {@link PiescriptAntlrParser#typeApp}.
     * @param ctx the parse tree
     */
    void enterTypeAppPassthrough(PiescriptAntlrParser.TypeAppPassthroughContext ctx);

    /**
     * Exit a parse tree produced by the {@code TypeAppPassthrough}
     * labeled alternative in {@link PiescriptAntlrParser#typeApp}.
     * @param ctx the parse tree
     */
    void exitTypeAppPassthrough(PiescriptAntlrParser.TypeAppPassthroughContext ctx);

    /**
     * Enter a parse tree produced by the {@code TypeApplication}
     * labeled alternative in {@link PiescriptAntlrParser#typeApp}.
     * @param ctx the parse tree
     */
    void enterTypeApplication(PiescriptAntlrParser.TypeApplicationContext ctx);

    /**
     * Exit a parse tree produced by the {@code TypeApplication}
     * labeled alternative in {@link PiescriptAntlrParser#typeApp}.
     * @param ctx the parse tree
     */
    void exitTypeApplication(PiescriptAntlrParser.TypeApplicationContext ctx);

    /**
     * Enter a parse tree produced by the {@code TypeCon}
     * labeled alternative in {@link PiescriptAntlrParser#typeAtom}.
     * @param ctx the parse tree
     */
    void enterTypeCon(PiescriptAntlrParser.TypeConContext ctx);

    /**
     * Exit a parse tree produced by the {@code TypeCon}
     * labeled alternative in {@link PiescriptAntlrParser#typeAtom}.
     * @param ctx the parse tree
     */
    void exitTypeCon(PiescriptAntlrParser.TypeConContext ctx);

    /**
     * Enter a parse tree produced by the {@code TypeVar}
     * labeled alternative in {@link PiescriptAntlrParser#typeAtom}.
     * @param ctx the parse tree
     */
    void enterTypeVar(PiescriptAntlrParser.TypeVarContext ctx);

    /**
     * Exit a parse tree produced by the {@code TypeVar}
     * labeled alternative in {@link PiescriptAntlrParser#typeAtom}.
     * @param ctx the parse tree
     */
    void exitTypeVar(PiescriptAntlrParser.TypeVarContext ctx);

    /**
     * Enter a parse tree produced by the {@code RecordType}
     * labeled alternative in {@link PiescriptAntlrParser#typeAtom}.
     * @param ctx the parse tree
     */
    void enterRecordType(PiescriptAntlrParser.RecordTypeContext ctx);

    /**
     * Exit a parse tree produced by the {@code RecordType}
     * labeled alternative in {@link PiescriptAntlrParser#typeAtom}.
     * @param ctx the parse tree
     */
    void exitRecordType(PiescriptAntlrParser.RecordTypeContext ctx);

    /**
     * Enter a parse tree produced by the {@code ParenType}
     * labeled alternative in {@link PiescriptAntlrParser#typeAtom}.
     * @param ctx the parse tree
     */
    void enterParenType(PiescriptAntlrParser.ParenTypeContext ctx);

    /**
     * Exit a parse tree produced by the {@code ParenType}
     * labeled alternative in {@link PiescriptAntlrParser#typeAtom}.
     * @param ctx the parse tree
     */
    void exitParenType(PiescriptAntlrParser.ParenTypeContext ctx);

    /**
     * Enter a parse tree produced by {@link PiescriptAntlrParser#rowType}.
     * @param ctx the parse tree
     */
    void enterRowType(PiescriptAntlrParser.RowTypeContext ctx);

    /**
     * Exit a parse tree produced by {@link PiescriptAntlrParser#rowType}.
     * @param ctx the parse tree
     */
    void exitRowType(PiescriptAntlrParser.RowTypeContext ctx);

    /**
     * Enter a parse tree produced by {@link PiescriptAntlrParser#rowField}.
     * @param ctx the parse tree
     */
    void enterRowField(PiescriptAntlrParser.RowFieldContext ctx);

    /**
     * Exit a parse tree produced by {@link PiescriptAntlrParser#rowField}.
     * @param ctx the parse tree
     */
    void exitRowField(PiescriptAntlrParser.RowFieldContext ctx);
}
