// Generated from /Users/t.vilaverde/Workspace/Elastic/elasticsearch/x-pack/plugin/piescript/src/main/antlr/PiescriptAntlrParser.g4 by ANTLR 4.9.2
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class PiescriptAntlrParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.9.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		LET=1, IN=2, FN=3, IF=4, THEN=5, ELSE=6, TRUE=7, FALSE=8, NULL=9, MATCH=10, 
		QUERY=11, PAR=12, DO=13, UNDERSCORE=14, PIPE_OP=15, OR_OP=16, AND_OP=17, 
		ARROW=18, EQ=19, NEQ=20, LTE=21, GTE=22, LT=23, GT=24, PLUS=25, MINUS=26, 
		ASTERISK=27, SLASH=28, PERCENT=29, BANG=30, DOT=31, COMMA=32, COLON=33, 
		SEMICOLON=34, ASSIGN=35, BAR=36, LPAREN=37, RPAREN=38, LBRACE=39, RBRACE=40, 
		INTEGER_LITERAL=41, DECIMAL_LITERAL=42, QUOTED_STRING=43, IDENTIFIER=44, 
		LINE_COMMENT=45, MULTILINE_COMMENT=46, WS=47, UPPER_IDENT=48, LOWER_IDENT=49;
	public static final int
		RULE_ident = 0, RULE_program = 1, RULE_topBinding = 2, RULE_expr = 3, 
		RULE_pipeExpr = 4, RULE_orExpr = 5, RULE_andExpr = 6, RULE_eqExpr = 7, 
		RULE_cmpExpr = 8, RULE_addExpr = 9, RULE_mulExpr = 10, RULE_unaryExpr = 11, 
		RULE_appExpr = 12, RULE_primary = 13, RULE_recordField = 14, RULE_recordUpdate = 15, 
		RULE_block = 16, RULE_blockStmt = 17, RULE_param = 18, RULE_type = 19, 
		RULE_typePrimary = 20, RULE_rowType = 21, RULE_rowField = 22;
	private static String[] makeRuleNames() {
		return new String[] {
			"ident", "program", "topBinding", "expr", "pipeExpr", "orExpr", "andExpr", 
			"eqExpr", "cmpExpr", "addExpr", "mulExpr", "unaryExpr", "appExpr", "primary", 
			"recordField", "recordUpdate", "block", "blockStmt", "param", "type", 
			"typePrimary", "rowType", "rowField"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'let'", "'in'", "'fn'", "'if'", "'then'", "'else'", "'true'", 
			"'false'", "'null'", "'match'", "'query'", "'par'", "'do'", "'_'", "'|>'", 
			"'||'", "'&&'", "'->'", "'=='", "'!='", "'<='", "'>='", "'<'", "'>'", 
			"'+'", "'-'", "'*'", "'/'", "'%'", "'!'", "'.'", "','", "':'", "';'", 
			"'='", "'|'", "'('", "')'", "'{'", "'}'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "LET", "IN", "FN", "IF", "THEN", "ELSE", "TRUE", "FALSE", "NULL", 
			"MATCH", "QUERY", "PAR", "DO", "UNDERSCORE", "PIPE_OP", "OR_OP", "AND_OP", 
			"ARROW", "EQ", "NEQ", "LTE", "GTE", "LT", "GT", "PLUS", "MINUS", "ASTERISK", 
			"SLASH", "PERCENT", "BANG", "DOT", "COMMA", "COLON", "SEMICOLON", "ASSIGN", 
			"BAR", "LPAREN", "RPAREN", "LBRACE", "RBRACE", "INTEGER_LITERAL", "DECIMAL_LITERAL", 
			"QUOTED_STRING", "IDENTIFIER", "LINE_COMMENT", "MULTILINE_COMMENT", "WS", 
			"UPPER_IDENT", "LOWER_IDENT"
		};
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
	public String getGrammarFileName() { return "PiescriptAntlrParser.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public PiescriptAntlrParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class IdentContext extends ParserRuleContext {
		public TerminalNode UPPER_IDENT() { return getToken(PiescriptAntlrParser.UPPER_IDENT, 0); }
		public TerminalNode LOWER_IDENT() { return getToken(PiescriptAntlrParser.LOWER_IDENT, 0); }
		public IdentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ident; }
	}

	public final IdentContext ident() throws RecognitionException {
		IdentContext _localctx = new IdentContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_ident);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(46);
			_la = _input.LA(1);
			if ( !(_la==UPPER_IDENT || _la==LOWER_IDENT) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ProgramContext extends ParserRuleContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode EOF() { return getToken(PiescriptAntlrParser.EOF, 0); }
		public List<TopBindingContext> topBinding() {
			return getRuleContexts(TopBindingContext.class);
		}
		public TopBindingContext topBinding(int i) {
			return getRuleContext(TopBindingContext.class,i);
		}
		public ProgramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_program; }
	}

	public final ProgramContext program() throws RecognitionException {
		ProgramContext _localctx = new ProgramContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_program);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(51);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(48);
					topBinding();
					}
					} 
				}
				setState(53);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			}
			setState(54);
			expr();
			setState(55);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TopBindingContext extends ParserRuleContext {
		public TerminalNode LET() { return getToken(PiescriptAntlrParser.LET, 0); }
		public IdentContext ident() {
			return getRuleContext(IdentContext.class,0);
		}
		public TerminalNode ASSIGN() { return getToken(PiescriptAntlrParser.ASSIGN, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode SEMICOLON() { return getToken(PiescriptAntlrParser.SEMICOLON, 0); }
		public TerminalNode COLON() { return getToken(PiescriptAntlrParser.COLON, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TopBindingContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_topBinding; }
	}

	public final TopBindingContext topBinding() throws RecognitionException {
		TopBindingContext _localctx = new TopBindingContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_topBinding);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(57);
			match(LET);
			setState(58);
			ident();
			setState(61);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COLON) {
				{
				setState(59);
				match(COLON);
				setState(60);
				type();
				}
			}

			setState(63);
			match(ASSIGN);
			setState(64);
			expr();
			setState(65);
			match(SEMICOLON);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExprContext extends ParserRuleContext {
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
	 
		public ExprContext() { }
		public void copyFrom(ExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class LetExprContext extends ExprContext {
		public TerminalNode LET() { return getToken(PiescriptAntlrParser.LET, 0); }
		public IdentContext ident() {
			return getRuleContext(IdentContext.class,0);
		}
		public TerminalNode ASSIGN() { return getToken(PiescriptAntlrParser.ASSIGN, 0); }
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode IN() { return getToken(PiescriptAntlrParser.IN, 0); }
		public TerminalNode COLON() { return getToken(PiescriptAntlrParser.COLON, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public LetExprContext(ExprContext ctx) { copyFrom(ctx); }
	}
	public static class LambdaExprContext extends ExprContext {
		public TerminalNode FN() { return getToken(PiescriptAntlrParser.FN, 0); }
		public TerminalNode ARROW() { return getToken(PiescriptAntlrParser.ARROW, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public List<ParamContext> param() {
			return getRuleContexts(ParamContext.class);
		}
		public ParamContext param(int i) {
			return getRuleContext(ParamContext.class,i);
		}
		public LambdaExprContext(ExprContext ctx) { copyFrom(ctx); }
	}
	public static class ExprPipeContext extends ExprContext {
		public PipeExprContext pipeExpr() {
			return getRuleContext(PipeExprContext.class,0);
		}
		public ExprPipeContext(ExprContext ctx) { copyFrom(ctx); }
	}

	public final ExprContext expr() throws RecognitionException {
		ExprContext _localctx = new ExprContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_expr);
		int _la;
		try {
			setState(88);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LET:
				_localctx = new LetExprContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(67);
				match(LET);
				setState(68);
				ident();
				setState(71);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COLON) {
					{
					setState(69);
					match(COLON);
					setState(70);
					type();
					}
				}

				setState(73);
				match(ASSIGN);
				setState(74);
				expr();
				setState(75);
				match(IN);
				setState(76);
				expr();
				}
				break;
			case FN:
				_localctx = new LambdaExprContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(78);
				match(FN);
				setState(80); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(79);
					param();
					}
					}
					setState(82); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==LPAREN || _la==IDENTIFIER );
				setState(84);
				match(ARROW);
				setState(85);
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
				enterOuterAlt(_localctx, 3);
				{
				setState(87);
				pipeExpr(0);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PipeExprContext extends ParserRuleContext {
		public PipeExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pipeExpr; }
	 
		public PipeExprContext() { }
		public void copyFrom(PipeExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class PipeOpContext extends PipeExprContext {
		public PipeExprContext pipeExpr() {
			return getRuleContext(PipeExprContext.class,0);
		}
		public TerminalNode PIPE_OP() { return getToken(PiescriptAntlrParser.PIPE_OP, 0); }
		public OrExprContext orExpr() {
			return getRuleContext(OrExprContext.class,0);
		}
		public PipeOpContext(PipeExprContext ctx) { copyFrom(ctx); }
	}
	public static class PipePassthroughContext extends PipeExprContext {
		public OrExprContext orExpr() {
			return getRuleContext(OrExprContext.class,0);
		}
		public PipePassthroughContext(PipeExprContext ctx) { copyFrom(ctx); }
	}

	public final PipeExprContext pipeExpr() throws RecognitionException {
		return pipeExpr(0);
	}

	private PipeExprContext pipeExpr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		PipeExprContext _localctx = new PipeExprContext(_ctx, _parentState);
		PipeExprContext _prevctx = _localctx;
		int _startState = 8;
		enterRecursionRule(_localctx, 8, RULE_pipeExpr, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			_localctx = new PipePassthroughContext(_localctx);
			_ctx = _localctx;
			_prevctx = _localctx;

			setState(91);
			orExpr(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(98);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new PipeOpContext(new PipeExprContext(_parentctx, _parentState));
					pushNewRecursionContext(_localctx, _startState, RULE_pipeExpr);
					setState(93);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(94);
					match(PIPE_OP);
					setState(95);
					orExpr(0);
					}
					} 
				}
				setState(100);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class OrExprContext extends ParserRuleContext {
		public OrExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_orExpr; }
	 
		public OrExprContext() { }
		public void copyFrom(OrExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class OrPassthroughContext extends OrExprContext {
		public AndExprContext andExpr() {
			return getRuleContext(AndExprContext.class,0);
		}
		public OrPassthroughContext(OrExprContext ctx) { copyFrom(ctx); }
	}
	public static class OrOpContext extends OrExprContext {
		public OrExprContext orExpr() {
			return getRuleContext(OrExprContext.class,0);
		}
		public TerminalNode OR_OP() { return getToken(PiescriptAntlrParser.OR_OP, 0); }
		public AndExprContext andExpr() {
			return getRuleContext(AndExprContext.class,0);
		}
		public OrOpContext(OrExprContext ctx) { copyFrom(ctx); }
	}

	public final OrExprContext orExpr() throws RecognitionException {
		return orExpr(0);
	}

	private OrExprContext orExpr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		OrExprContext _localctx = new OrExprContext(_ctx, _parentState);
		OrExprContext _prevctx = _localctx;
		int _startState = 10;
		enterRecursionRule(_localctx, 10, RULE_orExpr, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			_localctx = new OrPassthroughContext(_localctx);
			_ctx = _localctx;
			_prevctx = _localctx;

			setState(102);
			andExpr(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(109);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new OrOpContext(new OrExprContext(_parentctx, _parentState));
					pushNewRecursionContext(_localctx, _startState, RULE_orExpr);
					setState(104);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(105);
					match(OR_OP);
					setState(106);
					andExpr(0);
					}
					} 
				}
				setState(111);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class AndExprContext extends ParserRuleContext {
		public AndExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_andExpr; }
	 
		public AndExprContext() { }
		public void copyFrom(AndExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class AndPassthroughContext extends AndExprContext {
		public EqExprContext eqExpr() {
			return getRuleContext(EqExprContext.class,0);
		}
		public AndPassthroughContext(AndExprContext ctx) { copyFrom(ctx); }
	}
	public static class AndOpContext extends AndExprContext {
		public AndExprContext andExpr() {
			return getRuleContext(AndExprContext.class,0);
		}
		public TerminalNode AND_OP() { return getToken(PiescriptAntlrParser.AND_OP, 0); }
		public EqExprContext eqExpr() {
			return getRuleContext(EqExprContext.class,0);
		}
		public AndOpContext(AndExprContext ctx) { copyFrom(ctx); }
	}

	public final AndExprContext andExpr() throws RecognitionException {
		return andExpr(0);
	}

	private AndExprContext andExpr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		AndExprContext _localctx = new AndExprContext(_ctx, _parentState);
		AndExprContext _prevctx = _localctx;
		int _startState = 12;
		enterRecursionRule(_localctx, 12, RULE_andExpr, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			_localctx = new AndPassthroughContext(_localctx);
			_ctx = _localctx;
			_prevctx = _localctx;

			setState(113);
			eqExpr();
			}
			_ctx.stop = _input.LT(-1);
			setState(120);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new AndOpContext(new AndExprContext(_parentctx, _parentState));
					pushNewRecursionContext(_localctx, _startState, RULE_andExpr);
					setState(115);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(116);
					match(AND_OP);
					setState(117);
					eqExpr();
					}
					} 
				}
				setState(122);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class EqExprContext extends ParserRuleContext {
		public EqExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_eqExpr; }
	 
		public EqExprContext() { }
		public void copyFrom(EqExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class EqPassthroughContext extends EqExprContext {
		public CmpExprContext cmpExpr() {
			return getRuleContext(CmpExprContext.class,0);
		}
		public EqPassthroughContext(EqExprContext ctx) { copyFrom(ctx); }
	}
	public static class EqualityOpContext extends EqExprContext {
		public Token op;
		public List<CmpExprContext> cmpExpr() {
			return getRuleContexts(CmpExprContext.class);
		}
		public CmpExprContext cmpExpr(int i) {
			return getRuleContext(CmpExprContext.class,i);
		}
		public TerminalNode EQ() { return getToken(PiescriptAntlrParser.EQ, 0); }
		public TerminalNode NEQ() { return getToken(PiescriptAntlrParser.NEQ, 0); }
		public EqualityOpContext(EqExprContext ctx) { copyFrom(ctx); }
	}

	public final EqExprContext eqExpr() throws RecognitionException {
		EqExprContext _localctx = new EqExprContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_eqExpr);
		int _la;
		try {
			setState(128);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
			case 1:
				_localctx = new EqualityOpContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(123);
				cmpExpr();
				setState(124);
				((EqualityOpContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==EQ || _la==NEQ) ) {
					((EqualityOpContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(125);
				cmpExpr();
				}
				break;
			case 2:
				_localctx = new EqPassthroughContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(127);
				cmpExpr();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CmpExprContext extends ParserRuleContext {
		public CmpExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cmpExpr; }
	 
		public CmpExprContext() { }
		public void copyFrom(CmpExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ComparisonOpContext extends CmpExprContext {
		public Token op;
		public List<AddExprContext> addExpr() {
			return getRuleContexts(AddExprContext.class);
		}
		public AddExprContext addExpr(int i) {
			return getRuleContext(AddExprContext.class,i);
		}
		public TerminalNode LTE() { return getToken(PiescriptAntlrParser.LTE, 0); }
		public TerminalNode GTE() { return getToken(PiescriptAntlrParser.GTE, 0); }
		public TerminalNode LT() { return getToken(PiescriptAntlrParser.LT, 0); }
		public TerminalNode GT() { return getToken(PiescriptAntlrParser.GT, 0); }
		public ComparisonOpContext(CmpExprContext ctx) { copyFrom(ctx); }
	}
	public static class CmpPassthroughContext extends CmpExprContext {
		public AddExprContext addExpr() {
			return getRuleContext(AddExprContext.class,0);
		}
		public CmpPassthroughContext(CmpExprContext ctx) { copyFrom(ctx); }
	}

	public final CmpExprContext cmpExpr() throws RecognitionException {
		CmpExprContext _localctx = new CmpExprContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_cmpExpr);
		int _la;
		try {
			setState(135);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				_localctx = new ComparisonOpContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(130);
				addExpr(0);
				setState(131);
				((ComparisonOpContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LTE) | (1L << GTE) | (1L << LT) | (1L << GT))) != 0)) ) {
					((ComparisonOpContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(132);
				addExpr(0);
				}
				break;
			case 2:
				_localctx = new CmpPassthroughContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(134);
				addExpr(0);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AddExprContext extends ParserRuleContext {
		public AddExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_addExpr; }
	 
		public AddExprContext() { }
		public void copyFrom(AddExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class AdditiveOpContext extends AddExprContext {
		public Token op;
		public AddExprContext addExpr() {
			return getRuleContext(AddExprContext.class,0);
		}
		public MulExprContext mulExpr() {
			return getRuleContext(MulExprContext.class,0);
		}
		public TerminalNode PLUS() { return getToken(PiescriptAntlrParser.PLUS, 0); }
		public TerminalNode MINUS() { return getToken(PiescriptAntlrParser.MINUS, 0); }
		public AdditiveOpContext(AddExprContext ctx) { copyFrom(ctx); }
	}
	public static class AddPassthroughContext extends AddExprContext {
		public MulExprContext mulExpr() {
			return getRuleContext(MulExprContext.class,0);
		}
		public AddPassthroughContext(AddExprContext ctx) { copyFrom(ctx); }
	}

	public final AddExprContext addExpr() throws RecognitionException {
		return addExpr(0);
	}

	private AddExprContext addExpr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		AddExprContext _localctx = new AddExprContext(_ctx, _parentState);
		AddExprContext _prevctx = _localctx;
		int _startState = 18;
		enterRecursionRule(_localctx, 18, RULE_addExpr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			_localctx = new AddPassthroughContext(_localctx);
			_ctx = _localctx;
			_prevctx = _localctx;

			setState(138);
			mulExpr(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(145);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new AdditiveOpContext(new AddExprContext(_parentctx, _parentState));
					pushNewRecursionContext(_localctx, _startState, RULE_addExpr);
					setState(140);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(141);
					((AdditiveOpContext)_localctx).op = _input.LT(1);
					_la = _input.LA(1);
					if ( !(_la==PLUS || _la==MINUS) ) {
						((AdditiveOpContext)_localctx).op = (Token)_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(142);
					mulExpr(0);
					}
					} 
				}
				setState(147);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class MulExprContext extends ParserRuleContext {
		public MulExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mulExpr; }
	 
		public MulExprContext() { }
		public void copyFrom(MulExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class MultiplicativeOpContext extends MulExprContext {
		public Token op;
		public MulExprContext mulExpr() {
			return getRuleContext(MulExprContext.class,0);
		}
		public UnaryExprContext unaryExpr() {
			return getRuleContext(UnaryExprContext.class,0);
		}
		public TerminalNode ASTERISK() { return getToken(PiescriptAntlrParser.ASTERISK, 0); }
		public TerminalNode SLASH() { return getToken(PiescriptAntlrParser.SLASH, 0); }
		public TerminalNode PERCENT() { return getToken(PiescriptAntlrParser.PERCENT, 0); }
		public MultiplicativeOpContext(MulExprContext ctx) { copyFrom(ctx); }
	}
	public static class MulPassthroughContext extends MulExprContext {
		public UnaryExprContext unaryExpr() {
			return getRuleContext(UnaryExprContext.class,0);
		}
		public MulPassthroughContext(MulExprContext ctx) { copyFrom(ctx); }
	}

	public final MulExprContext mulExpr() throws RecognitionException {
		return mulExpr(0);
	}

	private MulExprContext mulExpr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		MulExprContext _localctx = new MulExprContext(_ctx, _parentState);
		MulExprContext _prevctx = _localctx;
		int _startState = 20;
		enterRecursionRule(_localctx, 20, RULE_mulExpr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			_localctx = new MulPassthroughContext(_localctx);
			_ctx = _localctx;
			_prevctx = _localctx;

			setState(149);
			unaryExpr();
			}
			_ctx.stop = _input.LT(-1);
			setState(156);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new MultiplicativeOpContext(new MulExprContext(_parentctx, _parentState));
					pushNewRecursionContext(_localctx, _startState, RULE_mulExpr);
					setState(151);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(152);
					((MultiplicativeOpContext)_localctx).op = _input.LT(1);
					_la = _input.LA(1);
					if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ASTERISK) | (1L << SLASH) | (1L << PERCENT))) != 0)) ) {
						((MultiplicativeOpContext)_localctx).op = (Token)_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(153);
					unaryExpr();
					}
					} 
				}
				setState(158);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class UnaryExprContext extends ParserRuleContext {
		public UnaryExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unaryExpr; }
	 
		public UnaryExprContext() { }
		public void copyFrom(UnaryExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class UnaryOpContext extends UnaryExprContext {
		public Token op;
		public UnaryExprContext unaryExpr() {
			return getRuleContext(UnaryExprContext.class,0);
		}
		public TerminalNode BANG() { return getToken(PiescriptAntlrParser.BANG, 0); }
		public TerminalNode MINUS() { return getToken(PiescriptAntlrParser.MINUS, 0); }
		public UnaryOpContext(UnaryExprContext ctx) { copyFrom(ctx); }
	}
	public static class UnaryPassthroughContext extends UnaryExprContext {
		public AppExprContext appExpr() {
			return getRuleContext(AppExprContext.class,0);
		}
		public UnaryPassthroughContext(UnaryExprContext ctx) { copyFrom(ctx); }
	}

	public final UnaryExprContext unaryExpr() throws RecognitionException {
		UnaryExprContext _localctx = new UnaryExprContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_unaryExpr);
		int _la;
		try {
			setState(162);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case MINUS:
			case BANG:
				_localctx = new UnaryOpContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(159);
				((UnaryOpContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==MINUS || _la==BANG) ) {
					((UnaryOpContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(160);
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
				enterOuterAlt(_localctx, 2);
				{
				setState(161);
				appExpr(0);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AppExprContext extends ParserRuleContext {
		public AppExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_appExpr; }
	 
		public AppExprContext() { }
		public void copyFrom(AppExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class AppPassthroughContext extends AppExprContext {
		public PrimaryContext primary() {
			return getRuleContext(PrimaryContext.class,0);
		}
		public AppPassthroughContext(AppExprContext ctx) { copyFrom(ctx); }
	}
	public static class ApplicationContext extends AppExprContext {
		public AppExprContext appExpr() {
			return getRuleContext(AppExprContext.class,0);
		}
		public PrimaryContext primary() {
			return getRuleContext(PrimaryContext.class,0);
		}
		public ApplicationContext(AppExprContext ctx) { copyFrom(ctx); }
	}

	public final AppExprContext appExpr() throws RecognitionException {
		return appExpr(0);
	}

	private AppExprContext appExpr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		AppExprContext _localctx = new AppExprContext(_ctx, _parentState);
		AppExprContext _prevctx = _localctx;
		int _startState = 24;
		enterRecursionRule(_localctx, 24, RULE_appExpr, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			_localctx = new AppPassthroughContext(_localctx);
			_ctx = _localctx;
			_prevctx = _localctx;

			setState(165);
			primary(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(171);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,13,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new ApplicationContext(new AppExprContext(_parentctx, _parentState));
					pushNewRecursionContext(_localctx, _startState, RULE_appExpr);
					setState(167);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(168);
					primary(0);
					}
					} 
				}
				setState(173);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,13,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class PrimaryContext extends ParserRuleContext {
		public PrimaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_primary; }
	 
		public PrimaryContext() { }
		public void copyFrom(PrimaryContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class EmptyRecordContext extends PrimaryContext {
		public TerminalNode LBRACE() { return getToken(PiescriptAntlrParser.LBRACE, 0); }
		public TerminalNode RBRACE() { return getToken(PiescriptAntlrParser.RBRACE, 0); }
		public EmptyRecordContext(PrimaryContext ctx) { copyFrom(ctx); }
	}
	public static class RecordLiteralContext extends PrimaryContext {
		public TerminalNode LBRACE() { return getToken(PiescriptAntlrParser.LBRACE, 0); }
		public List<RecordFieldContext> recordField() {
			return getRuleContexts(RecordFieldContext.class);
		}
		public RecordFieldContext recordField(int i) {
			return getRuleContext(RecordFieldContext.class,i);
		}
		public TerminalNode RBRACE() { return getToken(PiescriptAntlrParser.RBRACE, 0); }
		public List<TerminalNode> COMMA() { return getTokens(PiescriptAntlrParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(PiescriptAntlrParser.COMMA, i);
		}
		public RecordLiteralContext(PrimaryContext ctx) { copyFrom(ctx); }
	}
	public static class IfExprContext extends PrimaryContext {
		public TerminalNode IF() { return getToken(PiescriptAntlrParser.IF, 0); }
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode THEN() { return getToken(PiescriptAntlrParser.THEN, 0); }
		public TerminalNode ELSE() { return getToken(PiescriptAntlrParser.ELSE, 0); }
		public IfExprContext(PrimaryContext ctx) { copyFrom(ctx); }
	}
	public static class VariableContext extends PrimaryContext {
		public TerminalNode IDENTIFIER() { return getToken(PiescriptAntlrParser.IDENTIFIER, 0); }
		public VariableContext(PrimaryContext ctx) { copyFrom(ctx); }
	}
	public static class ProjectionContext extends PrimaryContext {
		public PrimaryContext primary() {
			return getRuleContext(PrimaryContext.class,0);
		}
		public TerminalNode DOT() { return getToken(PiescriptAntlrParser.DOT, 0); }
		public TerminalNode IDENTIFIER() { return getToken(PiescriptAntlrParser.IDENTIFIER, 0); }
		public ProjectionContext(PrimaryContext ctx) { copyFrom(ctx); }
	}
	public static class AccessorContext extends PrimaryContext {
		public TerminalNode DOT() { return getToken(PiescriptAntlrParser.DOT, 0); }
		public TerminalNode IDENTIFIER() { return getToken(PiescriptAntlrParser.IDENTIFIER, 0); }
		public AccessorContext(PrimaryContext ctx) { copyFrom(ctx); }
	}
	public static class BlockExprContext extends PrimaryContext {
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public BlockExprContext(PrimaryContext ctx) { copyFrom(ctx); }
	}
	public static class FalseLiteralContext extends PrimaryContext {
		public TerminalNode FALSE() { return getToken(PiescriptAntlrParser.FALSE, 0); }
		public FalseLiteralContext(PrimaryContext ctx) { copyFrom(ctx); }
	}
	public static class StringLiteralContext extends PrimaryContext {
		public TerminalNode QUOTED_STRING() { return getToken(PiescriptAntlrParser.QUOTED_STRING, 0); }
		public StringLiteralContext(PrimaryContext ctx) { copyFrom(ctx); }
	}
	public static class TrueLiteralContext extends PrimaryContext {
		public TerminalNode TRUE() { return getToken(PiescriptAntlrParser.TRUE, 0); }
		public TrueLiteralContext(PrimaryContext ctx) { copyFrom(ctx); }
	}
	public static class AscriptionContext extends PrimaryContext {
		public TerminalNode LPAREN() { return getToken(PiescriptAntlrParser.LPAREN, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode COLON() { return getToken(PiescriptAntlrParser.COLON, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(PiescriptAntlrParser.RPAREN, 0); }
		public AscriptionContext(PrimaryContext ctx) { copyFrom(ctx); }
	}
	public static class DecimalLiteralContext extends PrimaryContext {
		public TerminalNode DECIMAL_LITERAL() { return getToken(PiescriptAntlrParser.DECIMAL_LITERAL, 0); }
		public DecimalLiteralContext(PrimaryContext ctx) { copyFrom(ctx); }
	}
	public static class RecordUpdateExprContext extends PrimaryContext {
		public TerminalNode LBRACE() { return getToken(PiescriptAntlrParser.LBRACE, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode BAR() { return getToken(PiescriptAntlrParser.BAR, 0); }
		public List<RecordUpdateContext> recordUpdate() {
			return getRuleContexts(RecordUpdateContext.class);
		}
		public RecordUpdateContext recordUpdate(int i) {
			return getRuleContext(RecordUpdateContext.class,i);
		}
		public TerminalNode RBRACE() { return getToken(PiescriptAntlrParser.RBRACE, 0); }
		public List<TerminalNode> COMMA() { return getTokens(PiescriptAntlrParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(PiescriptAntlrParser.COMMA, i);
		}
		public RecordUpdateExprContext(PrimaryContext ctx) { copyFrom(ctx); }
	}
	public static class ParenExprContext extends PrimaryContext {
		public TerminalNode LPAREN() { return getToken(PiescriptAntlrParser.LPAREN, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(PiescriptAntlrParser.RPAREN, 0); }
		public ParenExprContext(PrimaryContext ctx) { copyFrom(ctx); }
	}
	public static class NullLiteralContext extends PrimaryContext {
		public TerminalNode NULL() { return getToken(PiescriptAntlrParser.NULL, 0); }
		public NullLiteralContext(PrimaryContext ctx) { copyFrom(ctx); }
	}
	public static class IntegerLiteralContext extends PrimaryContext {
		public TerminalNode INTEGER_LITERAL() { return getToken(PiescriptAntlrParser.INTEGER_LITERAL, 0); }
		public IntegerLiteralContext(PrimaryContext ctx) { copyFrom(ctx); }
	}
	public static class UpdateSugarContext extends PrimaryContext {
		public TerminalNode LBRACE() { return getToken(PiescriptAntlrParser.LBRACE, 0); }
		public TerminalNode UNDERSCORE() { return getToken(PiescriptAntlrParser.UNDERSCORE, 0); }
		public TerminalNode BAR() { return getToken(PiescriptAntlrParser.BAR, 0); }
		public List<RecordUpdateContext> recordUpdate() {
			return getRuleContexts(RecordUpdateContext.class);
		}
		public RecordUpdateContext recordUpdate(int i) {
			return getRuleContext(RecordUpdateContext.class,i);
		}
		public TerminalNode RBRACE() { return getToken(PiescriptAntlrParser.RBRACE, 0); }
		public List<TerminalNode> COMMA() { return getTokens(PiescriptAntlrParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(PiescriptAntlrParser.COMMA, i);
		}
		public UpdateSugarContext(PrimaryContext ctx) { copyFrom(ctx); }
	}

	public final PrimaryContext primary() throws RecognitionException {
		return primary(0);
	}

	private PrimaryContext primary(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		PrimaryContext _localctx = new PrimaryContext(_ctx, _parentState);
		PrimaryContext _prevctx = _localctx;
		int _startState = 26;
		enterRecursionRule(_localctx, 26, RULE_primary, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(241);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
			case 1:
				{
				_localctx = new AccessorContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(175);
				match(DOT);
				setState(176);
				match(IDENTIFIER);
				}
				break;
			case 2:
				{
				_localctx = new IntegerLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(177);
				match(INTEGER_LITERAL);
				}
				break;
			case 3:
				{
				_localctx = new DecimalLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(178);
				match(DECIMAL_LITERAL);
				}
				break;
			case 4:
				{
				_localctx = new StringLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(179);
				match(QUOTED_STRING);
				}
				break;
			case 5:
				{
				_localctx = new TrueLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(180);
				match(TRUE);
				}
				break;
			case 6:
				{
				_localctx = new FalseLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(181);
				match(FALSE);
				}
				break;
			case 7:
				{
				_localctx = new NullLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(182);
				match(NULL);
				}
				break;
			case 8:
				{
				_localctx = new VariableContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(183);
				match(IDENTIFIER);
				}
				break;
			case 9:
				{
				_localctx = new AscriptionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(184);
				match(LPAREN);
				setState(185);
				expr();
				setState(186);
				match(COLON);
				setState(187);
				type();
				setState(188);
				match(RPAREN);
				}
				break;
			case 10:
				{
				_localctx = new ParenExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(190);
				match(LPAREN);
				setState(191);
				expr();
				setState(192);
				match(RPAREN);
				}
				break;
			case 11:
				{
				_localctx = new EmptyRecordContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(194);
				match(LBRACE);
				setState(195);
				match(RBRACE);
				}
				break;
			case 12:
				{
				_localctx = new RecordLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(196);
				match(LBRACE);
				setState(197);
				recordField();
				setState(202);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(198);
					match(COMMA);
					setState(199);
					recordField();
					}
					}
					setState(204);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(205);
				match(RBRACE);
				}
				break;
			case 13:
				{
				_localctx = new RecordUpdateExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(207);
				match(LBRACE);
				setState(208);
				expr();
				setState(209);
				match(BAR);
				setState(210);
				recordUpdate();
				setState(215);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(211);
					match(COMMA);
					setState(212);
					recordUpdate();
					}
					}
					setState(217);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(218);
				match(RBRACE);
				}
				break;
			case 14:
				{
				_localctx = new UpdateSugarContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(220);
				match(LBRACE);
				setState(221);
				match(UNDERSCORE);
				setState(222);
				match(BAR);
				setState(223);
				recordUpdate();
				setState(228);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(224);
					match(COMMA);
					setState(225);
					recordUpdate();
					}
					}
					setState(230);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(231);
				match(RBRACE);
				}
				break;
			case 15:
				{
				_localctx = new IfExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(233);
				match(IF);
				setState(234);
				expr();
				setState(235);
				match(THEN);
				setState(236);
				expr();
				setState(237);
				match(ELSE);
				setState(238);
				expr();
				}
				break;
			case 16:
				{
				_localctx = new BlockExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(240);
				block();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(248);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,18,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new ProjectionContext(new PrimaryContext(_parentctx, _parentState));
					pushNewRecursionContext(_localctx, _startState, RULE_primary);
					setState(243);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(244);
					match(DOT);
					setState(245);
					match(IDENTIFIER);
					}
					} 
				}
				setState(250);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,18,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class RecordFieldContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(PiescriptAntlrParser.IDENTIFIER, 0); }
		public TerminalNode COLON() { return getToken(PiescriptAntlrParser.COLON, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public RecordFieldContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_recordField; }
	}

	public final RecordFieldContext recordField() throws RecognitionException {
		RecordFieldContext _localctx = new RecordFieldContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_recordField);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(251);
			match(IDENTIFIER);
			setState(252);
			match(COLON);
			setState(253);
			expr();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RecordUpdateContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(PiescriptAntlrParser.IDENTIFIER, 0); }
		public TerminalNode ASSIGN() { return getToken(PiescriptAntlrParser.ASSIGN, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public RecordUpdateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_recordUpdate; }
	}

	public final RecordUpdateContext recordUpdate() throws RecognitionException {
		RecordUpdateContext _localctx = new RecordUpdateContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_recordUpdate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(255);
			match(IDENTIFIER);
			setState(256);
			match(ASSIGN);
			setState(257);
			expr();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BlockContext extends ParserRuleContext {
		public TerminalNode LBRACE() { return getToken(PiescriptAntlrParser.LBRACE, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode RBRACE() { return getToken(PiescriptAntlrParser.RBRACE, 0); }
		public List<BlockStmtContext> blockStmt() {
			return getRuleContexts(BlockStmtContext.class);
		}
		public BlockStmtContext blockStmt(int i) {
			return getRuleContext(BlockStmtContext.class,i);
		}
		public BlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_block; }
	}

	public final BlockContext block() throws RecognitionException {
		BlockContext _localctx = new BlockContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_block);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(259);
			match(LBRACE);
			setState(261); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(260);
					blockStmt();
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(263); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,19,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(265);
			expr();
			setState(266);
			match(RBRACE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BlockStmtContext extends ParserRuleContext {
		public BlockStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_blockStmt; }
	 
		public BlockStmtContext() { }
		public void copyFrom(BlockStmtContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class BlockLetContext extends BlockStmtContext {
		public TerminalNode LET() { return getToken(PiescriptAntlrParser.LET, 0); }
		public TerminalNode IDENTIFIER() { return getToken(PiescriptAntlrParser.IDENTIFIER, 0); }
		public TerminalNode ASSIGN() { return getToken(PiescriptAntlrParser.ASSIGN, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode SEMICOLON() { return getToken(PiescriptAntlrParser.SEMICOLON, 0); }
		public TerminalNode COLON() { return getToken(PiescriptAntlrParser.COLON, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public BlockLetContext(BlockStmtContext ctx) { copyFrom(ctx); }
	}
	public static class BlockExprStmtContext extends BlockStmtContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode SEMICOLON() { return getToken(PiescriptAntlrParser.SEMICOLON, 0); }
		public BlockExprStmtContext(BlockStmtContext ctx) { copyFrom(ctx); }
	}

	public final BlockStmtContext blockStmt() throws RecognitionException {
		BlockStmtContext _localctx = new BlockStmtContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_blockStmt);
		int _la;
		try {
			setState(281);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
			case 1:
				_localctx = new BlockLetContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(268);
				match(LET);
				setState(269);
				match(IDENTIFIER);
				setState(272);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COLON) {
					{
					setState(270);
					match(COLON);
					setState(271);
					type();
					}
				}

				setState(274);
				match(ASSIGN);
				setState(275);
				expr();
				setState(276);
				match(SEMICOLON);
				}
				break;
			case 2:
				_localctx = new BlockExprStmtContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(278);
				expr();
				setState(279);
				match(SEMICOLON);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ParamContext extends ParserRuleContext {
		public ParamContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_param; }
	 
		public ParamContext() { }
		public void copyFrom(ParamContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class TypedParamContext extends ParamContext {
		public TerminalNode LPAREN() { return getToken(PiescriptAntlrParser.LPAREN, 0); }
		public TerminalNode IDENTIFIER() { return getToken(PiescriptAntlrParser.IDENTIFIER, 0); }
		public TerminalNode COLON() { return getToken(PiescriptAntlrParser.COLON, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(PiescriptAntlrParser.RPAREN, 0); }
		public TypedParamContext(ParamContext ctx) { copyFrom(ctx); }
	}
	public static class UntypedParamContext extends ParamContext {
		public TerminalNode IDENTIFIER() { return getToken(PiescriptAntlrParser.IDENTIFIER, 0); }
		public UntypedParamContext(ParamContext ctx) { copyFrom(ctx); }
	}

	public final ParamContext param() throws RecognitionException {
		ParamContext _localctx = new ParamContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_param);
		try {
			setState(290);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IDENTIFIER:
				_localctx = new UntypedParamContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(283);
				match(IDENTIFIER);
				}
				break;
			case LPAREN:
				_localctx = new TypedParamContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(284);
				match(LPAREN);
				setState(285);
				match(IDENTIFIER);
				setState(286);
				match(COLON);
				setState(287);
				type();
				setState(288);
				match(RPAREN);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TypeContext extends ParserRuleContext {
		public TypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type; }
	 
		public TypeContext() { }
		public void copyFrom(TypeContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class FunctionTypeContext extends TypeContext {
		public TypePrimaryContext typePrimary() {
			return getRuleContext(TypePrimaryContext.class,0);
		}
		public TerminalNode ARROW() { return getToken(PiescriptAntlrParser.ARROW, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public FunctionTypeContext(TypeContext ctx) { copyFrom(ctx); }
	}
	public static class TypeAtomContext extends TypeContext {
		public TypePrimaryContext typePrimary() {
			return getRuleContext(TypePrimaryContext.class,0);
		}
		public TypeAtomContext(TypeContext ctx) { copyFrom(ctx); }
	}

	public final TypeContext type() throws RecognitionException {
		TypeContext _localctx = new TypeContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_type);
		try {
			setState(297);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,23,_ctx) ) {
			case 1:
				_localctx = new FunctionTypeContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(292);
				typePrimary();
				setState(293);
				match(ARROW);
				setState(294);
				type();
				}
				break;
			case 2:
				_localctx = new TypeAtomContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(296);
				typePrimary();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TypePrimaryContext extends ParserRuleContext {
		public TypePrimaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typePrimary; }
	 
		public TypePrimaryContext() { }
		public void copyFrom(TypePrimaryContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class RecordTypeContext extends TypePrimaryContext {
		public TerminalNode LBRACE() { return getToken(PiescriptAntlrParser.LBRACE, 0); }
		public RowTypeContext rowType() {
			return getRuleContext(RowTypeContext.class,0);
		}
		public TerminalNode RBRACE() { return getToken(PiescriptAntlrParser.RBRACE, 0); }
		public RecordTypeContext(TypePrimaryContext ctx) { copyFrom(ctx); }
	}
	public static class TypeConContext extends TypePrimaryContext {
		public TerminalNode IDENTIFIER() { return getToken(PiescriptAntlrParser.IDENTIFIER, 0); }
		public TypeConContext(TypePrimaryContext ctx) { copyFrom(ctx); }
	}
	public static class ParenTypeContext extends TypePrimaryContext {
		public TerminalNode LPAREN() { return getToken(PiescriptAntlrParser.LPAREN, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(PiescriptAntlrParser.RPAREN, 0); }
		public ParenTypeContext(TypePrimaryContext ctx) { copyFrom(ctx); }
	}

	public final TypePrimaryContext typePrimary() throws RecognitionException {
		TypePrimaryContext _localctx = new TypePrimaryContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_typePrimary);
		try {
			setState(308);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IDENTIFIER:
				_localctx = new TypeConContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(299);
				match(IDENTIFIER);
				}
				break;
			case LBRACE:
				_localctx = new RecordTypeContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(300);
				match(LBRACE);
				setState(301);
				rowType();
				setState(302);
				match(RBRACE);
				}
				break;
			case LPAREN:
				_localctx = new ParenTypeContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(304);
				match(LPAREN);
				setState(305);
				type();
				setState(306);
				match(RPAREN);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RowTypeContext extends ParserRuleContext {
		public List<RowFieldContext> rowField() {
			return getRuleContexts(RowFieldContext.class);
		}
		public RowFieldContext rowField(int i) {
			return getRuleContext(RowFieldContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(PiescriptAntlrParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(PiescriptAntlrParser.COMMA, i);
		}
		public TerminalNode BAR() { return getToken(PiescriptAntlrParser.BAR, 0); }
		public TerminalNode IDENTIFIER() { return getToken(PiescriptAntlrParser.IDENTIFIER, 0); }
		public RowTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rowType; }
	}

	public final RowTypeContext rowType() throws RecognitionException {
		RowTypeContext _localctx = new RowTypeContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_rowType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(310);
			rowField();
			setState(315);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(311);
				match(COMMA);
				setState(312);
				rowField();
				}
				}
				setState(317);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(320);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==BAR) {
				{
				setState(318);
				match(BAR);
				setState(319);
				match(IDENTIFIER);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RowFieldContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(PiescriptAntlrParser.IDENTIFIER, 0); }
		public TerminalNode COLON() { return getToken(PiescriptAntlrParser.COLON, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public RowFieldContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rowField; }
	}

	public final RowFieldContext rowField() throws RecognitionException {
		RowFieldContext _localctx = new RowFieldContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_rowField);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(322);
			match(IDENTIFIER);
			setState(323);
			match(COLON);
			setState(324);
			type();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 4:
			return pipeExpr_sempred((PipeExprContext)_localctx, predIndex);
		case 5:
			return orExpr_sempred((OrExprContext)_localctx, predIndex);
		case 6:
			return andExpr_sempred((AndExprContext)_localctx, predIndex);
		case 9:
			return addExpr_sempred((AddExprContext)_localctx, predIndex);
		case 10:
			return mulExpr_sempred((MulExprContext)_localctx, predIndex);
		case 12:
			return appExpr_sempred((AppExprContext)_localctx, predIndex);
		case 13:
			return primary_sempred((PrimaryContext)_localctx, predIndex);
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

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\63\u0149\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\3\2\3\2\3"+
		"\3\7\3\64\n\3\f\3\16\3\67\13\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\5\4@\n\4\3"+
		"\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\5\5J\n\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\6"+
		"\5S\n\5\r\5\16\5T\3\5\3\5\3\5\3\5\5\5[\n\5\3\6\3\6\3\6\3\6\3\6\3\6\7\6"+
		"c\n\6\f\6\16\6f\13\6\3\7\3\7\3\7\3\7\3\7\3\7\7\7n\n\7\f\7\16\7q\13\7\3"+
		"\b\3\b\3\b\3\b\3\b\3\b\7\by\n\b\f\b\16\b|\13\b\3\t\3\t\3\t\3\t\3\t\5\t"+
		"\u0083\n\t\3\n\3\n\3\n\3\n\3\n\5\n\u008a\n\n\3\13\3\13\3\13\3\13\3\13"+
		"\3\13\7\13\u0092\n\13\f\13\16\13\u0095\13\13\3\f\3\f\3\f\3\f\3\f\3\f\7"+
		"\f\u009d\n\f\f\f\16\f\u00a0\13\f\3\r\3\r\3\r\5\r\u00a5\n\r\3\16\3\16\3"+
		"\16\3\16\3\16\7\16\u00ac\n\16\f\16\16\16\u00af\13\16\3\17\3\17\3\17\3"+
		"\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3"+
		"\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\7\17\u00cb\n\17\f\17\16\17"+
		"\u00ce\13\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\7\17\u00d8\n\17\f"+
		"\17\16\17\u00db\13\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\7\17\u00e5"+
		"\n\17\f\17\16\17\u00e8\13\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3"+
		"\17\3\17\5\17\u00f4\n\17\3\17\3\17\3\17\7\17\u00f9\n\17\f\17\16\17\u00fc"+
		"\13\17\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\22\3\22\6\22\u0108\n"+
		"\22\r\22\16\22\u0109\3\22\3\22\3\22\3\23\3\23\3\23\3\23\5\23\u0113\n\23"+
		"\3\23\3\23\3\23\3\23\3\23\3\23\3\23\5\23\u011c\n\23\3\24\3\24\3\24\3\24"+
		"\3\24\3\24\3\24\5\24\u0125\n\24\3\25\3\25\3\25\3\25\3\25\5\25\u012c\n"+
		"\25\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\5\26\u0137\n\26\3\27"+
		"\3\27\3\27\7\27\u013c\n\27\f\27\16\27\u013f\13\27\3\27\3\27\5\27\u0143"+
		"\n\27\3\30\3\30\3\30\3\30\3\30\2\t\n\f\16\24\26\32\34\31\2\4\6\b\n\f\16"+
		"\20\22\24\26\30\32\34\36 \"$&(*,.\2\b\3\2\62\63\3\2\25\26\3\2\27\32\3"+
		"\2\33\34\3\2\35\37\4\2\34\34  \2\u015c\2\60\3\2\2\2\4\65\3\2\2\2\6;\3"+
		"\2\2\2\bZ\3\2\2\2\n\\\3\2\2\2\fg\3\2\2\2\16r\3\2\2\2\20\u0082\3\2\2\2"+
		"\22\u0089\3\2\2\2\24\u008b\3\2\2\2\26\u0096\3\2\2\2\30\u00a4\3\2\2\2\32"+
		"\u00a6\3\2\2\2\34\u00f3\3\2\2\2\36\u00fd\3\2\2\2 \u0101\3\2\2\2\"\u0105"+
		"\3\2\2\2$\u011b\3\2\2\2&\u0124\3\2\2\2(\u012b\3\2\2\2*\u0136\3\2\2\2,"+
		"\u0138\3\2\2\2.\u0144\3\2\2\2\60\61\t\2\2\2\61\3\3\2\2\2\62\64\5\6\4\2"+
		"\63\62\3\2\2\2\64\67\3\2\2\2\65\63\3\2\2\2\65\66\3\2\2\2\668\3\2\2\2\67"+
		"\65\3\2\2\289\5\b\5\29:\7\2\2\3:\5\3\2\2\2;<\7\3\2\2<?\5\2\2\2=>\7#\2"+
		"\2>@\5(\25\2?=\3\2\2\2?@\3\2\2\2@A\3\2\2\2AB\7%\2\2BC\5\b\5\2CD\7$\2\2"+
		"D\7\3\2\2\2EF\7\3\2\2FI\5\2\2\2GH\7#\2\2HJ\5(\25\2IG\3\2\2\2IJ\3\2\2\2"+
		"JK\3\2\2\2KL\7%\2\2LM\5\b\5\2MN\7\4\2\2NO\5\b\5\2O[\3\2\2\2PR\7\5\2\2"+
		"QS\5&\24\2RQ\3\2\2\2ST\3\2\2\2TR\3\2\2\2TU\3\2\2\2UV\3\2\2\2VW\7\24\2"+
		"\2WX\5\b\5\2X[\3\2\2\2Y[\5\n\6\2ZE\3\2\2\2ZP\3\2\2\2ZY\3\2\2\2[\t\3\2"+
		"\2\2\\]\b\6\1\2]^\5\f\7\2^d\3\2\2\2_`\f\3\2\2`a\7\21\2\2ac\5\f\7\2b_\3"+
		"\2\2\2cf\3\2\2\2db\3\2\2\2de\3\2\2\2e\13\3\2\2\2fd\3\2\2\2gh\b\7\1\2h"+
		"i\5\16\b\2io\3\2\2\2jk\f\3\2\2kl\7\22\2\2ln\5\16\b\2mj\3\2\2\2nq\3\2\2"+
		"\2om\3\2\2\2op\3\2\2\2p\r\3\2\2\2qo\3\2\2\2rs\b\b\1\2st\5\20\t\2tz\3\2"+
		"\2\2uv\f\3\2\2vw\7\23\2\2wy\5\20\t\2xu\3\2\2\2y|\3\2\2\2zx\3\2\2\2z{\3"+
		"\2\2\2{\17\3\2\2\2|z\3\2\2\2}~\5\22\n\2~\177\t\3\2\2\177\u0080\5\22\n"+
		"\2\u0080\u0083\3\2\2\2\u0081\u0083\5\22\n\2\u0082}\3\2\2\2\u0082\u0081"+
		"\3\2\2\2\u0083\21\3\2\2\2\u0084\u0085\5\24\13\2\u0085\u0086\t\4\2\2\u0086"+
		"\u0087\5\24\13\2\u0087\u008a\3\2\2\2\u0088\u008a\5\24\13\2\u0089\u0084"+
		"\3\2\2\2\u0089\u0088\3\2\2\2\u008a\23\3\2\2\2\u008b\u008c\b\13\1\2\u008c"+
		"\u008d\5\26\f\2\u008d\u0093\3\2\2\2\u008e\u008f\f\3\2\2\u008f\u0090\t"+
		"\5\2\2\u0090\u0092\5\26\f\2\u0091\u008e\3\2\2\2\u0092\u0095\3\2\2\2\u0093"+
		"\u0091\3\2\2\2\u0093\u0094\3\2\2\2\u0094\25\3\2\2\2\u0095\u0093\3\2\2"+
		"\2\u0096\u0097\b\f\1\2\u0097\u0098\5\30\r\2\u0098\u009e\3\2\2\2\u0099"+
		"\u009a\f\3\2\2\u009a\u009b\t\6\2\2\u009b\u009d\5\30\r\2\u009c\u0099\3"+
		"\2\2\2\u009d\u00a0\3\2\2\2\u009e\u009c\3\2\2\2\u009e\u009f\3\2\2\2\u009f"+
		"\27\3\2\2\2\u00a0\u009e\3\2\2\2\u00a1\u00a2\t\7\2\2\u00a2\u00a5\5\30\r"+
		"\2\u00a3\u00a5\5\32\16\2\u00a4\u00a1\3\2\2\2\u00a4\u00a3\3\2\2\2\u00a5"+
		"\31\3\2\2\2\u00a6\u00a7\b\16\1\2\u00a7\u00a8\5\34\17\2\u00a8\u00ad\3\2"+
		"\2\2\u00a9\u00aa\f\3\2\2\u00aa\u00ac\5\34\17\2\u00ab\u00a9\3\2\2\2\u00ac"+
		"\u00af\3\2\2\2\u00ad\u00ab\3\2\2\2\u00ad\u00ae\3\2\2\2\u00ae\33\3\2\2"+
		"\2\u00af\u00ad\3\2\2\2\u00b0\u00b1\b\17\1\2\u00b1\u00b2\7!\2\2\u00b2\u00f4"+
		"\7.\2\2\u00b3\u00f4\7+\2\2\u00b4\u00f4\7,\2\2\u00b5\u00f4\7-\2\2\u00b6"+
		"\u00f4\7\t\2\2\u00b7\u00f4\7\n\2\2\u00b8\u00f4\7\13\2\2\u00b9\u00f4\7"+
		".\2\2\u00ba\u00bb\7\'\2\2\u00bb\u00bc\5\b\5\2\u00bc\u00bd\7#\2\2\u00bd"+
		"\u00be\5(\25\2\u00be\u00bf\7(\2\2\u00bf\u00f4\3\2\2\2\u00c0\u00c1\7\'"+
		"\2\2\u00c1\u00c2\5\b\5\2\u00c2\u00c3\7(\2\2\u00c3\u00f4\3\2\2\2\u00c4"+
		"\u00c5\7)\2\2\u00c5\u00f4\7*\2\2\u00c6\u00c7\7)\2\2\u00c7\u00cc\5\36\20"+
		"\2\u00c8\u00c9\7\"\2\2\u00c9\u00cb\5\36\20\2\u00ca\u00c8\3\2\2\2\u00cb"+
		"\u00ce\3\2\2\2\u00cc\u00ca\3\2\2\2\u00cc\u00cd\3\2\2\2\u00cd\u00cf\3\2"+
		"\2\2\u00ce\u00cc\3\2\2\2\u00cf\u00d0\7*\2\2\u00d0\u00f4\3\2\2\2\u00d1"+
		"\u00d2\7)\2\2\u00d2\u00d3\5\b\5\2\u00d3\u00d4\7&\2\2\u00d4\u00d9\5 \21"+
		"\2\u00d5\u00d6\7\"\2\2\u00d6\u00d8\5 \21\2\u00d7\u00d5\3\2\2\2\u00d8\u00db"+
		"\3\2\2\2\u00d9\u00d7\3\2\2\2\u00d9\u00da\3\2\2\2\u00da\u00dc\3\2\2\2\u00db"+
		"\u00d9\3\2\2\2\u00dc\u00dd\7*\2\2\u00dd\u00f4\3\2\2\2\u00de\u00df\7)\2"+
		"\2\u00df\u00e0\7\20\2\2\u00e0\u00e1\7&\2\2\u00e1\u00e6\5 \21\2\u00e2\u00e3"+
		"\7\"\2\2\u00e3\u00e5\5 \21\2\u00e4\u00e2\3\2\2\2\u00e5\u00e8\3\2\2\2\u00e6"+
		"\u00e4\3\2\2\2\u00e6\u00e7\3\2\2\2\u00e7\u00e9\3\2\2\2\u00e8\u00e6\3\2"+
		"\2\2\u00e9\u00ea\7*\2\2\u00ea\u00f4\3\2\2\2\u00eb\u00ec\7\6\2\2\u00ec"+
		"\u00ed\5\b\5\2\u00ed\u00ee\7\7\2\2\u00ee\u00ef\5\b\5\2\u00ef\u00f0\7\b"+
		"\2\2\u00f0\u00f1\5\b\5\2\u00f1\u00f4\3\2\2\2\u00f2\u00f4\5\"\22\2\u00f3"+
		"\u00b0\3\2\2\2\u00f3\u00b3\3\2\2\2\u00f3\u00b4\3\2\2\2\u00f3\u00b5\3\2"+
		"\2\2\u00f3\u00b6\3\2\2\2\u00f3\u00b7\3\2\2\2\u00f3\u00b8\3\2\2\2\u00f3"+
		"\u00b9\3\2\2\2\u00f3\u00ba\3\2\2\2\u00f3\u00c0\3\2\2\2\u00f3\u00c4\3\2"+
		"\2\2\u00f3\u00c6\3\2\2\2\u00f3\u00d1\3\2\2\2\u00f3\u00de\3\2\2\2\u00f3"+
		"\u00eb\3\2\2\2\u00f3\u00f2\3\2\2\2\u00f4\u00fa\3\2\2\2\u00f5\u00f6\f\3"+
		"\2\2\u00f6\u00f7\7!\2\2\u00f7\u00f9\7.\2\2\u00f8\u00f5\3\2\2\2\u00f9\u00fc"+
		"\3\2\2\2\u00fa\u00f8\3\2\2\2\u00fa\u00fb\3\2\2\2\u00fb\35\3\2\2\2\u00fc"+
		"\u00fa\3\2\2\2\u00fd\u00fe\7.\2\2\u00fe\u00ff\7#\2\2\u00ff\u0100\5\b\5"+
		"\2\u0100\37\3\2\2\2\u0101\u0102\7.\2\2\u0102\u0103\7%\2\2\u0103\u0104"+
		"\5\b\5\2\u0104!\3\2\2\2\u0105\u0107\7)\2\2\u0106\u0108\5$\23\2\u0107\u0106"+
		"\3\2\2\2\u0108\u0109\3\2\2\2\u0109\u0107\3\2\2\2\u0109\u010a\3\2\2\2\u010a"+
		"\u010b\3\2\2\2\u010b\u010c\5\b\5\2\u010c\u010d\7*\2\2\u010d#\3\2\2\2\u010e"+
		"\u010f\7\3\2\2\u010f\u0112\7.\2\2\u0110\u0111\7#\2\2\u0111\u0113\5(\25"+
		"\2\u0112\u0110\3\2\2\2\u0112\u0113\3\2\2\2\u0113\u0114\3\2\2\2\u0114\u0115"+
		"\7%\2\2\u0115\u0116\5\b\5\2\u0116\u0117\7$\2\2\u0117\u011c\3\2\2\2\u0118"+
		"\u0119\5\b\5\2\u0119\u011a\7$\2\2\u011a\u011c\3\2\2\2\u011b\u010e\3\2"+
		"\2\2\u011b\u0118\3\2\2\2\u011c%\3\2\2\2\u011d\u0125\7.\2\2\u011e\u011f"+
		"\7\'\2\2\u011f\u0120\7.\2\2\u0120\u0121\7#\2\2\u0121\u0122\5(\25\2\u0122"+
		"\u0123\7(\2\2\u0123\u0125\3\2\2\2\u0124\u011d\3\2\2\2\u0124\u011e\3\2"+
		"\2\2\u0125\'\3\2\2\2\u0126\u0127\5*\26\2\u0127\u0128\7\24\2\2\u0128\u0129"+
		"\5(\25\2\u0129\u012c\3\2\2\2\u012a\u012c\5*\26\2\u012b\u0126\3\2\2\2\u012b"+
		"\u012a\3\2\2\2\u012c)\3\2\2\2\u012d\u0137\7.\2\2\u012e\u012f\7)\2\2\u012f"+
		"\u0130\5,\27\2\u0130\u0131\7*\2\2\u0131\u0137\3\2\2\2\u0132\u0133\7\'"+
		"\2\2\u0133\u0134\5(\25\2\u0134\u0135\7(\2\2\u0135\u0137\3\2\2\2\u0136"+
		"\u012d\3\2\2\2\u0136\u012e\3\2\2\2\u0136\u0132\3\2\2\2\u0137+\3\2\2\2"+
		"\u0138\u013d\5.\30\2\u0139\u013a\7\"\2\2\u013a\u013c\5.\30\2\u013b\u0139"+
		"\3\2\2\2\u013c\u013f\3\2\2\2\u013d\u013b\3\2\2\2\u013d\u013e\3\2\2\2\u013e"+
		"\u0142\3\2\2\2\u013f\u013d\3\2\2\2\u0140\u0141\7&\2\2\u0141\u0143\7.\2"+
		"\2\u0142\u0140\3\2\2\2\u0142\u0143\3\2\2\2\u0143-\3\2\2\2\u0144\u0145"+
		"\7.\2\2\u0145\u0146\7#\2\2\u0146\u0147\5(\25\2\u0147/\3\2\2\2\35\65?I"+
		"TZdoz\u0082\u0089\u0093\u009e\u00a4\u00ad\u00cc\u00d9\u00e6\u00f3\u00fa"+
		"\u0109\u0112\u011b\u0124\u012b\u0136\u013d\u0142";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}