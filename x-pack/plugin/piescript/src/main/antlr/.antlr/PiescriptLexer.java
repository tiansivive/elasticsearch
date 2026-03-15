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
		QUERY=11, PAR=12, DO=13, UNDERSCORE=14, PIPE_OP=15, OR_OP=16, AND_OP=17, 
		ARROW=18, EQ=19, NEQ=20, LTE=21, GTE=22, LT=23, GT=24, PLUS=25, MINUS=26, 
		ASTERISK=27, SLASH=28, PERCENT=29, BANG=30, DOT=31, COMMA=32, COLON=33, 
		SEMICOLON=34, ASSIGN=35, BAR=36, LPAREN=37, RPAREN=38, LBRACE=39, RBRACE=40, 
		INTEGER_LITERAL=41, DECIMAL_LITERAL=42, QUOTED_STRING=43, UPPER_IDENT=44, 
		LOWER_IDENT=45, LINE_COMMENT=46, MULTILINE_COMMENT=47, WS=48, ESQL_BODY=49, 
		ESQL_WS=50;
	public static final int
		ESQL_MODE=1;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE", "ESQL_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"LET", "IN", "FN", "IF", "THEN", "ELSE", "TRUE", "FALSE", "NULL", "MATCH", 
			"QUERY", "PAR", "DO", "UNDERSCORE", "PIPE_OP", "OR_OP", "AND_OP", "ARROW", 
			"EQ", "NEQ", "LTE", "GTE", "LT", "GT", "PLUS", "MINUS", "ASTERISK", "SLASH", 
			"PERCENT", "BANG", "DOT", "COMMA", "COLON", "SEMICOLON", "ASSIGN", "BAR", 
			"LPAREN", "RPAREN", "LBRACE", "RBRACE", "INTEGER_LITERAL", "DECIMAL_LITERAL", 
			"QUOTED_STRING", "UPPER_IDENT", "LOWER_IDENT", "LINE_COMMENT", "MULTILINE_COMMENT", 
			"WS", "DIGIT", "LETTER", "ESCAPE_SEQUENCE", "EXPONENT", "ESQL_BODY", 
			"ESQL_WS"
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
			"QUOTED_STRING", "UPPER_IDENT", "LOWER_IDENT", "LINE_COMMENT", "MULTILINE_COMMENT", 
			"WS", "ESQL_BODY", "ESQL_WS"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\64\u0187\b\1\b\1"+
		"\4\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t"+
		"\n\4\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4"+
		"\22\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4"+
		"\31\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4"+
		" \t \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4"+
		"+\t+\4,\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4"+
		"\64\t\64\4\65\t\65\4\66\t\66\4\67\t\67\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\4"+
		"\3\4\3\4\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3"+
		"\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13"+
		"\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\16\3"+
		"\16\3\16\3\17\3\17\3\20\3\20\3\20\3\21\3\21\3\21\3\22\3\22\3\22\3\23\3"+
		"\23\3\23\3\24\3\24\3\24\3\25\3\25\3\25\3\26\3\26\3\26\3\27\3\27\3\27\3"+
		"\30\3\30\3\31\3\31\3\32\3\32\3\33\3\33\3\34\3\34\3\35\3\35\3\36\3\36\3"+
		"\37\3\37\3 \3 \3!\3!\3\"\3\"\3#\3#\3$\3$\3%\3%\3&\3&\3\'\3\'\3(\3(\3)"+
		"\3)\3*\6*\u00ec\n*\r*\16*\u00ed\3+\6+\u00f1\n+\r+\16+\u00f2\3+\3+\6+\u00f7"+
		"\n+\r+\16+\u00f8\3+\3+\6+\u00fd\n+\r+\16+\u00fe\3+\6+\u0102\n+\r+\16+"+
		"\u0103\3+\3+\6+\u0108\n+\r+\16+\u0109\5+\u010c\n+\3+\3+\3+\3+\6+\u0112"+
		"\n+\r+\16+\u0113\3+\3+\5+\u0118\n+\3,\3,\3,\7,\u011d\n,\f,\16,\u0120\13"+
		",\3,\3,\3-\3-\3-\3-\7-\u0128\n-\f-\16-\u012b\13-\3.\3.\3.\3.\7.\u0131"+
		"\n.\f.\16.\u0134\13.\3.\3.\3.\3.\6.\u013a\n.\r.\16.\u013b\5.\u013e\n."+
		"\3/\3/\3/\3/\7/\u0144\n/\f/\16/\u0147\13/\3/\5/\u014a\n/\3/\5/\u014d\n"+
		"/\3/\3/\3\60\3\60\3\60\3\60\7\60\u0155\n\60\f\60\16\60\u0158\13\60\3\60"+
		"\3\60\3\60\3\60\3\60\3\61\6\61\u0160\n\61\r\61\16\61\u0161\3\61\3\61\3"+
		"\62\3\62\3\63\3\63\3\64\3\64\3\64\3\65\3\65\5\65\u016f\n\65\3\65\6\65"+
		"\u0172\n\65\r\65\16\65\u0173\3\66\3\66\7\66\u0178\n\66\f\66\16\66\u017b"+
		"\13\66\3\66\3\66\3\66\3\66\3\67\6\67\u0182\n\67\r\67\16\67\u0183\3\67"+
		"\3\67\3\u0156\28\4\3\6\4\b\5\n\6\f\7\16\b\20\t\22\n\24\13\26\f\30\r\32"+
		"\16\34\17\36\20 \21\"\22$\23&\24(\25*\26,\27.\30\60\31\62\32\64\33\66"+
		"\348\35:\36<\37> @!B\"D#F$H%J&L\'N(P)R*T+V,X-Z.\\/^\60`\61b\62d\2f\2h"+
		"\2j\2l\63n\64\4\2\3\r\6\2\f\f\17\17$$^^\3\2C\\\3\2c|\4\2\f\f\17\17\5\2"+
		"\13\f\17\17\"\"\3\2\62;\4\2C\\c|\7\2$$^^ppttvv\4\2GGgg\4\2--//\3\2bb\2"+
		"\u01a1\2\4\3\2\2\2\2\6\3\2\2\2\2\b\3\2\2\2\2\n\3\2\2\2\2\f\3\2\2\2\2\16"+
		"\3\2\2\2\2\20\3\2\2\2\2\22\3\2\2\2\2\24\3\2\2\2\2\26\3\2\2\2\2\30\3\2"+
		"\2\2\2\32\3\2\2\2\2\34\3\2\2\2\2\36\3\2\2\2\2 \3\2\2\2\2\"\3\2\2\2\2$"+
		"\3\2\2\2\2&\3\2\2\2\2(\3\2\2\2\2*\3\2\2\2\2,\3\2\2\2\2.\3\2\2\2\2\60\3"+
		"\2\2\2\2\62\3\2\2\2\2\64\3\2\2\2\2\66\3\2\2\2\28\3\2\2\2\2:\3\2\2\2\2"+
		"<\3\2\2\2\2>\3\2\2\2\2@\3\2\2\2\2B\3\2\2\2\2D\3\2\2\2\2F\3\2\2\2\2H\3"+
		"\2\2\2\2J\3\2\2\2\2L\3\2\2\2\2N\3\2\2\2\2P\3\2\2\2\2R\3\2\2\2\2T\3\2\2"+
		"\2\2V\3\2\2\2\2X\3\2\2\2\2Z\3\2\2\2\2\\\3\2\2\2\2^\3\2\2\2\2`\3\2\2\2"+
		"\2b\3\2\2\2\3l\3\2\2\2\3n\3\2\2\2\4p\3\2\2\2\6t\3\2\2\2\bw\3\2\2\2\nz"+
		"\3\2\2\2\f}\3\2\2\2\16\u0082\3\2\2\2\20\u0087\3\2\2\2\22\u008c\3\2\2\2"+
		"\24\u0092\3\2\2\2\26\u0097\3\2\2\2\30\u009d\3\2\2\2\32\u00a5\3\2\2\2\34"+
		"\u00a9\3\2\2\2\36\u00ac\3\2\2\2 \u00ae\3\2\2\2\"\u00b1\3\2\2\2$\u00b4"+
		"\3\2\2\2&\u00b7\3\2\2\2(\u00ba\3\2\2\2*\u00bd\3\2\2\2,\u00c0\3\2\2\2."+
		"\u00c3\3\2\2\2\60\u00c6\3\2\2\2\62\u00c8\3\2\2\2\64\u00ca\3\2\2\2\66\u00cc"+
		"\3\2\2\28\u00ce\3\2\2\2:\u00d0\3\2\2\2<\u00d2\3\2\2\2>\u00d4\3\2\2\2@"+
		"\u00d6\3\2\2\2B\u00d8\3\2\2\2D\u00da\3\2\2\2F\u00dc\3\2\2\2H\u00de\3\2"+
		"\2\2J\u00e0\3\2\2\2L\u00e2\3\2\2\2N\u00e4\3\2\2\2P\u00e6\3\2\2\2R\u00e8"+
		"\3\2\2\2T\u00eb\3\2\2\2V\u0117\3\2\2\2X\u0119\3\2\2\2Z\u0123\3\2\2\2\\"+
		"\u013d\3\2\2\2^\u013f\3\2\2\2`\u0150\3\2\2\2b\u015f\3\2\2\2d\u0165\3\2"+
		"\2\2f\u0167\3\2\2\2h\u0169\3\2\2\2j\u016c\3\2\2\2l\u0175\3\2\2\2n\u0181"+
		"\3\2\2\2pq\7n\2\2qr\7g\2\2rs\7v\2\2s\5\3\2\2\2tu\7k\2\2uv\7p\2\2v\7\3"+
		"\2\2\2wx\7h\2\2xy\7p\2\2y\t\3\2\2\2z{\7k\2\2{|\7h\2\2|\13\3\2\2\2}~\7"+
		"v\2\2~\177\7j\2\2\177\u0080\7g\2\2\u0080\u0081\7p\2\2\u0081\r\3\2\2\2"+
		"\u0082\u0083\7g\2\2\u0083\u0084\7n\2\2\u0084\u0085\7u\2\2\u0085\u0086"+
		"\7g\2\2\u0086\17\3\2\2\2\u0087\u0088\7v\2\2\u0088\u0089\7t\2\2\u0089\u008a"+
		"\7w\2\2\u008a\u008b\7g\2\2\u008b\21\3\2\2\2\u008c\u008d\7h\2\2\u008d\u008e"+
		"\7c\2\2\u008e\u008f\7n\2\2\u008f\u0090\7u\2\2\u0090\u0091\7g\2\2\u0091"+
		"\23\3\2\2\2\u0092\u0093\7p\2\2\u0093\u0094\7w\2\2\u0094\u0095\7n\2\2\u0095"+
		"\u0096\7n\2\2\u0096\25\3\2\2\2\u0097\u0098\7o\2\2\u0098\u0099\7c\2\2\u0099"+
		"\u009a\7v\2\2\u009a\u009b\7e\2\2\u009b\u009c\7j\2\2\u009c\27\3\2\2\2\u009d"+
		"\u009e\7s\2\2\u009e\u009f\7w\2\2\u009f\u00a0\7g\2\2\u00a0\u00a1\7t\2\2"+
		"\u00a1\u00a2\7{\2\2\u00a2\u00a3\3\2\2\2\u00a3\u00a4\b\f\2\2\u00a4\31\3"+
		"\2\2\2\u00a5\u00a6\7r\2\2\u00a6\u00a7\7c\2\2\u00a7\u00a8\7t\2\2\u00a8"+
		"\33\3\2\2\2\u00a9\u00aa\7f\2\2\u00aa\u00ab\7q\2\2\u00ab\35\3\2\2\2\u00ac"+
		"\u00ad\7a\2\2\u00ad\37\3\2\2\2\u00ae\u00af\7~\2\2\u00af\u00b0\7@\2\2\u00b0"+
		"!\3\2\2\2\u00b1\u00b2\7~\2\2\u00b2\u00b3\7~\2\2\u00b3#\3\2\2\2\u00b4\u00b5"+
		"\7(\2\2\u00b5\u00b6\7(\2\2\u00b6%\3\2\2\2\u00b7\u00b8\7/\2\2\u00b8\u00b9"+
		"\7@\2\2\u00b9\'\3\2\2\2\u00ba\u00bb\7?\2\2\u00bb\u00bc\7?\2\2\u00bc)\3"+
		"\2\2\2\u00bd\u00be\7#\2\2\u00be\u00bf\7?\2\2\u00bf+\3\2\2\2\u00c0\u00c1"+
		"\7>\2\2\u00c1\u00c2\7?\2\2\u00c2-\3\2\2\2\u00c3\u00c4\7@\2\2\u00c4\u00c5"+
		"\7?\2\2\u00c5/\3\2\2\2\u00c6\u00c7\7>\2\2\u00c7\61\3\2\2\2\u00c8\u00c9"+
		"\7@\2\2\u00c9\63\3\2\2\2\u00ca\u00cb\7-\2\2\u00cb\65\3\2\2\2\u00cc\u00cd"+
		"\7/\2\2\u00cd\67\3\2\2\2\u00ce\u00cf\7,\2\2\u00cf9\3\2\2\2\u00d0\u00d1"+
		"\7\61\2\2\u00d1;\3\2\2\2\u00d2\u00d3\7\'\2\2\u00d3=\3\2\2\2\u00d4\u00d5"+
		"\7#\2\2\u00d5?\3\2\2\2\u00d6\u00d7\7\60\2\2\u00d7A\3\2\2\2\u00d8\u00d9"+
		"\7.\2\2\u00d9C\3\2\2\2\u00da\u00db\7<\2\2\u00dbE\3\2\2\2\u00dc\u00dd\7"+
		"=\2\2\u00ddG\3\2\2\2\u00de\u00df\7?\2\2\u00dfI\3\2\2\2\u00e0\u00e1\7~"+
		"\2\2\u00e1K\3\2\2\2\u00e2\u00e3\7*\2\2\u00e3M\3\2\2\2\u00e4\u00e5\7+\2"+
		"\2\u00e5O\3\2\2\2\u00e6\u00e7\7}\2\2\u00e7Q\3\2\2\2\u00e8\u00e9\7\177"+
		"\2\2\u00e9S\3\2\2\2\u00ea\u00ec\5d\62\2\u00eb\u00ea\3\2\2\2\u00ec\u00ed"+
		"\3\2\2\2\u00ed\u00eb\3\2\2\2\u00ed\u00ee\3\2\2\2\u00eeU\3\2\2\2\u00ef"+
		"\u00f1\5d\62\2\u00f0\u00ef\3\2\2\2\u00f1\u00f2\3\2\2\2\u00f2\u00f0\3\2"+
		"\2\2\u00f2\u00f3\3\2\2\2\u00f3\u00f4\3\2\2\2\u00f4\u00f6\7\60\2\2\u00f5"+
		"\u00f7\5d\62\2\u00f6\u00f5\3\2\2\2\u00f7\u00f8\3\2\2\2\u00f8\u00f6\3\2"+
		"\2\2\u00f8\u00f9\3\2\2\2\u00f9\u0118\3\2\2\2\u00fa\u00fc\7\60\2\2\u00fb"+
		"\u00fd\5d\62\2\u00fc\u00fb\3\2\2\2\u00fd\u00fe\3\2\2\2\u00fe\u00fc\3\2"+
		"\2\2\u00fe\u00ff\3\2\2\2\u00ff\u0118\3\2\2\2\u0100\u0102\5d\62\2\u0101"+
		"\u0100\3\2\2\2\u0102\u0103\3\2\2\2\u0103\u0101\3\2\2\2\u0103\u0104\3\2"+
		"\2\2\u0104\u010b\3\2\2\2\u0105\u0107\7\60\2\2\u0106\u0108\5d\62\2\u0107"+
		"\u0106\3\2\2\2\u0108\u0109\3\2\2\2\u0109\u0107\3\2\2\2\u0109\u010a\3\2"+
		"\2\2\u010a\u010c\3\2\2\2\u010b\u0105\3\2\2\2\u010b\u010c\3\2\2\2\u010c"+
		"\u010d\3\2\2\2\u010d\u010e\5j\65\2\u010e\u0118\3\2\2\2\u010f\u0111\7\60"+
		"\2\2\u0110\u0112\5d\62\2\u0111\u0110\3\2\2\2\u0112\u0113\3\2\2\2\u0113"+
		"\u0111\3\2\2\2\u0113\u0114\3\2\2\2\u0114\u0115\3\2\2\2\u0115\u0116\5j"+
		"\65\2\u0116\u0118\3\2\2\2\u0117\u00f0\3\2\2\2\u0117\u00fa\3\2\2\2\u0117"+
		"\u0101\3\2\2\2\u0117\u010f\3\2\2\2\u0118W\3\2\2\2\u0119\u011e\7$\2\2\u011a"+
		"\u011d\5h\64\2\u011b\u011d\n\2\2\2\u011c\u011a\3\2\2\2\u011c\u011b\3\2"+
		"\2\2\u011d\u0120\3\2\2\2\u011e\u011c\3\2\2\2\u011e\u011f\3\2\2\2\u011f"+
		"\u0121\3\2\2\2\u0120\u011e\3\2\2\2\u0121\u0122\7$\2\2\u0122Y\3\2\2\2\u0123"+
		"\u0129\t\3\2\2\u0124\u0128\5f\63\2\u0125\u0128\5d\62\2\u0126\u0128\7a"+
		"\2\2\u0127\u0124\3\2\2\2\u0127\u0125\3\2\2\2\u0127\u0126\3\2\2\2\u0128"+
		"\u012b\3\2\2\2\u0129\u0127\3\2\2\2\u0129\u012a\3\2\2\2\u012a[\3\2\2\2"+
		"\u012b\u0129\3\2\2\2\u012c\u0132\t\4\2\2\u012d\u0131\5f\63\2\u012e\u0131"+
		"\5d\62\2\u012f\u0131\7a\2\2\u0130\u012d\3\2\2\2\u0130\u012e\3\2\2\2\u0130"+
		"\u012f\3\2\2\2\u0131\u0134\3\2\2\2\u0132\u0130\3\2\2\2\u0132\u0133\3\2"+
		"\2\2\u0133\u013e\3\2\2\2\u0134\u0132\3\2\2\2\u0135\u0139\7a\2\2\u0136"+
		"\u013a\5f\63\2\u0137\u013a\5d\62\2\u0138\u013a\7a\2\2\u0139\u0136\3\2"+
		"\2\2\u0139\u0137\3\2\2\2\u0139\u0138\3\2\2\2\u013a\u013b\3\2\2\2\u013b"+
		"\u0139\3\2\2\2\u013b\u013c\3\2\2\2\u013c\u013e\3\2\2\2\u013d\u012c\3\2"+
		"\2\2\u013d\u0135\3\2\2\2\u013e]\3\2\2\2\u013f\u0140\7\61\2\2\u0140\u0141"+
		"\7\61\2\2\u0141\u0145\3\2\2\2\u0142\u0144\n\5\2\2\u0143\u0142\3\2\2\2"+
		"\u0144\u0147\3\2\2\2\u0145\u0143\3\2\2\2\u0145\u0146\3\2\2\2\u0146\u0149"+
		"\3\2\2\2\u0147\u0145\3\2\2\2\u0148\u014a\7\17\2\2\u0149\u0148\3\2\2\2"+
		"\u0149\u014a\3\2\2\2\u014a\u014c\3\2\2\2\u014b\u014d\7\f\2\2\u014c\u014b"+
		"\3\2\2\2\u014c\u014d\3\2\2\2\u014d\u014e\3\2\2\2\u014e\u014f\b/\3\2\u014f"+
		"_\3\2\2\2\u0150\u0151\7\61\2\2\u0151\u0152\7,\2\2\u0152\u0156\3\2\2\2"+
		"\u0153\u0155\13\2\2\2\u0154\u0153\3\2\2\2\u0155\u0158\3\2\2\2\u0156\u0157"+
		"\3\2\2\2\u0156\u0154\3\2\2\2\u0157\u0159\3\2\2\2\u0158\u0156\3\2\2\2\u0159"+
		"\u015a\7,\2\2\u015a\u015b\7\61\2\2\u015b\u015c\3\2\2\2\u015c\u015d\b\60"+
		"\3\2\u015da\3\2\2\2\u015e\u0160\t\6\2\2\u015f\u015e\3\2\2\2\u0160\u0161"+
		"\3\2\2\2\u0161\u015f\3\2\2\2\u0161\u0162\3\2\2\2\u0162\u0163\3\2\2\2\u0163"+
		"\u0164\b\61\3\2\u0164c\3\2\2\2\u0165\u0166\t\7\2\2\u0166e\3\2\2\2\u0167"+
		"\u0168\t\b\2\2\u0168g\3\2\2\2\u0169\u016a\7^\2\2\u016a\u016b\t\t\2\2\u016b"+
		"i\3\2\2\2\u016c\u016e\t\n\2\2\u016d\u016f\t\13\2\2\u016e\u016d\3\2\2\2"+
		"\u016e\u016f\3\2\2\2\u016f\u0171\3\2\2\2\u0170\u0172\5d\62\2\u0171\u0170"+
		"\3\2\2\2\u0172\u0173\3\2\2\2\u0173\u0171\3\2\2\2\u0173\u0174\3\2\2\2\u0174"+
		"k\3\2\2\2\u0175\u0179\7b\2\2\u0176\u0178\n\f\2\2\u0177\u0176\3\2\2\2\u0178"+
		"\u017b\3\2\2\2\u0179\u0177\3\2\2\2\u0179\u017a\3\2\2\2\u017a\u017c\3\2"+
		"\2\2\u017b\u0179\3\2\2\2\u017c\u017d\7b\2\2\u017d\u017e\3\2\2\2\u017e"+
		"\u017f\b\66\4\2\u017fm\3\2\2\2\u0180\u0182\t\6\2\2\u0181\u0180\3\2\2\2"+
		"\u0182\u0183\3\2\2\2\u0183\u0181\3\2\2\2\u0183\u0184\3\2\2\2\u0184\u0185"+
		"\3\2\2\2\u0185\u0186\b\67\3\2\u0186o\3\2\2\2\37\2\3\u00ed\u00f2\u00f8"+
		"\u00fe\u0103\u0109\u010b\u0113\u0117\u011c\u011e\u0127\u0129\u0130\u0132"+
		"\u0139\u013b\u013d\u0145\u0149\u014c\u0156\u0161\u016e\u0173\u0179\u0183"+
		"\5\7\3\2\b\2\2\6\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}