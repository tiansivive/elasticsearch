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
		QUERY=11, SPAWN_BANG=12, SPAWN=13, SEND=14, WHEN=15, PAR=16, DO=17, UNDERSCORE=18, 
		PIPE_OP=19, OR_OP=20, AND_OP=21, ARROW=22, EQ=23, NEQ=24, LTE=25, GTE=26, 
		LT=27, GT=28, PLUS=29, MINUS=30, ASTERISK=31, SLASH=32, PERCENT=33, BANG=34, 
		DOT=35, COMMA=36, COLON=37, SEMICOLON=38, ASSIGN=39, AMP=40, BAR=41, LPAREN=42, 
		RPAREN=43, LBRACE=44, RBRACE=45, INTEGER_LITERAL=46, DECIMAL_LITERAL=47, 
		QUOTED_STRING=48, UPPER_IDENT=49, LOWER_IDENT=50, LINE_COMMENT=51, MULTILINE_COMMENT=52, 
		WS=53, ESQL_BODY=54, ESQL_WS=55;
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
			"QUERY", "SPAWN_BANG", "SPAWN", "SEND", "WHEN", "PAR", "DO", "UNDERSCORE", 
			"PIPE_OP", "OR_OP", "AND_OP", "ARROW", "EQ", "NEQ", "LTE", "GTE", "LT", 
			"GT", "PLUS", "MINUS", "ASTERISK", "SLASH", "PERCENT", "BANG", "DOT", 
			"COMMA", "COLON", "SEMICOLON", "ASSIGN", "AMP", "BAR", "LPAREN", "RPAREN", 
			"LBRACE", "RBRACE", "INTEGER_LITERAL", "DECIMAL_LITERAL", "QUOTED_STRING", 
			"UPPER_IDENT", "LOWER_IDENT", "LINE_COMMENT", "MULTILINE_COMMENT", "WS", 
			"DIGIT", "LETTER", "ESCAPE_SEQUENCE", "EXPONENT", "ESQL_BODY", "ESQL_WS"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'let'", "'in'", "'fn'", "'if'", "'then'", "'else'", "'true'", 
			"'false'", "'null'", "'match'", "'query'", "'spawn!'", "'spawn'", "'send'", 
			"'when'", "'par'", "'do'", "'_'", "'|>'", "'||'", "'&&'", "'->'", "'=='", 
			"'!='", "'<='", "'>='", "'<'", "'>'", "'+'", "'-'", "'*'", "'/'", "'%'", 
			"'!'", "'.'", "','", "':'", "';'", "'='", "'&'", "'|'", "'('", "')'", 
			"'{'", "'}'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "LET", "IN", "FN", "IF", "THEN", "ELSE", "TRUE", "FALSE", "NULL", 
			"MATCH", "QUERY", "SPAWN_BANG", "SPAWN", "SEND", "WHEN", "PAR", "DO", 
			"UNDERSCORE", "PIPE_OP", "OR_OP", "AND_OP", "ARROW", "EQ", "NEQ", "LTE", 
			"GTE", "LT", "GT", "PLUS", "MINUS", "ASTERISK", "SLASH", "PERCENT", "BANG", 
			"DOT", "COMMA", "COLON", "SEMICOLON", "ASSIGN", "AMP", "BAR", "LPAREN", 
			"RPAREN", "LBRACE", "RBRACE", "INTEGER_LITERAL", "DECIMAL_LITERAL", "QUOTED_STRING", 
			"UPPER_IDENT", "LOWER_IDENT", "LINE_COMMENT", "MULTILINE_COMMENT", "WS", 
			"ESQL_BODY", "ESQL_WS"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\29\u01a9\b\1\b\1\4"+
		"\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n"+
		"\4\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t"+
		"+\4,\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64"+
		"\t\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\3\2"+
		"\3\2\3\2\3\2\3\3\3\3\3\3\3\4\3\4\3\4\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3"+
		"\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n"+
		"\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f"+
		"\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3\17\3"+
		"\17\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\22\3"+
		"\22\3\22\3\23\3\23\3\24\3\24\3\24\3\25\3\25\3\25\3\26\3\26\3\26\3\27\3"+
		"\27\3\27\3\30\3\30\3\30\3\31\3\31\3\31\3\32\3\32\3\32\3\33\3\33\3\33\3"+
		"\34\3\34\3\35\3\35\3\36\3\36\3\37\3\37\3 \3 \3!\3!\3\"\3\"\3#\3#\3$\3"+
		"$\3%\3%\3&\3&\3\'\3\'\3(\3(\3)\3)\3*\3*\3+\3+\3,\3,\3-\3-\3.\3.\3/\6/"+
		"\u010f\n/\r/\16/\u0110\3\60\6\60\u0114\n\60\r\60\16\60\u0115\3\60\3\60"+
		"\6\60\u011a\n\60\r\60\16\60\u011b\3\60\3\60\6\60\u0120\n\60\r\60\16\60"+
		"\u0121\3\60\6\60\u0125\n\60\r\60\16\60\u0126\3\60\3\60\6\60\u012b\n\60"+
		"\r\60\16\60\u012c\5\60\u012f\n\60\3\60\3\60\3\60\3\60\6\60\u0135\n\60"+
		"\r\60\16\60\u0136\3\60\3\60\5\60\u013b\n\60\3\61\3\61\3\61\7\61\u0140"+
		"\n\61\f\61\16\61\u0143\13\61\3\61\3\61\3\62\3\62\3\62\3\62\7\62\u014b"+
		"\n\62\f\62\16\62\u014e\13\62\3\63\3\63\3\63\3\63\7\63\u0154\n\63\f\63"+
		"\16\63\u0157\13\63\3\63\3\63\3\63\3\63\6\63\u015d\n\63\r\63\16\63\u015e"+
		"\5\63\u0161\n\63\3\64\3\64\3\64\3\64\7\64\u0167\n\64\f\64\16\64\u016a"+
		"\13\64\3\64\5\64\u016d\n\64\3\64\5\64\u0170\n\64\3\64\3\64\3\65\3\65\3"+
		"\65\3\65\7\65\u0178\n\65\f\65\16\65\u017b\13\65\3\65\3\65\3\65\3\65\3"+
		"\65\3\66\6\66\u0183\n\66\r\66\16\66\u0184\3\66\3\66\3\67\3\67\38\38\3"+
		"9\39\39\3:\3:\5:\u0192\n:\3:\6:\u0195\n:\r:\16:\u0196\3;\3;\6;\u019b\n"+
		";\r;\16;\u019c\3;\3;\3;\3;\3<\6<\u01a4\n<\r<\16<\u01a5\3<\3<\3\u0179\2"+
		"=\4\3\6\4\b\5\n\6\f\7\16\b\20\t\22\n\24\13\26\f\30\r\32\16\34\17\36\20"+
		" \21\"\22$\23&\24(\25*\26,\27.\30\60\31\62\32\64\33\66\348\35:\36<\37"+
		"> @!B\"D#F$H%J&L\'N(P)R*T+V,X-Z.\\/^\60`\61b\62d\63f\64h\65j\66l\67n\2"+
		"p\2r\2t\2v8x9\4\2\3\r\6\2\f\f\17\17$$^^\3\2C\\\3\2c|\4\2\f\f\17\17\5\2"+
		"\13\f\17\17\"\"\3\2\62;\4\2C\\c|\7\2$$^^ppttvv\4\2GGgg\4\2--//\3\2bb\2"+
		"\u01c3\2\4\3\2\2\2\2\6\3\2\2\2\2\b\3\2\2\2\2\n\3\2\2\2\2\f\3\2\2\2\2\16"+
		"\3\2\2\2\2\20\3\2\2\2\2\22\3\2\2\2\2\24\3\2\2\2\2\26\3\2\2\2\2\30\3\2"+
		"\2\2\2\32\3\2\2\2\2\34\3\2\2\2\2\36\3\2\2\2\2 \3\2\2\2\2\"\3\2\2\2\2$"+
		"\3\2\2\2\2&\3\2\2\2\2(\3\2\2\2\2*\3\2\2\2\2,\3\2\2\2\2.\3\2\2\2\2\60\3"+
		"\2\2\2\2\62\3\2\2\2\2\64\3\2\2\2\2\66\3\2\2\2\28\3\2\2\2\2:\3\2\2\2\2"+
		"<\3\2\2\2\2>\3\2\2\2\2@\3\2\2\2\2B\3\2\2\2\2D\3\2\2\2\2F\3\2\2\2\2H\3"+
		"\2\2\2\2J\3\2\2\2\2L\3\2\2\2\2N\3\2\2\2\2P\3\2\2\2\2R\3\2\2\2\2T\3\2\2"+
		"\2\2V\3\2\2\2\2X\3\2\2\2\2Z\3\2\2\2\2\\\3\2\2\2\2^\3\2\2\2\2`\3\2\2\2"+
		"\2b\3\2\2\2\2d\3\2\2\2\2f\3\2\2\2\2h\3\2\2\2\2j\3\2\2\2\2l\3\2\2\2\3v"+
		"\3\2\2\2\3x\3\2\2\2\4z\3\2\2\2\6~\3\2\2\2\b\u0081\3\2\2\2\n\u0084\3\2"+
		"\2\2\f\u0087\3\2\2\2\16\u008c\3\2\2\2\20\u0091\3\2\2\2\22\u0096\3\2\2"+
		"\2\24\u009c\3\2\2\2\26\u00a1\3\2\2\2\30\u00a7\3\2\2\2\32\u00af\3\2\2\2"+
		"\34\u00b6\3\2\2\2\36\u00bc\3\2\2\2 \u00c1\3\2\2\2\"\u00c6\3\2\2\2$\u00ca"+
		"\3\2\2\2&\u00cd\3\2\2\2(\u00cf\3\2\2\2*\u00d2\3\2\2\2,\u00d5\3\2\2\2."+
		"\u00d8\3\2\2\2\60\u00db\3\2\2\2\62\u00de\3\2\2\2\64\u00e1\3\2\2\2\66\u00e4"+
		"\3\2\2\28\u00e7\3\2\2\2:\u00e9\3\2\2\2<\u00eb\3\2\2\2>\u00ed\3\2\2\2@"+
		"\u00ef\3\2\2\2B\u00f1\3\2\2\2D\u00f3\3\2\2\2F\u00f5\3\2\2\2H\u00f7\3\2"+
		"\2\2J\u00f9\3\2\2\2L\u00fb\3\2\2\2N\u00fd\3\2\2\2P\u00ff\3\2\2\2R\u0101"+
		"\3\2\2\2T\u0103\3\2\2\2V\u0105\3\2\2\2X\u0107\3\2\2\2Z\u0109\3\2\2\2\\"+
		"\u010b\3\2\2\2^\u010e\3\2\2\2`\u013a\3\2\2\2b\u013c\3\2\2\2d\u0146\3\2"+
		"\2\2f\u0160\3\2\2\2h\u0162\3\2\2\2j\u0173\3\2\2\2l\u0182\3\2\2\2n\u0188"+
		"\3\2\2\2p\u018a\3\2\2\2r\u018c\3\2\2\2t\u018f\3\2\2\2v\u0198\3\2\2\2x"+
		"\u01a3\3\2\2\2z{\7n\2\2{|\7g\2\2|}\7v\2\2}\5\3\2\2\2~\177\7k\2\2\177\u0080"+
		"\7p\2\2\u0080\7\3\2\2\2\u0081\u0082\7h\2\2\u0082\u0083\7p\2\2\u0083\t"+
		"\3\2\2\2\u0084\u0085\7k\2\2\u0085\u0086\7h\2\2\u0086\13\3\2\2\2\u0087"+
		"\u0088\7v\2\2\u0088\u0089\7j\2\2\u0089\u008a\7g\2\2\u008a\u008b\7p\2\2"+
		"\u008b\r\3\2\2\2\u008c\u008d\7g\2\2\u008d\u008e\7n\2\2\u008e\u008f\7u"+
		"\2\2\u008f\u0090\7g\2\2\u0090\17\3\2\2\2\u0091\u0092\7v\2\2\u0092\u0093"+
		"\7t\2\2\u0093\u0094\7w\2\2\u0094\u0095\7g\2\2\u0095\21\3\2\2\2\u0096\u0097"+
		"\7h\2\2\u0097\u0098\7c\2\2\u0098\u0099\7n\2\2\u0099\u009a\7u\2\2\u009a"+
		"\u009b\7g\2\2\u009b\23\3\2\2\2\u009c\u009d\7p\2\2\u009d\u009e\7w\2\2\u009e"+
		"\u009f\7n\2\2\u009f\u00a0\7n\2\2\u00a0\25\3\2\2\2\u00a1\u00a2\7o\2\2\u00a2"+
		"\u00a3\7c\2\2\u00a3\u00a4\7v\2\2\u00a4\u00a5\7e\2\2\u00a5\u00a6\7j\2\2"+
		"\u00a6\27\3\2\2\2\u00a7\u00a8\7s\2\2\u00a8\u00a9\7w\2\2\u00a9\u00aa\7"+
		"g\2\2\u00aa\u00ab\7t\2\2\u00ab\u00ac\7{\2\2\u00ac\u00ad\3\2\2\2\u00ad"+
		"\u00ae\b\f\2\2\u00ae\31\3\2\2\2\u00af\u00b0\7u\2\2\u00b0\u00b1\7r\2\2"+
		"\u00b1\u00b2\7c\2\2\u00b2\u00b3\7y\2\2\u00b3\u00b4\7p\2\2\u00b4\u00b5"+
		"\7#\2\2\u00b5\33\3\2\2\2\u00b6\u00b7\7u\2\2\u00b7\u00b8\7r\2\2\u00b8\u00b9"+
		"\7c\2\2\u00b9\u00ba\7y\2\2\u00ba\u00bb\7p\2\2\u00bb\35\3\2\2\2\u00bc\u00bd"+
		"\7u\2\2\u00bd\u00be\7g\2\2\u00be\u00bf\7p\2\2\u00bf\u00c0\7f\2\2\u00c0"+
		"\37\3\2\2\2\u00c1\u00c2\7y\2\2\u00c2\u00c3\7j\2\2\u00c3\u00c4\7g\2\2\u00c4"+
		"\u00c5\7p\2\2\u00c5!\3\2\2\2\u00c6\u00c7\7r\2\2\u00c7\u00c8\7c\2\2\u00c8"+
		"\u00c9\7t\2\2\u00c9#\3\2\2\2\u00ca\u00cb\7f\2\2\u00cb\u00cc\7q\2\2\u00cc"+
		"%\3\2\2\2\u00cd\u00ce\7a\2\2\u00ce\'\3\2\2\2\u00cf\u00d0\7~\2\2\u00d0"+
		"\u00d1\7@\2\2\u00d1)\3\2\2\2\u00d2\u00d3\7~\2\2\u00d3\u00d4\7~\2\2\u00d4"+
		"+\3\2\2\2\u00d5\u00d6\7(\2\2\u00d6\u00d7\7(\2\2\u00d7-\3\2\2\2\u00d8\u00d9"+
		"\7/\2\2\u00d9\u00da\7@\2\2\u00da/\3\2\2\2\u00db\u00dc\7?\2\2\u00dc\u00dd"+
		"\7?\2\2\u00dd\61\3\2\2\2\u00de\u00df\7#\2\2\u00df\u00e0\7?\2\2\u00e0\63"+
		"\3\2\2\2\u00e1\u00e2\7>\2\2\u00e2\u00e3\7?\2\2\u00e3\65\3\2\2\2\u00e4"+
		"\u00e5\7@\2\2\u00e5\u00e6\7?\2\2\u00e6\67\3\2\2\2\u00e7\u00e8\7>\2\2\u00e8"+
		"9\3\2\2\2\u00e9\u00ea\7@\2\2\u00ea;\3\2\2\2\u00eb\u00ec\7-\2\2\u00ec="+
		"\3\2\2\2\u00ed\u00ee\7/\2\2\u00ee?\3\2\2\2\u00ef\u00f0\7,\2\2\u00f0A\3"+
		"\2\2\2\u00f1\u00f2\7\61\2\2\u00f2C\3\2\2\2\u00f3\u00f4\7\'\2\2\u00f4E"+
		"\3\2\2\2\u00f5\u00f6\7#\2\2\u00f6G\3\2\2\2\u00f7\u00f8\7\60\2\2\u00f8"+
		"I\3\2\2\2\u00f9\u00fa\7.\2\2\u00faK\3\2\2\2\u00fb\u00fc\7<\2\2\u00fcM"+
		"\3\2\2\2\u00fd\u00fe\7=\2\2\u00feO\3\2\2\2\u00ff\u0100\7?\2\2\u0100Q\3"+
		"\2\2\2\u0101\u0102\7(\2\2\u0102S\3\2\2\2\u0103\u0104\7~\2\2\u0104U\3\2"+
		"\2\2\u0105\u0106\7*\2\2\u0106W\3\2\2\2\u0107\u0108\7+\2\2\u0108Y\3\2\2"+
		"\2\u0109\u010a\7}\2\2\u010a[\3\2\2\2\u010b\u010c\7\177\2\2\u010c]\3\2"+
		"\2\2\u010d\u010f\5n\67\2\u010e\u010d\3\2\2\2\u010f\u0110\3\2\2\2\u0110"+
		"\u010e\3\2\2\2\u0110\u0111\3\2\2\2\u0111_\3\2\2\2\u0112\u0114\5n\67\2"+
		"\u0113\u0112\3\2\2\2\u0114\u0115\3\2\2\2\u0115\u0113\3\2\2\2\u0115\u0116"+
		"\3\2\2\2\u0116\u0117\3\2\2\2\u0117\u0119\7\60\2\2\u0118\u011a\5n\67\2"+
		"\u0119\u0118\3\2\2\2\u011a\u011b\3\2\2\2\u011b\u0119\3\2\2\2\u011b\u011c"+
		"\3\2\2\2\u011c\u013b\3\2\2\2\u011d\u011f\7\60\2\2\u011e\u0120\5n\67\2"+
		"\u011f\u011e\3\2\2\2\u0120\u0121\3\2\2\2\u0121\u011f\3\2\2\2\u0121\u0122"+
		"\3\2\2\2\u0122\u013b\3\2\2\2\u0123\u0125\5n\67\2\u0124\u0123\3\2\2\2\u0125"+
		"\u0126\3\2\2\2\u0126\u0124\3\2\2\2\u0126\u0127\3\2\2\2\u0127\u012e\3\2"+
		"\2\2\u0128\u012a\7\60\2\2\u0129\u012b\5n\67\2\u012a\u0129\3\2\2\2\u012b"+
		"\u012c\3\2\2\2\u012c\u012a\3\2\2\2\u012c\u012d\3\2\2\2\u012d\u012f\3\2"+
		"\2\2\u012e\u0128\3\2\2\2\u012e\u012f\3\2\2\2\u012f\u0130\3\2\2\2\u0130"+
		"\u0131\5t:\2\u0131\u013b\3\2\2\2\u0132\u0134\7\60\2\2\u0133\u0135\5n\67"+
		"\2\u0134\u0133\3\2\2\2\u0135\u0136\3\2\2\2\u0136\u0134\3\2\2\2\u0136\u0137"+
		"\3\2\2\2\u0137\u0138\3\2\2\2\u0138\u0139\5t:\2\u0139\u013b\3\2\2\2\u013a"+
		"\u0113\3\2\2\2\u013a\u011d\3\2\2\2\u013a\u0124\3\2\2\2\u013a\u0132\3\2"+
		"\2\2\u013ba\3\2\2\2\u013c\u0141\7$\2\2\u013d\u0140\5r9\2\u013e\u0140\n"+
		"\2\2\2\u013f\u013d\3\2\2\2\u013f\u013e\3\2\2\2\u0140\u0143\3\2\2\2\u0141"+
		"\u013f\3\2\2\2\u0141\u0142\3\2\2\2\u0142\u0144\3\2\2\2\u0143\u0141\3\2"+
		"\2\2\u0144\u0145\7$\2\2\u0145c\3\2\2\2\u0146\u014c\t\3\2\2\u0147\u014b"+
		"\5p8\2\u0148\u014b\5n\67\2\u0149\u014b\7a\2\2\u014a\u0147\3\2\2\2\u014a"+
		"\u0148\3\2\2\2\u014a\u0149\3\2\2\2\u014b\u014e\3\2\2\2\u014c\u014a\3\2"+
		"\2\2\u014c\u014d\3\2\2\2\u014de\3\2\2\2\u014e\u014c\3\2\2\2\u014f\u0155"+
		"\t\4\2\2\u0150\u0154\5p8\2\u0151\u0154\5n\67\2\u0152\u0154\7a\2\2\u0153"+
		"\u0150\3\2\2\2\u0153\u0151\3\2\2\2\u0153\u0152\3\2\2\2\u0154\u0157\3\2"+
		"\2\2\u0155\u0153\3\2\2\2\u0155\u0156\3\2\2\2\u0156\u0161\3\2\2\2\u0157"+
		"\u0155\3\2\2\2\u0158\u015c\7a\2\2\u0159\u015d\5p8\2\u015a\u015d\5n\67"+
		"\2\u015b\u015d\7a\2\2\u015c\u0159\3\2\2\2\u015c\u015a\3\2\2\2\u015c\u015b"+
		"\3\2\2\2\u015d\u015e\3\2\2\2\u015e\u015c\3\2\2\2\u015e\u015f\3\2\2\2\u015f"+
		"\u0161\3\2\2\2\u0160\u014f\3\2\2\2\u0160\u0158\3\2\2\2\u0161g\3\2\2\2"+
		"\u0162\u0163\7\61\2\2\u0163\u0164\7\61\2\2\u0164\u0168\3\2\2\2\u0165\u0167"+
		"\n\5\2\2\u0166\u0165\3\2\2\2\u0167\u016a\3\2\2\2\u0168\u0166\3\2\2\2\u0168"+
		"\u0169\3\2\2\2\u0169\u016c\3\2\2\2\u016a\u0168\3\2\2\2\u016b\u016d\7\17"+
		"\2\2\u016c\u016b\3\2\2\2\u016c\u016d\3\2\2\2\u016d\u016f\3\2\2\2\u016e"+
		"\u0170\7\f\2\2\u016f\u016e\3\2\2\2\u016f\u0170\3\2\2\2\u0170\u0171\3\2"+
		"\2\2\u0171\u0172\b\64\3\2\u0172i\3\2\2\2\u0173\u0174\7\61\2\2\u0174\u0175"+
		"\7,\2\2\u0175\u0179\3\2\2\2\u0176\u0178\13\2\2\2\u0177\u0176\3\2\2\2\u0178"+
		"\u017b\3\2\2\2\u0179\u017a\3\2\2\2\u0179\u0177\3\2\2\2\u017a\u017c\3\2"+
		"\2\2\u017b\u0179\3\2\2\2\u017c\u017d\7,\2\2\u017d\u017e\7\61\2\2\u017e"+
		"\u017f\3\2\2\2\u017f\u0180\b\65\3\2\u0180k\3\2\2\2\u0181\u0183\t\6\2\2"+
		"\u0182\u0181\3\2\2\2\u0183\u0184\3\2\2\2\u0184\u0182\3\2\2\2\u0184\u0185"+
		"\3\2\2\2\u0185\u0186\3\2\2\2\u0186\u0187\b\66\3\2\u0187m\3\2\2\2\u0188"+
		"\u0189\t\7\2\2\u0189o\3\2\2\2\u018a\u018b\t\b\2\2\u018bq\3\2\2\2\u018c"+
		"\u018d\7^\2\2\u018d\u018e\t\t\2\2\u018es\3\2\2\2\u018f\u0191\t\n\2\2\u0190"+
		"\u0192\t\13\2\2\u0191\u0190\3\2\2\2\u0191\u0192\3\2\2\2\u0192\u0194\3"+
		"\2\2\2\u0193\u0195\5n\67\2\u0194\u0193\3\2\2\2\u0195\u0196\3\2\2\2\u0196"+
		"\u0194\3\2\2\2\u0196\u0197\3\2\2\2\u0197u\3\2\2\2\u0198\u019a\7b\2\2\u0199"+
		"\u019b\n\f\2\2\u019a\u0199\3\2\2\2\u019b\u019c\3\2\2\2\u019c\u019a\3\2"+
		"\2\2\u019c\u019d\3\2\2\2\u019d\u019e\3\2\2\2\u019e\u019f\7b\2\2\u019f"+
		"\u01a0\3\2\2\2\u01a0\u01a1\b;\4\2\u01a1w\3\2\2\2\u01a2\u01a4\t\6\2\2\u01a3"+
		"\u01a2\3\2\2\2\u01a4\u01a5\3\2\2\2\u01a5\u01a3\3\2\2\2\u01a5\u01a6\3\2"+
		"\2\2\u01a6\u01a7\3\2\2\2\u01a7\u01a8\b<\3\2\u01a8y\3\2\2\2\37\2\3\u0110"+
		"\u0115\u011b\u0121\u0126\u012c\u012e\u0136\u013a\u013f\u0141\u014a\u014c"+
		"\u0153\u0155\u015c\u015e\u0160\u0168\u016c\u016f\u0179\u0184\u0191\u0196"+
		"\u019c\u01a5\5\7\3\2\b\2\2\6\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}