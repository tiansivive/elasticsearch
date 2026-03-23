// Generated from /Users/t.vilaverde/Workspace/Elastic/elasticsearch/x-pack/plugin/piescript/src/main/antlr/PiescriptLexer.g4 by ANTLR 4.9.2
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class PiescriptLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.9.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		LET=1, IN=2, FN=3, IF=4, THEN=5, ELSE=6, TRUE=7, FALSE=8, NULL=9, MATCH=10, 
		QUERY=11, SPAWN_BANG=12, SPAWN=13, SEND=14, WHEN=15, USE=16, AS=17, PAR=18, 
		DO=19, UNDERSCORE=20, PIPE_OP=21, OR_OP=22, AND_OP=23, ARROW=24, EQ=25, 
		NEQ=26, LTE=27, GTE=28, LT=29, GT=30, PLUS=31, MINUS=32, ASTERISK=33, 
		SLASH=34, PERCENT=35, BANG=36, DOT=37, COMMA=38, COLON=39, SEMICOLON=40, 
		ASSIGN=41, AMP=42, BAR=43, LPAREN=44, RPAREN=45, LBRACE=46, RBRACE=47, 
		LBRACKET=48, RBRACKET=49, INTEGER_LITERAL=50, DECIMAL_LITERAL=51, QUOTED_STRING=52, 
		UPPER_IDENT=53, LOWER_IDENT=54, LINE_COMMENT=55, MULTILINE_COMMENT=56, 
		WS=57;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"LET", "IN", "FN", "IF", "THEN", "ELSE", "TRUE", "FALSE", "NULL", "MATCH", 
			"QUERY", "SPAWN_BANG", "SPAWN", "SEND", "WHEN", "USE", "AS", "PAR", "DO", 
			"UNDERSCORE", "PIPE_OP", "OR_OP", "AND_OP", "ARROW", "EQ", "NEQ", "LTE", 
			"GTE", "LT", "GT", "PLUS", "MINUS", "ASTERISK", "SLASH", "PERCENT", "BANG", 
			"DOT", "COMMA", "COLON", "SEMICOLON", "ASSIGN", "AMP", "BAR", "LPAREN", 
			"RPAREN", "LBRACE", "RBRACE", "LBRACKET", "RBRACKET", "INTEGER_LITERAL", 
			"DECIMAL_LITERAL", "QUOTED_STRING", "UPPER_IDENT", "LOWER_IDENT", "LINE_COMMENT", 
			"MULTILINE_COMMENT", "WS", "DIGIT", "LETTER", "ESCAPE_SEQUENCE", "EXPONENT"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'let'", "'in'", "'fn'", "'if'", "'then'", "'else'", "'true'", 
			"'false'", "'null'", "'match'", "'query'", "'spawn!'", "'spawn'", "'send'", 
			"'when'", "'use'", "'as'", "'par'", "'do'", "'_'", "'|>'", "'||'", "'&&'", 
			"'->'", "'=='", "'!='", "'<='", "'>='", "'<'", "'>'", "'+'", "'-'", "'*'", 
			"'/'", "'%'", "'!'", "'.'", "','", "':'", "';'", "'='", "'&'", "'|'", 
			"'('", "')'", "'{'", "'}'", "'['", "']'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "LET", "IN", "FN", "IF", "THEN", "ELSE", "TRUE", "FALSE", "NULL", 
			"MATCH", "QUERY", "SPAWN_BANG", "SPAWN", "SEND", "WHEN", "USE", "AS", 
			"PAR", "DO", "UNDERSCORE", "PIPE_OP", "OR_OP", "AND_OP", "ARROW", "EQ", 
			"NEQ", "LTE", "GTE", "LT", "GT", "PLUS", "MINUS", "ASTERISK", "SLASH", 
			"PERCENT", "BANG", "DOT", "COMMA", "COLON", "SEMICOLON", "ASSIGN", "AMP", 
			"BAR", "LPAREN", "RPAREN", "LBRACE", "RBRACE", "LBRACKET", "RBRACKET", 
			"INTEGER_LITERAL", "DECIMAL_LITERAL", "QUOTED_STRING", "UPPER_IDENT", 
			"LOWER_IDENT", "LINE_COMMENT", "MULTILINE_COMMENT", "WS"
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


	public PiescriptLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "PiescriptLexer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2;\u01a4\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\4\3\4\3\4\3\5\3\5\3\5\3\6\3\6\3\6"+
		"\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3"+
		"\t\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3"+
		"\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3\17"+
		"\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\22"+
		"\3\22\3\22\3\23\3\23\3\23\3\23\3\24\3\24\3\24\3\25\3\25\3\26\3\26\3\26"+
		"\3\27\3\27\3\27\3\30\3\30\3\30\3\31\3\31\3\31\3\32\3\32\3\32\3\33\3\33"+
		"\3\33\3\34\3\34\3\34\3\35\3\35\3\35\3\36\3\36\3\37\3\37\3 \3 \3!\3!\3"+
		"\"\3\"\3#\3#\3$\3$\3%\3%\3&\3&\3\'\3\'\3(\3(\3)\3)\3*\3*\3+\3+\3,\3,\3"+
		"-\3-\3.\3.\3/\3/\3\60\3\60\3\61\3\61\3\62\3\62\3\63\6\63\u011b\n\63\r"+
		"\63\16\63\u011c\3\64\6\64\u0120\n\64\r\64\16\64\u0121\3\64\3\64\6\64\u0126"+
		"\n\64\r\64\16\64\u0127\3\64\3\64\6\64\u012c\n\64\r\64\16\64\u012d\3\64"+
		"\6\64\u0131\n\64\r\64\16\64\u0132\3\64\3\64\6\64\u0137\n\64\r\64\16\64"+
		"\u0138\5\64\u013b\n\64\3\64\3\64\3\64\3\64\6\64\u0141\n\64\r\64\16\64"+
		"\u0142\3\64\3\64\5\64\u0147\n\64\3\65\3\65\3\65\7\65\u014c\n\65\f\65\16"+
		"\65\u014f\13\65\3\65\3\65\3\66\3\66\3\66\3\66\7\66\u0157\n\66\f\66\16"+
		"\66\u015a\13\66\3\67\3\67\3\67\3\67\7\67\u0160\n\67\f\67\16\67\u0163\13"+
		"\67\3\67\3\67\3\67\3\67\6\67\u0169\n\67\r\67\16\67\u016a\5\67\u016d\n"+
		"\67\38\38\38\38\78\u0173\n8\f8\168\u0176\138\38\58\u0179\n8\38\58\u017c"+
		"\n8\38\38\39\39\39\39\79\u0184\n9\f9\169\u0187\139\39\39\39\39\39\3:\6"+
		":\u018f\n:\r:\16:\u0190\3:\3:\3;\3;\3<\3<\3=\3=\3=\3>\3>\5>\u019e\n>\3"+
		">\6>\u01a1\n>\r>\16>\u01a2\3\u0185\2?\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21"+
		"\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30"+
		"/\31\61\32\63\33\65\34\67\359\36;\37= ?!A\"C#E$G%I&K\'M(O)Q*S+U,W-Y.["+
		"/]\60_\61a\62c\63e\64g\65i\66k\67m8o9q:s;u\2w\2y\2{\2\3\2\f\6\2\f\f\17"+
		"\17$$^^\3\2C\\\3\2c|\4\2\f\f\17\17\5\2\13\f\17\17\"\"\3\2\62;\4\2C\\c"+
		"|\7\2$$^^ppttvv\4\2GGgg\4\2--//\2\u01bd\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3"+
		"\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2"+
		"\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35"+
		"\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)"+
		"\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2"+
		"\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2"+
		"A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3"+
		"\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2"+
		"\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2"+
		"g\3\2\2\2\2i\3\2\2\2\2k\3\2\2\2\2m\3\2\2\2\2o\3\2\2\2\2q\3\2\2\2\2s\3"+
		"\2\2\2\3}\3\2\2\2\5\u0081\3\2\2\2\7\u0084\3\2\2\2\t\u0087\3\2\2\2\13\u008a"+
		"\3\2\2\2\r\u008f\3\2\2\2\17\u0094\3\2\2\2\21\u0099\3\2\2\2\23\u009f\3"+
		"\2\2\2\25\u00a4\3\2\2\2\27\u00aa\3\2\2\2\31\u00b0\3\2\2\2\33\u00b7\3\2"+
		"\2\2\35\u00bd\3\2\2\2\37\u00c2\3\2\2\2!\u00c7\3\2\2\2#\u00cb\3\2\2\2%"+
		"\u00ce\3\2\2\2\'\u00d2\3\2\2\2)\u00d5\3\2\2\2+\u00d7\3\2\2\2-\u00da\3"+
		"\2\2\2/\u00dd\3\2\2\2\61\u00e0\3\2\2\2\63\u00e3\3\2\2\2\65\u00e6\3\2\2"+
		"\2\67\u00e9\3\2\2\29\u00ec\3\2\2\2;\u00ef\3\2\2\2=\u00f1\3\2\2\2?\u00f3"+
		"\3\2\2\2A\u00f5\3\2\2\2C\u00f7\3\2\2\2E\u00f9\3\2\2\2G\u00fb\3\2\2\2I"+
		"\u00fd\3\2\2\2K\u00ff\3\2\2\2M\u0101\3\2\2\2O\u0103\3\2\2\2Q\u0105\3\2"+
		"\2\2S\u0107\3\2\2\2U\u0109\3\2\2\2W\u010b\3\2\2\2Y\u010d\3\2\2\2[\u010f"+
		"\3\2\2\2]\u0111\3\2\2\2_\u0113\3\2\2\2a\u0115\3\2\2\2c\u0117\3\2\2\2e"+
		"\u011a\3\2\2\2g\u0146\3\2\2\2i\u0148\3\2\2\2k\u0152\3\2\2\2m\u016c\3\2"+
		"\2\2o\u016e\3\2\2\2q\u017f\3\2\2\2s\u018e\3\2\2\2u\u0194\3\2\2\2w\u0196"+
		"\3\2\2\2y\u0198\3\2\2\2{\u019b\3\2\2\2}~\7n\2\2~\177\7g\2\2\177\u0080"+
		"\7v\2\2\u0080\4\3\2\2\2\u0081\u0082\7k\2\2\u0082\u0083\7p\2\2\u0083\6"+
		"\3\2\2\2\u0084\u0085\7h\2\2\u0085\u0086\7p\2\2\u0086\b\3\2\2\2\u0087\u0088"+
		"\7k\2\2\u0088\u0089\7h\2\2\u0089\n\3\2\2\2\u008a\u008b\7v\2\2\u008b\u008c"+
		"\7j\2\2\u008c\u008d\7g\2\2\u008d\u008e\7p\2\2\u008e\f\3\2\2\2\u008f\u0090"+
		"\7g\2\2\u0090\u0091\7n\2\2\u0091\u0092\7u\2\2\u0092\u0093\7g\2\2\u0093"+
		"\16\3\2\2\2\u0094\u0095\7v\2\2\u0095\u0096\7t\2\2\u0096\u0097\7w\2\2\u0097"+
		"\u0098\7g\2\2\u0098\20\3\2\2\2\u0099\u009a\7h\2\2\u009a\u009b\7c\2\2\u009b"+
		"\u009c\7n\2\2\u009c\u009d\7u\2\2\u009d\u009e\7g\2\2\u009e\22\3\2\2\2\u009f"+
		"\u00a0\7p\2\2\u00a0\u00a1\7w\2\2\u00a1\u00a2\7n\2\2\u00a2\u00a3\7n\2\2"+
		"\u00a3\24\3\2\2\2\u00a4\u00a5\7o\2\2\u00a5\u00a6\7c\2\2\u00a6\u00a7\7"+
		"v\2\2\u00a7\u00a8\7e\2\2\u00a8\u00a9\7j\2\2\u00a9\26\3\2\2\2\u00aa\u00ab"+
		"\7s\2\2\u00ab\u00ac\7w\2\2\u00ac\u00ad\7g\2\2\u00ad\u00ae\7t\2\2\u00ae"+
		"\u00af\7{\2\2\u00af\30\3\2\2\2\u00b0\u00b1\7u\2\2\u00b1\u00b2\7r\2\2\u00b2"+
		"\u00b3\7c\2\2\u00b3\u00b4\7y\2\2\u00b4\u00b5\7p\2\2\u00b5\u00b6\7#\2\2"+
		"\u00b6\32\3\2\2\2\u00b7\u00b8\7u\2\2\u00b8\u00b9\7r\2\2\u00b9\u00ba\7"+
		"c\2\2\u00ba\u00bb\7y\2\2\u00bb\u00bc\7p\2\2\u00bc\34\3\2\2\2\u00bd\u00be"+
		"\7u\2\2\u00be\u00bf\7g\2\2\u00bf\u00c0\7p\2\2\u00c0\u00c1\7f\2\2\u00c1"+
		"\36\3\2\2\2\u00c2\u00c3\7y\2\2\u00c3\u00c4\7j\2\2\u00c4\u00c5\7g\2\2\u00c5"+
		"\u00c6\7p\2\2\u00c6 \3\2\2\2\u00c7\u00c8\7w\2\2\u00c8\u00c9\7u\2\2\u00c9"+
		"\u00ca\7g\2\2\u00ca\"\3\2\2\2\u00cb\u00cc\7c\2\2\u00cc\u00cd\7u\2\2\u00cd"+
		"$\3\2\2\2\u00ce\u00cf\7r\2\2\u00cf\u00d0\7c\2\2\u00d0\u00d1\7t\2\2\u00d1"+
		"&\3\2\2\2\u00d2\u00d3\7f\2\2\u00d3\u00d4\7q\2\2\u00d4(\3\2\2\2\u00d5\u00d6"+
		"\7a\2\2\u00d6*\3\2\2\2\u00d7\u00d8\7~\2\2\u00d8\u00d9\7@\2\2\u00d9,\3"+
		"\2\2\2\u00da\u00db\7~\2\2\u00db\u00dc\7~\2\2\u00dc.\3\2\2\2\u00dd\u00de"+
		"\7(\2\2\u00de\u00df\7(\2\2\u00df\60\3\2\2\2\u00e0\u00e1\7/\2\2\u00e1\u00e2"+
		"\7@\2\2\u00e2\62\3\2\2\2\u00e3\u00e4\7?\2\2\u00e4\u00e5\7?\2\2\u00e5\64"+
		"\3\2\2\2\u00e6\u00e7\7#\2\2\u00e7\u00e8\7?\2\2\u00e8\66\3\2\2\2\u00e9"+
		"\u00ea\7>\2\2\u00ea\u00eb\7?\2\2\u00eb8\3\2\2\2\u00ec\u00ed\7@\2\2\u00ed"+
		"\u00ee\7?\2\2\u00ee:\3\2\2\2\u00ef\u00f0\7>\2\2\u00f0<\3\2\2\2\u00f1\u00f2"+
		"\7@\2\2\u00f2>\3\2\2\2\u00f3\u00f4\7-\2\2\u00f4@\3\2\2\2\u00f5\u00f6\7"+
		"/\2\2\u00f6B\3\2\2\2\u00f7\u00f8\7,\2\2\u00f8D\3\2\2\2\u00f9\u00fa\7\61"+
		"\2\2\u00faF\3\2\2\2\u00fb\u00fc\7\'\2\2\u00fcH\3\2\2\2\u00fd\u00fe\7#"+
		"\2\2\u00feJ\3\2\2\2\u00ff\u0100\7\60\2\2\u0100L\3\2\2\2\u0101\u0102\7"+
		".\2\2\u0102N\3\2\2\2\u0103\u0104\7<\2\2\u0104P\3\2\2\2\u0105\u0106\7="+
		"\2\2\u0106R\3\2\2\2\u0107\u0108\7?\2\2\u0108T\3\2\2\2\u0109\u010a\7(\2"+
		"\2\u010aV\3\2\2\2\u010b\u010c\7~\2\2\u010cX\3\2\2\2\u010d\u010e\7*\2\2"+
		"\u010eZ\3\2\2\2\u010f\u0110\7+\2\2\u0110\\\3\2\2\2\u0111\u0112\7}\2\2"+
		"\u0112^\3\2\2\2\u0113\u0114\7\177\2\2\u0114`\3\2\2\2\u0115\u0116\7]\2"+
		"\2\u0116b\3\2\2\2\u0117\u0118\7_\2\2\u0118d\3\2\2\2\u0119\u011b\5u;\2"+
		"\u011a\u0119\3\2\2\2\u011b\u011c\3\2\2\2\u011c\u011a\3\2\2\2\u011c\u011d"+
		"\3\2\2\2\u011df\3\2\2\2\u011e\u0120\5u;\2\u011f\u011e\3\2\2\2\u0120\u0121"+
		"\3\2\2\2\u0121\u011f\3\2\2\2\u0121\u0122\3\2\2\2\u0122\u0123\3\2\2\2\u0123"+
		"\u0125\7\60\2\2\u0124\u0126\5u;\2\u0125\u0124\3\2\2\2\u0126\u0127\3\2"+
		"\2\2\u0127\u0125\3\2\2\2\u0127\u0128\3\2\2\2\u0128\u0147\3\2\2\2\u0129"+
		"\u012b\7\60\2\2\u012a\u012c\5u;\2\u012b\u012a\3\2\2\2\u012c\u012d\3\2"+
		"\2\2\u012d\u012b\3\2\2\2\u012d\u012e\3\2\2\2\u012e\u0147\3\2\2\2\u012f"+
		"\u0131\5u;\2\u0130\u012f\3\2\2\2\u0131\u0132\3\2\2\2\u0132\u0130\3\2\2"+
		"\2\u0132\u0133\3\2\2\2\u0133\u013a\3\2\2\2\u0134\u0136\7\60\2\2\u0135"+
		"\u0137\5u;\2\u0136\u0135\3\2\2\2\u0137\u0138\3\2\2\2\u0138\u0136\3\2\2"+
		"\2\u0138\u0139\3\2\2\2\u0139\u013b\3\2\2\2\u013a\u0134\3\2\2\2\u013a\u013b"+
		"\3\2\2\2\u013b\u013c\3\2\2\2\u013c\u013d\5{>\2\u013d\u0147\3\2\2\2\u013e"+
		"\u0140\7\60\2\2\u013f\u0141\5u;\2\u0140\u013f\3\2\2\2\u0141\u0142\3\2"+
		"\2\2\u0142\u0140\3\2\2\2\u0142\u0143\3\2\2\2\u0143\u0144\3\2\2\2\u0144"+
		"\u0145\5{>\2\u0145\u0147\3\2\2\2\u0146\u011f\3\2\2\2\u0146\u0129\3\2\2"+
		"\2\u0146\u0130\3\2\2\2\u0146\u013e\3\2\2\2\u0147h\3\2\2\2\u0148\u014d"+
		"\7$\2\2\u0149\u014c\5y=\2\u014a\u014c\n\2\2\2\u014b\u0149\3\2\2\2\u014b"+
		"\u014a\3\2\2\2\u014c\u014f\3\2\2\2\u014d\u014b\3\2\2\2\u014d\u014e\3\2"+
		"\2\2\u014e\u0150\3\2\2\2\u014f\u014d\3\2\2\2\u0150\u0151\7$\2\2\u0151"+
		"j\3\2\2\2\u0152\u0158\t\3\2\2\u0153\u0157\5w<\2\u0154\u0157\5u;\2\u0155"+
		"\u0157\7a\2\2\u0156\u0153\3\2\2\2\u0156\u0154\3\2\2\2\u0156\u0155\3\2"+
		"\2\2\u0157\u015a\3\2\2\2\u0158\u0156\3\2\2\2\u0158\u0159\3\2\2\2\u0159"+
		"l\3\2\2\2\u015a\u0158\3\2\2\2\u015b\u0161\t\4\2\2\u015c\u0160\5w<\2\u015d"+
		"\u0160\5u;\2\u015e\u0160\7a\2\2\u015f\u015c\3\2\2\2\u015f\u015d\3\2\2"+
		"\2\u015f\u015e\3\2\2\2\u0160\u0163\3\2\2\2\u0161\u015f\3\2\2\2\u0161\u0162"+
		"\3\2\2\2\u0162\u016d\3\2\2\2\u0163\u0161\3\2\2\2\u0164\u0168\7a\2\2\u0165"+
		"\u0169\5w<\2\u0166\u0169\5u;\2\u0167\u0169\7a\2\2\u0168\u0165\3\2\2\2"+
		"\u0168\u0166\3\2\2\2\u0168\u0167\3\2\2\2\u0169\u016a\3\2\2\2\u016a\u0168"+
		"\3\2\2\2\u016a\u016b\3\2\2\2\u016b\u016d\3\2\2\2\u016c\u015b\3\2\2\2\u016c"+
		"\u0164\3\2\2\2\u016dn\3\2\2\2\u016e\u016f\7\61\2\2\u016f\u0170\7\61\2"+
		"\2\u0170\u0174\3\2\2\2\u0171\u0173\n\5\2\2\u0172\u0171\3\2\2\2\u0173\u0176"+
		"\3\2\2\2\u0174\u0172\3\2\2\2\u0174\u0175\3\2\2\2\u0175\u0178\3\2\2\2\u0176"+
		"\u0174\3\2\2\2\u0177\u0179\7\17\2\2\u0178\u0177\3\2\2\2\u0178\u0179\3"+
		"\2\2\2\u0179\u017b\3\2\2\2\u017a\u017c\7\f\2\2\u017b\u017a\3\2\2\2\u017b"+
		"\u017c\3\2\2\2\u017c\u017d\3\2\2\2\u017d\u017e\b8\2\2\u017ep\3\2\2\2\u017f"+
		"\u0180\7\61\2\2\u0180\u0181\7,\2\2\u0181\u0185\3\2\2\2\u0182\u0184\13"+
		"\2\2\2\u0183\u0182\3\2\2\2\u0184\u0187\3\2\2\2\u0185\u0186\3\2\2\2\u0185"+
		"\u0183\3\2\2\2\u0186\u0188\3\2\2\2\u0187\u0185\3\2\2\2\u0188\u0189\7,"+
		"\2\2\u0189\u018a\7\61\2\2\u018a\u018b\3\2\2\2\u018b\u018c\b9\2\2\u018c"+
		"r\3\2\2\2\u018d\u018f\t\6\2\2\u018e\u018d\3\2\2\2\u018f\u0190\3\2\2\2"+
		"\u0190\u018e\3\2\2\2\u0190\u0191\3\2\2\2\u0191\u0192\3\2\2\2\u0192\u0193"+
		"\b:\2\2\u0193t\3\2\2\2\u0194\u0195\t\7\2\2\u0195v\3\2\2\2\u0196\u0197"+
		"\t\b\2\2\u0197x\3\2\2\2\u0198\u0199\7^\2\2\u0199\u019a\t\t\2\2\u019az"+
		"\3\2\2\2\u019b\u019d\t\n\2\2\u019c\u019e\t\13\2\2\u019d\u019c\3\2\2\2"+
		"\u019d\u019e\3\2\2\2\u019e\u01a0\3\2\2\2\u019f\u01a1\5u;\2\u01a0\u019f"+
		"\3\2\2\2\u01a1\u01a2\3\2\2\2\u01a2\u01a0\3\2\2\2\u01a2\u01a3\3\2\2\2\u01a3"+
		"|\3\2\2\2\34\2\u011c\u0121\u0127\u012d\u0132\u0138\u013a\u0142\u0146\u014b"+
		"\u014d\u0156\u0158\u015f\u0161\u0168\u016a\u016c\u0174\u0178\u017b\u0185"+
		"\u0190\u019d\u01a2\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}