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
		QUERY=11, SPAWN=12, WHEN=13, PAR=14, DO=15, UNDERSCORE=16, PIPE_OP=17, 
		OR_OP=18, AND_OP=19, ARROW=20, EQ=21, NEQ=22, LTE=23, GTE=24, LT=25, GT=26, 
		PLUS=27, MINUS=28, ASTERISK=29, SLASH=30, PERCENT=31, BANG=32, DOT=33, 
		COMMA=34, COLON=35, SEMICOLON=36, ASSIGN=37, AMP=38, BAR=39, LPAREN=40, 
		RPAREN=41, LBRACE=42, RBRACE=43, INTEGER_LITERAL=44, DECIMAL_LITERAL=45, 
		QUOTED_STRING=46, UPPER_IDENT=47, LOWER_IDENT=48, LINE_COMMENT=49, MULTILINE_COMMENT=50, 
		WS=51, ESQL_BODY=52, ESQL_WS=53;
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
			"QUERY", "SPAWN", "WHEN", "PAR", "DO", "UNDERSCORE", "PIPE_OP", "OR_OP", 
			"AND_OP", "ARROW", "EQ", "NEQ", "LTE", "GTE", "LT", "GT", "PLUS", "MINUS", 
			"ASTERISK", "SLASH", "PERCENT", "BANG", "DOT", "COMMA", "COLON", "SEMICOLON", 
			"ASSIGN", "AMP", "BAR", "LPAREN", "RPAREN", "LBRACE", "RBRACE", "INTEGER_LITERAL", 
			"DECIMAL_LITERAL", "QUOTED_STRING", "UPPER_IDENT", "LOWER_IDENT", "LINE_COMMENT", 
			"MULTILINE_COMMENT", "WS", "DIGIT", "LETTER", "ESCAPE_SEQUENCE", "EXPONENT", 
			"ESQL_BODY", "ESQL_WS"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'let'", "'in'", "'fn'", "'if'", "'then'", "'else'", "'true'", 
			"'false'", "'null'", "'match'", "'query'", "'spawn'", "'when'", "'par'", 
			"'do'", "'_'", "'|>'", "'||'", "'&&'", "'->'", "'=='", "'!='", "'<='", 
			"'>='", "'<'", "'>'", "'+'", "'-'", "'*'", "'/'", "'%'", "'!'", "'.'", 
			"','", "':'", "';'", "'='", "'&'", "'|'", "'('", "')'", "'{'", "'}'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "LET", "IN", "FN", "IF", "THEN", "ELSE", "TRUE", "FALSE", "NULL", 
			"MATCH", "QUERY", "SPAWN", "WHEN", "PAR", "DO", "UNDERSCORE", "PIPE_OP", 
			"OR_OP", "AND_OP", "ARROW", "EQ", "NEQ", "LTE", "GTE", "LT", "GT", "PLUS", 
			"MINUS", "ASTERISK", "SLASH", "PERCENT", "BANG", "DOT", "COMMA", "COLON", 
			"SEMICOLON", "ASSIGN", "AMP", "BAR", "LPAREN", "RPAREN", "LBRACE", "RBRACE", 
			"INTEGER_LITERAL", "DECIMAL_LITERAL", "QUOTED_STRING", "UPPER_IDENT", 
			"LOWER_IDENT", "LINE_COMMENT", "MULTILINE_COMMENT", "WS", "ESQL_BODY", 
			"ESQL_WS"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\67\u0199\b\1\b\1"+
		"\4\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t"+
		"\n\4\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4"+
		"\22\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4"+
		"\31\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4"+
		" \t \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4"+
		"+\t+\4,\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4"+
		"\64\t\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\3\2\3\2\3\2\3"+
		"\2\3\3\3\3\3\3\3\4\3\4\3\4\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7"+
		"\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3"+
		"\n\3\13\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\r\3"+
		"\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\20\3"+
		"\20\3\20\3\21\3\21\3\22\3\22\3\22\3\23\3\23\3\23\3\24\3\24\3\24\3\25\3"+
		"\25\3\25\3\26\3\26\3\26\3\27\3\27\3\27\3\30\3\30\3\30\3\31\3\31\3\31\3"+
		"\32\3\32\3\33\3\33\3\34\3\34\3\35\3\35\3\36\3\36\3\37\3\37\3 \3 \3!\3"+
		"!\3\"\3\"\3#\3#\3$\3$\3%\3%\3&\3&\3\'\3\'\3(\3(\3)\3)\3*\3*\3+\3+\3,\3"+
		",\3-\6-\u00ff\n-\r-\16-\u0100\3.\6.\u0104\n.\r.\16.\u0105\3.\3.\6.\u010a"+
		"\n.\r.\16.\u010b\3.\3.\6.\u0110\n.\r.\16.\u0111\3.\6.\u0115\n.\r.\16."+
		"\u0116\3.\3.\6.\u011b\n.\r.\16.\u011c\5.\u011f\n.\3.\3.\3.\3.\6.\u0125"+
		"\n.\r.\16.\u0126\3.\3.\5.\u012b\n.\3/\3/\3/\7/\u0130\n/\f/\16/\u0133\13"+
		"/\3/\3/\3\60\3\60\3\60\3\60\7\60\u013b\n\60\f\60\16\60\u013e\13\60\3\61"+
		"\3\61\3\61\3\61\7\61\u0144\n\61\f\61\16\61\u0147\13\61\3\61\3\61\3\61"+
		"\3\61\6\61\u014d\n\61\r\61\16\61\u014e\5\61\u0151\n\61\3\62\3\62\3\62"+
		"\3\62\7\62\u0157\n\62\f\62\16\62\u015a\13\62\3\62\5\62\u015d\n\62\3\62"+
		"\5\62\u0160\n\62\3\62\3\62\3\63\3\63\3\63\3\63\7\63\u0168\n\63\f\63\16"+
		"\63\u016b\13\63\3\63\3\63\3\63\3\63\3\63\3\64\6\64\u0173\n\64\r\64\16"+
		"\64\u0174\3\64\3\64\3\65\3\65\3\66\3\66\3\67\3\67\3\67\38\38\58\u0182"+
		"\n8\38\68\u0185\n8\r8\168\u0186\39\39\69\u018b\n9\r9\169\u018c\39\39\3"+
		"9\39\3:\6:\u0194\n:\r:\16:\u0195\3:\3:\3\u0169\2;\4\3\6\4\b\5\n\6\f\7"+
		"\16\b\20\t\22\n\24\13\26\f\30\r\32\16\34\17\36\20 \21\"\22$\23&\24(\25"+
		"*\26,\27.\30\60\31\62\32\64\33\66\348\35:\36<\37> @!B\"D#F$H%J&L\'N(P"+
		")R*T+V,X-Z.\\/^\60`\61b\62d\63f\64h\65j\2l\2n\2p\2r\66t\67\4\2\3\r\6\2"+
		"\f\f\17\17$$^^\3\2C\\\3\2c|\4\2\f\f\17\17\5\2\13\f\17\17\"\"\3\2\62;\4"+
		"\2C\\c|\7\2$$^^ppttvv\4\2GGgg\4\2--//\3\2bb\2\u01b3\2\4\3\2\2\2\2\6\3"+
		"\2\2\2\2\b\3\2\2\2\2\n\3\2\2\2\2\f\3\2\2\2\2\16\3\2\2\2\2\20\3\2\2\2\2"+
		"\22\3\2\2\2\2\24\3\2\2\2\2\26\3\2\2\2\2\30\3\2\2\2\2\32\3\2\2\2\2\34\3"+
		"\2\2\2\2\36\3\2\2\2\2 \3\2\2\2\2\"\3\2\2\2\2$\3\2\2\2\2&\3\2\2\2\2(\3"+
		"\2\2\2\2*\3\2\2\2\2,\3\2\2\2\2.\3\2\2\2\2\60\3\2\2\2\2\62\3\2\2\2\2\64"+
		"\3\2\2\2\2\66\3\2\2\2\28\3\2\2\2\2:\3\2\2\2\2<\3\2\2\2\2>\3\2\2\2\2@\3"+
		"\2\2\2\2B\3\2\2\2\2D\3\2\2\2\2F\3\2\2\2\2H\3\2\2\2\2J\3\2\2\2\2L\3\2\2"+
		"\2\2N\3\2\2\2\2P\3\2\2\2\2R\3\2\2\2\2T\3\2\2\2\2V\3\2\2\2\2X\3\2\2\2\2"+
		"Z\3\2\2\2\2\\\3\2\2\2\2^\3\2\2\2\2`\3\2\2\2\2b\3\2\2\2\2d\3\2\2\2\2f\3"+
		"\2\2\2\2h\3\2\2\2\3r\3\2\2\2\3t\3\2\2\2\4v\3\2\2\2\6z\3\2\2\2\b}\3\2\2"+
		"\2\n\u0080\3\2\2\2\f\u0083\3\2\2\2\16\u0088\3\2\2\2\20\u008d\3\2\2\2\22"+
		"\u0092\3\2\2\2\24\u0098\3\2\2\2\26\u009d\3\2\2\2\30\u00a3\3\2\2\2\32\u00ab"+
		"\3\2\2\2\34\u00b1\3\2\2\2\36\u00b6\3\2\2\2 \u00ba\3\2\2\2\"\u00bd\3\2"+
		"\2\2$\u00bf\3\2\2\2&\u00c2\3\2\2\2(\u00c5\3\2\2\2*\u00c8\3\2\2\2,\u00cb"+
		"\3\2\2\2.\u00ce\3\2\2\2\60\u00d1\3\2\2\2\62\u00d4\3\2\2\2\64\u00d7\3\2"+
		"\2\2\66\u00d9\3\2\2\28\u00db\3\2\2\2:\u00dd\3\2\2\2<\u00df\3\2\2\2>\u00e1"+
		"\3\2\2\2@\u00e3\3\2\2\2B\u00e5\3\2\2\2D\u00e7\3\2\2\2F\u00e9\3\2\2\2H"+
		"\u00eb\3\2\2\2J\u00ed\3\2\2\2L\u00ef\3\2\2\2N\u00f1\3\2\2\2P\u00f3\3\2"+
		"\2\2R\u00f5\3\2\2\2T\u00f7\3\2\2\2V\u00f9\3\2\2\2X\u00fb\3\2\2\2Z\u00fe"+
		"\3\2\2\2\\\u012a\3\2\2\2^\u012c\3\2\2\2`\u0136\3\2\2\2b\u0150\3\2\2\2"+
		"d\u0152\3\2\2\2f\u0163\3\2\2\2h\u0172\3\2\2\2j\u0178\3\2\2\2l\u017a\3"+
		"\2\2\2n\u017c\3\2\2\2p\u017f\3\2\2\2r\u0188\3\2\2\2t\u0193\3\2\2\2vw\7"+
		"n\2\2wx\7g\2\2xy\7v\2\2y\5\3\2\2\2z{\7k\2\2{|\7p\2\2|\7\3\2\2\2}~\7h\2"+
		"\2~\177\7p\2\2\177\t\3\2\2\2\u0080\u0081\7k\2\2\u0081\u0082\7h\2\2\u0082"+
		"\13\3\2\2\2\u0083\u0084\7v\2\2\u0084\u0085\7j\2\2\u0085\u0086\7g\2\2\u0086"+
		"\u0087\7p\2\2\u0087\r\3\2\2\2\u0088\u0089\7g\2\2\u0089\u008a\7n\2\2\u008a"+
		"\u008b\7u\2\2\u008b\u008c\7g\2\2\u008c\17\3\2\2\2\u008d\u008e\7v\2\2\u008e"+
		"\u008f\7t\2\2\u008f\u0090\7w\2\2\u0090\u0091\7g\2\2\u0091\21\3\2\2\2\u0092"+
		"\u0093\7h\2\2\u0093\u0094\7c\2\2\u0094\u0095\7n\2\2\u0095\u0096\7u\2\2"+
		"\u0096\u0097\7g\2\2\u0097\23\3\2\2\2\u0098\u0099\7p\2\2\u0099\u009a\7"+
		"w\2\2\u009a\u009b\7n\2\2\u009b\u009c\7n\2\2\u009c\25\3\2\2\2\u009d\u009e"+
		"\7o\2\2\u009e\u009f\7c\2\2\u009f\u00a0\7v\2\2\u00a0\u00a1\7e\2\2\u00a1"+
		"\u00a2\7j\2\2\u00a2\27\3\2\2\2\u00a3\u00a4\7s\2\2\u00a4\u00a5\7w\2\2\u00a5"+
		"\u00a6\7g\2\2\u00a6\u00a7\7t\2\2\u00a7\u00a8\7{\2\2\u00a8\u00a9\3\2\2"+
		"\2\u00a9\u00aa\b\f\2\2\u00aa\31\3\2\2\2\u00ab\u00ac\7u\2\2\u00ac\u00ad"+
		"\7r\2\2\u00ad\u00ae\7c\2\2\u00ae\u00af\7y\2\2\u00af\u00b0\7p\2\2\u00b0"+
		"\33\3\2\2\2\u00b1\u00b2\7y\2\2\u00b2\u00b3\7j\2\2\u00b3\u00b4\7g\2\2\u00b4"+
		"\u00b5\7p\2\2\u00b5\35\3\2\2\2\u00b6\u00b7\7r\2\2\u00b7\u00b8\7c\2\2\u00b8"+
		"\u00b9\7t\2\2\u00b9\37\3\2\2\2\u00ba\u00bb\7f\2\2\u00bb\u00bc\7q\2\2\u00bc"+
		"!\3\2\2\2\u00bd\u00be\7a\2\2\u00be#\3\2\2\2\u00bf\u00c0\7~\2\2\u00c0\u00c1"+
		"\7@\2\2\u00c1%\3\2\2\2\u00c2\u00c3\7~\2\2\u00c3\u00c4\7~\2\2\u00c4\'\3"+
		"\2\2\2\u00c5\u00c6\7(\2\2\u00c6\u00c7\7(\2\2\u00c7)\3\2\2\2\u00c8\u00c9"+
		"\7/\2\2\u00c9\u00ca\7@\2\2\u00ca+\3\2\2\2\u00cb\u00cc\7?\2\2\u00cc\u00cd"+
		"\7?\2\2\u00cd-\3\2\2\2\u00ce\u00cf\7#\2\2\u00cf\u00d0\7?\2\2\u00d0/\3"+
		"\2\2\2\u00d1\u00d2\7>\2\2\u00d2\u00d3\7?\2\2\u00d3\61\3\2\2\2\u00d4\u00d5"+
		"\7@\2\2\u00d5\u00d6\7?\2\2\u00d6\63\3\2\2\2\u00d7\u00d8\7>\2\2\u00d8\65"+
		"\3\2\2\2\u00d9\u00da\7@\2\2\u00da\67\3\2\2\2\u00db\u00dc\7-\2\2\u00dc"+
		"9\3\2\2\2\u00dd\u00de\7/\2\2\u00de;\3\2\2\2\u00df\u00e0\7,\2\2\u00e0="+
		"\3\2\2\2\u00e1\u00e2\7\61\2\2\u00e2?\3\2\2\2\u00e3\u00e4\7\'\2\2\u00e4"+
		"A\3\2\2\2\u00e5\u00e6\7#\2\2\u00e6C\3\2\2\2\u00e7\u00e8\7\60\2\2\u00e8"+
		"E\3\2\2\2\u00e9\u00ea\7.\2\2\u00eaG\3\2\2\2\u00eb\u00ec\7<\2\2\u00ecI"+
		"\3\2\2\2\u00ed\u00ee\7=\2\2\u00eeK\3\2\2\2\u00ef\u00f0\7?\2\2\u00f0M\3"+
		"\2\2\2\u00f1\u00f2\7(\2\2\u00f2O\3\2\2\2\u00f3\u00f4\7~\2\2\u00f4Q\3\2"+
		"\2\2\u00f5\u00f6\7*\2\2\u00f6S\3\2\2\2\u00f7\u00f8\7+\2\2\u00f8U\3\2\2"+
		"\2\u00f9\u00fa\7}\2\2\u00faW\3\2\2\2\u00fb\u00fc\7\177\2\2\u00fcY\3\2"+
		"\2\2\u00fd\u00ff\5j\65\2\u00fe\u00fd\3\2\2\2\u00ff\u0100\3\2\2\2\u0100"+
		"\u00fe\3\2\2\2\u0100\u0101\3\2\2\2\u0101[\3\2\2\2\u0102\u0104\5j\65\2"+
		"\u0103\u0102\3\2\2\2\u0104\u0105\3\2\2\2\u0105\u0103\3\2\2\2\u0105\u0106"+
		"\3\2\2\2\u0106\u0107\3\2\2\2\u0107\u0109\7\60\2\2\u0108\u010a\5j\65\2"+
		"\u0109\u0108\3\2\2\2\u010a\u010b\3\2\2\2\u010b\u0109\3\2\2\2\u010b\u010c"+
		"\3\2\2\2\u010c\u012b\3\2\2\2\u010d\u010f\7\60\2\2\u010e\u0110\5j\65\2"+
		"\u010f\u010e\3\2\2\2\u0110\u0111\3\2\2\2\u0111\u010f\3\2\2\2\u0111\u0112"+
		"\3\2\2\2\u0112\u012b\3\2\2\2\u0113\u0115\5j\65\2\u0114\u0113\3\2\2\2\u0115"+
		"\u0116\3\2\2\2\u0116\u0114\3\2\2\2\u0116\u0117\3\2\2\2\u0117\u011e\3\2"+
		"\2\2\u0118\u011a\7\60\2\2\u0119\u011b\5j\65\2\u011a\u0119\3\2\2\2\u011b"+
		"\u011c\3\2\2\2\u011c\u011a\3\2\2\2\u011c\u011d\3\2\2\2\u011d\u011f\3\2"+
		"\2\2\u011e\u0118\3\2\2\2\u011e\u011f\3\2\2\2\u011f\u0120\3\2\2\2\u0120"+
		"\u0121\5p8\2\u0121\u012b\3\2\2\2\u0122\u0124\7\60\2\2\u0123\u0125\5j\65"+
		"\2\u0124\u0123\3\2\2\2\u0125\u0126\3\2\2\2\u0126\u0124\3\2\2\2\u0126\u0127"+
		"\3\2\2\2\u0127\u0128\3\2\2\2\u0128\u0129\5p8\2\u0129\u012b\3\2\2\2\u012a"+
		"\u0103\3\2\2\2\u012a\u010d\3\2\2\2\u012a\u0114\3\2\2\2\u012a\u0122\3\2"+
		"\2\2\u012b]\3\2\2\2\u012c\u0131\7$\2\2\u012d\u0130\5n\67\2\u012e\u0130"+
		"\n\2\2\2\u012f\u012d\3\2\2\2\u012f\u012e\3\2\2\2\u0130\u0133\3\2\2\2\u0131"+
		"\u012f\3\2\2\2\u0131\u0132\3\2\2\2\u0132\u0134\3\2\2\2\u0133\u0131\3\2"+
		"\2\2\u0134\u0135\7$\2\2\u0135_\3\2\2\2\u0136\u013c\t\3\2\2\u0137\u013b"+
		"\5l\66\2\u0138\u013b\5j\65\2\u0139\u013b\7a\2\2\u013a\u0137\3\2\2\2\u013a"+
		"\u0138\3\2\2\2\u013a\u0139\3\2\2\2\u013b\u013e\3\2\2\2\u013c\u013a\3\2"+
		"\2\2\u013c\u013d\3\2\2\2\u013da\3\2\2\2\u013e\u013c\3\2\2\2\u013f\u0145"+
		"\t\4\2\2\u0140\u0144\5l\66\2\u0141\u0144\5j\65\2\u0142\u0144\7a\2\2\u0143"+
		"\u0140\3\2\2\2\u0143\u0141\3\2\2\2\u0143\u0142\3\2\2\2\u0144\u0147\3\2"+
		"\2\2\u0145\u0143\3\2\2\2\u0145\u0146\3\2\2\2\u0146\u0151\3\2\2\2\u0147"+
		"\u0145\3\2\2\2\u0148\u014c\7a\2\2\u0149\u014d\5l\66\2\u014a\u014d\5j\65"+
		"\2\u014b\u014d\7a\2\2\u014c\u0149\3\2\2\2\u014c\u014a\3\2\2\2\u014c\u014b"+
		"\3\2\2\2\u014d\u014e\3\2\2\2\u014e\u014c\3\2\2\2\u014e\u014f\3\2\2\2\u014f"+
		"\u0151\3\2\2\2\u0150\u013f\3\2\2\2\u0150\u0148\3\2\2\2\u0151c\3\2\2\2"+
		"\u0152\u0153\7\61\2\2\u0153\u0154\7\61\2\2\u0154\u0158\3\2\2\2\u0155\u0157"+
		"\n\5\2\2\u0156\u0155\3\2\2\2\u0157\u015a\3\2\2\2\u0158\u0156\3\2\2\2\u0158"+
		"\u0159\3\2\2\2\u0159\u015c\3\2\2\2\u015a\u0158\3\2\2\2\u015b\u015d\7\17"+
		"\2\2\u015c\u015b\3\2\2\2\u015c\u015d\3\2\2\2\u015d\u015f\3\2\2\2\u015e"+
		"\u0160\7\f\2\2\u015f\u015e\3\2\2\2\u015f\u0160\3\2\2\2\u0160\u0161\3\2"+
		"\2\2\u0161\u0162\b\62\3\2\u0162e\3\2\2\2\u0163\u0164\7\61\2\2\u0164\u0165"+
		"\7,\2\2\u0165\u0169\3\2\2\2\u0166\u0168\13\2\2\2\u0167\u0166\3\2\2\2\u0168"+
		"\u016b\3\2\2\2\u0169\u016a\3\2\2\2\u0169\u0167\3\2\2\2\u016a\u016c\3\2"+
		"\2\2\u016b\u0169\3\2\2\2\u016c\u016d\7,\2\2\u016d\u016e\7\61\2\2\u016e"+
		"\u016f\3\2\2\2\u016f\u0170\b\63\3\2\u0170g\3\2\2\2\u0171\u0173\t\6\2\2"+
		"\u0172\u0171\3\2\2\2\u0173\u0174\3\2\2\2\u0174\u0172\3\2\2\2\u0174\u0175"+
		"\3\2\2\2\u0175\u0176\3\2\2\2\u0176\u0177\b\64\3\2\u0177i\3\2\2\2\u0178"+
		"\u0179\t\7\2\2\u0179k\3\2\2\2\u017a\u017b\t\b\2\2\u017bm\3\2\2\2\u017c"+
		"\u017d\7^\2\2\u017d\u017e\t\t\2\2\u017eo\3\2\2\2\u017f\u0181\t\n\2\2\u0180"+
		"\u0182\t\13\2\2\u0181\u0180\3\2\2\2\u0181\u0182\3\2\2\2\u0182\u0184\3"+
		"\2\2\2\u0183\u0185\5j\65\2\u0184\u0183\3\2\2\2\u0185\u0186\3\2\2\2\u0186"+
		"\u0184\3\2\2\2\u0186\u0187\3\2\2\2\u0187q\3\2\2\2\u0188\u018a\7b\2\2\u0189"+
		"\u018b\n\f\2\2\u018a\u0189\3\2\2\2\u018b\u018c\3\2\2\2\u018c\u018a\3\2"+
		"\2\2\u018c\u018d\3\2\2\2\u018d\u018e\3\2\2\2\u018e\u018f\7b\2\2\u018f"+
		"\u0190\3\2\2\2\u0190\u0191\b9\4\2\u0191s\3\2\2\2\u0192\u0194\t\6\2\2\u0193"+
		"\u0192\3\2\2\2\u0194\u0195\3\2\2\2\u0195\u0193\3\2\2\2\u0195\u0196\3\2"+
		"\2\2\u0196\u0197\3\2\2\2\u0197\u0198\b:\3\2\u0198u\3\2\2\2\37\2\3\u0100"+
		"\u0105\u010b\u0111\u0116\u011c\u011e\u0126\u012a\u012f\u0131\u013a\u013c"+
		"\u0143\u0145\u014c\u014e\u0150\u0158\u015c\u015f\u0169\u0174\u0181\u0186"+
		"\u018c\u0195\5\7\3\2\b\2\2\6\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}