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
        PAR = 12, DO = 13, UNDERSCORE = 14, PIPE_OP = 15, OR_OP = 16, AND_OP = 17, ARROW = 18, EQ = 19, NEQ = 20, LTE = 21, GTE = 22, LT =
            23, GT = 24, PLUS = 25, MINUS = 26, ASTERISK = 27, SLASH = 28, PERCENT = 29, BANG = 30, DOT = 31, COMMA = 32, COLON = 33,
        SEMICOLON = 34, ASSIGN = 35, BAR = 36, LPAREN = 37, RPAREN = 38, LBRACE = 39, RBRACE = 40, INTEGER_LITERAL = 41, DECIMAL_LITERAL =
            42, QUOTED_STRING = 43, IDENTIFIER = 44, LINE_COMMENT = 45, MULTILINE_COMMENT = 46, WS = 47;
    public static final int RULE_program = 0, RULE_topBinding = 1, RULE_expr = 2, RULE_pipeExpr = 3, RULE_orExpr = 4, RULE_andExpr = 5,
        RULE_eqExpr = 6, RULE_cmpExpr = 7, RULE_addExpr = 8, RULE_mulExpr = 9, RULE_unaryExpr = 10, RULE_appExpr = 11, RULE_primary = 12,
        RULE_recordField = 13, RULE_recordUpdate = 14, RULE_block = 15, RULE_blockStmt = 16, RULE_param = 17, RULE_type = 18,
        RULE_typePrimary = 19, RULE_rowType = 20, RULE_rowField = 21;

    private static String[] makeRuleNames() {
        return new String[] {
            "program",
            "topBinding",
            "expr",
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
            "typePrimary",
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
            "BAR",
            "LPAREN",
            "RPAREN",
            "LBRACE",
            "RBRACE",
            "INTEGER_LITERAL",
            "DECIMAL_LITERAL",
            "QUOTED_STRING",
            "IDENTIFIER",
            "LINE_COMMENT",
            "MULTILINE_COMMENT",
            "WS" };
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
        enterRule(_localctx, 0, RULE_program);
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                setState(47);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 0, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        {
                            {
                                setState(44);
                                topBinding();
                            }
                        }
                    }
                    setState(49);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 0, _ctx);
                }
                setState(50);
                expr();
                setState(51);
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

        public TerminalNode IDENTIFIER() {
            return getToken(PiescriptAntlrParser.IDENTIFIER, 0);
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
        enterRule(_localctx, 2, RULE_topBinding);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(53);
                match(LET);
                setState(54);
                match(IDENTIFIER);
                setState(57);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == COLON) {
                    {
                        setState(55);
                        match(COLON);
                        setState(56);
                        type();
                    }
                }

                setState(59);
                match(ASSIGN);
                setState(60);
                expr();
                setState(61);
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
    public static class LetExprContext extends ExprContext {
        public TerminalNode LET() {
            return getToken(PiescriptAntlrParser.LET, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(PiescriptAntlrParser.IDENTIFIER, 0);
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
        enterRule(_localctx, 4, RULE_expr);
        int _la;
        try {
            setState(84);
            _errHandler.sync(this);
            switch (_input.LA(1)) {
                case LET:
                    _localctx = new LetExprContext(_localctx);
                    enterOuterAlt(_localctx, 1); {
                    setState(63);
                    match(LET);
                    setState(64);
                    match(IDENTIFIER);
                    setState(67);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                    if (_la == COLON) {
                        {
                            setState(65);
                            match(COLON);
                            setState(66);
                            type();
                        }
                    }

                    setState(69);
                    match(ASSIGN);
                    setState(70);
                    expr();
                    setState(71);
                    match(IN);
                    setState(72);
                    expr();
                }
                    break;
                case FN:
                    _localctx = new LambdaExprContext(_localctx);
                    enterOuterAlt(_localctx, 2); {
                    setState(74);
                    match(FN);
                    setState(76);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                    do {
                        {
                            {
                                setState(75);
                                param();
                            }
                        }
                        setState(78);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                    } while (_la == LPAREN || _la == IDENTIFIER);
                    setState(80);
                    match(ARROW);
                    setState(81);
                    expr();
                }
                    break;
                case IF:
                case TRUE:
                case FALSE:
                case NULL:
                case MINUS:
                case BANG:
                case DOT:
                case LPAREN:
                case LBRACE:
                case INTEGER_LITERAL:
                case DECIMAL_LITERAL:
                case QUOTED_STRING:
                case IDENTIFIER:
                    _localctx = new ExprPipeContext(_localctx);
                    enterOuterAlt(_localctx, 3); {
                    setState(83);
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
        int _startState = 6;
        enterRecursionRule(_localctx, 6, RULE_pipeExpr, _p);
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                {
                    _localctx = new PipePassthroughContext(_localctx);
                    _ctx = _localctx;
                    _prevctx = _localctx;

                    setState(87);
                    orExpr(0);
                }
                _ctx.stop = _input.LT(-1);
                setState(94);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 5, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        if (_parseListeners != null) triggerExitRuleEvent();
                        _prevctx = _localctx;
                        {
                            {
                                _localctx = new PipeOpContext(new PipeExprContext(_parentctx, _parentState));
                                pushNewRecursionContext(_localctx, _startState, RULE_pipeExpr);
                                setState(89);
                                if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
                                setState(90);
                                match(PIPE_OP);
                                setState(91);
                                orExpr(0);
                            }
                        }
                    }
                    setState(96);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 5, _ctx);
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
        int _startState = 8;
        enterRecursionRule(_localctx, 8, RULE_orExpr, _p);
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                {
                    _localctx = new OrPassthroughContext(_localctx);
                    _ctx = _localctx;
                    _prevctx = _localctx;

                    setState(98);
                    andExpr(0);
                }
                _ctx.stop = _input.LT(-1);
                setState(105);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 6, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        if (_parseListeners != null) triggerExitRuleEvent();
                        _prevctx = _localctx;
                        {
                            {
                                _localctx = new OrOpContext(new OrExprContext(_parentctx, _parentState));
                                pushNewRecursionContext(_localctx, _startState, RULE_orExpr);
                                setState(100);
                                if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
                                setState(101);
                                match(OR_OP);
                                setState(102);
                                andExpr(0);
                            }
                        }
                    }
                    setState(107);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 6, _ctx);
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
        int _startState = 10;
        enterRecursionRule(_localctx, 10, RULE_andExpr, _p);
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                {
                    _localctx = new AndPassthroughContext(_localctx);
                    _ctx = _localctx;
                    _prevctx = _localctx;

                    setState(109);
                    eqExpr();
                }
                _ctx.stop = _input.LT(-1);
                setState(116);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 7, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        if (_parseListeners != null) triggerExitRuleEvent();
                        _prevctx = _localctx;
                        {
                            {
                                _localctx = new AndOpContext(new AndExprContext(_parentctx, _parentState));
                                pushNewRecursionContext(_localctx, _startState, RULE_andExpr);
                                setState(111);
                                if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
                                setState(112);
                                match(AND_OP);
                                setState(113);
                                eqExpr();
                            }
                        }
                    }
                    setState(118);
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
        enterRule(_localctx, 12, RULE_eqExpr);
        int _la;
        try {
            setState(124);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 8, _ctx)) {
                case 1:
                    _localctx = new EqualityOpContext(_localctx);
                    enterOuterAlt(_localctx, 1); {
                    setState(119);
                    cmpExpr();
                    setState(120);
                    ((EqualityOpContext) _localctx).op = _input.LT(1);
                    _la = _input.LA(1);
                    if (!(_la == EQ || _la == NEQ)) {
                        ((EqualityOpContext) _localctx).op = (Token) _errHandler.recoverInline(this);
                    } else {
                        if (_input.LA(1) == Token.EOF) matchedEOF = true;
                        _errHandler.reportMatch(this);
                        consume();
                    }
                    setState(121);
                    cmpExpr();
                }
                    break;
                case 2:
                    _localctx = new EqPassthroughContext(_localctx);
                    enterOuterAlt(_localctx, 2); {
                    setState(123);
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
        enterRule(_localctx, 14, RULE_cmpExpr);
        int _la;
        try {
            setState(131);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 9, _ctx)) {
                case 1:
                    _localctx = new ComparisonOpContext(_localctx);
                    enterOuterAlt(_localctx, 1); {
                    setState(126);
                    addExpr(0);
                    setState(127);
                    ((ComparisonOpContext) _localctx).op = _input.LT(1);
                    _la = _input.LA(1);
                    if (!((((_la) & ~0x3f) == 0 && ((1L << _la) & 31457280L) != 0))) {
                        ((ComparisonOpContext) _localctx).op = (Token) _errHandler.recoverInline(this);
                    } else {
                        if (_input.LA(1) == Token.EOF) matchedEOF = true;
                        _errHandler.reportMatch(this);
                        consume();
                    }
                    setState(128);
                    addExpr(0);
                }
                    break;
                case 2:
                    _localctx = new CmpPassthroughContext(_localctx);
                    enterOuterAlt(_localctx, 2); {
                    setState(130);
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
        int _startState = 16;
        enterRecursionRule(_localctx, 16, RULE_addExpr, _p);
        int _la;
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                {
                    _localctx = new AddPassthroughContext(_localctx);
                    _ctx = _localctx;
                    _prevctx = _localctx;

                    setState(134);
                    mulExpr(0);
                }
                _ctx.stop = _input.LT(-1);
                setState(141);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 10, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        if (_parseListeners != null) triggerExitRuleEvent();
                        _prevctx = _localctx;
                        {
                            {
                                _localctx = new AdditiveOpContext(new AddExprContext(_parentctx, _parentState));
                                pushNewRecursionContext(_localctx, _startState, RULE_addExpr);
                                setState(136);
                                if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
                                setState(137);
                                ((AdditiveOpContext) _localctx).op = _input.LT(1);
                                _la = _input.LA(1);
                                if (!(_la == PLUS || _la == MINUS)) {
                                    ((AdditiveOpContext) _localctx).op = (Token) _errHandler.recoverInline(this);
                                } else {
                                    if (_input.LA(1) == Token.EOF) matchedEOF = true;
                                    _errHandler.reportMatch(this);
                                    consume();
                                }
                                setState(138);
                                mulExpr(0);
                            }
                        }
                    }
                    setState(143);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 10, _ctx);
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
        int _startState = 18;
        enterRecursionRule(_localctx, 18, RULE_mulExpr, _p);
        int _la;
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                {
                    _localctx = new MulPassthroughContext(_localctx);
                    _ctx = _localctx;
                    _prevctx = _localctx;

                    setState(145);
                    unaryExpr();
                }
                _ctx.stop = _input.LT(-1);
                setState(152);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 11, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        if (_parseListeners != null) triggerExitRuleEvent();
                        _prevctx = _localctx;
                        {
                            {
                                _localctx = new MultiplicativeOpContext(new MulExprContext(_parentctx, _parentState));
                                pushNewRecursionContext(_localctx, _startState, RULE_mulExpr);
                                setState(147);
                                if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
                                setState(148);
                                ((MultiplicativeOpContext) _localctx).op = _input.LT(1);
                                _la = _input.LA(1);
                                if (!((((_la) & ~0x3f) == 0 && ((1L << _la) & 939524096L) != 0))) {
                                    ((MultiplicativeOpContext) _localctx).op = (Token) _errHandler.recoverInline(this);
                                } else {
                                    if (_input.LA(1) == Token.EOF) matchedEOF = true;
                                    _errHandler.reportMatch(this);
                                    consume();
                                }
                                setState(149);
                                unaryExpr();
                            }
                        }
                    }
                    setState(154);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 11, _ctx);
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
        enterRule(_localctx, 20, RULE_unaryExpr);
        int _la;
        try {
            setState(158);
            _errHandler.sync(this);
            switch (_input.LA(1)) {
                case MINUS:
                case BANG:
                    _localctx = new UnaryOpContext(_localctx);
                    enterOuterAlt(_localctx, 1); {
                    setState(155);
                    ((UnaryOpContext) _localctx).op = _input.LT(1);
                    _la = _input.LA(1);
                    if (!(_la == MINUS || _la == BANG)) {
                        ((UnaryOpContext) _localctx).op = (Token) _errHandler.recoverInline(this);
                    } else {
                        if (_input.LA(1) == Token.EOF) matchedEOF = true;
                        _errHandler.reportMatch(this);
                        consume();
                    }
                    setState(156);
                    unaryExpr();
                }
                    break;
                case IF:
                case TRUE:
                case FALSE:
                case NULL:
                case DOT:
                case LPAREN:
                case LBRACE:
                case INTEGER_LITERAL:
                case DECIMAL_LITERAL:
                case QUOTED_STRING:
                case IDENTIFIER:
                    _localctx = new UnaryPassthroughContext(_localctx);
                    enterOuterAlt(_localctx, 2); {
                    setState(157);
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
        int _startState = 22;
        enterRecursionRule(_localctx, 22, RULE_appExpr, _p);
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                {
                    _localctx = new AppPassthroughContext(_localctx);
                    _ctx = _localctx;
                    _prevctx = _localctx;

                    setState(161);
                    primary(0);
                }
                _ctx.stop = _input.LT(-1);
                setState(167);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 13, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        if (_parseListeners != null) triggerExitRuleEvent();
                        _prevctx = _localctx;
                        {
                            {
                                _localctx = new ApplicationContext(new AppExprContext(_parentctx, _parentState));
                                pushNewRecursionContext(_localctx, _startState, RULE_appExpr);
                                setState(163);
                                if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
                                setState(164);
                                primary(0);
                            }
                        }
                    }
                    setState(169);
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
        public TerminalNode IDENTIFIER() {
            return getToken(PiescriptAntlrParser.IDENTIFIER, 0);
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
    public static class ProjectionContext extends PrimaryContext {
        public PrimaryContext primary() {
            return getRuleContext(PrimaryContext.class, 0);
        }

        public TerminalNode DOT() {
            return getToken(PiescriptAntlrParser.DOT, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(PiescriptAntlrParser.IDENTIFIER, 0);
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

        public TerminalNode IDENTIFIER() {
            return getToken(PiescriptAntlrParser.IDENTIFIER, 0);
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
        int _startState = 24;
        enterRecursionRule(_localctx, 24, RULE_primary, _p);
        int _la;
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                setState(237);
                _errHandler.sync(this);
                switch (getInterpreter().adaptivePredict(_input, 17, _ctx)) {
                    case 1: {
                        _localctx = new AccessorContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;

                        setState(171);
                        match(DOT);
                        setState(172);
                        match(IDENTIFIER);
                    }
                        break;
                    case 2: {
                        _localctx = new IntegerLiteralContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(173);
                        match(INTEGER_LITERAL);
                    }
                        break;
                    case 3: {
                        _localctx = new DecimalLiteralContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(174);
                        match(DECIMAL_LITERAL);
                    }
                        break;
                    case 4: {
                        _localctx = new StringLiteralContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(175);
                        match(QUOTED_STRING);
                    }
                        break;
                    case 5: {
                        _localctx = new TrueLiteralContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(176);
                        match(TRUE);
                    }
                        break;
                    case 6: {
                        _localctx = new FalseLiteralContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(177);
                        match(FALSE);
                    }
                        break;
                    case 7: {
                        _localctx = new NullLiteralContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(178);
                        match(NULL);
                    }
                        break;
                    case 8: {
                        _localctx = new VariableContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(179);
                        match(IDENTIFIER);
                    }
                        break;
                    case 9: {
                        _localctx = new AscriptionContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(180);
                        match(LPAREN);
                        setState(181);
                        expr();
                        setState(182);
                        match(COLON);
                        setState(183);
                        type();
                        setState(184);
                        match(RPAREN);
                    }
                        break;
                    case 10: {
                        _localctx = new ParenExprContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(186);
                        match(LPAREN);
                        setState(187);
                        expr();
                        setState(188);
                        match(RPAREN);
                    }
                        break;
                    case 11: {
                        _localctx = new EmptyRecordContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(190);
                        match(LBRACE);
                        setState(191);
                        match(RBRACE);
                    }
                        break;
                    case 12: {
                        _localctx = new RecordLiteralContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(192);
                        match(LBRACE);
                        setState(193);
                        recordField();
                        setState(198);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        while (_la == COMMA) {
                            {
                                {
                                    setState(194);
                                    match(COMMA);
                                    setState(195);
                                    recordField();
                                }
                            }
                            setState(200);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                        }
                        setState(201);
                        match(RBRACE);
                    }
                        break;
                    case 13: {
                        _localctx = new RecordUpdateExprContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(203);
                        match(LBRACE);
                        setState(204);
                        expr();
                        setState(205);
                        match(BAR);
                        setState(206);
                        recordUpdate();
                        setState(211);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        while (_la == COMMA) {
                            {
                                {
                                    setState(207);
                                    match(COMMA);
                                    setState(208);
                                    recordUpdate();
                                }
                            }
                            setState(213);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                        }
                        setState(214);
                        match(RBRACE);
                    }
                        break;
                    case 14: {
                        _localctx = new UpdateSugarContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(216);
                        match(LBRACE);
                        setState(217);
                        match(UNDERSCORE);
                        setState(218);
                        match(BAR);
                        setState(219);
                        recordUpdate();
                        setState(224);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        while (_la == COMMA) {
                            {
                                {
                                    setState(220);
                                    match(COMMA);
                                    setState(221);
                                    recordUpdate();
                                }
                            }
                            setState(226);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                        }
                        setState(227);
                        match(RBRACE);
                    }
                        break;
                    case 15: {
                        _localctx = new IfExprContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(229);
                        match(IF);
                        setState(230);
                        expr();
                        setState(231);
                        match(THEN);
                        setState(232);
                        expr();
                        setState(233);
                        match(ELSE);
                        setState(234);
                        expr();
                    }
                        break;
                    case 16: {
                        _localctx = new BlockExprContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(236);
                        block();
                    }
                        break;
                }
                _ctx.stop = _input.LT(-1);
                setState(244);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 18, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        if (_parseListeners != null) triggerExitRuleEvent();
                        _prevctx = _localctx;
                        {
                            {
                                _localctx = new ProjectionContext(new PrimaryContext(_parentctx, _parentState));
                                pushNewRecursionContext(_localctx, _startState, RULE_primary);
                                setState(239);
                                if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
                                setState(240);
                                match(DOT);
                                setState(241);
                                match(IDENTIFIER);
                            }
                        }
                    }
                    setState(246);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 18, _ctx);
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
        public TerminalNode IDENTIFIER() {
            return getToken(PiescriptAntlrParser.IDENTIFIER, 0);
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
        enterRule(_localctx, 26, RULE_recordField);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(247);
                match(IDENTIFIER);
                setState(248);
                match(COLON);
                setState(249);
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
        public TerminalNode IDENTIFIER() {
            return getToken(PiescriptAntlrParser.IDENTIFIER, 0);
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
        enterRule(_localctx, 28, RULE_recordUpdate);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(251);
                match(IDENTIFIER);
                setState(252);
                match(ASSIGN);
                setState(253);
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
        enterRule(_localctx, 30, RULE_block);
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                setState(255);
                match(LBRACE);
                setState(257);
                _errHandler.sync(this);
                _alt = 1;
                do {
                    switch (_alt) {
                        case 1: {
                            {
                                setState(256);
                                blockStmt();
                            }
                        }
                            break;
                        default:
                            throw new NoViableAltException(this);
                    }
                    setState(259);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 19, _ctx);
                } while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER);
                setState(261);
                expr();
                setState(262);
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

        public TerminalNode IDENTIFIER() {
            return getToken(PiescriptAntlrParser.IDENTIFIER, 0);
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
        enterRule(_localctx, 32, RULE_blockStmt);
        int _la;
        try {
            setState(277);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 21, _ctx)) {
                case 1:
                    _localctx = new BlockLetContext(_localctx);
                    enterOuterAlt(_localctx, 1); {
                    setState(264);
                    match(LET);
                    setState(265);
                    match(IDENTIFIER);
                    setState(268);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                    if (_la == COLON) {
                        {
                            setState(266);
                            match(COLON);
                            setState(267);
                            type();
                        }
                    }

                    setState(270);
                    match(ASSIGN);
                    setState(271);
                    expr();
                    setState(272);
                    match(SEMICOLON);
                }
                    break;
                case 2:
                    _localctx = new BlockExprStmtContext(_localctx);
                    enterOuterAlt(_localctx, 2); {
                    setState(274);
                    expr();
                    setState(275);
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

        public TerminalNode IDENTIFIER() {
            return getToken(PiescriptAntlrParser.IDENTIFIER, 0);
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
        public TerminalNode IDENTIFIER() {
            return getToken(PiescriptAntlrParser.IDENTIFIER, 0);
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
        enterRule(_localctx, 34, RULE_param);
        try {
            setState(286);
            _errHandler.sync(this);
            switch (_input.LA(1)) {
                case IDENTIFIER:
                    _localctx = new UntypedParamContext(_localctx);
                    enterOuterAlt(_localctx, 1); {
                    setState(279);
                    match(IDENTIFIER);
                }
                    break;
                case LPAREN:
                    _localctx = new TypedParamContext(_localctx);
                    enterOuterAlt(_localctx, 2); {
                    setState(280);
                    match(LPAREN);
                    setState(281);
                    match(IDENTIFIER);
                    setState(282);
                    match(COLON);
                    setState(283);
                    type();
                    setState(284);
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
        public TypePrimaryContext typePrimary() {
            return getRuleContext(TypePrimaryContext.class, 0);
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
    public static class TypeAtomContext extends TypeContext {
        public TypePrimaryContext typePrimary() {
            return getRuleContext(TypePrimaryContext.class, 0);
        }

        public TypeAtomContext(TypeContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).enterTypeAtom(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PiescriptAntlrParserListener) ((PiescriptAntlrParserListener) listener).exitTypeAtom(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PiescriptAntlrParserVisitor) return ((PiescriptAntlrParserVisitor<? extends T>) visitor).visitTypeAtom(
                this
            );
            else return visitor.visitChildren(this);
        }
    }

    public final TypeContext type() throws RecognitionException {
        TypeContext _localctx = new TypeContext(_ctx, getState());
        enterRule(_localctx, 36, RULE_type);
        try {
            setState(293);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 23, _ctx)) {
                case 1:
                    _localctx = new FunctionTypeContext(_localctx);
                    enterOuterAlt(_localctx, 1); {
                    setState(288);
                    typePrimary();
                    setState(289);
                    match(ARROW);
                    setState(290);
                    type();
                }
                    break;
                case 2:
                    _localctx = new TypeAtomContext(_localctx);
                    enterOuterAlt(_localctx, 2); {
                    setState(292);
                    typePrimary();
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
    public static class TypePrimaryContext extends ParserRuleContext {
        public TypePrimaryContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_typePrimary;
        }

        public TypePrimaryContext() {}

        public void copyFrom(TypePrimaryContext ctx) {
            super.copyFrom(ctx);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class RecordTypeContext extends TypePrimaryContext {
        public TerminalNode LBRACE() {
            return getToken(PiescriptAntlrParser.LBRACE, 0);
        }

        public RowTypeContext rowType() {
            return getRuleContext(RowTypeContext.class, 0);
        }

        public TerminalNode RBRACE() {
            return getToken(PiescriptAntlrParser.RBRACE, 0);
        }

        public RecordTypeContext(TypePrimaryContext ctx) {
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
    public static class TypeConContext extends TypePrimaryContext {
        public TerminalNode IDENTIFIER() {
            return getToken(PiescriptAntlrParser.IDENTIFIER, 0);
        }

        public TypeConContext(TypePrimaryContext ctx) {
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
    public static class ParenTypeContext extends TypePrimaryContext {
        public TerminalNode LPAREN() {
            return getToken(PiescriptAntlrParser.LPAREN, 0);
        }

        public TypeContext type() {
            return getRuleContext(TypeContext.class, 0);
        }

        public TerminalNode RPAREN() {
            return getToken(PiescriptAntlrParser.RPAREN, 0);
        }

        public ParenTypeContext(TypePrimaryContext ctx) {
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

    public final TypePrimaryContext typePrimary() throws RecognitionException {
        TypePrimaryContext _localctx = new TypePrimaryContext(_ctx, getState());
        enterRule(_localctx, 38, RULE_typePrimary);
        try {
            setState(304);
            _errHandler.sync(this);
            switch (_input.LA(1)) {
                case IDENTIFIER:
                    _localctx = new TypeConContext(_localctx);
                    enterOuterAlt(_localctx, 1); {
                    setState(295);
                    match(IDENTIFIER);
                }
                    break;
                case LBRACE:
                    _localctx = new RecordTypeContext(_localctx);
                    enterOuterAlt(_localctx, 2); {
                    setState(296);
                    match(LBRACE);
                    setState(297);
                    rowType();
                    setState(298);
                    match(RBRACE);
                }
                    break;
                case LPAREN:
                    _localctx = new ParenTypeContext(_localctx);
                    enterOuterAlt(_localctx, 3); {
                    setState(300);
                    match(LPAREN);
                    setState(301);
                    type();
                    setState(302);
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

        public TerminalNode IDENTIFIER() {
            return getToken(PiescriptAntlrParser.IDENTIFIER, 0);
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
        enterRule(_localctx, 40, RULE_rowType);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(306);
                rowField();
                setState(311);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == COMMA) {
                    {
                        {
                            setState(307);
                            match(COMMA);
                            setState(308);
                            rowField();
                        }
                    }
                    setState(313);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
                setState(316);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == BAR) {
                    {
                        setState(314);
                        match(BAR);
                        setState(315);
                        match(IDENTIFIER);
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
        public TerminalNode IDENTIFIER() {
            return getToken(PiescriptAntlrParser.IDENTIFIER, 0);
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
        enterRule(_localctx, 42, RULE_rowField);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(318);
                match(IDENTIFIER);
                setState(319);
                match(COLON);
                setState(320);
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
            case 3:
                return pipeExpr_sempred((PipeExprContext) _localctx, predIndex);
            case 4:
                return orExpr_sempred((OrExprContext) _localctx, predIndex);
            case 5:
                return andExpr_sempred((AndExprContext) _localctx, predIndex);
            case 8:
                return addExpr_sempred((AddExprContext) _localctx, predIndex);
            case 9:
                return mulExpr_sempred((MulExprContext) _localctx, predIndex);
            case 11:
                return appExpr_sempred((AppExprContext) _localctx, predIndex);
            case 12:
                return primary_sempred((PrimaryContext) _localctx, predIndex);
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

    public static final String _serializedATN = "\u0004\u0001/\u0143\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"
        + "\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"
        + "\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"
        + "\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"
        + "\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"
        + "\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"
        + "\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"
        + "\u0001\u0000\u0005\u0000.\b\u0000\n\u0000\f\u00001\t\u0000\u0001\u0000"
        + "\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"
        + "\u0003\u0001:\b\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"
        + "\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0003\u0002D\b\u0002"
        + "\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"
        + "\u0001\u0002\u0004\u0002M\b\u0002\u000b\u0002\f\u0002N\u0001\u0002\u0001"
        + "\u0002\u0001\u0002\u0001\u0002\u0003\u0002U\b\u0002\u0001\u0003\u0001"
        + "\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0005\u0003]\b"
        + "\u0003\n\u0003\f\u0003`\t\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001"
        + "\u0004\u0001\u0004\u0001\u0004\u0005\u0004h\b\u0004\n\u0004\f\u0004k\t"
        + "\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"
        + "\u0005\u0005\u0005s\b\u0005\n\u0005\f\u0005v\t\u0005\u0001\u0006\u0001"
        + "\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0003\u0006}\b\u0006\u0001"
        + "\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0003\u0007\u0084"
        + "\b\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0005\b\u008c"
        + "\b\b\n\b\f\b\u008f\t\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t"
        + "\u0005\t\u0097\b\t\n\t\f\t\u009a\t\t\u0001\n\u0001\n\u0001\n\u0003\n\u009f"
        + "\b\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0005"
        + "\u000b\u00a6\b\u000b\n\u000b\f\u000b\u00a9\t\u000b\u0001\f\u0001\f\u0001"
        + "\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001"
        + "\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001"
        + "\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0005\f\u00c5\b\f\n\f\f\f\u00c8"
        + "\t\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0005"
        + "\f\u00d2\b\f\n\f\f\f\u00d5\t\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f"
        + "\u0001\f\u0001\f\u0001\f\u0005\f\u00df\b\f\n\f\f\f\u00e2\t\f\u0001\f\u0001"
        + "\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0003"
        + "\f\u00ee\b\f\u0001\f\u0001\f\u0001\f\u0005\f\u00f3\b\f\n\f\f\f\u00f6\t"
        + "\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001\u000e"
        + "\u0001\u000e\u0001\u000f\u0001\u000f\u0004\u000f\u0102\b\u000f\u000b\u000f"
        + "\f\u000f\u0103\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u0010\u0001\u0010"
        + "\u0001\u0010\u0001\u0010\u0003\u0010\u010d\b\u0010\u0001\u0010\u0001\u0010"
        + "\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0003\u0010"
        + "\u0116\b\u0010\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011"
        + "\u0001\u0011\u0001\u0011\u0003\u0011\u011f\b\u0011\u0001\u0012\u0001\u0012"
        + "\u0001\u0012\u0001\u0012\u0001\u0012\u0003\u0012\u0126\b\u0012\u0001\u0013"
        + "\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013"
        + "\u0001\u0013\u0001\u0013\u0003\u0013\u0131\b\u0013\u0001\u0014\u0001\u0014"
        + "\u0001\u0014\u0005\u0014\u0136\b\u0014\n\u0014\f\u0014\u0139\t\u0014\u0001"
        + "\u0014\u0001\u0014\u0003\u0014\u013d\b\u0014\u0001\u0015\u0001\u0015\u0001"
        + "\u0015\u0001\u0015\u0001\u0015\u0000\u0007\u0006\b\n\u0010\u0012\u0016"
        + "\u0018\u0016\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016"
        + "\u0018\u001a\u001c\u001e \"$&(*\u0000\u0005\u0001\u0000\u0013\u0014\u0001"
        + "\u0000\u0015\u0018\u0001\u0000\u0019\u001a\u0001\u0000\u001b\u001d\u0002"
        + "\u0000\u001a\u001a\u001e\u001e\u0157\u0000/\u0001\u0000\u0000\u0000\u0002"
        + "5\u0001\u0000\u0000\u0000\u0004T\u0001\u0000\u0000\u0000\u0006V\u0001"
        + "\u0000\u0000\u0000\ba\u0001\u0000\u0000\u0000\nl\u0001\u0000\u0000\u0000"
        + "\f|\u0001\u0000\u0000\u0000\u000e\u0083\u0001\u0000\u0000\u0000\u0010"
        + "\u0085\u0001\u0000\u0000\u0000\u0012\u0090\u0001\u0000\u0000\u0000\u0014"
        + "\u009e\u0001\u0000\u0000\u0000\u0016\u00a0\u0001\u0000\u0000\u0000\u0018"
        + "\u00ed\u0001\u0000\u0000\u0000\u001a\u00f7\u0001\u0000\u0000\u0000\u001c"
        + "\u00fb\u0001\u0000\u0000\u0000\u001e\u00ff\u0001\u0000\u0000\u0000 \u0115"
        + "\u0001\u0000\u0000\u0000\"\u011e\u0001\u0000\u0000\u0000$\u0125\u0001"
        + "\u0000\u0000\u0000&\u0130\u0001\u0000\u0000\u0000(\u0132\u0001\u0000\u0000"
        + "\u0000*\u013e\u0001\u0000\u0000\u0000,.\u0003\u0002\u0001\u0000-,\u0001"
        + "\u0000\u0000\u0000.1\u0001\u0000\u0000\u0000/-\u0001\u0000\u0000\u0000"
        + "/0\u0001\u0000\u0000\u000002\u0001\u0000\u0000\u00001/\u0001\u0000\u0000"
        + "\u000023\u0003\u0004\u0002\u000034\u0005\u0000\u0000\u00014\u0001\u0001"
        + "\u0000\u0000\u000056\u0005\u0001\u0000\u000069\u0005,\u0000\u000078\u0005"
        + "!\u0000\u00008:\u0003$\u0012\u000097\u0001\u0000\u0000\u00009:\u0001\u0000"
        + "\u0000\u0000:;\u0001\u0000\u0000\u0000;<\u0005#\u0000\u0000<=\u0003\u0004"
        + "\u0002\u0000=>\u0005\"\u0000\u0000>\u0003\u0001\u0000\u0000\u0000?@\u0005"
        + "\u0001\u0000\u0000@C\u0005,\u0000\u0000AB\u0005!\u0000\u0000BD\u0003$"
        + "\u0012\u0000CA\u0001\u0000\u0000\u0000CD\u0001\u0000\u0000\u0000DE\u0001"
        + "\u0000\u0000\u0000EF\u0005#\u0000\u0000FG\u0003\u0004\u0002\u0000GH\u0005"
        + "\u0002\u0000\u0000HI\u0003\u0004\u0002\u0000IU\u0001\u0000\u0000\u0000"
        + "JL\u0005\u0003\u0000\u0000KM\u0003\"\u0011\u0000LK\u0001\u0000\u0000\u0000"
        + "MN\u0001\u0000\u0000\u0000NL\u0001\u0000\u0000\u0000NO\u0001\u0000\u0000"
        + "\u0000OP\u0001\u0000\u0000\u0000PQ\u0005\u0012\u0000\u0000QR\u0003\u0004"
        + "\u0002\u0000RU\u0001\u0000\u0000\u0000SU\u0003\u0006\u0003\u0000T?\u0001"
        + "\u0000\u0000\u0000TJ\u0001\u0000\u0000\u0000TS\u0001\u0000\u0000\u0000"
        + "U\u0005\u0001\u0000\u0000\u0000VW\u0006\u0003\uffff\uffff\u0000WX\u0003"
        + "\b\u0004\u0000X^\u0001\u0000\u0000\u0000YZ\n\u0001\u0000\u0000Z[\u0005"
        + "\u000f\u0000\u0000[]\u0003\b\u0004\u0000\\Y\u0001\u0000\u0000\u0000]`"
        + "\u0001\u0000\u0000\u0000^\\\u0001\u0000\u0000\u0000^_\u0001\u0000\u0000"
        + "\u0000_\u0007\u0001\u0000\u0000\u0000`^\u0001\u0000\u0000\u0000ab\u0006"
        + "\u0004\uffff\uffff\u0000bc\u0003\n\u0005\u0000ci\u0001\u0000\u0000\u0000"
        + "de\n\u0001\u0000\u0000ef\u0005\u0010\u0000\u0000fh\u0003\n\u0005\u0000"
        + "gd\u0001\u0000\u0000\u0000hk\u0001\u0000\u0000\u0000ig\u0001\u0000\u0000"
        + "\u0000ij\u0001\u0000\u0000\u0000j\t\u0001\u0000\u0000\u0000ki\u0001\u0000"
        + "\u0000\u0000lm\u0006\u0005\uffff\uffff\u0000mn\u0003\f\u0006\u0000nt\u0001"
        + "\u0000\u0000\u0000op\n\u0001\u0000\u0000pq\u0005\u0011\u0000\u0000qs\u0003"
        + "\f\u0006\u0000ro\u0001\u0000\u0000\u0000sv\u0001\u0000\u0000\u0000tr\u0001"
        + "\u0000\u0000\u0000tu\u0001\u0000\u0000\u0000u\u000b\u0001\u0000\u0000"
        + "\u0000vt\u0001\u0000\u0000\u0000wx\u0003\u000e\u0007\u0000xy\u0007\u0000"
        + "\u0000\u0000yz\u0003\u000e\u0007\u0000z}\u0001\u0000\u0000\u0000{}\u0003"
        + "\u000e\u0007\u0000|w\u0001\u0000\u0000\u0000|{\u0001\u0000\u0000\u0000"
        + "}\r\u0001\u0000\u0000\u0000~\u007f\u0003\u0010\b\u0000\u007f\u0080\u0007"
        + "\u0001\u0000\u0000\u0080\u0081\u0003\u0010\b\u0000\u0081\u0084\u0001\u0000"
        + "\u0000\u0000\u0082\u0084\u0003\u0010\b\u0000\u0083~\u0001\u0000\u0000"
        + "\u0000\u0083\u0082\u0001\u0000\u0000\u0000\u0084\u000f\u0001\u0000\u0000"
        + "\u0000\u0085\u0086\u0006\b\uffff\uffff\u0000\u0086\u0087\u0003\u0012\t"
        + "\u0000\u0087\u008d\u0001\u0000\u0000\u0000\u0088\u0089\n\u0001\u0000\u0000"
        + "\u0089\u008a\u0007\u0002\u0000\u0000\u008a\u008c\u0003\u0012\t\u0000\u008b"
        + "\u0088\u0001\u0000\u0000\u0000\u008c\u008f\u0001\u0000\u0000\u0000\u008d"
        + "\u008b\u0001\u0000\u0000\u0000\u008d\u008e\u0001\u0000\u0000\u0000\u008e"
        + "\u0011\u0001\u0000\u0000\u0000\u008f\u008d\u0001\u0000\u0000\u0000\u0090"
        + "\u0091\u0006\t\uffff\uffff\u0000\u0091\u0092\u0003\u0014\n\u0000\u0092"
        + "\u0098\u0001\u0000\u0000\u0000\u0093\u0094\n\u0001\u0000\u0000\u0094\u0095"
        + "\u0007\u0003\u0000\u0000\u0095\u0097\u0003\u0014\n\u0000\u0096\u0093\u0001"
        + "\u0000\u0000\u0000\u0097\u009a\u0001\u0000\u0000\u0000\u0098\u0096\u0001"
        + "\u0000\u0000\u0000\u0098\u0099\u0001\u0000\u0000\u0000\u0099\u0013\u0001"
        + "\u0000\u0000\u0000\u009a\u0098\u0001\u0000\u0000\u0000\u009b\u009c\u0007"
        + "\u0004\u0000\u0000\u009c\u009f\u0003\u0014\n\u0000\u009d\u009f\u0003\u0016"
        + "\u000b\u0000\u009e\u009b\u0001\u0000\u0000\u0000\u009e\u009d\u0001\u0000"
        + "\u0000\u0000\u009f\u0015\u0001\u0000\u0000\u0000\u00a0\u00a1\u0006\u000b"
        + "\uffff\uffff\u0000\u00a1\u00a2\u0003\u0018\f\u0000\u00a2\u00a7\u0001\u0000"
        + "\u0000\u0000\u00a3\u00a4\n\u0001\u0000\u0000\u00a4\u00a6\u0003\u0018\f"
        + "\u0000\u00a5\u00a3\u0001\u0000\u0000\u0000\u00a6\u00a9\u0001\u0000\u0000"
        + "\u0000\u00a7\u00a5\u0001\u0000\u0000\u0000\u00a7\u00a8\u0001\u0000\u0000"
        + "\u0000\u00a8\u0017\u0001\u0000\u0000\u0000\u00a9\u00a7\u0001\u0000\u0000"
        + "\u0000\u00aa\u00ab\u0006\f\uffff\uffff\u0000\u00ab\u00ac\u0005\u001f\u0000"
        + "\u0000\u00ac\u00ee\u0005,\u0000\u0000\u00ad\u00ee\u0005)\u0000\u0000\u00ae"
        + "\u00ee\u0005*\u0000\u0000\u00af\u00ee\u0005+\u0000\u0000\u00b0\u00ee\u0005"
        + "\u0007\u0000\u0000\u00b1\u00ee\u0005\b\u0000\u0000\u00b2\u00ee\u0005\t"
        + "\u0000\u0000\u00b3\u00ee\u0005,\u0000\u0000\u00b4\u00b5\u0005%\u0000\u0000"
        + "\u00b5\u00b6\u0003\u0004\u0002\u0000\u00b6\u00b7\u0005!\u0000\u0000\u00b7"
        + "\u00b8\u0003$\u0012\u0000\u00b8\u00b9\u0005&\u0000\u0000\u00b9\u00ee\u0001"
        + "\u0000\u0000\u0000\u00ba\u00bb\u0005%\u0000\u0000\u00bb\u00bc\u0003\u0004"
        + "\u0002\u0000\u00bc\u00bd\u0005&\u0000\u0000\u00bd\u00ee\u0001\u0000\u0000"
        + "\u0000\u00be\u00bf\u0005\'\u0000\u0000\u00bf\u00ee\u0005(\u0000\u0000"
        + "\u00c0\u00c1\u0005\'\u0000\u0000\u00c1\u00c6\u0003\u001a\r\u0000\u00c2"
        + "\u00c3\u0005 \u0000\u0000\u00c3\u00c5\u0003\u001a\r\u0000\u00c4\u00c2"
        + "\u0001\u0000\u0000\u0000\u00c5\u00c8\u0001\u0000\u0000\u0000\u00c6\u00c4"
        + "\u0001\u0000\u0000\u0000\u00c6\u00c7\u0001\u0000\u0000\u0000\u00c7\u00c9"
        + "\u0001\u0000\u0000\u0000\u00c8\u00c6\u0001\u0000\u0000\u0000\u00c9\u00ca"
        + "\u0005(\u0000\u0000\u00ca\u00ee\u0001\u0000\u0000\u0000\u00cb\u00cc\u0005"
        + "\'\u0000\u0000\u00cc\u00cd\u0003\u0004\u0002\u0000\u00cd\u00ce\u0005$"
        + "\u0000\u0000\u00ce\u00d3\u0003\u001c\u000e\u0000\u00cf\u00d0\u0005 \u0000"
        + "\u0000\u00d0\u00d2\u0003\u001c\u000e\u0000\u00d1\u00cf\u0001\u0000\u0000"
        + "\u0000\u00d2\u00d5\u0001\u0000\u0000\u0000\u00d3\u00d1\u0001\u0000\u0000"
        + "\u0000\u00d3\u00d4\u0001\u0000\u0000\u0000\u00d4\u00d6\u0001\u0000\u0000"
        + "\u0000\u00d5\u00d3\u0001\u0000\u0000\u0000\u00d6\u00d7\u0005(\u0000\u0000"
        + "\u00d7\u00ee\u0001\u0000\u0000\u0000\u00d8\u00d9\u0005\'\u0000\u0000\u00d9"
        + "\u00da\u0005\u000e\u0000\u0000\u00da\u00db\u0005$\u0000\u0000\u00db\u00e0"
        + "\u0003\u001c\u000e\u0000\u00dc\u00dd\u0005 \u0000\u0000\u00dd\u00df\u0003"
        + "\u001c\u000e\u0000\u00de\u00dc\u0001\u0000\u0000\u0000\u00df\u00e2\u0001"
        + "\u0000\u0000\u0000\u00e0\u00de\u0001\u0000\u0000\u0000\u00e0\u00e1\u0001"
        + "\u0000\u0000\u0000\u00e1\u00e3\u0001\u0000\u0000\u0000\u00e2\u00e0\u0001"
        + "\u0000\u0000\u0000\u00e3\u00e4\u0005(\u0000\u0000\u00e4\u00ee\u0001\u0000"
        + "\u0000\u0000\u00e5\u00e6\u0005\u0004\u0000\u0000\u00e6\u00e7\u0003\u0004"
        + "\u0002\u0000\u00e7\u00e8\u0005\u0005\u0000\u0000\u00e8\u00e9\u0003\u0004"
        + "\u0002\u0000\u00e9\u00ea\u0005\u0006\u0000\u0000\u00ea\u00eb\u0003\u0004"
        + "\u0002\u0000\u00eb\u00ee\u0001\u0000\u0000\u0000\u00ec\u00ee\u0003\u001e"
        + "\u000f\u0000\u00ed\u00aa\u0001\u0000\u0000\u0000\u00ed\u00ad\u0001\u0000"
        + "\u0000\u0000\u00ed\u00ae\u0001\u0000\u0000\u0000\u00ed\u00af\u0001\u0000"
        + "\u0000\u0000\u00ed\u00b0\u0001\u0000\u0000\u0000\u00ed\u00b1\u0001\u0000"
        + "\u0000\u0000\u00ed\u00b2\u0001\u0000\u0000\u0000\u00ed\u00b3\u0001\u0000"
        + "\u0000\u0000\u00ed\u00b4\u0001\u0000\u0000\u0000\u00ed\u00ba\u0001\u0000"
        + "\u0000\u0000\u00ed\u00be\u0001\u0000\u0000\u0000\u00ed\u00c0\u0001\u0000"
        + "\u0000\u0000\u00ed\u00cb\u0001\u0000\u0000\u0000\u00ed\u00d8\u0001\u0000"
        + "\u0000\u0000\u00ed\u00e5\u0001\u0000\u0000\u0000\u00ed\u00ec\u0001\u0000"
        + "\u0000\u0000\u00ee\u00f4\u0001\u0000\u0000\u0000\u00ef\u00f0\n\u0001\u0000"
        + "\u0000\u00f0\u00f1\u0005\u001f\u0000\u0000\u00f1\u00f3\u0005,\u0000\u0000"
        + "\u00f2\u00ef\u0001\u0000\u0000\u0000\u00f3\u00f6\u0001\u0000\u0000\u0000"
        + "\u00f4\u00f2\u0001\u0000\u0000\u0000\u00f4\u00f5\u0001\u0000\u0000\u0000"
        + "\u00f5\u0019\u0001\u0000\u0000\u0000\u00f6\u00f4\u0001\u0000\u0000\u0000"
        + "\u00f7\u00f8\u0005,\u0000\u0000\u00f8\u00f9\u0005!\u0000\u0000\u00f9\u00fa"
        + "\u0003\u0004\u0002\u0000\u00fa\u001b\u0001\u0000\u0000\u0000\u00fb\u00fc"
        + "\u0005,\u0000\u0000\u00fc\u00fd\u0005#\u0000\u0000\u00fd\u00fe\u0003\u0004"
        + "\u0002\u0000\u00fe\u001d\u0001\u0000\u0000\u0000\u00ff\u0101\u0005\'\u0000"
        + "\u0000\u0100\u0102\u0003 \u0010\u0000\u0101\u0100\u0001\u0000\u0000\u0000"
        + "\u0102\u0103\u0001\u0000\u0000\u0000\u0103\u0101\u0001\u0000\u0000\u0000"
        + "\u0103\u0104\u0001\u0000\u0000\u0000\u0104\u0105\u0001\u0000\u0000\u0000"
        + "\u0105\u0106\u0003\u0004\u0002\u0000\u0106\u0107\u0005(\u0000\u0000\u0107"
        + "\u001f\u0001\u0000\u0000\u0000\u0108\u0109\u0005\u0001\u0000\u0000\u0109"
        + "\u010c\u0005,\u0000\u0000\u010a\u010b\u0005!\u0000\u0000\u010b\u010d\u0003"
        + "$\u0012\u0000\u010c\u010a\u0001\u0000\u0000\u0000\u010c\u010d\u0001\u0000"
        + "\u0000\u0000\u010d\u010e\u0001\u0000\u0000\u0000\u010e\u010f\u0005#\u0000"
        + "\u0000\u010f\u0110\u0003\u0004\u0002\u0000\u0110\u0111\u0005\"\u0000\u0000"
        + "\u0111\u0116\u0001\u0000\u0000\u0000\u0112\u0113\u0003\u0004\u0002\u0000"
        + "\u0113\u0114\u0005\"\u0000\u0000\u0114\u0116\u0001\u0000\u0000\u0000\u0115"
        + "\u0108\u0001\u0000\u0000\u0000\u0115\u0112\u0001\u0000\u0000\u0000\u0116"
        + "!\u0001\u0000\u0000\u0000\u0117\u011f\u0005,\u0000\u0000\u0118\u0119\u0005"
        + "%\u0000\u0000\u0119\u011a\u0005,\u0000\u0000\u011a\u011b\u0005!\u0000"
        + "\u0000\u011b\u011c\u0003$\u0012\u0000\u011c\u011d\u0005&\u0000\u0000\u011d"
        + "\u011f\u0001\u0000\u0000\u0000\u011e\u0117\u0001\u0000\u0000\u0000\u011e"
        + "\u0118\u0001\u0000\u0000\u0000\u011f#\u0001\u0000\u0000\u0000\u0120\u0121"
        + "\u0003&\u0013\u0000\u0121\u0122\u0005\u0012\u0000\u0000\u0122\u0123\u0003"
        + "$\u0012\u0000\u0123\u0126\u0001\u0000\u0000\u0000\u0124\u0126\u0003&\u0013"
        + "\u0000\u0125\u0120\u0001\u0000\u0000\u0000\u0125\u0124\u0001\u0000\u0000"
        + "\u0000\u0126%\u0001\u0000\u0000\u0000\u0127\u0131\u0005,\u0000\u0000\u0128"
        + "\u0129\u0005\'\u0000\u0000\u0129\u012a\u0003(\u0014\u0000\u012a\u012b"
        + "\u0005(\u0000\u0000\u012b\u0131\u0001\u0000\u0000\u0000\u012c\u012d\u0005"
        + "%\u0000\u0000\u012d\u012e\u0003$\u0012\u0000\u012e\u012f\u0005&\u0000"
        + "\u0000\u012f\u0131\u0001\u0000\u0000\u0000\u0130\u0127\u0001\u0000\u0000"
        + "\u0000\u0130\u0128\u0001\u0000\u0000\u0000\u0130\u012c\u0001\u0000\u0000"
        + "\u0000\u0131\'\u0001\u0000\u0000\u0000\u0132\u0137\u0003*\u0015\u0000"
        + "\u0133\u0134\u0005 \u0000\u0000\u0134\u0136\u0003*\u0015\u0000\u0135\u0133"
        + "\u0001\u0000\u0000\u0000\u0136\u0139\u0001\u0000\u0000\u0000\u0137\u0135"
        + "\u0001\u0000\u0000\u0000\u0137\u0138\u0001\u0000\u0000\u0000\u0138\u013c"
        + "\u0001\u0000\u0000\u0000\u0139\u0137\u0001\u0000\u0000\u0000\u013a\u013b"
        + "\u0005$\u0000\u0000\u013b\u013d\u0005,\u0000\u0000\u013c\u013a\u0001\u0000"
        + "\u0000\u0000\u013c\u013d\u0001\u0000\u0000\u0000\u013d)\u0001\u0000\u0000"
        + "\u0000\u013e\u013f\u0005,\u0000\u0000\u013f\u0140\u0005!\u0000\u0000\u0140"
        + "\u0141\u0003$\u0012\u0000\u0141+\u0001\u0000\u0000\u0000\u001b/9CNT^i"
        + "t|\u0083\u008d\u0098\u009e\u00a7\u00c6\u00d3\u00e0\u00ed\u00f4\u0103\u010c"
        + "\u0115\u011e\u0125\u0130\u0137\u013c";
    public static final ATN _ATN = new ATNDeserializer().deserialize(_serializedATN.toCharArray());
    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}
