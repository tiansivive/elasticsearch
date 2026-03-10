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
		INTEGER_LITERAL=41, DECIMAL_LITERAL=42, QUOTED_STRING=43, IDENTIFIER=44, 
		LINE_COMMENT=45, MULTILINE_COMMENT=46, WS=47;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"LET", "IN", "FN", "IF", "THEN", "ELSE", "TRUE", "FALSE", "NULL", "MATCH", 
			"QUERY", "PAR", "DO", "UNDERSCORE", "PIPE_OP", "OR_OP", "AND_OP", "ARROW", 
			"EQ", "NEQ", "LTE", "GTE", "LT", "GT", "PLUS", "MINUS", "ASTERISK", "SLASH", 
			"PERCENT", "BANG", "DOT", "COMMA", "COLON", "SEMICOLON", "ASSIGN", "BAR", 
			"LPAREN", "RPAREN", "LBRACE", "RBRACE", "INTEGER_LITERAL", "DECIMAL_LITERAL", 
			"QUOTED_STRING", "IDENTIFIER", "LINE_COMMENT", "MULTILINE_COMMENT", "WS", 
			"DIGIT", "LETTER", "ESCAPE_SEQUENCE", "EXPONENT"
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
			"QUOTED_STRING", "IDENTIFIER", "LINE_COMMENT", "MULTILINE_COMMENT", "WS"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\61\u0165\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t"+
		"+\4,\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64"+
		"\t\64\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\4\3\4\3\4\3\5\3\5\3\5\3\6\3\6\3\6"+
		"\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3"+
		"\t\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3"+
		"\f\3\f\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\17\3\17\3\20\3\20\3\20\3\21\3"+
		"\21\3\21\3\22\3\22\3\22\3\23\3\23\3\23\3\24\3\24\3\24\3\25\3\25\3\25\3"+
		"\26\3\26\3\26\3\27\3\27\3\27\3\30\3\30\3\31\3\31\3\32\3\32\3\33\3\33\3"+
		"\34\3\34\3\35\3\35\3\36\3\36\3\37\3\37\3 \3 \3!\3!\3\"\3\"\3#\3#\3$\3"+
		"$\3%\3%\3&\3&\3\'\3\'\3(\3(\3)\3)\3*\6*\u00e3\n*\r*\16*\u00e4\3+\6+\u00e8"+
		"\n+\r+\16+\u00e9\3+\3+\7+\u00ee\n+\f+\16+\u00f1\13+\3+\3+\6+\u00f5\n+"+
		"\r+\16+\u00f6\3+\6+\u00fa\n+\r+\16+\u00fb\3+\3+\7+\u0100\n+\f+\16+\u0103"+
		"\13+\5+\u0105\n+\3+\3+\3+\3+\6+\u010b\n+\r+\16+\u010c\3+\3+\5+\u0111\n"+
		"+\3,\3,\3,\7,\u0116\n,\f,\16,\u0119\13,\3,\3,\3-\3-\3-\3-\7-\u0121\n-"+
		"\f-\16-\u0124\13-\3-\3-\3-\3-\6-\u012a\n-\r-\16-\u012b\5-\u012e\n-\3."+
		"\3.\3.\3.\7.\u0134\n.\f.\16.\u0137\13.\3.\5.\u013a\n.\3.\5.\u013d\n.\3"+
		".\3.\3/\3/\3/\3/\7/\u0145\n/\f/\16/\u0148\13/\3/\3/\3/\3/\3/\3\60\6\60"+
		"\u0150\n\60\r\60\16\60\u0151\3\60\3\60\3\61\3\61\3\62\3\62\3\63\3\63\3"+
		"\63\3\64\3\64\5\64\u015f\n\64\3\64\6\64\u0162\n\64\r\64\16\64\u0163\3"+
		"\u0146\2\65\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33"+
		"\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67"+
		"\359\36;\37= ?!A\"C#E$G%I&K\'M(O)Q*S+U,W-Y.[/]\60_\61a\2c\2e\2g\2\3\2"+
		"\n\6\2\f\f\17\17$$^^\4\2\f\f\17\17\5\2\13\f\17\17\"\"\3\2\62;\4\2C\\c"+
		"|\7\2$$^^ppttvv\4\2GGgg\4\2--//\2\u017b\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3"+
		"\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2"+
		"\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35"+
		"\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)"+
		"\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2"+
		"\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2"+
		"A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3"+
		"\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2"+
		"\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\3i\3\2\2\2\5m\3\2\2\2\7p\3\2\2\2\t"+
		"s\3\2\2\2\13v\3\2\2\2\r{\3\2\2\2\17\u0080\3\2\2\2\21\u0085\3\2\2\2\23"+
		"\u008b\3\2\2\2\25\u0090\3\2\2\2\27\u0096\3\2\2\2\31\u009c\3\2\2\2\33\u00a0"+
		"\3\2\2\2\35\u00a3\3\2\2\2\37\u00a5\3\2\2\2!\u00a8\3\2\2\2#\u00ab\3\2\2"+
		"\2%\u00ae\3\2\2\2\'\u00b1\3\2\2\2)\u00b4\3\2\2\2+\u00b7\3\2\2\2-\u00ba"+
		"\3\2\2\2/\u00bd\3\2\2\2\61\u00bf\3\2\2\2\63\u00c1\3\2\2\2\65\u00c3\3\2"+
		"\2\2\67\u00c5\3\2\2\29\u00c7\3\2\2\2;\u00c9\3\2\2\2=\u00cb\3\2\2\2?\u00cd"+
		"\3\2\2\2A\u00cf\3\2\2\2C\u00d1\3\2\2\2E\u00d3\3\2\2\2G\u00d5\3\2\2\2I"+
		"\u00d7\3\2\2\2K\u00d9\3\2\2\2M\u00db\3\2\2\2O\u00dd\3\2\2\2Q\u00df\3\2"+
		"\2\2S\u00e2\3\2\2\2U\u0110\3\2\2\2W\u0112\3\2\2\2Y\u012d\3\2\2\2[\u012f"+
		"\3\2\2\2]\u0140\3\2\2\2_\u014f\3\2\2\2a\u0155\3\2\2\2c\u0157\3\2\2\2e"+
		"\u0159\3\2\2\2g\u015c\3\2\2\2ij\7n\2\2jk\7g\2\2kl\7v\2\2l\4\3\2\2\2mn"+
		"\7k\2\2no\7p\2\2o\6\3\2\2\2pq\7h\2\2qr\7p\2\2r\b\3\2\2\2st\7k\2\2tu\7"+
		"h\2\2u\n\3\2\2\2vw\7v\2\2wx\7j\2\2xy\7g\2\2yz\7p\2\2z\f\3\2\2\2{|\7g\2"+
		"\2|}\7n\2\2}~\7u\2\2~\177\7g\2\2\177\16\3\2\2\2\u0080\u0081\7v\2\2\u0081"+
		"\u0082\7t\2\2\u0082\u0083\7w\2\2\u0083\u0084\7g\2\2\u0084\20\3\2\2\2\u0085"+
		"\u0086\7h\2\2\u0086\u0087\7c\2\2\u0087\u0088\7n\2\2\u0088\u0089\7u\2\2"+
		"\u0089\u008a\7g\2\2\u008a\22\3\2\2\2\u008b\u008c\7p\2\2\u008c\u008d\7"+
		"w\2\2\u008d\u008e\7n\2\2\u008e\u008f\7n\2\2\u008f\24\3\2\2\2\u0090\u0091"+
		"\7o\2\2\u0091\u0092\7c\2\2\u0092\u0093\7v\2\2\u0093\u0094\7e\2\2\u0094"+
		"\u0095\7j\2\2\u0095\26\3\2\2\2\u0096\u0097\7s\2\2\u0097\u0098\7w\2\2\u0098"+
		"\u0099\7g\2\2\u0099\u009a\7t\2\2\u009a\u009b\7{\2\2\u009b\30\3\2\2\2\u009c"+
		"\u009d\7r\2\2\u009d\u009e\7c\2\2\u009e\u009f\7t\2\2\u009f\32\3\2\2\2\u00a0"+
		"\u00a1\7f\2\2\u00a1\u00a2\7q\2\2\u00a2\34\3\2\2\2\u00a3\u00a4\7a\2\2\u00a4"+
		"\36\3\2\2\2\u00a5\u00a6\7~\2\2\u00a6\u00a7\7@\2\2\u00a7 \3\2\2\2\u00a8"+
		"\u00a9\7~\2\2\u00a9\u00aa\7~\2\2\u00aa\"\3\2\2\2\u00ab\u00ac\7(\2\2\u00ac"+
		"\u00ad\7(\2\2\u00ad$\3\2\2\2\u00ae\u00af\7/\2\2\u00af\u00b0\7@\2\2\u00b0"+
		"&\3\2\2\2\u00b1\u00b2\7?\2\2\u00b2\u00b3\7?\2\2\u00b3(\3\2\2\2\u00b4\u00b5"+
		"\7#\2\2\u00b5\u00b6\7?\2\2\u00b6*\3\2\2\2\u00b7\u00b8\7>\2\2\u00b8\u00b9"+
		"\7?\2\2\u00b9,\3\2\2\2\u00ba\u00bb\7@\2\2\u00bb\u00bc\7?\2\2\u00bc.\3"+
		"\2\2\2\u00bd\u00be\7>\2\2\u00be\60\3\2\2\2\u00bf\u00c0\7@\2\2\u00c0\62"+
		"\3\2\2\2\u00c1\u00c2\7-\2\2\u00c2\64\3\2\2\2\u00c3\u00c4\7/\2\2\u00c4"+
		"\66\3\2\2\2\u00c5\u00c6\7,\2\2\u00c68\3\2\2\2\u00c7\u00c8\7\61\2\2\u00c8"+
		":\3\2\2\2\u00c9\u00ca\7\'\2\2\u00ca<\3\2\2\2\u00cb\u00cc\7#\2\2\u00cc"+
		">\3\2\2\2\u00cd\u00ce\7\60\2\2\u00ce@\3\2\2\2\u00cf\u00d0\7.\2\2\u00d0"+
		"B\3\2\2\2\u00d1\u00d2\7<\2\2\u00d2D\3\2\2\2\u00d3\u00d4\7=\2\2\u00d4F"+
		"\3\2\2\2\u00d5\u00d6\7?\2\2\u00d6H\3\2\2\2\u00d7\u00d8\7~\2\2\u00d8J\3"+
		"\2\2\2\u00d9\u00da\7*\2\2\u00daL\3\2\2\2\u00db\u00dc\7+\2\2\u00dcN\3\2"+
		"\2\2\u00dd\u00de\7}\2\2\u00deP\3\2\2\2\u00df\u00e0\7\177\2\2\u00e0R\3"+
		"\2\2\2\u00e1\u00e3\5a\61\2\u00e2\u00e1\3\2\2\2\u00e3\u00e4\3\2\2\2\u00e4"+
		"\u00e2\3\2\2\2\u00e4\u00e5\3\2\2\2\u00e5T\3\2\2\2\u00e6\u00e8\5a\61\2"+
		"\u00e7\u00e6\3\2\2\2\u00e8\u00e9\3\2\2\2\u00e9\u00e7\3\2\2\2\u00e9\u00ea"+
		"\3\2\2\2\u00ea\u00eb\3\2\2\2\u00eb\u00ef\7\60\2\2\u00ec\u00ee\5a\61\2"+
		"\u00ed\u00ec\3\2\2\2\u00ee\u00f1\3\2\2\2\u00ef\u00ed\3\2\2\2\u00ef\u00f0"+
		"\3\2\2\2\u00f0\u0111\3\2\2\2\u00f1\u00ef\3\2\2\2\u00f2\u00f4\7\60\2\2"+
		"\u00f3\u00f5\5a\61\2\u00f4\u00f3\3\2\2\2\u00f5\u00f6\3\2\2\2\u00f6\u00f4"+
		"\3\2\2\2\u00f6\u00f7\3\2\2\2\u00f7\u0111\3\2\2\2\u00f8\u00fa\5a\61\2\u00f9"+
		"\u00f8\3\2\2\2\u00fa\u00fb\3\2\2\2\u00fb\u00f9\3\2\2\2\u00fb\u00fc\3\2"+
		"\2\2\u00fc\u0104\3\2\2\2\u00fd\u0101\7\60\2\2\u00fe\u0100\5a\61\2\u00ff"+
		"\u00fe\3\2\2\2\u0100\u0103\3\2\2\2\u0101\u00ff\3\2\2\2\u0101\u0102\3\2"+
		"\2\2\u0102\u0105\3\2\2\2\u0103\u0101\3\2\2\2\u0104\u00fd\3\2\2\2\u0104"+
		"\u0105\3\2\2\2\u0105\u0106\3\2\2\2\u0106\u0107\5g\64\2\u0107\u0111\3\2"+
		"\2\2\u0108\u010a\7\60\2\2\u0109\u010b\5a\61\2\u010a\u0109\3\2\2\2\u010b"+
		"\u010c\3\2\2\2\u010c\u010a\3\2\2\2\u010c\u010d\3\2\2\2\u010d\u010e\3\2"+
		"\2\2\u010e\u010f\5g\64\2\u010f\u0111\3\2\2\2\u0110\u00e7\3\2\2\2\u0110"+
		"\u00f2\3\2\2\2\u0110\u00f9\3\2\2\2\u0110\u0108\3\2\2\2\u0111V\3\2\2\2"+
		"\u0112\u0117\7$\2\2\u0113\u0116\5e\63\2\u0114\u0116\n\2\2\2\u0115\u0113"+
		"\3\2\2\2\u0115\u0114\3\2\2\2\u0116\u0119\3\2\2\2\u0117\u0115\3\2\2\2\u0117"+
		"\u0118\3\2\2\2\u0118\u011a\3\2\2\2\u0119\u0117\3\2\2\2\u011a\u011b\7$"+
		"\2\2\u011bX\3\2\2\2\u011c\u0122\5c\62\2\u011d\u0121\5c\62\2\u011e\u0121"+
		"\5a\61\2\u011f\u0121\7a\2\2\u0120\u011d\3\2\2\2\u0120\u011e\3\2\2\2\u0120"+
		"\u011f\3\2\2\2\u0121\u0124\3\2\2\2\u0122\u0120\3\2\2\2\u0122\u0123\3\2"+
		"\2\2\u0123\u012e\3\2\2\2\u0124\u0122\3\2\2\2\u0125\u0129\7a\2\2\u0126"+
		"\u012a\5c\62\2\u0127\u012a\5a\61\2\u0128\u012a\7a\2\2\u0129\u0126\3\2"+
		"\2\2\u0129\u0127\3\2\2\2\u0129\u0128\3\2\2\2\u012a\u012b\3\2\2\2\u012b"+
		"\u0129\3\2\2\2\u012b\u012c\3\2\2\2\u012c\u012e\3\2\2\2\u012d\u011c\3\2"+
		"\2\2\u012d\u0125\3\2\2\2\u012eZ\3\2\2\2\u012f\u0130\7\61\2\2\u0130\u0131"+
		"\7\61\2\2\u0131\u0135\3\2\2\2\u0132\u0134\n\3\2\2\u0133\u0132\3\2\2\2"+
		"\u0134\u0137\3\2\2\2\u0135\u0133\3\2\2\2\u0135\u0136\3\2\2\2\u0136\u0139"+
		"\3\2\2\2\u0137\u0135\3\2\2\2\u0138\u013a\7\17\2\2\u0139\u0138\3\2\2\2"+
		"\u0139\u013a\3\2\2\2\u013a\u013c\3\2\2\2\u013b\u013d\7\f\2\2\u013c\u013b"+
		"\3\2\2\2\u013c\u013d\3\2\2\2\u013d\u013e\3\2\2\2\u013e\u013f\b.\2\2\u013f"+
		"\\\3\2\2\2\u0140\u0141\7\61\2\2\u0141\u0142\7,\2\2\u0142\u0146\3\2\2\2"+
		"\u0143\u0145\13\2\2\2\u0144\u0143\3\2\2\2\u0145\u0148\3\2\2\2\u0146\u0147"+
		"\3\2\2\2\u0146\u0144\3\2\2\2\u0147\u0149\3\2\2\2\u0148\u0146\3\2\2\2\u0149"+
		"\u014a\7,\2\2\u014a\u014b\7\61\2\2\u014b\u014c\3\2\2\2\u014c\u014d\b/"+
		"\2\2\u014d^\3\2\2\2\u014e\u0150\t\4\2\2\u014f\u014e\3\2\2\2\u0150\u0151"+
		"\3\2\2\2\u0151\u014f\3\2\2\2\u0151\u0152\3\2\2\2\u0152\u0153\3\2\2\2\u0153"+
		"\u0154\b\60\2\2\u0154`\3\2\2\2\u0155\u0156\t\5\2\2\u0156b\3\2\2\2\u0157"+
		"\u0158\t\6\2\2\u0158d\3\2\2\2\u0159\u015a\7^\2\2\u015a\u015b\t\7\2\2\u015b"+
		"f\3\2\2\2\u015c\u015e\t\b\2\2\u015d\u015f\t\t\2\2\u015e\u015d\3\2\2\2"+
		"\u015e\u015f\3\2\2\2\u015f\u0161\3\2\2\2\u0160\u0162\5a\61\2\u0161\u0160"+
		"\3\2\2\2\u0162\u0163\3\2\2\2\u0163\u0161\3\2\2\2\u0163\u0164\3\2\2\2\u0164"+
		"h\3\2\2\2\32\2\u00e4\u00e9\u00ef\u00f6\u00fb\u0101\u0104\u010c\u0110\u0115"+
		"\u0117\u0120\u0122\u0129\u012b\u012d\u0135\u0139\u013c\u0146\u0151\u015e"+
		"\u0163\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}