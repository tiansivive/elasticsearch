/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.piescript.parser;

import org.elasticsearch.test.ESTestCase;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;

public class PiescriptParserTests extends ESTestCase {

    // ──── Literals ────

    public void testIntegerLiteral() {
        assertParses("42");
    }

    public void testDecimalLiteral() {
        assertParses("3.14");
    }

    public void testDecimalWithExponent() {
        assertParses("1.5e10");
    }

    public void testDecimalStartingWithDot() {
        assertParses(".5");
    }

    public void testStringLiteral() {
        assertParses("\"hello world\"");
    }

    public void testStringWithEscapes() {
        assertParses("\"line\\nbreak\"");
    }

    public void testTrueLiteral() {
        assertParses("true");
    }

    public void testFalseLiteral() {
        assertParses("false");
    }

    public void testNullLiteral() {
        assertParses("null");
    }

    // ──── Variables ────

    public void testSimpleVariable() {
        assertParses("x");
    }

    public void testMultiCharVariable() {
        assertParses("myVariable");
    }

    public void testVariableWithUnderscore() {
        assertParses("my_var");
    }

    public void testUnderscorePrefix() {
        assertParses("_unused");
    }

    public void testUnderscoreIsReserved() {
        expectThrows(PiescriptParsingException.class, () -> parse("_"));
    }

    // ──── Arithmetic operators ────

    public void testAddition() {
        assertParses("1 + 2");
    }

    public void testSubtraction() {
        assertParses("3 - 1");
    }

    public void testMultiplication() {
        assertParses("2 * 3");
    }

    public void testDivision() {
        assertParses("10 / 2");
    }

    public void testModulo() {
        assertParses("10 % 3");
    }

    public void testUnaryMinus() {
        assertParses("-x");
    }

    public void testUnaryBang() {
        assertParses("!flag");
    }

    public void testNestedUnary() {
        assertParses("--x");
    }

    // ──── Comparison & equality ────

    public void testEquality() {
        assertParses("x == y");
    }

    public void testInequality() {
        assertParses("x != y");
    }

    public void testLessThan() {
        assertParses("x < y");
    }

    public void testGreaterThan() {
        assertParses("x > y");
    }

    public void testLessThanOrEqual() {
        assertParses("x <= y");
    }

    public void testGreaterThanOrEqual() {
        assertParses("x >= y");
    }

    // ──── Boolean operators ────

    public void testLogicalAnd() {
        assertParses("a && b");
    }

    public void testLogicalOr() {
        assertParses("a || b");
    }

    public void testBooleanChain() {
        assertParses("a && b || c && d");
    }

    // ──── Operator precedence ────

    public void testMulBeforeAdd() {
        String tree = parse("1 + 2 * 3");
        // mul should be nested inside addExpr: (addExpr ... + (mulExpr ... * ...))
        assertThat(tree, containsString("(mulExpr"));
        assertThat(tree, containsString("(addExpr"));
    }

    public void testParensOverridePrecedence() {
        assertParses("(1 + 2) * 3");
    }

    public void testComplexPrecedence() {
        assertParses("a + b * c == d - e / f");
    }

    // ──── Pipe operator ────

    public void testPipeOp() {
        String tree = parse("x |> f");
        assertThat(tree, containsString("|>"));
    }

    public void testPipeChain() {
        assertParses("x |> f |> g |> h");
    }

    public void testPipeWithExpr() {
        assertParses("x + 1 |> f");
    }

    // ──── Let bindings ────

    public void testLetExpr() {
        assertParses("let x = 1 in x + 1");
    }

    public void testLetWithTypeAnnotation() {
        assertParses("let x : Int = 1 in x");
    }

    public void testNestedLet() {
        assertParses("let x = 1 in let y = 2 in x + y");
    }

    // ──── Top-level bindings ────

    public void testTopBinding() {
        assertParses("let f = fn x -> x; f 42");
    }

    public void testTopBindingWithType() {
        assertParses("let f : Int -> Int = fn x -> x; f 42");
    }

    public void testMultipleTopBindings() {
        assertParses("let x = 1; let y = 2; x + y");
    }

    // ──── Lambda expressions ────

    public void testSimpleLambda() {
        assertParses("fn x -> x");
    }

    public void testMultiParamLambda() {
        assertParses("fn x y z -> x + y + z");
    }

    public void testTypedParam() {
        assertParses("fn (x : Int) -> x + 1");
    }

    public void testMixedParams() {
        assertParses("fn x (y : Int) z -> x + y + z");
    }

    // ──── Application ────

    public void testSimpleApplication() {
        assertParses("f x");
    }

    public void testMultiArgApplication() {
        assertParses("f x y z");
    }

    public void testApplicationOfLiteral() {
        assertParses("f 42");
    }

    public void testNestedApplication() {
        assertParses("f (g x)");
    }

    // ──── Records ────

    public void testEmptyRecord() {
        String tree = parse("{}");
        assertThat(tree, containsString("{ }"));
    }

    public void testSingleFieldRecord() {
        assertParses("{ name: \"alice\" }");
    }

    public void testMultiFieldRecord() {
        assertParses("{ name: \"alice\", age: 30 }");
    }

