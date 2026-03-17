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
		LINE_COMMENT=45, MULTILINE_COMMENT=46, WS=47, UPPER_IDENT=48, LOWER_IDENT=49, 
		SPAWN_BANG=50, SPAWN=51, SEND=52, WHEN=53, AMP=54, ESQL_BODY=55;
	public static final int
		RULE_ident = 0, RULE_program = 1, RULE_topBinding = 2, RULE_expr = 3, 
		RULE_whenBinding = 4, RULE_pipeExpr = 5, RULE_orExpr = 6, RULE_andExpr = 7, 
		RULE_eqExpr = 8, RULE_cmpExpr = 9, RULE_addExpr = 10, RULE_mulExpr = 11, 
		RULE_unaryExpr = 12, RULE_appExpr = 13, RULE_primary = 14, RULE_recordField = 15, 
		RULE_recordUpdate = 16, RULE_block = 17, RULE_blockStmt = 18, RULE_param = 19, 
		RULE_type = 20, RULE_typeApp = 21, RULE_typeAtom = 22, RULE_rowType = 23, 
		RULE_rowField = 24;
	private static String[] makeRuleNames() {
		return new String[] {
			"ident", "program", "topBinding", "expr", "whenBinding", "pipeExpr", 
			"orExpr", "andExpr", "eqExpr", "cmpExpr", "addExpr", "mulExpr", "unaryExpr", 
			"appExpr", "primary", "recordField", "recordUpdate", "block", "blockStmt", 
			"param", "type", "typeApp", "typeAtom", "rowType", "rowField"
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
			"UPPER_IDENT", "LOWER_IDENT", "SPAWN_BANG", "SPAWN", "SEND", "WHEN", 
			"AMP", "ESQL_BODY"
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
			setState(50);
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
		public TerminalNode SEMICOLON() { return getToken(PiescriptAntlrParser.SEMICOLON, 0); }
		public ProgramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_program; }
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
			_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(52);
					topBinding();
					}
					} 
				}
				setState(57);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			}
			setState(58);
			expr();
			setState(60);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SEMICOLON) {
				{
				setState(59);
				match(SEMICOLON);
				}
			}

			setState(62);
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
			setState(64);
			match(LET);
			setState(65);
			ident();
			setState(68);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COLON) {
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
	public static class SpawnExprContext extends ExprContext {
		public TerminalNode SPAWN() { return getToken(PiescriptAntlrParser.SPAWN, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public SpawnExprContext(ExprContext ctx) { copyFrom(ctx); }
	}
	public static class SendExprContext extends ExprContext {
		public TerminalNode SEND() { return getToken(PiescriptAntlrParser.SEND, 0); }
		public PrimaryContext primary() {
			return getRuleContext(PrimaryContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public SendExprContext(ExprContext ctx) { copyFrom(ctx); }
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
	public static class WhenExprContext extends ExprContext {
		public TerminalNode WHEN() { return getToken(PiescriptAntlrParser.WHEN, 0); }
		public List<WhenBindingContext> whenBinding() {
			return getRuleContexts(WhenBindingContext.class);
		}
		public WhenBindingContext whenBinding(int i) {
			return getRuleContext(WhenBindingContext.class,i);
		}
		public TerminalNode ARROW() { return getToken(PiescriptAntlrParser.ARROW, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public List<TerminalNode> AMP() { return getTokens(PiescriptAntlrParser.AMP); }
		public TerminalNode AMP(int i) {
			return getToken(PiescriptAntlrParser.AMP, i);
		}
		public WhenExprContext(ExprContext ctx) { copyFrom(ctx); }
	}
	public static class SpawnBangExprContext extends ExprContext {
		public TerminalNode SPAWN_BANG() { return getToken(PiescriptAntlrParser.SPAWN_BANG, 0); }
		public SpawnBangExprContext(ExprContext ctx) { copyFrom(ctx); }
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
			setState(114);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LET:
				_localctx = new LetExprContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(74);
				match(LET);
				setState(75);
				ident();
				setState(78);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COLON) {
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
				enterOuterAlt(_localctx, 2);
				{
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
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LPAREN) | (1L << UPPER_IDENT) | (1L << LOWER_IDENT))) != 0) );
				setState(91);
				match(ARROW);
				setState(92);
				expr();
				}
				break;
			case SPAWN_BANG:
				_localctx = new SpawnBangExprContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(94);
				match(SPAWN_BANG);
				}
				break;
			case SPAWN:
				_localctx = new SpawnExprContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(95);
				match(SPAWN);
				setState(96);
				expr();
				}
				break;
			case SEND:
				_localctx = new SendExprContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
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
				enterOuterAlt(_localctx, 6);
				{
				setState(101);
				match(WHEN);
				setState(102);
				whenBinding();
				setState(107);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==AMP) {
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
				enterOuterAlt(_localctx, 7);
				{
				setState(113);
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

	public static class WhenBindingContext extends ParserRuleContext {
		public TerminalNode LPAREN() { return getToken(PiescriptAntlrParser.LPAREN, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public IdentContext ident() {
			return getRuleContext(IdentContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(PiescriptAntlrParser.RPAREN, 0); }
		public WhenBindingContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_whenBinding; }
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
			_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
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
			_alt = getInterpreter().adaptivePredict(_input,8,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
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
				_alt = getInterpreter().adaptivePredict(_input,8,_ctx);
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
			_alt = getInterpreter().adaptivePredict(_input,9,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
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
				_alt = getInterpreter().adaptivePredict(_input,9,_ctx);
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
		enterRule(_localctx, 16, RULE_eqExpr);
		int _la;
		try {
			setState(159);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
			case 1:
				_localctx = new EqualityOpContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(154);
				cmpExpr();
				setState(155);
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
				setState(156);
				cmpExpr();
				}
				break;
			case 2:
				_localctx = new EqPassthroughContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(158);
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
		enterRule(_localctx, 18, RULE_cmpExpr);
		int _la;
		try {
			setState(166);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
			case 1:
				_localctx = new ComparisonOpContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(161);
				addExpr(0);
				setState(162);
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
				setState(163);
				addExpr(0);
				}
				break;
			case 2:
				_localctx = new CmpPassthroughContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(165);
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
			_alt = getInterpreter().adaptivePredict(_input,12,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new AdditiveOpContext(new AddExprContext(_parentctx, _parentState));
					pushNewRecursionContext(_localctx, _startState, RULE_addExpr);
					setState(171);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(172);
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
					setState(173);
					mulExpr(0);
					}
					} 
				}
				setState(178);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,12,_ctx);
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
			_alt = getInterpreter().adaptivePredict(_input,13,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new MultiplicativeOpContext(new MulExprContext(_parentctx, _parentState));
					pushNewRecursionContext(_localctx, _startState, RULE_mulExpr);
					setState(182);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(183);
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
					setState(184);
					unaryExpr();
					}
					} 
				}
				setState(189);
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
		enterRule(_localctx, 24, RULE_unaryExpr);
		int _la;
		try {
			setState(193);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case MINUS:
			case BANG:
				_localctx = new UnaryOpContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(190);
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
				enterOuterAlt(_localctx, 2);
				{
				setState(192);
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
			_alt = getInterpreter().adaptivePredict(_input,15,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
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
				_alt = getInterpreter().adaptivePredict(_input,15,_ctx);
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
		public IdentContext ident() {
			return getRuleContext(IdentContext.class,0);
		}
		public VariableContext(PrimaryContext ctx) { copyFrom(ctx); }
	}
	public static class QueryExprContext extends PrimaryContext {
		public TerminalNode QUERY() { return getToken(PiescriptAntlrParser.QUERY, 0); }
		public TerminalNode ESQL_BODY() { return getToken(PiescriptAntlrParser.ESQL_BODY, 0); }
		public QueryExprContext(PrimaryContext ctx) { copyFrom(ctx); }
	}
	public static class ProjectionContext extends PrimaryContext {
		public PrimaryContext primary() {
			return getRuleContext(PrimaryContext.class,0);
		}
		public TerminalNode DOT() { return getToken(PiescriptAntlrParser.DOT, 0); }
		public IdentContext ident() {
			return getRuleContext(IdentContext.class,0);
		}
		public ProjectionContext(PrimaryContext ctx) { copyFrom(ctx); }
	}
	public static class AccessorContext extends PrimaryContext {
		public TerminalNode DOT() { return getToken(PiescriptAntlrParser.DOT, 0); }
		public IdentContext ident() {
			return getRuleContext(IdentContext.class,0);
		}
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
		int _startState = 28;
		enterRecursionRule(_localctx, 28, RULE_primary, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(274);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
			case 1:
				{
				_localctx = new AccessorContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(206);
				match(DOT);
				setState(207);
				ident();
				}
				break;
			case 2:
				{
				_localctx = new IntegerLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(208);
				match(INTEGER_LITERAL);
				}
				break;
			case 3:
				{
				_localctx = new DecimalLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(209);
				match(DECIMAL_LITERAL);
				}
				break;
			case 4:
				{
				_localctx = new StringLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(210);
				match(QUOTED_STRING);
				}
				break;
			case 5:
				{
				_localctx = new TrueLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(211);
				match(TRUE);
				}
				break;
			case 6:
				{
				_localctx = new FalseLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(212);
				match(FALSE);
				}
				break;
			case 7:
				{
				_localctx = new NullLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(213);
				match(NULL);
				}
				break;
			case 8:
				{
				_localctx = new VariableContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(214);
				ident();
				}
				break;
			case 9:
				{
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
			case 10:
				{
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
			case 11:
				{
				_localctx = new EmptyRecordContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(225);
				match(LBRACE);
				setState(226);
				match(RBRACE);
				}
				break;
			case 12:
				{
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
				while (_la==COMMA) {
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
			case 13:
				{
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
				while (_la==COMMA) {
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
			case 14:
				{
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
				while (_la==COMMA) {
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
			case 15:
				{
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
			case 16:
				{
				_localctx = new QueryExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(271);
				match(QUERY);
				setState(272);
				match(ESQL_BODY);
				}
				break;
			case 17:
				{
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
			_alt = getInterpreter().adaptivePredict(_input,20,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
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
				_alt = getInterpreter().adaptivePredict(_input,20,_ctx);
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
		public IdentContext ident() {
			return getRuleContext(IdentContext.class,0);
		}
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
		public IdentContext ident() {
			return getRuleContext(IdentContext.class,0);
		}
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
				case 1:
					{
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
				_alt = getInterpreter().adaptivePredict(_input,21,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(298);
			expr();
			setState(299);
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
		enterRule(_localctx, 36, RULE_blockStmt);
		int _la;
		try {
			setState(314);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,23,_ctx) ) {
			case 1:
				_localctx = new BlockLetContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(301);
				match(LET);
				setState(302);
				ident();
				setState(305);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COLON) {
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
				enterOuterAlt(_localctx, 2);
				{
				setState(311);
				expr();
				setState(312);
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
		public IdentContext ident() {
			return getRuleContext(IdentContext.class,0);
		}
		public TerminalNode COLON() { return getToken(PiescriptAntlrParser.COLON, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(PiescriptAntlrParser.RPAREN, 0); }
		public TypedParamContext(ParamContext ctx) { copyFrom(ctx); }
	}
	public static class UntypedParamContext extends ParamContext {
		public IdentContext ident() {
			return getRuleContext(IdentContext.class,0);
		}
		public UntypedParamContext(ParamContext ctx) { copyFrom(ctx); }
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
				enterOuterAlt(_localctx, 1);
				{
				setState(316);
				ident();
				}
				break;
			case LPAREN:
				_localctx = new TypedParamContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
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
		public TypeAppContext typeApp() {
			return getRuleContext(TypeAppContext.class,0);
		}
		public TerminalNode ARROW() { return getToken(PiescriptAntlrParser.ARROW, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public FunctionTypeContext(TypeContext ctx) { copyFrom(ctx); }
	}
	public static class TypeNonArrowContext extends TypeContext {
		public TypeAppContext typeApp() {
			return getRuleContext(TypeAppContext.class,0);
		}
		public TypeNonArrowContext(TypeContext ctx) { copyFrom(ctx); }
	}

	public final TypeContext type() throws RecognitionException {
		TypeContext _localctx = new TypeContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_type);
		try {
			setState(330);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,25,_ctx) ) {
			case 1:
				_localctx = new FunctionTypeContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
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
				enterOuterAlt(_localctx, 2);
				{
				setState(329);
				typeApp(0);
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

	public static class TypeAppContext extends ParserRuleContext {
		public TypeAppContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeApp; }
	 
		public TypeAppContext() { }
		public void copyFrom(TypeAppContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class TypeAppPassthroughContext extends TypeAppContext {
		public TypeAtomContext typeAtom() {
			return getRuleContext(TypeAtomContext.class,0);
		}
		public TypeAppPassthroughContext(TypeAppContext ctx) { copyFrom(ctx); }
	}
	public static class TypeApplicationContext extends TypeAppContext {
		public TypeAppContext typeApp() {
			return getRuleContext(TypeAppContext.class,0);
		}
		public TypeAtomContext typeAtom() {
			return getRuleContext(TypeAtomContext.class,0);
		}
		public TypeApplicationContext(TypeAppContext ctx) { copyFrom(ctx); }
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
			_alt = getInterpreter().adaptivePredict(_input,26,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
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
				_alt = getInterpreter().adaptivePredict(_input,26,_ctx);
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

	public static class TypeAtomContext extends ParserRuleContext {
		public TypeAtomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeAtom; }
	 
		public TypeAtomContext() { }
		public void copyFrom(TypeAtomContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class TypeVarContext extends TypeAtomContext {
		public TerminalNode LOWER_IDENT() { return getToken(PiescriptAntlrParser.LOWER_IDENT, 0); }
		public TypeVarContext(TypeAtomContext ctx) { copyFrom(ctx); }
	}
	public static class RecordTypeContext extends TypeAtomContext {
		public TerminalNode LBRACE() { return getToken(PiescriptAntlrParser.LBRACE, 0); }
		public RowTypeContext rowType() {
			return getRuleContext(RowTypeContext.class,0);
		}
		public TerminalNode RBRACE() { return getToken(PiescriptAntlrParser.RBRACE, 0); }
		public RecordTypeContext(TypeAtomContext ctx) { copyFrom(ctx); }
	}
	public static class TypeConContext extends TypeAtomContext {
		public TerminalNode UPPER_IDENT() { return getToken(PiescriptAntlrParser.UPPER_IDENT, 0); }
		public TypeConContext(TypeAtomContext ctx) { copyFrom(ctx); }
	}
	public static class ParenTypeContext extends TypeAtomContext {
		public TerminalNode LPAREN() { return getToken(PiescriptAntlrParser.LPAREN, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(PiescriptAntlrParser.RPAREN, 0); }
		public ParenTypeContext(TypeAtomContext ctx) { copyFrom(ctx); }
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
				enterOuterAlt(_localctx, 1);
				{
				setState(342);
				match(UPPER_IDENT);
				}
				break;
			case LOWER_IDENT:
				_localctx = new TypeVarContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(343);
				match(LOWER_IDENT);
				}
				break;
			case LBRACE:
				_localctx = new RecordTypeContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
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
				enterOuterAlt(_localctx, 4);
				{
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
		public TerminalNode LOWER_IDENT() { return getToken(PiescriptAntlrParser.LOWER_IDENT, 0); }
		public RowTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rowType; }
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
			while (_la==COMMA) {
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
			if (_la==BAR) {
				{
				setState(362);
				match(BAR);
				setState(363);
				match(LOWER_IDENT);
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
		public IdentContext ident() {
			return getRuleContext(IdentContext.class,0);
		}
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
		case 5:
			return pipeExpr_sempred((PipeExprContext)_localctx, predIndex);
		case 6:
			return orExpr_sempred((OrExprContext)_localctx, predIndex);
		case 7:
			return andExpr_sempred((AndExprContext)_localctx, predIndex);
		case 10:
			return addExpr_sempred((AddExprContext)_localctx, predIndex);
		case 11:
			return mulExpr_sempred((MulExprContext)_localctx, predIndex);
		case 13:
			return appExpr_sempred((AppExprContext)_localctx, predIndex);
		case 14:
			return primary_sempred((PrimaryContext)_localctx, predIndex);
		case 21:
			return typeApp_sempred((TypeAppContext)_localctx, predIndex);
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

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\39\u0175\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\3\2\3\2\3\3\7\38\n\3\f\3\16\3;\13\3\3\3\3\3\5\3?\n\3\3\3\3"+
		"\3\3\4\3\4\3\4\3\4\5\4G\n\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\5\5Q\n\5\3"+
		"\5\3\5\3\5\3\5\3\5\3\5\3\5\6\5Z\n\5\r\5\16\5[\3\5\3\5\3\5\3\5\3\5\3\5"+
		"\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\7\5l\n\5\f\5\16\5o\13\5\3\5\3\5\3\5\3"+
		"\5\5\5u\n\5\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\7\7\u0082\n\7"+
		"\f\7\16\7\u0085\13\7\3\b\3\b\3\b\3\b\3\b\3\b\7\b\u008d\n\b\f\b\16\b\u0090"+
		"\13\b\3\t\3\t\3\t\3\t\3\t\3\t\7\t\u0098\n\t\f\t\16\t\u009b\13\t\3\n\3"+
		"\n\3\n\3\n\3\n\5\n\u00a2\n\n\3\13\3\13\3\13\3\13\3\13\5\13\u00a9\n\13"+
		"\3\f\3\f\3\f\3\f\3\f\3\f\7\f\u00b1\n\f\f\f\16\f\u00b4\13\f\3\r\3\r\3\r"+
		"\3\r\3\r\3\r\7\r\u00bc\n\r\f\r\16\r\u00bf\13\r\3\16\3\16\3\16\5\16\u00c4"+
		"\n\16\3\17\3\17\3\17\3\17\3\17\7\17\u00cb\n\17\f\17\16\17\u00ce\13\17"+
		"\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20"+
		"\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\7\20\u00ea"+
		"\n\20\f\20\16\20\u00ed\13\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\7"+
		"\20\u00f7\n\20\f\20\16\20\u00fa\13\20\3\20\3\20\3\20\3\20\3\20\3\20\3"+
		"\20\3\20\7\20\u0104\n\20\f\20\16\20\u0107\13\20\3\20\3\20\3\20\3\20\3"+
		"\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\5\20\u0115\n\20\3\20\3\20\3\20"+
		"\7\20\u011a\n\20\f\20\16\20\u011d\13\20\3\21\3\21\3\21\3\21\3\22\3\22"+
		"\3\22\3\22\3\23\3\23\6\23\u0129\n\23\r\23\16\23\u012a\3\23\3\23\3\23\3"+
		"\24\3\24\3\24\3\24\5\24\u0134\n\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24"+
		"\5\24\u013d\n\24\3\25\3\25\3\25\3\25\3\25\3\25\3\25\5\25\u0146\n\25\3"+
		"\26\3\26\3\26\3\26\3\26\5\26\u014d\n\26\3\27\3\27\3\27\3\27\3\27\7\27"+
		"\u0154\n\27\f\27\16\27\u0157\13\27\3\30\3\30\3\30\3\30\3\30\3\30\3\30"+
		"\3\30\3\30\3\30\5\30\u0163\n\30\3\31\3\31\3\31\7\31\u0168\n\31\f\31\16"+
		"\31\u016b\13\31\3\31\3\31\5\31\u016f\n\31\3\32\3\32\3\32\3\32\3\32\2\n"+
		"\f\16\20\26\30\34\36,\33\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&("+
		"*,.\60\62\2\b\3\2\62\63\3\2\25\26\3\2\27\32\3\2\33\34\3\2\35\37\4\2\34"+
		"\34  \2\u018f\2\64\3\2\2\2\49\3\2\2\2\6B\3\2\2\2\bt\3\2\2\2\nv\3\2\2\2"+
		"\f{\3\2\2\2\16\u0086\3\2\2\2\20\u0091\3\2\2\2\22\u00a1\3\2\2\2\24\u00a8"+
		"\3\2\2\2\26\u00aa\3\2\2\2\30\u00b5\3\2\2\2\32\u00c3\3\2\2\2\34\u00c5\3"+
		"\2\2\2\36\u0114\3\2\2\2 \u011e\3\2\2\2\"\u0122\3\2\2\2$\u0126\3\2\2\2"+
		"&\u013c\3\2\2\2(\u0145\3\2\2\2*\u014c\3\2\2\2,\u014e\3\2\2\2.\u0162\3"+
		"\2\2\2\60\u0164\3\2\2\2\62\u0170\3\2\2\2\64\65\t\2\2\2\65\3\3\2\2\2\66"+
		"8\5\6\4\2\67\66\3\2\2\28;\3\2\2\29\67\3\2\2\29:\3\2\2\2:<\3\2\2\2;9\3"+
		"\2\2\2<>\5\b\5\2=?\7$\2\2>=\3\2\2\2>?\3\2\2\2?@\3\2\2\2@A\7\2\2\3A\5\3"+
		"\2\2\2BC\7\3\2\2CF\5\2\2\2DE\7#\2\2EG\5*\26\2FD\3\2\2\2FG\3\2\2\2GH\3"+
		"\2\2\2HI\7%\2\2IJ\5\b\5\2JK\7$\2\2K\7\3\2\2\2LM\7\3\2\2MP\5\2\2\2NO\7"+
		"#\2\2OQ\5*\26\2PN\3\2\2\2PQ\3\2\2\2QR\3\2\2\2RS\7%\2\2ST\5\b\5\2TU\7\4"+
		"\2\2UV\5\b\5\2Vu\3\2\2\2WY\7\5\2\2XZ\5(\25\2YX\3\2\2\2Z[\3\2\2\2[Y\3\2"+
		"\2\2[\\\3\2\2\2\\]\3\2\2\2]^\7\24\2\2^_\5\b\5\2_u\3\2\2\2`u\7\64\2\2a"+
		"b\7\65\2\2bu\5\b\5\2cd\7\66\2\2de\5\36\20\2ef\5\b\5\2fu\3\2\2\2gh\7\67"+
		"\2\2hm\5\n\6\2ij\78\2\2jl\5\n\6\2ki\3\2\2\2lo\3\2\2\2mk\3\2\2\2mn\3\2"+
		"\2\2np\3\2\2\2om\3\2\2\2pq\7\24\2\2qr\5\b\5\2ru\3\2\2\2su\5\f\7\2tL\3"+
		"\2\2\2tW\3\2\2\2t`\3\2\2\2ta\3\2\2\2tc\3\2\2\2tg\3\2\2\2ts\3\2\2\2u\t"+
		"\3\2\2\2vw\7\'\2\2wx\5\b\5\2xy\5\2\2\2yz\7(\2\2z\13\3\2\2\2{|\b\7\1\2"+
		"|}\5\16\b\2}\u0083\3\2\2\2~\177\f\3\2\2\177\u0080\7\21\2\2\u0080\u0082"+
		"\5\16\b\2\u0081~\3\2\2\2\u0082\u0085\3\2\2\2\u0083\u0081\3\2\2\2\u0083"+
		"\u0084\3\2\2\2\u0084\r\3\2\2\2\u0085\u0083\3\2\2\2\u0086\u0087\b\b\1\2"+
		"\u0087\u0088\5\20\t\2\u0088\u008e\3\2\2\2\u0089\u008a\f\3\2\2\u008a\u008b"+
		"\7\22\2\2\u008b\u008d\5\20\t\2\u008c\u0089\3\2\2\2\u008d\u0090\3\2\2\2"+
		"\u008e\u008c\3\2\2\2\u008e\u008f\3\2\2\2\u008f\17\3\2\2\2\u0090\u008e"+
		"\3\2\2\2\u0091\u0092\b\t\1\2\u0092\u0093\5\22\n\2\u0093\u0099\3\2\2\2"+
		"\u0094\u0095\f\3\2\2\u0095\u0096\7\23\2\2\u0096\u0098\5\22\n\2\u0097\u0094"+
		"\3\2\2\2\u0098\u009b\3\2\2\2\u0099\u0097\3\2\2\2\u0099\u009a\3\2\2\2\u009a"+
		"\21\3\2\2\2\u009b\u0099\3\2\2\2\u009c\u009d\5\24\13\2\u009d\u009e\t\3"+
		"\2\2\u009e\u009f\5\24\13\2\u009f\u00a2\3\2\2\2\u00a0\u00a2\5\24\13\2\u00a1"+
		"\u009c\3\2\2\2\u00a1\u00a0\3\2\2\2\u00a2\23\3\2\2\2\u00a3\u00a4\5\26\f"+
		"\2\u00a4\u00a5\t\4\2\2\u00a5\u00a6\5\26\f\2\u00a6\u00a9\3\2\2\2\u00a7"+
		"\u00a9\5\26\f\2\u00a8\u00a3\3\2\2\2\u00a8\u00a7\3\2\2\2\u00a9\25\3\2\2"+
		"\2\u00aa\u00ab\b\f\1\2\u00ab\u00ac\5\30\r\2\u00ac\u00b2\3\2\2\2\u00ad"+
		"\u00ae\f\3\2\2\u00ae\u00af\t\5\2\2\u00af\u00b1\5\30\r\2\u00b0\u00ad\3"+
		"\2\2\2\u00b1\u00b4\3\2\2\2\u00b2\u00b0\3\2\2\2\u00b2\u00b3\3\2\2\2\u00b3"+
		"\27\3\2\2\2\u00b4\u00b2\3\2\2\2\u00b5\u00b6\b\r\1\2\u00b6\u00b7\5\32\16"+
		"\2\u00b7\u00bd\3\2\2\2\u00b8\u00b9\f\3\2\2\u00b9\u00ba\t\6\2\2\u00ba\u00bc"+
		"\5\32\16\2\u00bb\u00b8\3\2\2\2\u00bc\u00bf\3\2\2\2\u00bd\u00bb\3\2\2\2"+
		"\u00bd\u00be\3\2\2\2\u00be\31\3\2\2\2\u00bf\u00bd\3\2\2\2\u00c0\u00c1"+
		"\t\7\2\2\u00c1\u00c4\5\32\16\2\u00c2\u00c4\5\34\17\2\u00c3\u00c0\3\2\2"+
		"\2\u00c3\u00c2\3\2\2\2\u00c4\33\3\2\2\2\u00c5\u00c6\b\17\1\2\u00c6\u00c7"+
		"\5\36\20\2\u00c7\u00cc\3\2\2\2\u00c8\u00c9\f\3\2\2\u00c9\u00cb\5\36\20"+
		"\2\u00ca\u00c8\3\2\2\2\u00cb\u00ce\3\2\2\2\u00cc\u00ca\3\2\2\2\u00cc\u00cd"+
		"\3\2\2\2\u00cd\35\3\2\2\2\u00ce\u00cc\3\2\2\2\u00cf\u00d0\b\20\1\2\u00d0"+
		"\u00d1\7!\2\2\u00d1\u0115\5\2\2\2\u00d2\u0115\7+\2\2\u00d3\u0115\7,\2"+
		"\2\u00d4\u0115\7-\2\2\u00d5\u0115\7\t\2\2\u00d6\u0115\7\n\2\2\u00d7\u0115"+
		"\7\13\2\2\u00d8\u0115\5\2\2\2\u00d9\u00da\7\'\2\2\u00da\u00db\5\b\5\2"+
		"\u00db\u00dc\7#\2\2\u00dc\u00dd\5*\26\2\u00dd\u00de\7(\2\2\u00de\u0115"+
		"\3\2\2\2\u00df\u00e0\7\'\2\2\u00e0\u00e1\5\b\5\2\u00e1\u00e2\7(\2\2\u00e2"+
		"\u0115\3\2\2\2\u00e3\u00e4\7)\2\2\u00e4\u0115\7*\2\2\u00e5\u00e6\7)\2"+
		"\2\u00e6\u00eb\5 \21\2\u00e7\u00e8\7\"\2\2\u00e8\u00ea\5 \21\2\u00e9\u00e7"+
		"\3\2\2\2\u00ea\u00ed\3\2\2\2\u00eb\u00e9\3\2\2\2\u00eb\u00ec\3\2\2\2\u00ec"+
		"\u00ee\3\2\2\2\u00ed\u00eb\3\2\2\2\u00ee\u00ef\7*\2\2\u00ef\u0115\3\2"+
		"\2\2\u00f0\u00f1\7)\2\2\u00f1\u00f2\5\b\5\2\u00f2\u00f3\7&\2\2\u00f3\u00f8"+
		"\5\"\22\2\u00f4\u00f5\7\"\2\2\u00f5\u00f7\5\"\22\2\u00f6\u00f4\3\2\2\2"+
		"\u00f7\u00fa\3\2\2\2\u00f8\u00f6\3\2\2\2\u00f8\u00f9\3\2\2\2\u00f9\u00fb"+
		"\3\2\2\2\u00fa\u00f8\3\2\2\2\u00fb\u00fc\7*\2\2\u00fc\u0115\3\2\2\2\u00fd"+
		"\u00fe\7)\2\2\u00fe\u00ff\7\20\2\2\u00ff\u0100\7&\2\2\u0100\u0105\5\""+
		"\22\2\u0101\u0102\7\"\2\2\u0102\u0104\5\"\22\2\u0103\u0101\3\2\2\2\u0104"+
		"\u0107\3\2\2\2\u0105\u0103\3\2\2\2\u0105\u0106\3\2\2\2\u0106\u0108\3\2"+
		"\2\2\u0107\u0105\3\2\2\2\u0108\u0109\7*\2\2\u0109\u0115\3\2\2\2\u010a"+
		"\u010b\7\6\2\2\u010b\u010c\5\b\5\2\u010c\u010d\7\7\2\2\u010d\u010e\5\b"+
		"\5\2\u010e\u010f\7\b\2\2\u010f\u0110\5\b\5\2\u0110\u0115\3\2\2\2\u0111"+
		"\u0112\7\r\2\2\u0112\u0115\79\2\2\u0113\u0115\5$\23\2\u0114\u00cf\3\2"+
		"\2\2\u0114\u00d2\3\2\2\2\u0114\u00d3\3\2\2\2\u0114\u00d4\3\2\2\2\u0114"+
		"\u00d5\3\2\2\2\u0114\u00d6\3\2\2\2\u0114\u00d7\3\2\2\2\u0114\u00d8\3\2"+
		"\2\2\u0114\u00d9\3\2\2\2\u0114\u00df\3\2\2\2\u0114\u00e3\3\2\2\2\u0114"+
		"\u00e5\3\2\2\2\u0114\u00f0\3\2\2\2\u0114\u00fd\3\2\2\2\u0114\u010a\3\2"+
		"\2\2\u0114\u0111\3\2\2\2\u0114\u0113\3\2\2\2\u0115\u011b\3\2\2\2\u0116"+
		"\u0117\f\3\2\2\u0117\u0118\7!\2\2\u0118\u011a\5\2\2\2\u0119\u0116\3\2"+
		"\2\2\u011a\u011d\3\2\2\2\u011b\u0119\3\2\2\2\u011b\u011c\3\2\2\2\u011c"+
		"\37\3\2\2\2\u011d\u011b\3\2\2\2\u011e\u011f\5\2\2\2\u011f\u0120\7#\2\2"+
		"\u0120\u0121\5\b\5\2\u0121!\3\2\2\2\u0122\u0123\5\2\2\2\u0123\u0124\7"+
		"%\2\2\u0124\u0125\5\b\5\2\u0125#\3\2\2\2\u0126\u0128\7)\2\2\u0127\u0129"+
		"\5&\24\2\u0128\u0127\3\2\2\2\u0129\u012a\3\2\2\2\u012a\u0128\3\2\2\2\u012a"+
		"\u012b\3\2\2\2\u012b\u012c\3\2\2\2\u012c\u012d\5\b\5\2\u012d\u012e\7*"+
		"\2\2\u012e%\3\2\2\2\u012f\u0130\7\3\2\2\u0130\u0133\5\2\2\2\u0131\u0132"+
		"\7#\2\2\u0132\u0134\5*\26\2\u0133\u0131\3\2\2\2\u0133\u0134\3\2\2\2\u0134"+
		"\u0135\3\2\2\2\u0135\u0136\7%\2\2\u0136\u0137\5\b\5\2\u0137\u0138\7$\2"+
		"\2\u0138\u013d\3\2\2\2\u0139\u013a\5\b\5\2\u013a\u013b\7$\2\2\u013b\u013d"+
		"\3\2\2\2\u013c\u012f\3\2\2\2\u013c\u0139\3\2\2\2\u013d\'\3\2\2\2\u013e"+
		"\u0146\5\2\2\2\u013f\u0140\7\'\2\2\u0140\u0141\5\2\2\2\u0141\u0142\7#"+
		"\2\2\u0142\u0143\5*\26\2\u0143\u0144\7(\2\2\u0144\u0146\3\2\2\2\u0145"+
		"\u013e\3\2\2\2\u0145\u013f\3\2\2\2\u0146)\3\2\2\2\u0147\u0148\5,\27\2"+
		"\u0148\u0149\7\24\2\2\u0149\u014a\5*\26\2\u014a\u014d\3\2\2\2\u014b\u014d"+
		"\5,\27\2\u014c\u0147\3\2\2\2\u014c\u014b\3\2\2\2\u014d+\3\2\2\2\u014e"+
		"\u014f\b\27\1\2\u014f\u0150\5.\30\2\u0150\u0155\3\2\2\2\u0151\u0152\f"+
		"\4\2\2\u0152\u0154\5.\30\2\u0153\u0151\3\2\2\2\u0154\u0157\3\2\2\2\u0155"+
		"\u0153\3\2\2\2\u0155\u0156\3\2\2\2\u0156-\3\2\2\2\u0157\u0155\3\2\2\2"+
		"\u0158\u0163\7\62\2\2\u0159\u0163\7\63\2\2\u015a\u015b\7)\2\2\u015b\u015c"+
		"\5\60\31\2\u015c\u015d\7*\2\2\u015d\u0163\3\2\2\2\u015e\u015f\7\'\2\2"+
		"\u015f\u0160\5*\26\2\u0160\u0161\7(\2\2\u0161\u0163\3\2\2\2\u0162\u0158"+
		"\3\2\2\2\u0162\u0159\3\2\2\2\u0162\u015a\3\2\2\2\u0162\u015e\3\2\2\2\u0163"+
		"/\3\2\2\2\u0164\u0169\5\62\32\2\u0165\u0166\7\"\2\2\u0166\u0168\5\62\32"+
		"\2\u0167\u0165\3\2\2\2\u0168\u016b\3\2\2\2\u0169\u0167\3\2\2\2\u0169\u016a"+
		"\3\2\2\2\u016a\u016e\3\2\2\2\u016b\u0169\3\2\2\2\u016c\u016d\7&\2\2\u016d"+
		"\u016f\7\63\2\2\u016e\u016c\3\2\2\2\u016e\u016f\3\2\2\2\u016f\61\3\2\2"+
		"\2\u0170\u0171\5\2\2\2\u0171\u0172\7#\2\2\u0172\u0173\5*\26\2\u0173\63"+
		"\3\2\2\2 9>FP[mt\u0083\u008e\u0099\u00a1\u00a8\u00b2\u00bd\u00c3\u00cc"+
		"\u00eb\u00f8\u0105\u0114\u011b\u012a\u0133\u013c\u0145\u014c\u0155\u0162"+
		"\u0169\u016e";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}