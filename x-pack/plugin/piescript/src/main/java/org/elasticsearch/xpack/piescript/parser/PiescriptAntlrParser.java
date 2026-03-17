// Generated from /Users/t.vilaverde/Workspace/Elastic/elasticsearch/x-pack/plugin/piescript/src/main/antlr/PiescriptAntlrParser.g4 by ANTLR 4.13.1
package org.elasticsearch.xpack.piescript.parser;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;

import java.util.List;

@SuppressWarnings({ "all", "this-escape", "cast", "warnings", "unchecked", "unused", "CheckReturnValue" })
public class PiescriptAntlrParser extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.1", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache = new PredictionContextCache();
    public static final int LET = 1, IN = 2, FN = 3, IF = 4, THEN = 5, ELSE = 6, TRUE = 7, FALSE = 8, NULL = 9, MATCH = 10, QUERY = 11,
        SPAWN_BANG = 12, SPAWN = 13, SEND = 14, WHEN = 15, PAR = 16, DO = 17, UNDERSCORE = 18, PIPE_OP = 19, OR_OP = 20, AND_OP = 21,
        ARROW = 22, EQ = 23, NEQ = 24, LTE = 25, GTE = 26, LT = 27, GT = 28, PLUS = 29, MINUS = 30, ASTERISK = 31, SLASH = 32, PERCENT = 33,
        BANG = 34, DOT = 35, COMMA = 36, COLON = 37, SEMICOLON = 38, ASSIGN = 39, AMP = 40, BAR = 41, LPAREN = 42, RPAREN = 43, LBRACE = 44,
        RBRACE = 45, INTEGER_LITERAL = 46, DECIMAL_LITERAL = 47, QUOTED_STRING = 48, UPPER_IDENT = 49, LOWER_IDENT = 50, LINE_COMMENT = 51,
        MULTILINE_COMMENT = 52, WS = 53, ESQL_BODY = 54, ESQL_WS = 55;
    public static final int RULE_ident = 0, RULE_program = 1, RULE_topBinding = 2, RULE_expr = 3, RULE_whenBinding = 4, RULE_pipeExpr = 5,
        RULE_orExpr = 6, RULE_andExpr = 7, RULE_eqExpr = 8, RULE_cmpExpr = 9, RULE_addExpr = 10, RULE_mulExpr = 11, RULE_unaryExpr = 12,
        RULE_appExpr = 13, RULE_primary = 14, RULE_recordField = 15, RULE_recordUpdate = 16, RULE_block = 17, RULE_blockStmt = 18,
        RULE_param = 19, RULE_type = 20, RULE_typeApp = 21, RULE_typeAtom = 22, RULE_rowType = 23, RULE_rowField = 24;

    private static String[] makeRuleNames() {
        return new String[] {
            "ident",
            "program",
            "topBinding",
            "expr",
            "whenBinding",
            "pipeExpr",
            "orExpr",
            "andExpr",
            "eqExpr",
            "cmpExpr",
            "addExpr",
            "mulExpr",
            "unaryExpr",
            "appExpr",
            "primary",
            "recordField",
            "recordUpdate",
            "block",
            "blockStmt",
            "param",
            "type",
            "typeApp",
            "typeAtom",
            "rowType",
            "rowField" };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[] {
            null,
            "'let'",
            "'in'",
            "'fn'",
            "'if'",
            "'then'",
            "'else'",
            "'true'",
            "'false'",
            "'null'",
            "'match'",
            "'query'",
            "'spawn!'",
            "'spawn'",
            "'send'",
            "'when'",
            "'par'",
            "'do'",
            "'_'",
            "'|>'",
            "'||'",
            "'&&'",
            "'->'",
            "'=='",
            "'!='",
            "'<='",
            "'>='",
            "'<'",
            "'>'",
            "'+'",
            "'-'",
            "'*'",
            "'/'",
            "'%'",
            "'!'",
            "'.'",
            "','",
            "':'",
            "';'",
            "'='",
            "'&'",
            "'|'",
            "'('",
            "')'",
            "'{'",
            "'}'" };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[] {
            null,
            "LET",
            "IN",
            "FN",
            "IF",
            "THEN",
            "ELSE",
            "TRUE",
            "FALSE",
            "NULL",
            "MATCH",
            "QUERY",
            "SPAWN_BANG",
            "SPAWN",
            "SEND",
            "WHEN",
            "PAR",
            "DO",
            "UNDERSCORE",
            "PIPE_OP",
            "OR_OP",
            "AND_OP",
            "ARROW",
            "EQ",
            "NEQ",
            "LTE",
            "GTE",
            "LT",
            "GT",
            "PLUS",
            "MINUS",
            "ASTERISK",
            "SLASH",
            "PERCENT",
            "BANG",
            "DOT",
            "COMMA",
            "COLON",
            "SEMICOLON",
            "ASSIGN",
            "AMP",
            "BAR",
            "LPAREN",
            "RPAREN",
            "LBRACE",
            "RBRACE",
            "INTEGER_LITERAL",
            "DECIMAL_LITERAL",
            "QUOTED_STRING",
            "UPPER_IDENT",
            "LOWER_IDENT",
            "LINE_COMMENT",
            "MULTILINE_COMMENT",
            "WS",
            "ESQL_BODY",
            "ESQL_WS" };
    }

    private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
    public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

    /**
     * @deprecated Use {@link #VOCABULARY} instead.
     */
    @Deprecated
    public static final String[] tokenNames;
    static {
        tokenNames = new String[_SYMBOLIC_NAMES.length];
        for (int i = 0; i < tokenNames.length; i++) {
            tokenNames[i] = VOCABULARY.getLiteralName(i);
            if (tokenNames[i] == null) {
                tokenNames[i] = VOCABULARY.getSymbolicName(i);
            }

            if (tokenNames[i] == null) {
                tokenNames[i] = "<INVALID>";
            }
        }
    }

    @Override
    @Deprecated
    public String[] getTokenNames() {
        return tokenNames;
    }

    @Override

    public Vocabulary getVocabulary() {
        return VOCABULARY;
    }

    @Override
    public String getGrammarFileName() {
        return "PiescriptAntlrParser.g4";
    }

    @Override
    public String[] getRuleNames() {
        return ruleNames;
    }

    @Override
    public String getSerializedATN() {
        return _serializedATN;
    }

    @Override
    public ATN getATN() {
        return _ATN;
    }

    public PiescriptAntlrParser(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class IdentContext extends ParserRuleContext {
        public TerminalNode UPPER_IDENT() {
            return getToken(PiescriptAntlrParser.UPPER_IDENT, 0);
        }

        public TerminalNode LOWER_IDENT() {
            return getToken(PiescriptAntlrParser.LOWER_IDENT, 0);
        }

        public IdentContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_ident;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterIdent(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitIdent(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor).visitIdent(
                this
            );
            else return visitor.visitChildren(this);
        }
    }

    public final IdentContext ident() throws RecognitionException {
        IdentContext _localctx = new IdentContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_ident);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(50);
                _la = _input.LA(1);
                if (!(_la == UPPER_IDENT || _la == LOWER_IDENT)) {
                    _errHandler.recoverInline(this);
                } else {
                    if (_input.LA(1) == Token.EOF) matchedEOF = true;
                    _errHandler.reportMatch(this);
                    consume();
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class ProgramContext extends ParserRuleContext {
        public ExprContext expr() {
            return getRuleContext(ExprContext.class, 0);
        }

        public TerminalNode EOF() {
            return getToken(PiescriptAntlrParser.EOF, 0);
        }

        public List<TopBindingContext> topBinding() {
            return getRuleContexts(TopBindingContext.class);
        }

        public TopBindingContext topBinding(int i) {
            return getRuleContext(TopBindingContext.class, i);
        }

        public TerminalNode SEMICOLON() {
            return getToken(PiescriptAntlrParser.SEMICOLON, 0);
        }

        public ProgramContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_program;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterProgram(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitProgram(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor).visitProgram(
                this
            );
            else return visitor.visitChildren(this);
        }
    }

    public final ProgramContext program() throws RecognitionException {
        ProgramContext _localctx = new ProgramContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_program);
        int _la;
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                setState(55);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 0, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        {
                            {
                                setState(52);
                                topBinding();
                            }
                        }
                    }
                    setState(57);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 0, _ctx);
                }
                setState(58);
                expr();
                setState(60);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == SEMICOLON) {
                    {
                        setState(59);
                        match(SEMICOLON);
                    }
                }

                setState(62);
                match(EOF);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class TopBindingContext extends ParserRuleContext {
        public TerminalNode LET() {
            return getToken(PiescriptAntlrParser.LET, 0);
        }

        public IdentContext ident() {
            return getRuleContext(IdentContext.class, 0);
        }

        public TerminalNode ASSIGN() {
            return getToken(PiescriptAntlrParser.ASSIGN, 0);
        }

        public ExprContext expr() {
            return getRuleContext(ExprContext.class, 0);
        }

        public TerminalNode SEMICOLON() {
            return getToken(PiescriptAntlrParser.SEMICOLON, 0);
        }

        public TerminalNode COLON() {
            return getToken(PiescriptAntlrParser.COLON, 0);
        }

        public TypeContext type() {
            return getRuleContext(TypeContext.class, 0);
        }

        public TopBindingContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_topBinding;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterTopBinding(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitTopBinding(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor).visitTopBinding(
                this
            );
            else return visitor.visitChildren(this);
        }
    }

    public final TopBindingContext topBinding() throws RecognitionException {
        TopBindingContext _localctx = new TopBindingContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_topBinding);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(64);
                match(LET);
                setState(65);
                ident();
                setState(68);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == COLON) {
                    {
                        setState(66);
                        match(COLON);
                        setState(67);
                        type();
                    }
                }

                setState(70);
                match(ASSIGN);
                setState(71);
                expr();
                setState(72);
                match(SEMICOLON);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class ExprContext extends ParserRuleContext {
        public ExprContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_expr;
        }

        public ExprContext() {}

        public void copyFrom(ExprContext ctx) {
            super.copyFrom(ctx);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class SpawnExprContext extends ExprContext {
        public TerminalNode SPAWN() {
            return getToken(PiescriptAntlrParser.SPAWN, 0);
        }

        public ExprContext expr() {
            return getRuleContext(ExprContext.class, 0);
        }

        public SpawnExprContext(ExprContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterSpawnExpr(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitSpawnExpr(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor).visitSpawnExpr(
                this
            );
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class SendExprContext extends ExprContext {
        public TerminalNode SEND() {
            return getToken(PiescriptAntlrParser.SEND, 0);
        }

        public PrimaryContext primary() {
            return getRuleContext(PrimaryContext.class, 0);
        }

        public ExprContext expr() {
            return getRuleContext(ExprContext.class, 0);
        }

        public SendExprContext(ExprContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterSendExpr(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitSendExpr(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor).visitSendExpr(
                this
            );
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class LetExprContext extends ExprContext {
        public TerminalNode LET() {
            return getToken(PiescriptAntlrParser.LET, 0);
        }

        public IdentContext ident() {
            return getRuleContext(IdentContext.class, 0);
        }

        public TerminalNode ASSIGN() {
            return getToken(PiescriptAntlrParser.ASSIGN, 0);
        }

        public List<ExprContext> expr() {
            return getRuleContexts(ExprContext.class);
        }

        public ExprContext expr(int i) {
            return getRuleContext(ExprContext.class, i);
        }

        public TerminalNode IN() {
            return getToken(PiescriptAntlrParser.IN, 0);
        }

        public TerminalNode COLON() {
            return getToken(PiescriptAntlrParser.COLON, 0);
        }

        public TypeContext type() {
            return getRuleContext(TypeContext.class, 0);
        }

        public LetExprContext(ExprContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterLetExpr(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitLetExpr(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor).visitLetExpr(
                this
            );
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class LambdaExprContext extends ExprContext {
        public TerminalNode FN() {
            return getToken(PiescriptAntlrParser.FN, 0);
        }

        public TerminalNode ARROW() {
            return getToken(PiescriptAntlrParser.ARROW, 0);
        }

        public ExprContext expr() {
            return getRuleContext(ExprContext.class, 0);
        }

        public List<ParamContext> param() {
            return getRuleContexts(ParamContext.class);
        }

        public ParamContext param(int i) {
            return getRuleContext(ParamContext.class, i);
        }

        public LambdaExprContext(ExprContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterLambdaExpr(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitLambdaExpr(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor).visitLambdaExpr(
                this
            );
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class WhenExprContext extends ExprContext {
        public TerminalNode WHEN() {
            return getToken(PiescriptAntlrParser.WHEN, 0);
        }

        public List<WhenBindingContext> whenBinding() {
            return getRuleContexts(WhenBindingContext.class);
        }

        public WhenBindingContext whenBinding(int i) {
            return getRuleContext(WhenBindingContext.class, i);
        }

        public TerminalNode ARROW() {
            return getToken(PiescriptAntlrParser.ARROW, 0);
        }

        public ExprContext expr() {
            return getRuleContext(ExprContext.class, 0);
        }

        public List<TerminalNode> AMP() {
            return getTokens(PiescriptAntlrParser.AMP);
        }

        public TerminalNode AMP(int i) {
            return getToken(PiescriptAntlrParser.AMP, i);
        }

        public WhenExprContext(ExprContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterWhenExpr(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitWhenExpr(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor).visitWhenExpr(
                this
            );
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class SpawnBangExprContext extends ExprContext {
        public TerminalNode SPAWN_BANG() {
            return getToken(PiescriptAntlrParser.SPAWN_BANG, 0);
        }

        public SpawnBangExprContext(ExprContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterSpawnBangExpr(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitSpawnBangExpr(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor)
                .visitSpawnBangExpr(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class ExprPipeContext extends ExprContext {
        public PipeExprContext pipeExpr() {
            return getRuleContext(PipeExprContext.class, 0);
        }

        public ExprPipeContext(ExprContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterExprPipe(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitExprPipe(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor).visitExprPipe(
                this
            );
            else return visitor.visitChildren(this);
        }
    }

    public final ExprContext expr() throws RecognitionException {
        ExprContext _localctx = new ExprContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_expr);
        int _la;
        try {
            setState(114);
            _errHandler.sync(this);
            switch (_input.LA(1)) {
                case LET:
                    _localctx = new LetExprContext(_localctx);
                    enterOuterAlt(_localctx, 1); {
                    setState(74);
                    match(LET);
                    setState(75);
                    ident();
                    setState(78);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                    if (_la == COLON) {
                        {
                            setState(76);
                            match(COLON);
                            setState(77);
                            type();
                        }
                    }

                    setState(80);
                    match(ASSIGN);
                    setState(81);
                    expr();
                    setState(82);
                    match(IN);
                    setState(83);
                    expr();
                }
                    break;
                case FN:
                    _localctx = new LambdaExprContext(_localctx);
                    enterOuterAlt(_localctx, 2); {
                    setState(85);
                    match(FN);
                    setState(87);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                    do {
                        {
                            {
                                setState(86);
                                param();
                            }
                        }
                        setState(89);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                    } while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 1693247906775040L) != 0));
                    setState(91);
                    match(ARROW);
                    setState(92);
                    expr();
                }
                    break;
                case SPAWN_BANG:
                    _localctx = new SpawnBangExprContext(_localctx);
                    enterOuterAlt(_localctx, 3); {
                    setState(94);
                    match(SPAWN_BANG);
                }
                    break;
                case SPAWN:
                    _localctx = new SpawnExprContext(_localctx);
                    enterOuterAlt(_localctx, 4); {
                    setState(95);
                    match(SPAWN);
                    setState(96);
                    expr();
                }
                    break;
                case SEND:
                    _localctx = new SendExprContext(_localctx);
                    enterOuterAlt(_localctx, 5); {
                    setState(97);
                    match(SEND);
                    setState(98);
                    primary(0);
                    setState(99);
                    expr();
                }
                    break;
                case WHEN:
                    _localctx = new WhenExprContext(_localctx);
                    enterOuterAlt(_localctx, 6); {
                    setState(101);
                    match(WHEN);
                    setState(102);
                    whenBinding();
                    setState(107);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                    while (_la == AMP) {
                        {
                            {
                                setState(103);
                                match(AMP);
                                setState(104);
                                whenBinding();
                            }
                        }
                        setState(109);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                    }
                    setState(110);
                    match(ARROW);
                    setState(111);
                    expr();
                }
                    break;
                case IF:
                case TRUE:
                case FALSE:
                case NULL:
                case QUERY:
                case MINUS:
                case BANG:
                case DOT:
                case LPAREN:
                case LBRACE:
                case INTEGER_LITERAL:
                case DECIMAL_LITERAL:
                case QUOTED_STRING:
                case UPPER_IDENT:
                case LOWER_IDENT:
                    _localctx = new ExprPipeContext(_localctx);
                    enterOuterAlt(_localctx, 7); {
                    setState(113);
                    pipeExpr(0);
                }
                    break;
                default:
                    throw new NoViableAltException(this);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class WhenBindingContext extends ParserRuleContext {
        public TerminalNode LPAREN() {
            return getToken(PiescriptAntlrParser.LPAREN, 0);
        }

        public ExprContext expr() {
            return getRuleContext(ExprContext.class, 0);
        }

        public IdentContext ident() {
            return getRuleContext(IdentContext.class, 0);
        }

        public TerminalNode RPAREN() {
            return getToken(PiescriptAntlrParser.RPAREN, 0);
        }

        public WhenBindingContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_whenBinding;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterWhenBinding(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitWhenBinding(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor)
                .visitWhenBinding(this);
            else return visitor.visitChildren(this);
        }
    }

    public final WhenBindingContext whenBinding() throws RecognitionException {
        WhenBindingContext _localctx = new WhenBindingContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_whenBinding);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(116);
                match(LPAREN);
                setState(117);
                expr();
                setState(118);
                ident();
                setState(119);
                match(RPAREN);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class PipeExprContext extends ParserRuleContext {
        public PipeExprContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_pipeExpr;
        }

        public PipeExprContext() {}

        public void copyFrom(PipeExprContext ctx) {
            super.copyFrom(ctx);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class PipeOpContext extends PipeExprContext {
        public PipeExprContext pipeExpr() {
            return getRuleContext(PipeExprContext.class, 0);
        }

        public TerminalNode PIPE_OP() {
            return getToken(PiescriptAntlrParser.PIPE_OP, 0);
        }

        public OrExprContext orExpr() {
            return getRuleContext(OrExprContext.class, 0);
        }

        public PipeOpContext(PipeExprContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterPipeOp(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitPipeOp(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor).visitPipeOp(
                this
            );
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class PipePassthroughContext extends PipeExprContext {
        public OrExprContext orExpr() {
            return getRuleContext(OrExprContext.class, 0);
        }

        public PipePassthroughContext(PipeExprContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterPipePassthrough(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitPipePassthrough(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor)
                .visitPipePassthrough(this);
            else return visitor.visitChildren(this);
        }
    }

    public final PipeExprContext pipeExpr() throws RecognitionException {
        return pipeExpr(0);
    }

    private PipeExprContext pipeExpr(int _p) throws RecognitionException {
        ParserRuleContext _parentctx = _ctx;
        int _parentState = getState();
        PipeExprContext _localctx = new PipeExprContext(_ctx, _parentState);
        PipeExprContext _prevctx = _localctx;
        int _startState = 10;
        enterRecursionRule(_localctx, 10, RULE_pipeExpr, _p);
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                {
                    _localctx = new PipePassthroughContext(_localctx);
                    _ctx = _localctx;
                    _prevctx = _localctx;

                    setState(122);
                    orExpr(0);
                }
                _ctx.stop = _input.LT(-1);
                setState(129);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 7, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        if (_parseListeners != null) triggerExitRuleEvent();
                        _prevctx = _localctx;
                        {
                            {
                                _localctx = new PipeOpContext(new PipeExprContext(_parentctx, _parentState));
                                pushNewRecursionContext(_localctx, _startState, RULE_pipeExpr);
                                setState(124);
                                if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
                                setState(125);
                                match(PIPE_OP);
                                setState(126);
                                orExpr(0);
                            }
                        }
                    }
                    setState(131);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 7, _ctx);
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            unrollRecursionContexts(_parentctx);
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class OrExprContext extends ParserRuleContext {
        public OrExprContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_orExpr;
        }

        public OrExprContext() {}

        public void copyFrom(OrExprContext ctx) {
            super.copyFrom(ctx);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class OrPassthroughContext extends OrExprContext {
        public AndExprContext andExpr() {
            return getRuleContext(AndExprContext.class, 0);
        }

        public OrPassthroughContext(OrExprContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterOrPassthrough(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitOrPassthrough(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor)
                .visitOrPassthrough(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class OrOpContext extends OrExprContext {
        public OrExprContext orExpr() {
            return getRuleContext(OrExprContext.class, 0);
        }

        public TerminalNode OR_OP() {
            return getToken(PiescriptAntlrParser.OR_OP, 0);
        }

        public AndExprContext andExpr() {
            return getRuleContext(AndExprContext.class, 0);
        }

        public OrOpContext(OrExprContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterOrOp(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitOrOp(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor).visitOrOp(this);
            else return visitor.visitChildren(this);
        }
    }

    public final OrExprContext orExpr() throws RecognitionException {
        return orExpr(0);
    }

    private OrExprContext orExpr(int _p) throws RecognitionException {
        ParserRuleContext _parentctx = _ctx;
        int _parentState = getState();
        OrExprContext _localctx = new OrExprContext(_ctx, _parentState);
        OrExprContext _prevctx = _localctx;
        int _startState = 12;
        enterRecursionRule(_localctx, 12, RULE_orExpr, _p);
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                {
                    _localctx = new OrPassthroughContext(_localctx);
                    _ctx = _localctx;
                    _prevctx = _localctx;

                    setState(133);
                    andExpr(0);
                }
                _ctx.stop = _input.LT(-1);
                setState(140);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 8, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        if (_parseListeners != null) triggerExitRuleEvent();
                        _prevctx = _localctx;
                        {
                            {
                                _localctx = new OrOpContext(new OrExprContext(_parentctx, _parentState));
                                pushNewRecursionContext(_localctx, _startState, RULE_orExpr);
                                setState(135);
                                if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
                                setState(136);
                                match(OR_OP);
                                setState(137);
                                andExpr(0);
                            }
                        }
                    }
                    setState(142);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 8, _ctx);
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            unrollRecursionContexts(_parentctx);
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class AndExprContext extends ParserRuleContext {
        public AndExprContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_andExpr;
        }

        public AndExprContext() {}

        public void copyFrom(AndExprContext ctx) {
            super.copyFrom(ctx);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class AndPassthroughContext extends AndExprContext {
        public EqExprContext eqExpr() {
            return getRuleContext(EqExprContext.class, 0);
        }

        public AndPassthroughContext(AndExprContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterAndPassthrough(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitAndPassthrough(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor)
                .visitAndPassthrough(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class AndOpContext extends AndExprContext {
        public AndExprContext andExpr() {
            return getRuleContext(AndExprContext.class, 0);
        }

        public TerminalNode AND_OP() {
            return getToken(PiescriptAntlrParser.AND_OP, 0);
        }

        public EqExprContext eqExpr() {
            return getRuleContext(EqExprContext.class, 0);
        }

        public AndOpContext(AndExprContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterAndOp(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitAndOp(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor).visitAndOp(
                this
            );
            else return visitor.visitChildren(this);
        }
    }

    public final AndExprContext andExpr() throws RecognitionException {
        return andExpr(0);
    }

    private AndExprContext andExpr(int _p) throws RecognitionException {
        ParserRuleContext _parentctx = _ctx;
        int _parentState = getState();
        AndExprContext _localctx = new AndExprContext(_ctx, _parentState);
        AndExprContext _prevctx = _localctx;
        int _startState = 14;
        enterRecursionRule(_localctx, 14, RULE_andExpr, _p);
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                {
                    _localctx = new AndPassthroughContext(_localctx);
                    _ctx = _localctx;
                    _prevctx = _localctx;

                    setState(144);
                    eqExpr();
                }
                _ctx.stop = _input.LT(-1);
                setState(151);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 9, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        if (_parseListeners != null) triggerExitRuleEvent();
                        _prevctx = _localctx;
                        {
                            {
                                _localctx = new AndOpContext(new AndExprContext(_parentctx, _parentState));
                                pushNewRecursionContext(_localctx, _startState, RULE_andExpr);
                                setState(146);
                                if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
                                setState(147);
                                match(AND_OP);
                                setState(148);
                                eqExpr();
                            }
                        }
                    }
                    setState(153);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 9, _ctx);
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            unrollRecursionContexts(_parentctx);
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class EqExprContext extends ParserRuleContext {
        public EqExprContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_eqExpr;
        }

        public EqExprContext() {}

        public void copyFrom(EqExprContext ctx) {
            super.copyFrom(ctx);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class EqPassthroughContext extends EqExprContext {
        public CmpExprContext cmpExpr() {
            return getRuleContext(CmpExprContext.class, 0);
        }

        public EqPassthroughContext(EqExprContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterEqPassthrough(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitEqPassthrough(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor)
                .visitEqPassthrough(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class EqualityOpContext extends EqExprContext {
        public Token op;

        public List<CmpExprContext> cmpExpr() {
            return getRuleContexts(CmpExprContext.class);
        }

        public CmpExprContext cmpExpr(int i) {
            return getRuleContext(CmpExprContext.class, i);
        }

        public TerminalNode EQ() {
            return getToken(PiescriptAntlrParser.EQ, 0);
        }

        public TerminalNode NEQ() {
            return getToken(PiescriptAntlrParser.NEQ, 0);
        }

        public EqualityOpContext(EqExprContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterEqualityOp(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitEqualityOp(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor).visitEqualityOp(
                this
            );
            else return visitor.visitChildren(this);
        }
    }

    public final EqExprContext eqExpr() throws RecognitionException {
        EqExprContext _localctx = new EqExprContext(_ctx, getState());
        enterRule(_localctx, 16, RULE_eqExpr);
        int _la;
        try {
            setState(159);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 10, _ctx)) {
                case 1:
                    _localctx = new EqualityOpContext(_localctx);
                    enterOuterAlt(_localctx, 1); {
                    setState(154);
                    cmpExpr();
                    setState(155);
                    ((EqualityOpContext) _localctx).op = _input.LT(1);
                    _la = _input.LA(1);
                    if (!(_la == EQ || _la == NEQ)) {
                        ((EqualityOpContext) _localctx).op = (Token) _errHandler.recoverInline(this);
                    } else {
                        if (_input.LA(1) == Token.EOF) matchedEOF = true;
                        _errHandler.reportMatch(this);
                        consume();
                    }
                    setState(156);
                    cmpExpr();
                }
                    break;
                case 2:
                    _localctx = new EqPassthroughContext(_localctx);
                    enterOuterAlt(_localctx, 2); {
                    setState(158);
                    cmpExpr();
                }
                    break;
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class CmpExprContext extends ParserRuleContext {
        public CmpExprContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_cmpExpr;
        }

        public CmpExprContext() {}

        public void copyFrom(CmpExprContext ctx) {
            super.copyFrom(ctx);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class ComparisonOpContext extends CmpExprContext {
        public Token op;

        public List<AddExprContext> addExpr() {
            return getRuleContexts(AddExprContext.class);
        }

        public AddExprContext addExpr(int i) {
            return getRuleContext(AddExprContext.class, i);
        }

        public TerminalNode LTE() {
            return getToken(PiescriptAntlrParser.LTE, 0);
        }

        public TerminalNode GTE() {
            return getToken(PiescriptAntlrParser.GTE, 0);
        }

        public TerminalNode LT() {
            return getToken(PiescriptAntlrParser.LT, 0);
        }

        public TerminalNode GT() {
            return getToken(PiescriptAntlrParser.GT, 0);
        }

        public ComparisonOpContext(CmpExprContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterComparisonOp(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitComparisonOp(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor)
                .visitComparisonOp(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class CmpPassthroughContext extends CmpExprContext {
        public AddExprContext addExpr() {
            return getRuleContext(AddExprContext.class, 0);
        }

        public CmpPassthroughContext(CmpExprContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterCmpPassthrough(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitCmpPassthrough(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor)
                .visitCmpPassthrough(this);
            else return visitor.visitChildren(this);
        }
    }

    public final CmpExprContext cmpExpr() throws RecognitionException {
        CmpExprContext _localctx = new CmpExprContext(_ctx, getState());
        enterRule(_localctx, 18, RULE_cmpExpr);
        int _la;
        try {
            setState(166);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 11, _ctx)) {
                case 1:
                    _localctx = new ComparisonOpContext(_localctx);
                    enterOuterAlt(_localctx, 1); {
                    setState(161);
                    addExpr(0);
                    setState(162);
                    ((ComparisonOpContext) _localctx).op = _input.LT(1);
                    _la = _input.LA(1);
                    if (!((((_la) & ~0x3f) == 0 && ((1L << _la) & 503316480L) != 0))) {
                        ((ComparisonOpContext) _localctx).op = (Token) _errHandler.recoverInline(this);
                    } else {
                        if (_input.LA(1) == Token.EOF) matchedEOF = true;
                        _errHandler.reportMatch(this);
                        consume();
                    }
                    setState(163);
                    addExpr(0);
                }
                    break;
                case 2:
                    _localctx = new CmpPassthroughContext(_localctx);
                    enterOuterAlt(_localctx, 2); {
                    setState(165);
                    addExpr(0);
                }
                    break;
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class AddExprContext extends ParserRuleContext {
        public AddExprContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_addExpr;
        }

        public AddExprContext() {}

        public void copyFrom(AddExprContext ctx) {
            super.copyFrom(ctx);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class AdditiveOpContext extends AddExprContext {
        public Token op;

        public AddExprContext addExpr() {
            return getRuleContext(AddExprContext.class, 0);
        }

        public MulExprContext mulExpr() {
            return getRuleContext(MulExprContext.class, 0);
        }

        public TerminalNode PLUS() {
            return getToken(PiescriptAntlrParser.PLUS, 0);
        }

        public TerminalNode MINUS() {
            return getToken(PiescriptAntlrParser.MINUS, 0);
        }

        public AdditiveOpContext(AddExprContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterAdditiveOp(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitAdditiveOp(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor).visitAdditiveOp(
                this
            );
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class AddPassthroughContext extends AddExprContext {
        public MulExprContext mulExpr() {
            return getRuleContext(MulExprContext.class, 0);
        }

        public AddPassthroughContext(AddExprContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterAddPassthrough(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitAddPassthrough(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor)
                .visitAddPassthrough(this);
            else return visitor.visitChildren(this);
        }
    }

    public final AddExprContext addExpr() throws RecognitionException {
        return addExpr(0);
    }

    private AddExprContext addExpr(int _p) throws RecognitionException {
        ParserRuleContext _parentctx = _ctx;
        int _parentState = getState();
        AddExprContext _localctx = new AddExprContext(_ctx, _parentState);
        AddExprContext _prevctx = _localctx;
        int _startState = 20;
        enterRecursionRule(_localctx, 20, RULE_addExpr, _p);
        int _la;
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                {
                    _localctx = new AddPassthroughContext(_localctx);
                    _ctx = _localctx;
                    _prevctx = _localctx;

                    setState(169);
                    mulExpr(0);
                }
                _ctx.stop = _input.LT(-1);
                setState(176);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 12, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        if (_parseListeners != null) triggerExitRuleEvent();
                        _prevctx = _localctx;
                        {
                            {
                                _localctx = new AdditiveOpContext(new AddExprContext(_parentctx, _parentState));
                                pushNewRecursionContext(_localctx, _startState, RULE_addExpr);
                                setState(171);
                                if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
                                setState(172);
                                ((AdditiveOpContext) _localctx).op = _input.LT(1);
                                _la = _input.LA(1);
                                if (!(_la == PLUS || _la == MINUS)) {
                                    ((AdditiveOpContext) _localctx).op = (Token) _errHandler.recoverInline(this);
                                } else {
                                    if (_input.LA(1) == Token.EOF) matchedEOF = true;
                                    _errHandler.reportMatch(this);
                                    consume();
                                }
                                setState(173);
                                mulExpr(0);
                            }
                        }
                    }
                    setState(178);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 12, _ctx);
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            unrollRecursionContexts(_parentctx);
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class MulExprContext extends ParserRuleContext {
        public MulExprContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_mulExpr;
        }

        public MulExprContext() {}

        public void copyFrom(MulExprContext ctx) {
            super.copyFrom(ctx);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class MultiplicativeOpContext extends MulExprContext {
        public Token op;

        public MulExprContext mulExpr() {
            return getRuleContext(MulExprContext.class, 0);
        }

        public UnaryExprContext unaryExpr() {
            return getRuleContext(UnaryExprContext.class, 0);
        }

        public TerminalNode ASTERISK() {
            return getToken(PiescriptAntlrParser.ASTERISK, 0);
        }

        public TerminalNode SLASH() {
            return getToken(PiescriptAntlrParser.SLASH, 0);
        }

        public TerminalNode PERCENT() {
            return getToken(PiescriptAntlrParser.PERCENT, 0);
        }

        public MultiplicativeOpContext(MulExprContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterMultiplicativeOp(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitMultiplicativeOp(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor)
                .visitMultiplicativeOp(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class MulPassthroughContext extends MulExprContext {
        public UnaryExprContext unaryExpr() {
            return getRuleContext(UnaryExprContext.class, 0);
        }

        public MulPassthroughContext(MulExprContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterMulPassthrough(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitMulPassthrough(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor)
                .visitMulPassthrough(this);
            else return visitor.visitChildren(this);
        }
    }

    public final MulExprContext mulExpr() throws RecognitionException {
        return mulExpr(0);
    }

    private MulExprContext mulExpr(int _p) throws RecognitionException {
        ParserRuleContext _parentctx = _ctx;
        int _parentState = getState();
        MulExprContext _localctx = new MulExprContext(_ctx, _parentState);
        MulExprContext _prevctx = _localctx;
        int _startState = 22;
        enterRecursionRule(_localctx, 22, RULE_mulExpr, _p);
        int _la;
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                {
                    _localctx = new MulPassthroughContext(_localctx);
                    _ctx = _localctx;
                    _prevctx = _localctx;

                    setState(180);
                    unaryExpr();
                }
                _ctx.stop = _input.LT(-1);
                setState(187);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 13, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        if (_parseListeners != null) triggerExitRuleEvent();
                        _prevctx = _localctx;
                        {
                            {
                                _localctx = new MultiplicativeOpContext(new MulExprContext(_parentctx, _parentState));
                                pushNewRecursionContext(_localctx, _startState, RULE_mulExpr);
                                setState(182);
                                if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
                                setState(183);
                                ((MultiplicativeOpContext) _localctx).op = _input.LT(1);
                                _la = _input.LA(1);
                                if (!((((_la) & ~0x3f) == 0 && ((1L << _la) & 15032385536L) != 0))) {
                                    ((MultiplicativeOpContext) _localctx).op = (Token) _errHandler.recoverInline(this);
                                } else {
                                    if (_input.LA(1) == Token.EOF) matchedEOF = true;
                                    _errHandler.reportMatch(this);
                                    consume();
                                }
                                setState(184);
                                unaryExpr();
                            }
                        }
                    }
                    setState(189);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 13, _ctx);
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            unrollRecursionContexts(_parentctx);
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class UnaryExprContext extends ParserRuleContext {
        public UnaryExprContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_unaryExpr;
        }

        public UnaryExprContext() {}

        public void copyFrom(UnaryExprContext ctx) {
            super.copyFrom(ctx);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class UnaryOpContext extends UnaryExprContext {
        public Token op;

        public UnaryExprContext unaryExpr() {
            return getRuleContext(UnaryExprContext.class, 0);
        }

        public TerminalNode BANG() {
            return getToken(PiescriptAntlrParser.BANG, 0);
        }

        public TerminalNode MINUS() {
            return getToken(PiescriptAntlrParser.MINUS, 0);
        }

        public UnaryOpContext(UnaryExprContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterUnaryOp(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitUnaryOp(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor).visitUnaryOp(
                this
            );
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class UnaryPassthroughContext extends UnaryExprContext {
        public AppExprContext appExpr() {
            return getRuleContext(AppExprContext.class, 0);
        }

        public UnaryPassthroughContext(UnaryExprContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterUnaryPassthrough(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitUnaryPassthrough(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor)
                .visitUnaryPassthrough(this);
            else return visitor.visitChildren(this);
        }
    }

    public final UnaryExprContext unaryExpr() throws RecognitionException {
        UnaryExprContext _localctx = new UnaryExprContext(_ctx, getState());
        enterRule(_localctx, 24, RULE_unaryExpr);
        int _la;
        try {
            setState(193);
            _errHandler.sync(this);
            switch (_input.LA(1)) {
                case MINUS:
                case BANG:
                    _localctx = new UnaryOpContext(_localctx);
                    enterOuterAlt(_localctx, 1); {
                    setState(190);
                    ((UnaryOpContext) _localctx).op = _input.LT(1);
                    _la = _input.LA(1);
                    if (!(_la == MINUS || _la == BANG)) {
                        ((UnaryOpContext) _localctx).op = (Token) _errHandler.recoverInline(this);
                    } else {
                        if (_input.LA(1) == Token.EOF) matchedEOF = true;
                        _errHandler.reportMatch(this);
                        consume();
                    }
                    setState(191);
                    unaryExpr();
                }
                    break;
                case IF:
                case TRUE:
                case FALSE:
                case NULL:
                case QUERY:
                case DOT:
                case LPAREN:
                case LBRACE:
                case INTEGER_LITERAL:
                case DECIMAL_LITERAL:
                case QUOTED_STRING:
                case UPPER_IDENT:
                case LOWER_IDENT:
                    _localctx = new UnaryPassthroughContext(_localctx);
                    enterOuterAlt(_localctx, 2); {
                    setState(192);
                    appExpr(0);
                }
                    break;
                default:
                    throw new NoViableAltException(this);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class AppExprContext extends ParserRuleContext {
        public AppExprContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_appExpr;
        }

        public AppExprContext() {}

        public void copyFrom(AppExprContext ctx) {
            super.copyFrom(ctx);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class AppPassthroughContext extends AppExprContext {
        public PrimaryContext primary() {
            return getRuleContext(PrimaryContext.class, 0);
        }

        public AppPassthroughContext(AppExprContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterAppPassthrough(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitAppPassthrough(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor)
                .visitAppPassthrough(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class ApplicationContext extends AppExprContext {
        public AppExprContext appExpr() {
            return getRuleContext(AppExprContext.class, 0);
        }

        public PrimaryContext primary() {
            return getRuleContext(PrimaryContext.class, 0);
        }

        public ApplicationContext(AppExprContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterApplication(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitApplication(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor)
                .visitApplication(this);
            else return visitor.visitChildren(this);
        }
    }

    public final AppExprContext appExpr() throws RecognitionException {
        return appExpr(0);
    }

    private AppExprContext appExpr(int _p) throws RecognitionException {
        ParserRuleContext _parentctx = _ctx;
        int _parentState = getState();
        AppExprContext _localctx = new AppExprContext(_ctx, _parentState);
        AppExprContext _prevctx = _localctx;
        int _startState = 26;
        enterRecursionRule(_localctx, 26, RULE_appExpr, _p);
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                {
                    _localctx = new AppPassthroughContext(_localctx);
                    _ctx = _localctx;
                    _prevctx = _localctx;

                    setState(196);
                    primary(0);
                }
                _ctx.stop = _input.LT(-1);
                setState(202);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 15, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        if (_parseListeners != null) triggerExitRuleEvent();
                        _prevctx = _localctx;
                        {
                            {
                                _localctx = new ApplicationContext(new AppExprContext(_parentctx, _parentState));
                                pushNewRecursionContext(_localctx, _startState, RULE_appExpr);
                                setState(198);
                                if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
                                setState(199);
                                primary(0);
                            }
                        }
                    }
                    setState(204);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 15, _ctx);
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            unrollRecursionContexts(_parentctx);
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class PrimaryContext extends ParserRuleContext {
        public PrimaryContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_primary;
        }

        public PrimaryContext() {}

        public void copyFrom(PrimaryContext ctx) {
            super.copyFrom(ctx);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class EmptyRecordContext extends PrimaryContext {
        public TerminalNode LBRACE() {
            return getToken(PiescriptAntlrParser.LBRACE, 0);
        }

        public TerminalNode RBRACE() {
            return getToken(PiescriptAntlrParser.RBRACE, 0);
        }

        public EmptyRecordContext(PrimaryContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterEmptyRecord(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitEmptyRecord(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor)
                .visitEmptyRecord(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class RecordLiteralContext extends PrimaryContext {
        public TerminalNode LBRACE() {
            return getToken(PiescriptAntlrParser.LBRACE, 0);
        }

        public List<RecordFieldContext> recordField() {
            return getRuleContexts(RecordFieldContext.class);
        }

        public RecordFieldContext recordField(int i) {
            return getRuleContext(RecordFieldContext.class, i);
        }

        public TerminalNode RBRACE() {
            return getToken(PiescriptAntlrParser.RBRACE, 0);
        }

        public List<TerminalNode> COMMA() {
            return getTokens(PiescriptAntlrParser.COMMA);
        }

        public TerminalNode COMMA(int i) {
            return getToken(PiescriptAntlrParser.COMMA, i);
        }

        public RecordLiteralContext(PrimaryContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterRecordLiteral(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitRecordLiteral(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor)
                .visitRecordLiteral(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class IfExprContext extends PrimaryContext {
        public TerminalNode IF() {
            return getToken(PiescriptAntlrParser.IF, 0);
        }

        public List<ExprContext> expr() {
            return getRuleContexts(ExprContext.class);
        }

        public ExprContext expr(int i) {
            return getRuleContext(ExprContext.class, i);
        }

        public TerminalNode THEN() {
            return getToken(PiescriptAntlrParser.THEN, 0);
        }

        public TerminalNode ELSE() {
            return getToken(PiescriptAntlrParser.ELSE, 0);
        }

        public IfExprContext(PrimaryContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterIfExpr(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitIfExpr(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor).visitIfExpr(
                this
            );
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class VariableContext extends PrimaryContext {
        public IdentContext ident() {
            return getRuleContext(IdentContext.class, 0);
        }

        public VariableContext(PrimaryContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterVariable(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitVariable(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor).visitVariable(
                this
            );
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class QueryExprContext extends PrimaryContext {
        public TerminalNode QUERY() {
            return getToken(PiescriptAntlrParser.QUERY, 0);
        }

        public TerminalNode ESQL_BODY() {
            return getToken(PiescriptAntlrParser.ESQL_BODY, 0);
        }

        public QueryExprContext(PrimaryContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterQueryExpr(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitQueryExpr(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor).visitQueryExpr(
                this
            );
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class ProjectionContext extends PrimaryContext {
        public PrimaryContext primary() {
            return getRuleContext(PrimaryContext.class, 0);
        }

        public TerminalNode DOT() {
            return getToken(PiescriptAntlrParser.DOT, 0);
        }

        public IdentContext ident() {
            return getRuleContext(IdentContext.class, 0);
        }

        public ProjectionContext(PrimaryContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterProjection(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitProjection(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor).visitProjection(
                this
            );
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class AccessorContext extends PrimaryContext {
        public TerminalNode DOT() {
            return getToken(PiescriptAntlrParser.DOT, 0);
        }

        public IdentContext ident() {
            return getRuleContext(IdentContext.class, 0);
        }

        public AccessorContext(PrimaryContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterAccessor(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitAccessor(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor).visitAccessor(
                this
            );
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class BlockExprContext extends PrimaryContext {
        public BlockContext block() {
            return getRuleContext(BlockContext.class, 0);
        }

        public BlockExprContext(PrimaryContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterBlockExpr(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitBlockExpr(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor).visitBlockExpr(
                this
            );
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class FalseLiteralContext extends PrimaryContext {
        public TerminalNode FALSE() {
            return getToken(PiescriptAntlrParser.FALSE, 0);
        }

        public FalseLiteralContext(PrimaryContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterFalseLiteral(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitFalseLiteral(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor)
                .visitFalseLiteral(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class StringLiteralContext extends PrimaryContext {
        public TerminalNode QUOTED_STRING() {
            return getToken(PiescriptAntlrParser.QUOTED_STRING, 0);
        }

        public StringLiteralContext(PrimaryContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterStringLiteral(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitStringLiteral(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor)
                .visitStringLiteral(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class TrueLiteralContext extends PrimaryContext {
        public TerminalNode TRUE() {
            return getToken(PiescriptAntlrParser.TRUE, 0);
        }

        public TrueLiteralContext(PrimaryContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterTrueLiteral(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitTrueLiteral(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor)
                .visitTrueLiteral(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class AscriptionContext extends PrimaryContext {
        public TerminalNode LPAREN() {
            return getToken(PiescriptAntlrParser.LPAREN, 0);
        }

        public ExprContext expr() {
            return getRuleContext(ExprContext.class, 0);
        }

        public TerminalNode COLON() {
            return getToken(PiescriptAntlrParser.COLON, 0);
        }

        public TypeContext type() {
            return getRuleContext(TypeContext.class, 0);
        }

        public TerminalNode RPAREN() {
            return getToken(PiescriptAntlrParser.RPAREN, 0);
        }

        public AscriptionContext(PrimaryContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterAscription(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitAscription(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor).visitAscription(
                this
            );
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class DecimalLiteralContext extends PrimaryContext {
        public TerminalNode DECIMAL_LITERAL() {
            return getToken(PiescriptAntlrParser.DECIMAL_LITERAL, 0);
        }

        public DecimalLiteralContext(PrimaryContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterDecimalLiteral(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitDecimalLiteral(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor)
                .visitDecimalLiteral(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class RecordUpdateExprContext extends PrimaryContext {
        public TerminalNode LBRACE() {
            return getToken(PiescriptAntlrParser.LBRACE, 0);
        }

        public ExprContext expr() {
            return getRuleContext(ExprContext.class, 0);
        }

        public TerminalNode BAR() {
            return getToken(PiescriptAntlrParser.BAR, 0);
        }

        public List<RecordUpdateContext> recordUpdate() {
            return getRuleContexts(RecordUpdateContext.class);
        }

        public RecordUpdateContext recordUpdate(int i) {
            return getRuleContext(RecordUpdateContext.class, i);
        }

        public TerminalNode RBRACE() {
            return getToken(PiescriptAntlrParser.RBRACE, 0);
        }

        public List<TerminalNode> COMMA() {
            return getTokens(PiescriptAntlrParser.COMMA);
        }

        public TerminalNode COMMA(int i) {
            return getToken(PiescriptAntlrParser.COMMA, i);
        }

        public RecordUpdateExprContext(PrimaryContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterRecordUpdateExpr(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitRecordUpdateExpr(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor)
                .visitRecordUpdateExpr(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class ParenExprContext extends PrimaryContext {
        public TerminalNode LPAREN() {
            return getToken(PiescriptAntlrParser.LPAREN, 0);
        }

        public ExprContext expr() {
            return getRuleContext(ExprContext.class, 0);
        }

        public TerminalNode RPAREN() {
            return getToken(PiescriptAntlrParser.RPAREN, 0);
        }

        public ParenExprContext(PrimaryContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterParenExpr(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitParenExpr(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor).visitParenExpr(
                this
            );
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class NullLiteralContext extends PrimaryContext {
        public TerminalNode NULL() {
            return getToken(PiescriptAntlrParser.NULL, 0);
        }

        public NullLiteralContext(PrimaryContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterNullLiteral(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitNullLiteral(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor)
                .visitNullLiteral(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class IntegerLiteralContext extends PrimaryContext {
        public TerminalNode INTEGER_LITERAL() {
            return getToken(PiescriptAntlrParser.INTEGER_LITERAL, 0);
        }

        public IntegerLiteralContext(PrimaryContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterIntegerLiteral(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitIntegerLiteral(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor)
                .visitIntegerLiteral(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class UpdateSugarContext extends PrimaryContext {
        public TerminalNode LBRACE() {
            return getToken(PiescriptAntlrParser.LBRACE, 0);
        }

        public TerminalNode UNDERSCORE() {
            return getToken(PiescriptAntlrParser.UNDERSCORE, 0);
        }

        public TerminalNode BAR() {
            return getToken(PiescriptAntlrParser.BAR, 0);
        }

        public List<RecordUpdateContext> recordUpdate() {
            return getRuleContexts(RecordUpdateContext.class);
        }

        public RecordUpdateContext recordUpdate(int i) {
            return getRuleContext(RecordUpdateContext.class, i);
        }

        public TerminalNode RBRACE() {
            return getToken(PiescriptAntlrParser.RBRACE, 0);
        }

        public List<TerminalNode> COMMA() {
            return getTokens(PiescriptAntlrParser.COMMA);
        }

        public TerminalNode COMMA(int i) {
            return getToken(PiescriptAntlrParser.COMMA, i);
        }

        public UpdateSugarContext(PrimaryContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterUpdateSugar(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitUpdateSugar(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor)
                .visitUpdateSugar(this);
            else return visitor.visitChildren(this);
        }
    }

    public final PrimaryContext primary() throws RecognitionException {
        return primary(0);
    }

    private PrimaryContext primary(int _p) throws RecognitionException {
        ParserRuleContext _parentctx = _ctx;
        int _parentState = getState();
        PrimaryContext _localctx = new PrimaryContext(_ctx, _parentState);
        PrimaryContext _prevctx = _localctx;
        int _startState = 28;
        enterRecursionRule(_localctx, 28, RULE_primary, _p);
        int _la;
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                setState(274);
                _errHandler.sync(this);
                switch (getInterpreter().adaptivePredict(_input, 19, _ctx)) {
                    case 1: {
                        _localctx = new AccessorContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;

                        setState(206);
                        match(DOT);
                        setState(207);
                        ident();
                    }
                        break;
                    case 2: {
                        _localctx = new IntegerLiteralContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(208);
                        match(INTEGER_LITERAL);
                    }
                        break;
                    case 3: {
                        _localctx = new DecimalLiteralContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(209);
                        match(DECIMAL_LITERAL);
                    }
                        break;
                    case 4: {
                        _localctx = new StringLiteralContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(210);
                        match(QUOTED_STRING);
                    }
                        break;
                    case 5: {
                        _localctx = new TrueLiteralContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(211);
                        match(TRUE);
                    }
                        break;
                    case 6: {
                        _localctx = new FalseLiteralContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(212);
                        match(FALSE);
                    }
                        break;
                    case 7: {
                        _localctx = new NullLiteralContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(213);
                        match(NULL);
                    }
                        break;
                    case 8: {
                        _localctx = new VariableContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(214);
                        ident();
                    }
                        break;
                    case 9: {
                        _localctx = new AscriptionContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(215);
                        match(LPAREN);
                        setState(216);
                        expr();
                        setState(217);
                        match(COLON);
                        setState(218);
                        type();
                        setState(219);
                        match(RPAREN);
                    }
                        break;
                    case 10: {
                        _localctx = new ParenExprContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(221);
                        match(LPAREN);
                        setState(222);
                        expr();
                        setState(223);
                        match(RPAREN);
                    }
                        break;
                    case 11: {
                        _localctx = new EmptyRecordContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(225);
                        match(LBRACE);
                        setState(226);
                        match(RBRACE);
                    }
                        break;
                    case 12: {
                        _localctx = new RecordLiteralContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(227);
                        match(LBRACE);
                        setState(228);
                        recordField();
                        setState(233);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        while (_la == COMMA) {
                            {
                                {
                                    setState(229);
                                    match(COMMA);
                                    setState(230);
                                    recordField();
                                }
                            }
                            setState(235);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                        }
                        setState(236);
                        match(RBRACE);
                    }
                        break;
                    case 13: {
                        _localctx = new RecordUpdateExprContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(238);
                        match(LBRACE);
                        setState(239);
                        expr();
                        setState(240);
                        match(BAR);
                        setState(241);
                        recordUpdate();
                        setState(246);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        while (_la == COMMA) {
                            {
                                {
                                    setState(242);
                                    match(COMMA);
                                    setState(243);
                                    recordUpdate();
                                }
                            }
                            setState(248);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                        }
                        setState(249);
                        match(RBRACE);
                    }
                        break;
                    case 14: {
                        _localctx = new UpdateSugarContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(251);
                        match(LBRACE);
                        setState(252);
                        match(UNDERSCORE);
                        setState(253);
                        match(BAR);
                        setState(254);
                        recordUpdate();
                        setState(259);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        while (_la == COMMA) {
                            {
                                {
                                    setState(255);
                                    match(COMMA);
                                    setState(256);
                                    recordUpdate();
                                }
                            }
                            setState(261);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                        }
                        setState(262);
                        match(RBRACE);
                    }
                        break;
                    case 15: {
                        _localctx = new IfExprContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(264);
                        match(IF);
                        setState(265);
                        expr();
                        setState(266);
                        match(THEN);
                        setState(267);
                        expr();
                        setState(268);
                        match(ELSE);
                        setState(269);
                        expr();
                    }
                        break;
                    case 16: {
                        _localctx = new QueryExprContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(271);
                        match(QUERY);
                        setState(272);
                        match(ESQL_BODY);
                    }
                        break;
                    case 17: {
                        _localctx = new BlockExprContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(273);
                        block();
                    }
                        break;
                }
                _ctx.stop = _input.LT(-1);
                setState(281);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 20, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        if (_parseListeners != null) triggerExitRuleEvent();
                        _prevctx = _localctx;
                        {
                            {
                                _localctx = new ProjectionContext(new PrimaryContext(_parentctx, _parentState));
                                pushNewRecursionContext(_localctx, _startState, RULE_primary);
                                setState(276);
                                if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
                                setState(277);
                                match(DOT);
                                setState(278);
                                ident();
                            }
                        }
                    }
                    setState(283);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 20, _ctx);
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            unrollRecursionContexts(_parentctx);
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class RecordFieldContext extends ParserRuleContext {
        public IdentContext ident() {
            return getRuleContext(IdentContext.class, 0);
        }

        public TerminalNode COLON() {
            return getToken(PiescriptAntlrParser.COLON, 0);
        }

        public ExprContext expr() {
            return getRuleContext(ExprContext.class, 0);
        }

        public RecordFieldContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_recordField;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterRecordField(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitRecordField(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor)
                .visitRecordField(this);
            else return visitor.visitChildren(this);
        }
    }

    public final RecordFieldContext recordField() throws RecognitionException {
        RecordFieldContext _localctx = new RecordFieldContext(_ctx, getState());
        enterRule(_localctx, 30, RULE_recordField);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(284);
                ident();
                setState(285);
                match(COLON);
                setState(286);
                expr();
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class RecordUpdateContext extends ParserRuleContext {
        public IdentContext ident() {
            return getRuleContext(IdentContext.class, 0);
        }

        public TerminalNode ASSIGN() {
            return getToken(PiescriptAntlrParser.ASSIGN, 0);
        }

        public ExprContext expr() {
            return getRuleContext(ExprContext.class, 0);
        }

        public RecordUpdateContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_recordUpdate;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterRecordUpdate(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitRecordUpdate(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor)
                .visitRecordUpdate(this);
            else return visitor.visitChildren(this);
        }
    }

    public final RecordUpdateContext recordUpdate() throws RecognitionException {
        RecordUpdateContext _localctx = new RecordUpdateContext(_ctx, getState());
        enterRule(_localctx, 32, RULE_recordUpdate);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(288);
                ident();
                setState(289);
                match(ASSIGN);
                setState(290);
                expr();
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class BlockContext extends ParserRuleContext {
        public TerminalNode LBRACE() {
            return getToken(PiescriptAntlrParser.LBRACE, 0);
        }

        public ExprContext expr() {
            return getRuleContext(ExprContext.class, 0);
        }

        public TerminalNode RBRACE() {
            return getToken(PiescriptAntlrParser.RBRACE, 0);
        }

        public List<BlockStmtContext> blockStmt() {
            return getRuleContexts(BlockStmtContext.class);
        }

        public BlockStmtContext blockStmt(int i) {
            return getRuleContext(BlockStmtContext.class, i);
        }

        public BlockContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_block;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterBlock(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitBlock(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor).visitBlock(
                this
            );
            else return visitor.visitChildren(this);
        }
    }

    public final BlockContext block() throws RecognitionException {
        BlockContext _localctx = new BlockContext(_ctx, getState());
        enterRule(_localctx, 34, RULE_block);
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                setState(292);
                match(LBRACE);
                setState(294);
                _errHandler.sync(this);
                _alt = 1;
                do {
                    switch (_alt) {
                        case 1: {
                            {
                                setState(293);
                                blockStmt();
                            }
                        }
                            break;
                        default:
                            throw new NoViableAltException(this);
                    }
                    setState(296);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 21, _ctx);
                } while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER);
                setState(298);
                expr();
                setState(299);
                match(RBRACE);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class BlockStmtContext extends ParserRuleContext {
        public BlockStmtContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_blockStmt;
        }

        public BlockStmtContext() {}

        public void copyFrom(BlockStmtContext ctx) {
            super.copyFrom(ctx);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class BlockLetContext extends BlockStmtContext {
        public TerminalNode LET() {
            return getToken(PiescriptAntlrParser.LET, 0);
        }

        public IdentContext ident() {
            return getRuleContext(IdentContext.class, 0);
        }

        public TerminalNode ASSIGN() {
            return getToken(PiescriptAntlrParser.ASSIGN, 0);
        }

        public ExprContext expr() {
            return getRuleContext(ExprContext.class, 0);
        }

        public TerminalNode SEMICOLON() {
            return getToken(PiescriptAntlrParser.SEMICOLON, 0);
        }

        public TerminalNode COLON() {
            return getToken(PiescriptAntlrParser.COLON, 0);
        }

        public TypeContext type() {
            return getRuleContext(TypeContext.class, 0);
        }

        public BlockLetContext(BlockStmtContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterBlockLet(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitBlockLet(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor).visitBlockLet(
                this
            );
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class BlockExprStmtContext extends BlockStmtContext {
        public ExprContext expr() {
            return getRuleContext(ExprContext.class, 0);
        }

        public TerminalNode SEMICOLON() {
            return getToken(PiescriptAntlrParser.SEMICOLON, 0);
        }

        public BlockExprStmtContext(BlockStmtContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterBlockExprStmt(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitBlockExprStmt(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor)
                .visitBlockExprStmt(this);
            else return visitor.visitChildren(this);
        }
    }

    public final BlockStmtContext blockStmt() throws RecognitionException {
        BlockStmtContext _localctx = new BlockStmtContext(_ctx, getState());
        enterRule(_localctx, 36, RULE_blockStmt);
        int _la;
        try {
            setState(314);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 23, _ctx)) {
                case 1:
                    _localctx = new BlockLetContext(_localctx);
                    enterOuterAlt(_localctx, 1); {
                    setState(301);
                    match(LET);
                    setState(302);
                    ident();
                    setState(305);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                    if (_la == COLON) {
                        {
                            setState(303);
                            match(COLON);
                            setState(304);
                            type();
                        }
                    }

                    setState(307);
                    match(ASSIGN);
                    setState(308);
                    expr();
                    setState(309);
                    match(SEMICOLON);
                }
                    break;
                case 2:
                    _localctx = new BlockExprStmtContext(_localctx);
                    enterOuterAlt(_localctx, 2); {
                    setState(311);
                    expr();
                    setState(312);
                    match(SEMICOLON);
                }
                    break;
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class ParamContext extends ParserRuleContext {
        public ParamContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_param;
        }

        public ParamContext() {}

        public void copyFrom(ParamContext ctx) {
            super.copyFrom(ctx);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class TypedParamContext extends ParamContext {
        public TerminalNode LPAREN() {
            return getToken(PiescriptAntlrParser.LPAREN, 0);
        }

        public IdentContext ident() {
            return getRuleContext(IdentContext.class, 0);
        }

        public TerminalNode COLON() {
            return getToken(PiescriptAntlrParser.COLON, 0);
        }

        public TypeContext type() {
            return getRuleContext(TypeContext.class, 0);
        }

        public TerminalNode RPAREN() {
            return getToken(PiescriptAntlrParser.RPAREN, 0);
        }

        public TypedParamContext(ParamContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterTypedParam(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitTypedParam(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor).visitTypedParam(
                this
            );
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class UntypedParamContext extends ParamContext {
        public IdentContext ident() {
            return getRuleContext(IdentContext.class, 0);
        }

        public UntypedParamContext(ParamContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterUntypedParam(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitUntypedParam(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor)
                .visitUntypedParam(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ParamContext param() throws RecognitionException {
        ParamContext _localctx = new ParamContext(_ctx, getState());
        enterRule(_localctx, 38, RULE_param);
        try {
            setState(323);
            _errHandler.sync(this);
            switch (_input.LA(1)) {
                case UPPER_IDENT:
                case LOWER_IDENT:
                    _localctx = new UntypedParamContext(_localctx);
                    enterOuterAlt(_localctx, 1); {
                    setState(316);
                    ident();
                }
                    break;
                case LPAREN:
                    _localctx = new TypedParamContext(_localctx);
                    enterOuterAlt(_localctx, 2); {
                    setState(317);
                    match(LPAREN);
                    setState(318);
                    ident();
                    setState(319);
                    match(COLON);
                    setState(320);
                    type();
                    setState(321);
                    match(RPAREN);
                }
                    break;
                default:
                    throw new NoViableAltException(this);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class TypeContext extends ParserRuleContext {
        public TypeContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_type;
        }

        public TypeContext() {}

        public void copyFrom(TypeContext ctx) {
            super.copyFrom(ctx);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class FunctionTypeContext extends TypeContext {
        public TypeAppContext typeApp() {
            return getRuleContext(TypeAppContext.class, 0);
        }

        public TerminalNode ARROW() {
            return getToken(PiescriptAntlrParser.ARROW, 0);
        }

        public TypeContext type() {
            return getRuleContext(TypeContext.class, 0);
        }

        public FunctionTypeContext(TypeContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterFunctionType(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitFunctionType(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor)
                .visitFunctionType(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class TypeNonArrowContext extends TypeContext {
        public TypeAppContext typeApp() {
            return getRuleContext(TypeAppContext.class, 0);
        }

        public TypeNonArrowContext(TypeContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterTypeNonArrow(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitTypeNonArrow(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor)
                .visitTypeNonArrow(this);
            else return visitor.visitChildren(this);
        }
    }

    public final TypeContext type() throws RecognitionException {
        TypeContext _localctx = new TypeContext(_ctx, getState());
        enterRule(_localctx, 40, RULE_type);
        try {
            setState(330);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 25, _ctx)) {
                case 1:
                    _localctx = new FunctionTypeContext(_localctx);
                    enterOuterAlt(_localctx, 1); {
                    setState(325);
                    typeApp(0);
                    setState(326);
                    match(ARROW);
                    setState(327);
                    type();
                }
                    break;
                case 2:
                    _localctx = new TypeNonArrowContext(_localctx);
                    enterOuterAlt(_localctx, 2); {
                    setState(329);
                    typeApp(0);
                }
                    break;
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class TypeAppContext extends ParserRuleContext {
        public TypeAppContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_typeApp;
        }

        public TypeAppContext() {}

        public void copyFrom(TypeAppContext ctx) {
            super.copyFrom(ctx);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class TypeAppPassthroughContext extends TypeAppContext {
        public TypeAtomContext typeAtom() {
            return getRuleContext(TypeAtomContext.class, 0);
        }

        public TypeAppPassthroughContext(TypeAppContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterTypeAppPassthrough(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitTypeAppPassthrough(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor)
                .visitTypeAppPassthrough(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class TypeApplicationContext extends TypeAppContext {
        public TypeAppContext typeApp() {
            return getRuleContext(TypeAppContext.class, 0);
        }

        public TypeAtomContext typeAtom() {
            return getRuleContext(TypeAtomContext.class, 0);
        }

        public TypeApplicationContext(TypeAppContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterTypeApplication(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitTypeApplication(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor)
                .visitTypeApplication(this);
            else return visitor.visitChildren(this);
        }
    }

    public final TypeAppContext typeApp() throws RecognitionException {
        return typeApp(0);
    }

    private TypeAppContext typeApp(int _p) throws RecognitionException {
        ParserRuleContext _parentctx = _ctx;
        int _parentState = getState();
        TypeAppContext _localctx = new TypeAppContext(_ctx, _parentState);
        TypeAppContext _prevctx = _localctx;
        int _startState = 42;
        enterRecursionRule(_localctx, 42, RULE_typeApp, _p);
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                {
                    _localctx = new TypeAppPassthroughContext(_localctx);
                    _ctx = _localctx;
                    _prevctx = _localctx;

                    setState(333);
                    typeAtom();
                }
                _ctx.stop = _input.LT(-1);
                setState(339);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 26, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        if (_parseListeners != null) triggerExitRuleEvent();
                        _prevctx = _localctx;
                        {
                            {
                                _localctx = new TypeApplicationContext(new TypeAppContext(_parentctx, _parentState));
                                pushNewRecursionContext(_localctx, _startState, RULE_typeApp);
                                setState(335);
                                if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
                                setState(336);
                                typeAtom();
                            }
                        }
                    }
                    setState(341);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 26, _ctx);
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            unrollRecursionContexts(_parentctx);
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class TypeAtomContext extends ParserRuleContext {
        public TypeAtomContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_typeAtom;
        }

        public TypeAtomContext() {}

        public void copyFrom(TypeAtomContext ctx) {
            super.copyFrom(ctx);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class TypeVarContext extends TypeAtomContext {
        public TerminalNode LOWER_IDENT() {
            return getToken(PiescriptAntlrParser.LOWER_IDENT, 0);
        }

        public TypeVarContext(TypeAtomContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterTypeVar(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitTypeVar(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor).visitTypeVar(
                this
            );
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class RecordTypeContext extends TypeAtomContext {
        public TerminalNode LBRACE() {
            return getToken(PiescriptAntlrParser.LBRACE, 0);
        }

        public RowTypeContext rowType() {
            return getRuleContext(RowTypeContext.class, 0);
        }

        public TerminalNode RBRACE() {
            return getToken(PiescriptAntlrParser.RBRACE, 0);
        }

        public RecordTypeContext(TypeAtomContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterRecordType(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitRecordType(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor).visitRecordType(
                this
            );
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class TypeConContext extends TypeAtomContext {
        public TerminalNode UPPER_IDENT() {
            return getToken(PiescriptAntlrParser.UPPER_IDENT, 0);
        }

        public TypeConContext(TypeAtomContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterTypeCon(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitTypeCon(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor).visitTypeCon(
                this
            );
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class ParenTypeContext extends TypeAtomContext {
        public TerminalNode LPAREN() {
            return getToken(PiescriptAntlrParser.LPAREN, 0);
        }

        public TypeContext type() {
            return getRuleContext(TypeContext.class, 0);
        }

        public TerminalNode RPAREN() {
            return getToken(PiescriptAntlrParser.RPAREN, 0);
        }

        public ParenTypeContext(TypeAtomContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterParenType(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitParenType(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor).visitParenType(
                this
            );
            else return visitor.visitChildren(this);
        }
    }

    public final TypeAtomContext typeAtom() throws RecognitionException {
        TypeAtomContext _localctx = new TypeAtomContext(_ctx, getState());
        enterRule(_localctx, 44, RULE_typeAtom);
        try {
            setState(352);
            _errHandler.sync(this);
            switch (_input.LA(1)) {
                case UPPER_IDENT:
                    _localctx = new TypeConContext(_localctx);
                    enterOuterAlt(_localctx, 1); {
                    setState(342);
                    match(UPPER_IDENT);
                }
                    break;
                case LOWER_IDENT:
                    _localctx = new TypeVarContext(_localctx);
                    enterOuterAlt(_localctx, 2); {
                    setState(343);
                    match(LOWER_IDENT);
                }
                    break;
                case LBRACE:
                    _localctx = new RecordTypeContext(_localctx);
                    enterOuterAlt(_localctx, 3); {
                    setState(344);
                    match(LBRACE);
                    setState(345);
                    rowType();
                    setState(346);
                    match(RBRACE);
                }
                    break;
                case LPAREN:
                    _localctx = new ParenTypeContext(_localctx);
                    enterOuterAlt(_localctx, 4); {
                    setState(348);
                    match(LPAREN);
                    setState(349);
                    type();
                    setState(350);
                    match(RPAREN);
                }
                    break;
                default:
                    throw new NoViableAltException(this);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class RowTypeContext extends ParserRuleContext {
        public List<RowFieldContext> rowField() {
            return getRuleContexts(RowFieldContext.class);
        }

        public RowFieldContext rowField(int i) {
            return getRuleContext(RowFieldContext.class, i);
        }

        public List<TerminalNode> COMMA() {
            return getTokens(PiescriptAntlrParser.COMMA);
        }

        public TerminalNode COMMA(int i) {
            return getToken(PiescriptAntlrParser.COMMA, i);
        }

        public TerminalNode BAR() {
            return getToken(PiescriptAntlrParser.BAR, 0);
        }

        public TerminalNode LOWER_IDENT() {
            return getToken(PiescriptAntlrParser.LOWER_IDENT, 0);
        }

        public RowTypeContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_rowType;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterRowType(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitRowType(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor).visitRowType(
                this
            );
            else return visitor.visitChildren(this);
        }
    }

    public final RowTypeContext rowType() throws RecognitionException {
        RowTypeContext _localctx = new RowTypeContext(_ctx, getState());
        enterRule(_localctx, 46, RULE_rowType);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(354);
                rowField();
                setState(359);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == COMMA) {
                    {
                        {
                            setState(355);
                            match(COMMA);
                            setState(356);
                            rowField();
                        }
                    }
                    setState(361);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
                setState(364);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == BAR) {
                    {
                        setState(362);
                        match(BAR);
                        setState(363);
                        match(LOWER_IDENT);
                    }
                }

            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class RowFieldContext extends ParserRuleContext {
        public IdentContext ident() {
            return getRuleContext(IdentContext.class, 0);
        }

        public TerminalNode COLON() {
            return getToken(PiescriptAntlrParser.COLON, 0);
        }

        public TypeContext type() {
            return getRuleContext(TypeContext.class, 0);
        }

        public RowFieldContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_rowField;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterRowField(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitRowField(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor).visitRowField(
                this
            );
            else return visitor.visitChildren(this);
        }
    }

    public final RowFieldContext rowField() throws RecognitionException {
        RowFieldContext _localctx = new RowFieldContext(_ctx, getState());
        enterRule(_localctx, 48, RULE_rowField);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(366);
                ident();
                setState(367);
                match(COLON);
                setState(368);
                type();
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
        switch (ruleIndex) {
            case 5:
                return pipeExpr_sempred((PipeExprContext) _localctx, predIndex);
            case 6:
                return orExpr_sempred((OrExprContext) _localctx, predIndex);
            case 7:
                return andExpr_sempred((AndExprContext) _localctx, predIndex);
            case 10:
                return addExpr_sempred((AddExprContext) _localctx, predIndex);
            case 11:
                return mulExpr_sempred((MulExprContext) _localctx, predIndex);
            case 13:
                return appExpr_sempred((AppExprContext) _localctx, predIndex);
            case 14:
                return primary_sempred((PrimaryContext) _localctx, predIndex);
            case 21:
                return typeApp_sempred((TypeAppContext) _localctx, predIndex);
        }
        return true;
    }

    private boolean pipeExpr_sempred(PipeExprContext _localctx, int predIndex) {
        switch (predIndex) {
            case 0:
                return precpred(_ctx, 1);
        }
        return true;
    }

    private boolean orExpr_sempred(OrExprContext _localctx, int predIndex) {
        switch (predIndex) {
            case 1:
                return precpred(_ctx, 1);
        }
        return true;
    }

    private boolean andExpr_sempred(AndExprContext _localctx, int predIndex) {
        switch (predIndex) {
            case 2:
                return precpred(_ctx, 1);
        }
        return true;
    }

    private boolean addExpr_sempred(AddExprContext _localctx, int predIndex) {
        switch (predIndex) {
            case 3:
                return precpred(_ctx, 1);
        }
        return true;
    }

    private boolean mulExpr_sempred(MulExprContext _localctx, int predIndex) {
        switch (predIndex) {
            case 4:
                return precpred(_ctx, 1);
        }
        return true;
    }

    private boolean appExpr_sempred(AppExprContext _localctx, int predIndex) {
        switch (predIndex) {
            case 5:
                return precpred(_ctx, 1);
        }
        return true;
    }

    private boolean primary_sempred(PrimaryContext _localctx, int predIndex) {
        switch (predIndex) {
            case 6:
                return precpred(_ctx, 1);
        }
        return true;
    }

    private boolean typeApp_sempred(TypeAppContext _localctx, int predIndex) {
        switch (predIndex) {
            case 7:
                return precpred(_ctx, 2);
        }
        return true;
    }

    public static final String _serializedATN = "\u0004\u00017\u0173\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"
        + "\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"
        + "\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"
        + "\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"
        + "\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"
        + "\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"
        + "\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"
        + "\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018"
        + "\u0001\u0000\u0001\u0000\u0001\u0001\u0005\u00016\b\u0001\n\u0001\f\u0001"
        + "9\t\u0001\u0001\u0001\u0001\u0001\u0003\u0001=\b\u0001\u0001\u0001\u0001"
        + "\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0003\u0002E\b"
        + "\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001"
        + "\u0003\u0001\u0003\u0001\u0003\u0003\u0003O\b\u0003\u0001\u0003\u0001"
        + "\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0004"
        + "\u0003X\b\u0003\u000b\u0003\f\u0003Y\u0001\u0003\u0001\u0003\u0001\u0003"
        + "\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"
        + "\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0005\u0003"
        + "j\b\u0003\n\u0003\f\u0003m\t\u0003\u0001\u0003\u0001\u0003\u0001\u0003"
        + "\u0001\u0003\u0003\u0003s\b\u0003\u0001\u0004\u0001\u0004\u0001\u0004"
        + "\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005"
        + "\u0001\u0005\u0001\u0005\u0005\u0005\u0080\b\u0005\n\u0005\f\u0005\u0083"
        + "\t\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001"
        + "\u0006\u0005\u0006\u008b\b\u0006\n\u0006\f\u0006\u008e\t\u0006\u0001\u0007"
        + "\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0005\u0007"
        + "\u0096\b\u0007\n\u0007\f\u0007\u0099\t\u0007\u0001\b\u0001\b\u0001\b\u0001"
        + "\b\u0001\b\u0003\b\u00a0\b\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0003"
        + "\t\u00a7\b\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0005\n\u00af"
        + "\b\n\n\n\f\n\u00b2\t\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b"
        + "\u0001\u000b\u0001\u000b\u0005\u000b\u00ba\b\u000b\n\u000b\f\u000b\u00bd"
        + "\t\u000b\u0001\f\u0001\f\u0001\f\u0003\f\u00c2\b\f\u0001\r\u0001\r\u0001"
        + "\r\u0001\r\u0001\r\u0005\r\u00c9\b\r\n\r\f\r\u00cc\t\r\u0001\u000e\u0001"
        + "\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001"
        + "\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001"
        + "\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001"
        + "\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001"
        + "\u000e\u0005\u000e\u00e8\b\u000e\n\u000e\f\u000e\u00eb\t\u000e\u0001\u000e"
        + "\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e"
        + "\u0001\u000e\u0005\u000e\u00f5\b\u000e\n\u000e\f\u000e\u00f8\t\u000e\u0001"
        + "\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001"
        + "\u000e\u0001\u000e\u0005\u000e\u0102\b\u000e\n\u000e\f\u000e\u0105\t\u000e"
        + "\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e"
        + "\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e"
        + "\u0003\u000e\u0113\b\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0005\u000e"
        + "\u0118\b\u000e\n\u000e\f\u000e\u011b\t\u000e\u0001\u000f\u0001\u000f\u0001"
        + "\u000f\u0001\u000f\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001"
        + "\u0011\u0001\u0011\u0004\u0011\u0127\b\u0011\u000b\u0011\f\u0011\u0128"
        + "\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0012\u0001\u0012\u0001\u0012"
        + "\u0001\u0012\u0003\u0012\u0132\b\u0012\u0001\u0012\u0001\u0012\u0001\u0012"
        + "\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0003\u0012\u013b\b\u0012"
        + "\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013"
        + "\u0001\u0013\u0003\u0013\u0144\b\u0013\u0001\u0014\u0001\u0014\u0001\u0014"
        + "\u0001\u0014\u0001\u0014\u0003\u0014\u014b\b\u0014\u0001\u0015\u0001\u0015"
        + "\u0001\u0015\u0001\u0015\u0001\u0015\u0005\u0015\u0152\b\u0015\n\u0015"
        + "\f\u0015\u0155\t\u0015\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016"
        + "\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016"
        + "\u0003\u0016\u0161\b\u0016\u0001\u0017\u0001\u0017\u0001\u0017\u0005\u0017"
        + "\u0166\b\u0017\n\u0017\f\u0017\u0169\t\u0017\u0001\u0017\u0001\u0017\u0003"
        + "\u0017\u016d\b\u0017\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001"
        + "\u0018\u0000\b\n\f\u000e\u0014\u0016\u001a\u001c*\u0019\u0000\u0002\u0004"
        + "\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e \""
        + "$&(*,.0\u0000\u0006\u0001\u000012\u0001\u0000\u0017\u0018\u0001\u0000"
        + "\u0019\u001c\u0001\u0000\u001d\u001e\u0001\u0000\u001f!\u0002\u0000\u001e"
        + "\u001e\"\"\u018d\u00002\u0001\u0000\u0000\u0000\u00027\u0001\u0000\u0000"
        + "\u0000\u0004@\u0001\u0000\u0000\u0000\u0006r\u0001\u0000\u0000\u0000\b"
        + "t\u0001\u0000\u0000\u0000\ny\u0001\u0000\u0000\u0000\f\u0084\u0001\u0000"
        + "\u0000\u0000\u000e\u008f\u0001\u0000\u0000\u0000\u0010\u009f\u0001\u0000"
        + "\u0000\u0000\u0012\u00a6\u0001\u0000\u0000\u0000\u0014\u00a8\u0001\u0000"
        + "\u0000\u0000\u0016\u00b3\u0001\u0000\u0000\u0000\u0018\u00c1\u0001\u0000"
        + "\u0000\u0000\u001a\u00c3\u0001\u0000\u0000\u0000\u001c\u0112\u0001\u0000"
        + "\u0000\u0000\u001e\u011c\u0001\u0000\u0000\u0000 \u0120\u0001\u0000\u0000"
        + "\u0000\"\u0124\u0001\u0000\u0000\u0000$\u013a\u0001\u0000\u0000\u0000"
        + "&\u0143\u0001\u0000\u0000\u0000(\u014a\u0001\u0000\u0000\u0000*\u014c"
        + "\u0001\u0000\u0000\u0000,\u0160\u0001\u0000\u0000\u0000.\u0162\u0001\u0000"
        + "\u0000\u00000\u016e\u0001\u0000\u0000\u000023\u0007\u0000\u0000\u0000"
        + "3\u0001\u0001\u0000\u0000\u000046\u0003\u0004\u0002\u000054\u0001\u0000"
        + "\u0000\u000069\u0001\u0000\u0000\u000075\u0001\u0000\u0000\u000078\u0001"
        + "\u0000\u0000\u00008:\u0001\u0000\u0000\u000097\u0001\u0000\u0000\u0000"
        + ":<\u0003\u0006\u0003\u0000;=\u0005&\u0000\u0000<;\u0001\u0000\u0000\u0000"
        + "<=\u0001\u0000\u0000\u0000=>\u0001\u0000\u0000\u0000>?\u0005\u0000\u0000"
        + "\u0001?\u0003\u0001\u0000\u0000\u0000@A\u0005\u0001\u0000\u0000AD\u0003"
        + "\u0000\u0000\u0000BC\u0005%\u0000\u0000CE\u0003(\u0014\u0000DB\u0001\u0000"
        + "\u0000\u0000DE\u0001\u0000\u0000\u0000EF\u0001\u0000\u0000\u0000FG\u0005"
        + "\'\u0000\u0000GH\u0003\u0006\u0003\u0000HI\u0005&\u0000\u0000I\u0005\u0001"
        + "\u0000\u0000\u0000JK\u0005\u0001\u0000\u0000KN\u0003\u0000\u0000\u0000"
        + "LM\u0005%\u0000\u0000MO\u0003(\u0014\u0000NL\u0001\u0000\u0000\u0000N"
        + "O\u0001\u0000\u0000\u0000OP\u0001\u0000\u0000\u0000PQ\u0005\'\u0000\u0000"
        + "QR\u0003\u0006\u0003\u0000RS\u0005\u0002\u0000\u0000ST\u0003\u0006\u0003"
        + "\u0000Ts\u0001\u0000\u0000\u0000UW\u0005\u0003\u0000\u0000VX\u0003&\u0013"
        + "\u0000WV\u0001\u0000\u0000\u0000XY\u0001\u0000\u0000\u0000YW\u0001\u0000"
        + "\u0000\u0000YZ\u0001\u0000\u0000\u0000Z[\u0001\u0000\u0000\u0000[\\\u0005"
        + "\u0016\u0000\u0000\\]\u0003\u0006\u0003\u0000]s\u0001\u0000\u0000\u0000"
        + "^s\u0005\f\u0000\u0000_`\u0005\r\u0000\u0000`s\u0003\u0006\u0003\u0000"
        + "ab\u0005\u000e\u0000\u0000bc\u0003\u001c\u000e\u0000cd\u0003\u0006\u0003"
        + "\u0000ds\u0001\u0000\u0000\u0000ef\u0005\u000f\u0000\u0000fk\u0003\b\u0004"
        + "\u0000gh\u0005(\u0000\u0000hj\u0003\b\u0004\u0000ig\u0001\u0000\u0000"
        + "\u0000jm\u0001\u0000\u0000\u0000ki\u0001\u0000\u0000\u0000kl\u0001\u0000"
        + "\u0000\u0000ln\u0001\u0000\u0000\u0000mk\u0001\u0000\u0000\u0000no\u0005"
        + "\u0016\u0000\u0000op\u0003\u0006\u0003\u0000ps\u0001\u0000\u0000\u0000"
        + "qs\u0003\n\u0005\u0000rJ\u0001\u0000\u0000\u0000rU\u0001\u0000\u0000\u0000"
        + "r^\u0001\u0000\u0000\u0000r_\u0001\u0000\u0000\u0000ra\u0001\u0000\u0000"
        + "\u0000re\u0001\u0000\u0000\u0000rq\u0001\u0000\u0000\u0000s\u0007\u0001"
        + "\u0000\u0000\u0000tu\u0005*\u0000\u0000uv\u0003\u0006\u0003\u0000vw\u0003"
        + "\u0000\u0000\u0000wx\u0005+\u0000\u0000x\t\u0001\u0000\u0000\u0000yz\u0006"
        + "\u0005\uffff\uffff\u0000z{\u0003\f\u0006\u0000{\u0081\u0001\u0000\u0000"
        + "\u0000|}\n\u0001\u0000\u0000}~\u0005\u0013\u0000\u0000~\u0080\u0003\f"
        + "\u0006\u0000\u007f|\u0001\u0000\u0000\u0000\u0080\u0083\u0001\u0000\u0000"
        + "\u0000\u0081\u007f\u0001\u0000\u0000\u0000\u0081\u0082\u0001\u0000\u0000"
        + "\u0000\u0082\u000b\u0001\u0000\u0000\u0000\u0083\u0081\u0001\u0000\u0000"
        + "\u0000\u0084\u0085\u0006\u0006\uffff\uffff\u0000\u0085\u0086\u0003\u000e"
        + "\u0007\u0000\u0086\u008c\u0001\u0000\u0000\u0000\u0087\u0088\n\u0001\u0000"
        + "\u0000\u0088\u0089\u0005\u0014\u0000\u0000\u0089\u008b\u0003\u000e\u0007"
        + "\u0000\u008a\u0087\u0001\u0000\u0000\u0000\u008b\u008e\u0001\u0000\u0000"
        + "\u0000\u008c\u008a\u0001\u0000\u0000\u0000\u008c\u008d\u0001\u0000\u0000"
        + "\u0000\u008d\r\u0001\u0000\u0000\u0000\u008e\u008c\u0001\u0000\u0000\u0000"
        + "\u008f\u0090\u0006\u0007\uffff\uffff\u0000\u0090\u0091\u0003\u0010\b\u0000"
        + "\u0091\u0097\u0001\u0000\u0000\u0000\u0092\u0093\n\u0001\u0000\u0000\u0093"
        + "\u0094\u0005\u0015\u0000\u0000\u0094\u0096\u0003\u0010\b\u0000\u0095\u0092"
        + "\u0001\u0000\u0000\u0000\u0096\u0099\u0001\u0000\u0000\u0000\u0097\u0095"
        + "\u0001\u0000\u0000\u0000\u0097\u0098\u0001\u0000\u0000\u0000\u0098\u000f"
        + "\u0001\u0000\u0000\u0000\u0099\u0097\u0001\u0000\u0000\u0000\u009a\u009b"
        + "\u0003\u0012\t\u0000\u009b\u009c\u0007\u0001\u0000\u0000\u009c\u009d\u0003"
        + "\u0012\t\u0000\u009d\u00a0\u0001\u0000\u0000\u0000\u009e\u00a0\u0003\u0012"
        + "\t\u0000\u009f\u009a\u0001\u0000\u0000\u0000\u009f\u009e\u0001\u0000\u0000"
        + "\u0000\u00a0\u0011\u0001\u0000\u0000\u0000\u00a1\u00a2\u0003\u0014\n\u0000"
        + "\u00a2\u00a3\u0007\u0002\u0000\u0000\u00a3\u00a4\u0003\u0014\n\u0000\u00a4"
        + "\u00a7\u0001\u0000\u0000\u0000\u00a5\u00a7\u0003\u0014\n\u0000\u00a6\u00a1"
        + "\u0001\u0000\u0000\u0000\u00a6\u00a5\u0001\u0000\u0000\u0000\u00a7\u0013"
        + "\u0001\u0000\u0000\u0000\u00a8\u00a9\u0006\n\uffff\uffff\u0000\u00a9\u00aa"
        + "\u0003\u0016\u000b\u0000\u00aa\u00b0\u0001\u0000\u0000\u0000\u00ab\u00ac"
        + "\n\u0001\u0000\u0000\u00ac\u00ad\u0007\u0003\u0000\u0000\u00ad\u00af\u0003"
        + "\u0016\u000b\u0000\u00ae\u00ab\u0001\u0000\u0000\u0000\u00af\u00b2\u0001"
        + "\u0000\u0000\u0000\u00b0\u00ae\u0001\u0000\u0000\u0000\u00b0\u00b1\u0001"
        + "\u0000\u0000\u0000\u00b1\u0015\u0001\u0000\u0000\u0000\u00b2\u00b0\u0001"
        + "\u0000\u0000\u0000\u00b3\u00b4\u0006\u000b\uffff\uffff\u0000\u00b4\u00b5"
        + "\u0003\u0018\f\u0000\u00b5\u00bb\u0001\u0000\u0000\u0000\u00b6\u00b7\n"
        + "\u0001\u0000\u0000\u00b7\u00b8\u0007\u0004\u0000\u0000\u00b8\u00ba\u0003"
        + "\u0018\f\u0000\u00b9\u00b6\u0001\u0000\u0000\u0000\u00ba\u00bd\u0001\u0000"
        + "\u0000\u0000\u00bb\u00b9\u0001\u0000\u0000\u0000\u00bb\u00bc\u0001\u0000"
        + "\u0000\u0000\u00bc\u0017\u0001\u0000\u0000\u0000\u00bd\u00bb\u0001\u0000"
        + "\u0000\u0000\u00be\u00bf\u0007\u0005\u0000\u0000\u00bf\u00c2\u0003\u0018"
        + "\f\u0000\u00c0\u00c2\u0003\u001a\r\u0000\u00c1\u00be\u0001\u0000\u0000"
        + "\u0000\u00c1\u00c0\u0001\u0000\u0000\u0000\u00c2\u0019\u0001\u0000\u0000"
        + "\u0000\u00c3\u00c4\u0006\r\uffff\uffff\u0000\u00c4\u00c5\u0003\u001c\u000e"
        + "\u0000\u00c5\u00ca\u0001\u0000\u0000\u0000\u00c6\u00c7\n\u0001\u0000\u0000"
        + "\u00c7\u00c9\u0003\u001c\u000e\u0000\u00c8\u00c6\u0001\u0000\u0000\u0000"
        + "\u00c9\u00cc\u0001\u0000\u0000\u0000\u00ca\u00c8\u0001\u0000\u0000\u0000"
        + "\u00ca\u00cb\u0001\u0000\u0000\u0000\u00cb\u001b\u0001\u0000\u0000\u0000"
        + "\u00cc\u00ca\u0001\u0000\u0000\u0000\u00cd\u00ce\u0006\u000e\uffff\uffff"
        + "\u0000\u00ce\u00cf\u0005#\u0000\u0000\u00cf\u0113\u0003\u0000\u0000\u0000"
        + "\u00d0\u0113\u0005.\u0000\u0000\u00d1\u0113\u0005/\u0000\u0000\u00d2\u0113"
        + "\u00050\u0000\u0000\u00d3\u0113\u0005\u0007\u0000\u0000\u00d4\u0113\u0005"
        + "\b\u0000\u0000\u00d5\u0113\u0005\t\u0000\u0000\u00d6\u0113\u0003\u0000"
        + "\u0000\u0000\u00d7\u00d8\u0005*\u0000\u0000\u00d8\u00d9\u0003\u0006\u0003"
        + "\u0000\u00d9\u00da\u0005%\u0000\u0000\u00da\u00db\u0003(\u0014\u0000\u00db"
        + "\u00dc\u0005+\u0000\u0000\u00dc\u0113\u0001\u0000\u0000\u0000\u00dd\u00de"
        + "\u0005*\u0000\u0000\u00de\u00df\u0003\u0006\u0003\u0000\u00df\u00e0\u0005"
        + "+\u0000\u0000\u00e0\u0113\u0001\u0000\u0000\u0000\u00e1\u00e2\u0005,\u0000"
        + "\u0000\u00e2\u0113\u0005-\u0000\u0000\u00e3\u00e4\u0005,\u0000\u0000\u00e4"
        + "\u00e9\u0003\u001e\u000f\u0000\u00e5\u00e6\u0005$\u0000\u0000\u00e6\u00e8"
        + "\u0003\u001e\u000f\u0000\u00e7\u00e5\u0001\u0000\u0000\u0000\u00e8\u00eb"
        + "\u0001\u0000\u0000\u0000\u00e9\u00e7\u0001\u0000\u0000\u0000\u00e9\u00ea"
        + "\u0001\u0000\u0000\u0000\u00ea\u00ec\u0001\u0000\u0000\u0000\u00eb\u00e9"
        + "\u0001\u0000\u0000\u0000\u00ec\u00ed\u0005-\u0000\u0000\u00ed\u0113\u0001"
        + "\u0000\u0000\u0000\u00ee\u00ef\u0005,\u0000\u0000\u00ef\u00f0\u0003\u0006"
        + "\u0003\u0000\u00f0\u00f1\u0005)\u0000\u0000\u00f1\u00f6\u0003 \u0010\u0000"
        + "\u00f2\u00f3\u0005$\u0000\u0000\u00f3\u00f5\u0003 \u0010\u0000\u00f4\u00f2"
        + "\u0001\u0000\u0000\u0000\u00f5\u00f8\u0001\u0000\u0000\u0000\u00f6\u00f4"
        + "\u0001\u0000\u0000\u0000\u00f6\u00f7\u0001\u0000\u0000\u0000\u00f7\u00f9"
        + "\u0001\u0000\u0000\u0000\u00f8\u00f6\u0001\u0000\u0000\u0000\u00f9\u00fa"
        + "\u0005-\u0000\u0000\u00fa\u0113\u0001\u0000\u0000\u0000\u00fb\u00fc\u0005"
        + ",\u0000\u0000\u00fc\u00fd\u0005\u0012\u0000\u0000\u00fd\u00fe\u0005)\u0000"
        + "\u0000\u00fe\u0103\u0003 \u0010\u0000\u00ff\u0100\u0005$\u0000\u0000\u0100"
        + "\u0102\u0003 \u0010\u0000\u0101\u00ff\u0001\u0000\u0000\u0000\u0102\u0105"
        + "\u0001\u0000\u0000\u0000\u0103\u0101\u0001\u0000\u0000\u0000\u0103\u0104"
        + "\u0001\u0000\u0000\u0000\u0104\u0106\u0001\u0000\u0000\u0000\u0105\u0103"
        + "\u0001\u0000\u0000\u0000\u0106\u0107\u0005-\u0000\u0000\u0107\u0113\u0001"
        + "\u0000\u0000\u0000\u0108\u0109\u0005\u0004\u0000\u0000\u0109\u010a\u0003"
        + "\u0006\u0003\u0000\u010a\u010b\u0005\u0005\u0000\u0000\u010b\u010c\u0003"
        + "\u0006\u0003\u0000\u010c\u010d\u0005\u0006\u0000\u0000\u010d\u010e\u0003"
        + "\u0006\u0003\u0000\u010e\u0113\u0001\u0000\u0000\u0000\u010f\u0110\u0005"
        + "\u000b\u0000\u0000\u0110\u0113\u00056\u0000\u0000\u0111\u0113\u0003\""
        + "\u0011\u0000\u0112\u00cd\u0001\u0000\u0000\u0000\u0112\u00d0\u0001\u0000"
        + "\u0000\u0000\u0112\u00d1\u0001\u0000\u0000\u0000\u0112\u00d2\u0001\u0000"
        + "\u0000\u0000\u0112\u00d3\u0001\u0000\u0000\u0000\u0112\u00d4\u0001\u0000"
        + "\u0000\u0000\u0112\u00d5\u0001\u0000\u0000\u0000\u0112\u00d6\u0001\u0000"
        + "\u0000\u0000\u0112\u00d7\u0001\u0000\u0000\u0000\u0112\u00dd\u0001\u0000"
        + "\u0000\u0000\u0112\u00e1\u0001\u0000\u0000\u0000\u0112\u00e3\u0001\u0000"
        + "\u0000\u0000\u0112\u00ee\u0001\u0000\u0000\u0000\u0112\u00fb\u0001\u0000"
        + "\u0000\u0000\u0112\u0108\u0001\u0000\u0000\u0000\u0112\u010f\u0001\u0000"
        + "\u0000\u0000\u0112\u0111\u0001\u0000\u0000\u0000\u0113\u0119\u0001\u0000"
        + "\u0000\u0000\u0114\u0115\n\u0001\u0000\u0000\u0115\u0116\u0005#\u0000"
        + "\u0000\u0116\u0118\u0003\u0000\u0000\u0000\u0117\u0114\u0001\u0000\u0000"
        + "\u0000\u0118\u011b\u0001\u0000\u0000\u0000\u0119\u0117\u0001\u0000\u0000"
        + "\u0000\u0119\u011a\u0001\u0000\u0000\u0000\u011a\u001d\u0001\u0000\u0000"
        + "\u0000\u011b\u0119\u0001\u0000\u0000\u0000\u011c\u011d\u0003\u0000\u0000"
        + "\u0000\u011d\u011e\u0005%\u0000\u0000\u011e\u011f\u0003\u0006\u0003\u0000"
        + "\u011f\u001f\u0001\u0000\u0000\u0000\u0120\u0121\u0003\u0000\u0000\u0000"
        + "\u0121\u0122\u0005\'\u0000\u0000\u0122\u0123\u0003\u0006\u0003\u0000\u0123"
        + "!\u0001\u0000\u0000\u0000\u0124\u0126\u0005,\u0000\u0000\u0125\u0127\u0003"
        + "$\u0012\u0000\u0126\u0125\u0001\u0000\u0000\u0000\u0127\u0128\u0001\u0000"
        + "\u0000\u0000\u0128\u0126\u0001\u0000\u0000\u0000\u0128\u0129\u0001\u0000"
        + "\u0000\u0000\u0129\u012a\u0001\u0000\u0000\u0000\u012a\u012b\u0003\u0006"
        + "\u0003\u0000\u012b\u012c\u0005-\u0000\u0000\u012c#\u0001\u0000\u0000\u0000"
        + "\u012d\u012e\u0005\u0001\u0000\u0000\u012e\u0131\u0003\u0000\u0000\u0000"
        + "\u012f\u0130\u0005%\u0000\u0000\u0130\u0132\u0003(\u0014\u0000\u0131\u012f"
        + "\u0001\u0000\u0000\u0000\u0131\u0132\u0001\u0000\u0000\u0000\u0132\u0133"
        + "\u0001\u0000\u0000\u0000\u0133\u0134\u0005\'\u0000\u0000\u0134\u0135\u0003"
        + "\u0006\u0003\u0000\u0135\u0136\u0005&\u0000\u0000\u0136\u013b\u0001\u0000"
        + "\u0000\u0000\u0137\u0138\u0003\u0006\u0003\u0000\u0138\u0139\u0005&\u0000"
        + "\u0000\u0139\u013b\u0001\u0000\u0000\u0000\u013a\u012d\u0001\u0000\u0000"
        + "\u0000\u013a\u0137\u0001\u0000\u0000\u0000\u013b%\u0001\u0000\u0000\u0000"
        + "\u013c\u0144\u0003\u0000\u0000\u0000\u013d\u013e\u0005*\u0000\u0000\u013e"
        + "\u013f\u0003\u0000\u0000\u0000\u013f\u0140\u0005%\u0000\u0000\u0140\u0141"
        + "\u0003(\u0014\u0000\u0141\u0142\u0005+\u0000\u0000\u0142\u0144\u0001\u0000"
        + "\u0000\u0000\u0143\u013c\u0001\u0000\u0000\u0000\u0143\u013d\u0001\u0000"
        + "\u0000\u0000\u0144\'\u0001\u0000\u0000\u0000\u0145\u0146\u0003*\u0015"
        + "\u0000\u0146\u0147\u0005\u0016\u0000\u0000\u0147\u0148\u0003(\u0014\u0000"
        + "\u0148\u014b\u0001\u0000\u0000\u0000\u0149\u014b\u0003*\u0015\u0000\u014a"
        + "\u0145\u0001\u0000\u0000\u0000\u014a\u0149\u0001\u0000\u0000\u0000\u014b"
        + ")\u0001\u0000\u0000\u0000\u014c\u014d\u0006\u0015\uffff\uffff\u0000\u014d"
        + "\u014e\u0003,\u0016\u0000\u014e\u0153\u0001\u0000\u0000\u0000\u014f\u0150"
        + "\n\u0002\u0000\u0000\u0150\u0152\u0003,\u0016\u0000\u0151\u014f\u0001"
        + "\u0000\u0000\u0000\u0152\u0155\u0001\u0000\u0000\u0000\u0153\u0151\u0001"
        + "\u0000\u0000\u0000\u0153\u0154\u0001\u0000\u0000\u0000\u0154+\u0001\u0000"
        + "\u0000\u0000\u0155\u0153\u0001\u0000\u0000\u0000\u0156\u0161\u00051\u0000"
        + "\u0000\u0157\u0161\u00052\u0000\u0000\u0158\u0159\u0005,\u0000\u0000\u0159"
        + "\u015a\u0003.\u0017\u0000\u015a\u015b\u0005-\u0000\u0000\u015b\u0161\u0001"
        + "\u0000\u0000\u0000\u015c\u015d\u0005*\u0000\u0000\u015d\u015e\u0003(\u0014"
        + "\u0000\u015e\u015f\u0005+\u0000\u0000\u015f\u0161\u0001\u0000\u0000\u0000"
        + "\u0160\u0156\u0001\u0000\u0000\u0000\u0160\u0157\u0001\u0000\u0000\u0000"
        + "\u0160\u0158\u0001\u0000\u0000\u0000\u0160\u015c\u0001\u0000\u0000\u0000"
        + "\u0161-\u0001\u0000\u0000\u0000\u0162\u0167\u00030\u0018\u0000\u0163\u0164"
        + "\u0005$\u0000\u0000\u0164\u0166\u00030\u0018\u0000\u0165\u0163\u0001\u0000"
        + "\u0000\u0000\u0166\u0169\u0001\u0000\u0000\u0000\u0167\u0165\u0001\u0000"
        + "\u0000\u0000\u0167\u0168\u0001\u0000\u0000\u0000\u0168\u016c\u0001\u0000"
        + "\u0000\u0000\u0169\u0167\u0001\u0000\u0000\u0000\u016a\u016b\u0005)\u0000"
        + "\u0000\u016b\u016d\u00052\u0000\u0000\u016c\u016a\u0001\u0000\u0000\u0000"
        + "\u016c\u016d\u0001\u0000\u0000\u0000\u016d/\u0001\u0000\u0000\u0000\u016e"
        + "\u016f\u0003\u0000\u0000\u0000\u016f\u0170\u0005%\u0000\u0000\u0170\u0171"
        + "\u0003(\u0014\u0000\u01711\u0001\u0000\u0000\u0000\u001e7<DNYkr\u0081"
        + "\u008c\u0097\u009f\u00a6\u00b0\u00bb\u00c1\u00ca\u00e9\u00f6\u0103\u0112"
        + "\u0119\u0128\u0131\u013a\u0143\u014a\u0153\u0160\u0167\u016c";
    public static final ATN _ATN = new ATNDeserializer().deserialize(_serializedATN.toCharArray());
    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}