    public void testRecordUpdate() {
        assertParses("{ r | name = \"bob\" }");
    }

    public void testRecordUpdateMultiField() {
        assertParses("{ r | name = \"bob\", age = 31 }");
    }

    public void testUpdateSugar() {
        assertParses("{ _ | name = \"bob\" }");
    }

    // ──── Projection / accessor ────

    public void testProjection() {
        assertParses("r.name");
    }

    public void testChainedProjection() {
        assertParses("r.a.b.c");
    }

    public void testAccessorSection() {
        assertParses(".name");
    }

    // ──── Type ascription ────

    public void testAscription() {
        assertParses("(42 : Int)");
    }

    public void testAscriptionFunctionType() {
        assertParses("(f : Int -> Int)");
    }

    // ──── If / then / else ────

    public void testIfThenElse() {
        assertParses("if true then 1 else 0");
    }

    public void testNestedIf() {
        assertParses("if a then if b then 1 else 2 else 3");
    }

    // ──── Blocks ────

    public void testBlock() {
        assertParses("{ let x = 1; x + 1 }");
    }

    public void testBlockMultipleStatements() {
        assertParses("{ let x = 1; let y = 2; x + y }");
    }

    public void testBlockExprStatement() {
        assertParses("{ f x; g y; 42 }");
    }

    // ──── Type syntax ────

    public void testSimpleType() {
        assertParses("let x : Int = 42 in x");
    }

    public void testFunctionType() {
        assertParses("let f : Int -> Bool = fn x -> true in f 1");
    }

    public void testRecordType() {
        assertParses("let r : { name: String, age: Int } = { name: \"a\", age: 1 } in r");
    }

    public void testRowTypeWithVariable() {
        assertParses("let f : { name: String | r } -> String = .name in f { name: \"a\", age: 1 }");
    }

    public void testTypeApplication() {
        assertParses("(fn x -> x : Stream Int -> Stream Int)");
    }

    public void testTypeAppWithRecord() {
        assertParses("(fn x -> x : Stream { name: Int } -> Stream { name: Int })");
    }

    public void testTypeAppPrecedenceOverArrow() {
        // f a -> f b -> f c should parse as (f a) -> ((f b) -> (f c))
        assertParses("(fn x -> x : Stream Int -> Stream Int -> Stream Int)");
    }

    public void testNestedFunctionType() {
        assertParses("let f : (Int -> Int) -> Int = fn g -> g 0 in f (fn x -> x)");
    }

    // ──── Comments ────

    public void testLineComment() {
        assertParses("// this is a comment\n42");
    }

    public void testMultilineComment() {
        assertParses("/* block comment */ 42");
    }

    public void testCommentInExpression() {
        assertParses("1 + /* inline */ 2");
    }

    // ──── Query expressions ────

    public void testQueryExprStandalone() {
        String tree = parse("query `FROM logs-*`");
        assertThat(tree, containsString("query"));
        assertThat(tree, containsString("FROM logs-*"));
    }

    public void testQueryExprInTopBinding() {
        assertParses("let docs = query `FROM logs-*`; docs");
    }

    public void testQueryExprInBlock() {
        assertParses("{ let docs = query `FROM my_index`; docs }");
    }

    public void testQueryExprWithEsqlPipes() {
        String tree = parse("query `FROM logs-* | WHERE status >= 500 | LIMIT 10`");
        assertThat(tree, containsString("FROM logs-*"));
        assertThat(tree, containsString("WHERE status >= 500"));
    }

    public void testQueryExprMultipleInTopBindings() {
        assertParses("let a = query `FROM idx_a`; let b = query `FROM idx_b`; a");
    }

    public void testQueryExprWithTrailingExpression() {
        assertParses("let docs = query `FROM logs-*`; docs |> f");
    }

    public void testQueryExprInLetIn() {
        assertParses("let docs = query `FROM logs-*` in docs");
    }

    public void testQueryExprEmpty() {
        expectThrows(PiescriptParsingException.class, () -> parse("query ``"));
    }

    // ──── Error cases ────

    public void testEmptyProgram() {
        expectThrows(PiescriptParsingException.class, () -> parse(""));
    }

    public void testMissingOperand() {
        expectThrows(PiescriptParsingException.class, () -> parse("1 +"));
    }

    public void testMissingLetBody() {
        expectThrows(PiescriptParsingException.class, () -> parse("let x ="));
    }

    public void testUnclosedParen() {
        expectThrows(PiescriptParsingException.class, () -> parse("(1 + 2"));
    }

    public void testUnclosedString() {
        expectThrows(PiescriptParsingException.class, () -> parse("\"unterminated"));
    }

    public void testMissingArrowInLambda() {
        expectThrows(PiescriptParsingException.class, () -> parse("fn x x"));
    }

    // ──── Scratch pad for manual inspection ────

    public void testScratch() {
        String tree = parse("let id = fn x -> x; id 42");
        logger.info("Parse tree:\n{}", tree);
    }

    // ──── Helpers ────

    private String parse(String program) {
        PiescriptParser parser = new PiescriptParser();
        String tree = parser.parseToTreeString(program);
        assertThat(tree, notNullValue());
        return tree;
    }

    private void assertParses(String program) {
        parse(program);
    }
}
