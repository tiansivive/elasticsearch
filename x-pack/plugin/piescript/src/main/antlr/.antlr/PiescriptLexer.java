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
		INTEGER_LITERAL=48, DECIMAL_LITERAL=49, QUOTED_STRING=50, UPPER_IDENT=51, 
		LOWER_IDENT=52, LINE_COMMENT=53, MULTILINE_COMMENT=54, WS=55, ESQL_BODY=56, 
		ESQL_WS=57;
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
			"QUERY", "SPAWN_BANG", "SPAWN", "SEND", "WHEN", "USE", "AS", "PAR", "DO", 
			"UNDERSCORE", "PIPE_OP", "OR_OP", "AND_OP", "ARROW", "EQ", "NEQ", "LTE", 
			"GTE", "LT", "GT", "PLUS", "MINUS", "ASTERISK", "SLASH", "PERCENT", "BANG", 
			"DOT", "COMMA", "COLON", "SEMICOLON", "ASSIGN", "AMP", "BAR", "LPAREN", 
			"RPAREN", "LBRACE", "RBRACE", "INTEGER_LITERAL", "DECIMAL_LITERAL", "QUOTED_STRING", 
			"UPPER_IDENT", "LOWER_IDENT", "LINE_COMMENT", "MULTILINE_COMMENT", "WS", 
			"DIGIT", "LETTER", "ESCAPE_SEQUENCE", "EXPONENT", "ESQL_BODY", "ESQL_WS"
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
			"'('", "')'", "'{'", "'}'"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2;\u01b4\b\1\b\1\4"+
		"\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n"+
		"\4\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t"+
		"+\4,\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64"+
		"\t\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t"+
		"=\4>\t>\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\4\3\4\3\4\3\5\3\5\3\5\3\6\3\6\3"+
		"\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t"+
		"\3\t\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f"+
		"\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16"+
		"\3\16\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3\21\3\21\3\21"+
		"\3\21\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3\24\3\24\3\24\3\25\3\25\3\26"+
		"\3\26\3\26\3\27\3\27\3\27\3\30\3\30\3\30\3\31\3\31\3\31\3\32\3\32\3\32"+
		"\3\33\3\33\3\33\3\34\3\34\3\34\3\35\3\35\3\35\3\36\3\36\3\37\3\37\3 \3"+
		" \3!\3!\3\"\3\"\3#\3#\3$\3$\3%\3%\3&\3&\3\'\3\'\3(\3(\3)\3)\3*\3*\3+\3"+
		"+\3,\3,\3-\3-\3.\3.\3/\3/\3\60\3\60\3\61\6\61\u011a\n\61\r\61\16\61\u011b"+
		"\3\62\6\62\u011f\n\62\r\62\16\62\u0120\3\62\3\62\6\62\u0125\n\62\r\62"+
		"\16\62\u0126\3\62\3\62\6\62\u012b\n\62\r\62\16\62\u012c\3\62\6\62\u0130"+
		"\n\62\r\62\16\62\u0131\3\62\3\62\6\62\u0136\n\62\r\62\16\62\u0137\5\62"+
		"\u013a\n\62\3\62\3\62\3\62\3\62\6\62\u0140\n\62\r\62\16\62\u0141\3\62"+
		"\3\62\5\62\u0146\n\62\3\63\3\63\3\63\7\63\u014b\n\63\f\63\16\63\u014e"+
		"\13\63\3\63\3\63\3\64\3\64\3\64\3\64\7\64\u0156\n\64\f\64\16\64\u0159"+
		"\13\64\3\65\3\65\3\65\3\65\7\65\u015f\n\65\f\65\16\65\u0162\13\65\3\65"+
		"\3\65\3\65\3\65\6\65\u0168\n\65\r\65\16\65\u0169\5\65\u016c\n\65\3\66"+
		"\3\66\3\66\3\66\7\66\u0172\n\66\f\66\16\66\u0175\13\66\3\66\5\66\u0178"+
		"\n\66\3\66\5\66\u017b\n\66\3\66\3\66\3\67\3\67\3\67\3\67\7\67\u0183\n"+
		"\67\f\67\16\67\u0186\13\67\3\67\3\67\3\67\3\67\3\67\38\68\u018e\n8\r8"+
		"\168\u018f\38\38\39\39\3:\3:\3;\3;\3;\3<\3<\5<\u019d\n<\3<\6<\u01a0\n"+
		"<\r<\16<\u01a1\3=\3=\6=\u01a6\n=\r=\16=\u01a7\3=\3=\3=\3=\3>\6>\u01af"+
		"\n>\r>\16>\u01b0\3>\3>\3\u0184\2?\4\3\6\4\b\5\n\6\f\7\16\b\20\t\22\n\24"+
		"\13\26\f\30\r\32\16\34\17\36\20 \21\"\22$\23&\24(\25*\26,\27.\30\60\31"+
		"\62\32\64\33\66\348\35:\36<\37> @!B\"D#F$H%J&L\'N(P)R*T+V,X-Z.\\/^\60"+
		"`\61b\62d\63f\64h\65j\66l\67n8p9r\2t\2v\2x\2z:|;\4\2\3\r\6\2\f\f\17\17"+
		"$$^^\3\2C\\\3\2c|\4\2\f\f\17\17\5\2\13\f\17\17\"\"\3\2\62;\4\2C\\c|\7"+
		"\2$$^^ppttvv\4\2GGgg\4\2--//\3\2bb\2\u01ce\2\4\3\2\2\2\2\6\3\2\2\2\2\b"+
		"\3\2\2\2\2\n\3\2\2\2\2\f\3\2\2\2\2\16\3\2\2\2\2\20\3\2\2\2\2\22\3\2\2"+
		"\2\2\24\3\2\2\2\2\26\3\2\2\2\2\30\3\2\2\2\2\32\3\2\2\2\2\34\3\2\2\2\2"+
		"\36\3\2\2\2\2 \3\2\2\2\2\"\3\2\2\2\2$\3\2\2\2\2&\3\2\2\2\2(\3\2\2\2\2"+
		"*\3\2\2\2\2,\3\2\2\2\2.\3\2\2\2\2\60\3\2\2\2\2\62\3\2\2\2\2\64\3\2\2\2"+
		"\2\66\3\2\2\2\28\3\2\2\2\2:\3\2\2\2\2<\3\2\2\2\2>\3\2\2\2\2@\3\2\2\2\2"+
		"B\3\2\2\2\2D\3\2\2\2\2F\3\2\2\2\2H\3\2\2\2\2J\3\2\2\2\2L\3\2\2\2\2N\3"+
		"\2\2\2\2P\3\2\2\2\2R\3\2\2\2\2T\3\2\2\2\2V\3\2\2\2\2X\3\2\2\2\2Z\3\2\2"+
		"\2\2\\\3\2\2\2\2^\3\2\2\2\2`\3\2\2\2\2b\3\2\2\2\2d\3\2\2\2\2f\3\2\2\2"+
		"\2h\3\2\2\2\2j\3\2\2\2\2l\3\2\2\2\2n\3\2\2\2\2p\3\2\2\2\3z\3\2\2\2\3|"+
		"\3\2\2\2\4~\3\2\2\2\6\u0082\3\2\2\2\b\u0085\3\2\2\2\n\u0088\3\2\2\2\f"+
		"\u008b\3\2\2\2\16\u0090\3\2\2\2\20\u0095\3\2\2\2\22\u009a\3\2\2\2\24\u00a0"+
		"\3\2\2\2\26\u00a5\3\2\2\2\30\u00ab\3\2\2\2\32\u00b3\3\2\2\2\34\u00ba\3"+
		"\2\2\2\36\u00c0\3\2\2\2 \u00c5\3\2\2\2\"\u00ca\3\2\2\2$\u00ce\3\2\2\2"+
		"&\u00d1\3\2\2\2(\u00d5\3\2\2\2*\u00d8\3\2\2\2,\u00da\3\2\2\2.\u00dd\3"+
		"\2\2\2\60\u00e0\3\2\2\2\62\u00e3\3\2\2\2\64\u00e6\3\2\2\2\66\u00e9\3\2"+
		"\2\28\u00ec\3\2\2\2:\u00ef\3\2\2\2<\u00f2\3\2\2\2>\u00f4\3\2\2\2@\u00f6"+
		"\3\2\2\2B\u00f8\3\2\2\2D\u00fa\3\2\2\2F\u00fc\3\2\2\2H\u00fe\3\2\2\2J"+
		"\u0100\3\2\2\2L\u0102\3\2\2\2N\u0104\3\2\2\2P\u0106\3\2\2\2R\u0108\3\2"+
		"\2\2T\u010a\3\2\2\2V\u010c\3\2\2\2X\u010e\3\2\2\2Z\u0110\3\2\2\2\\\u0112"+
		"\3\2\2\2^\u0114\3\2\2\2`\u0116\3\2\2\2b\u0119\3\2\2\2d\u0145\3\2\2\2f"+
		"\u0147\3\2\2\2h\u0151\3\2\2\2j\u016b\3\2\2\2l\u016d\3\2\2\2n\u017e\3\2"+
		"\2\2p\u018d\3\2\2\2r\u0193\3\2\2\2t\u0195\3\2\2\2v\u0197\3\2\2\2x\u019a"+
		"\3\2\2\2z\u01a3\3\2\2\2|\u01ae\3\2\2\2~\177\7n\2\2\177\u0080\7g\2\2\u0080"+
		"\u0081\7v\2\2\u0081\5\3\2\2\2\u0082\u0083\7k\2\2\u0083\u0084\7p\2\2\u0084"+
		"\7\3\2\2\2\u0085\u0086\7h\2\2\u0086\u0087\7p\2\2\u0087\t\3\2\2\2\u0088"+
		"\u0089\7k\2\2\u0089\u008a\7h\2\2\u008a\13\3\2\2\2\u008b\u008c\7v\2\2\u008c"+
		"\u008d\7j\2\2\u008d\u008e\7g\2\2\u008e\u008f\7p\2\2\u008f\r\3\2\2\2\u0090"+
		"\u0091\7g\2\2\u0091\u0092\7n\2\2\u0092\u0093\7u\2\2\u0093\u0094\7g\2\2"+
		"\u0094\17\3\2\2\2\u0095\u0096\7v\2\2\u0096\u0097\7t\2\2\u0097\u0098\7"+
		"w\2\2\u0098\u0099\7g\2\2\u0099\21\3\2\2\2\u009a\u009b\7h\2\2\u009b\u009c"+
		"\7c\2\2\u009c\u009d\7n\2\2\u009d\u009e\7u\2\2\u009e\u009f\7g\2\2\u009f"+
		"\23\3\2\2\2\u00a0\u00a1\7p\2\2\u00a1\u00a2\7w\2\2\u00a2\u00a3\7n\2\2\u00a3"+
		"\u00a4\7n\2\2\u00a4\25\3\2\2\2\u00a5\u00a6\7o\2\2\u00a6\u00a7\7c\2\2\u00a7"+
		"\u00a8\7v\2\2\u00a8\u00a9\7e\2\2\u00a9\u00aa\7j\2\2\u00aa\27\3\2\2\2\u00ab"+
		"\u00ac\7s\2\2\u00ac\u00ad\7w\2\2\u00ad\u00ae\7g\2\2\u00ae\u00af\7t\2\2"+
		"\u00af\u00b0\7{\2\2\u00b0\u00b1\3\2\2\2\u00b1\u00b2\b\f\2\2\u00b2\31\3"+
		"\2\2\2\u00b3\u00b4\7u\2\2\u00b4\u00b5\7r\2\2\u00b5\u00b6\7c\2\2\u00b6"+
		"\u00b7\7y\2\2\u00b7\u00b8\7p\2\2\u00b8\u00b9\7#\2\2\u00b9\33\3\2\2\2\u00ba"+
		"\u00bb\7u\2\2\u00bb\u00bc\7r\2\2\u00bc\u00bd\7c\2\2\u00bd\u00be\7y\2\2"+
		"\u00be\u00bf\7p\2\2\u00bf\35\3\2\2\2\u00c0\u00c1\7u\2\2\u00c1\u00c2\7"+
		"g\2\2\u00c2\u00c3\7p\2\2\u00c3\u00c4\7f\2\2\u00c4\37\3\2\2\2\u00c5\u00c6"+
		"\7y\2\2\u00c6\u00c7\7j\2\2\u00c7\u00c8\7g\2\2\u00c8\u00c9\7p\2\2\u00c9"+
		"!\3\2\2\2\u00ca\u00cb\7w\2\2\u00cb\u00cc\7u\2\2\u00cc\u00cd\7g\2\2\u00cd"+
		"#\3\2\2\2\u00ce\u00cf\7c\2\2\u00cf\u00d0\7u\2\2\u00d0%\3\2\2\2\u00d1\u00d2"+
		"\7r\2\2\u00d2\u00d3\7c\2\2\u00d3\u00d4\7t\2\2\u00d4\'\3\2\2\2\u00d5\u00d6"+
		"\7f\2\2\u00d6\u00d7\7q\2\2\u00d7)\3\2\2\2\u00d8\u00d9\7a\2\2\u00d9+\3"+
		"\2\2\2\u00da\u00db\7~\2\2\u00db\u00dc\7@\2\2\u00dc-\3\2\2\2\u00dd\u00de"+
		"\7~\2\2\u00de\u00df\7~\2\2\u00df/\3\2\2\2\u00e0\u00e1\7(\2\2\u00e1\u00e2"+
		"\7(\2\2\u00e2\61\3\2\2\2\u00e3\u00e4\7/\2\2\u00e4\u00e5\7@\2\2\u00e5\63"+
		"\3\2\2\2\u00e6\u00e7\7?\2\2\u00e7\u00e8\7?\2\2\u00e8\65\3\2\2\2\u00e9"+
		"\u00ea\7#\2\2\u00ea\u00eb\7?\2\2\u00eb\67\3\2\2\2\u00ec\u00ed\7>\2\2\u00ed"+
		"\u00ee\7?\2\2\u00ee9\3\2\2\2\u00ef\u00f0\7@\2\2\u00f0\u00f1\7?\2\2\u00f1"+
		";\3\2\2\2\u00f2\u00f3\7>\2\2\u00f3=\3\2\2\2\u00f4\u00f5\7@\2\2\u00f5?"+
		"\3\2\2\2\u00f6\u00f7\7-\2\2\u00f7A\3\2\2\2\u00f8\u00f9\7/\2\2\u00f9C\3"+
		"\2\2\2\u00fa\u00fb\7,\2\2\u00fbE\3\2\2\2\u00fc\u00fd\7\61\2\2\u00fdG\3"+
		"\2\2\2\u00fe\u00ff\7\'\2\2\u00ffI\3\2\2\2\u0100\u0101\7#\2\2\u0101K\3"+
		"\2\2\2\u0102\u0103\7\60\2\2\u0103M\3\2\2\2\u0104\u0105\7.\2\2\u0105O\3"+
		"\2\2\2\u0106\u0107\7<\2\2\u0107Q\3\2\2\2\u0108\u0109\7=\2\2\u0109S\3\2"+
		"\2\2\u010a\u010b\7?\2\2\u010bU\3\2\2\2\u010c\u010d\7(\2\2\u010dW\3\2\2"+
		"\2\u010e\u010f\7~\2\2\u010fY\3\2\2\2\u0110\u0111\7*\2\2\u0111[\3\2\2\2"+
		"\u0112\u0113\7+\2\2\u0113]\3\2\2\2\u0114\u0115\7}\2\2\u0115_\3\2\2\2\u0116"+
		"\u0117\7\177\2\2\u0117a\3\2\2\2\u0118\u011a\5r9\2\u0119\u0118\3\2\2\2"+
		"\u011a\u011b\3\2\2\2\u011b\u0119\3\2\2\2\u011b\u011c\3\2\2\2\u011cc\3"+
		"\2\2\2\u011d\u011f\5r9\2\u011e\u011d\3\2\2\2\u011f\u0120\3\2\2\2\u0120"+
		"\u011e\3\2\2\2\u0120\u0121\3\2\2\2\u0121\u0122\3\2\2\2\u0122\u0124\7\60"+
		"\2\2\u0123\u0125\5r9\2\u0124\u0123\3\2\2\2\u0125\u0126\3\2\2\2\u0126\u0124"+
		"\3\2\2\2\u0126\u0127\3\2\2\2\u0127\u0146\3\2\2\2\u0128\u012a\7\60\2\2"+
		"\u0129\u012b\5r9\2\u012a\u0129\3\2\2\2\u012b\u012c\3\2\2\2\u012c\u012a"+
		"\3\2\2\2\u012c\u012d\3\2\2\2\u012d\u0146\3\2\2\2\u012e\u0130\5r9\2\u012f"+
		"\u012e\3\2\2\2\u0130\u0131\3\2\2\2\u0131\u012f\3\2\2\2\u0131\u0132\3\2"+
		"\2\2\u0132\u0139\3\2\2\2\u0133\u0135\7\60\2\2\u0134\u0136\5r9\2\u0135"+
		"\u0134\3\2\2\2\u0136\u0137\3\2\2\2\u0137\u0135\3\2\2\2\u0137\u0138\3\2"+
		"\2\2\u0138\u013a\3\2\2\2\u0139\u0133\3\2\2\2\u0139\u013a\3\2\2\2\u013a"+
		"\u013b\3\2\2\2\u013b\u013c\5x<\2\u013c\u0146\3\2\2\2\u013d\u013f\7\60"+
		"\2\2\u013e\u0140\5r9\2\u013f\u013e\3\2\2\2\u0140\u0141\3\2\2\2\u0141\u013f"+
		"\3\2\2\2\u0141\u0142\3\2\2\2\u0142\u0143\3\2\2\2\u0143\u0144\5x<\2\u0144"+
		"\u0146\3\2\2\2\u0145\u011e\3\2\2\2\u0145\u0128\3\2\2\2\u0145\u012f\3\2"+
		"\2\2\u0145\u013d\3\2\2\2\u0146e\3\2\2\2\u0147\u014c\7$\2\2\u0148\u014b"+
		"\5v;\2\u0149\u014b\n\2\2\2\u014a\u0148\3\2\2\2\u014a\u0149\3\2\2\2\u014b"+
		"\u014e\3\2\2\2\u014c\u014a\3\2\2\2\u014c\u014d\3\2\2\2\u014d\u014f\3\2"+
		"\2\2\u014e\u014c\3\2\2\2\u014f\u0150\7$\2\2\u0150g\3\2\2\2\u0151\u0157"+
		"\t\3\2\2\u0152\u0156\5t:\2\u0153\u0156\5r9\2\u0154\u0156\7a\2\2\u0155"+
		"\u0152\3\2\2\2\u0155\u0153\3\2\2\2\u0155\u0154\3\2\2\2\u0156\u0159\3\2"+
		"\2\2\u0157\u0155\3\2\2\2\u0157\u0158\3\2\2\2\u0158i\3\2\2\2\u0159\u0157"+
		"\3\2\2\2\u015a\u0160\t\4\2\2\u015b\u015f\5t:\2\u015c\u015f\5r9\2\u015d"+
		"\u015f\7a\2\2\u015e\u015b\3\2\2\2\u015e\u015c\3\2\2\2\u015e\u015d\3\2"+
		"\2\2\u015f\u0162\3\2\2\2\u0160\u015e\3\2\2\2\u0160\u0161\3\2\2\2\u0161"+
		"\u016c\3\2\2\2\u0162\u0160\3\2\2\2\u0163\u0167\7a\2\2\u0164\u0168\5t:"+
		"\2\u0165\u0168\5r9\2\u0166\u0168\7a\2\2\u0167\u0164\3\2\2\2\u0167\u0165"+
		"\3\2\2\2\u0167\u0166\3\2\2\2\u0168\u0169\3\2\2\2\u0169\u0167\3\2\2\2\u0169"+
		"\u016a\3\2\2\2\u016a\u016c\3\2\2\2\u016b\u015a\3\2\2\2\u016b\u0163\3\2"+
		"\2\2\u016ck\3\2\2\2\u016d\u016e\7\61\2\2\u016e\u016f\7\61\2\2\u016f\u0173"+
		"\3\2\2\2\u0170\u0172\n\5\2\2\u0171\u0170\3\2\2\2\u0172\u0175\3\2\2\2\u0173"+
		"\u0171\3\2\2\2\u0173\u0174\3\2\2\2\u0174\u0177\3\2\2\2\u0175\u0173\3\2"+
		"\2\2\u0176\u0178\7\17\2\2\u0177\u0176\3\2\2\2\u0177\u0178\3\2\2\2\u0178"+
		"\u017a\3\2\2\2\u0179\u017b\7\f\2\2\u017a\u0179\3\2\2\2\u017a\u017b\3\2"+
		"\2\2\u017b\u017c\3\2\2\2\u017c\u017d\b\66\3\2\u017dm\3\2\2\2\u017e\u017f"+
		"\7\61\2\2\u017f\u0180\7,\2\2\u0180\u0184\3\2\2\2\u0181\u0183\13\2\2\2"+
		"\u0182\u0181\3\2\2\2\u0183\u0186\3\2\2\2\u0184\u0185\3\2\2\2\u0184\u0182"+
		"\3\2\2\2\u0185\u0187\3\2\2\2\u0186\u0184\3\2\2\2\u0187\u0188\7,\2\2\u0188"+
		"\u0189\7\61\2\2\u0189\u018a\3\2\2\2\u018a\u018b\b\67\3\2\u018bo\3\2\2"+
		"\2\u018c\u018e\t\6\2\2\u018d\u018c\3\2\2\2\u018e\u018f\3\2\2\2\u018f\u018d"+
		"\3\2\2\2\u018f\u0190\3\2\2\2\u0190\u0191\3\2\2\2\u0191\u0192\b8\3\2\u0192"+
		"q\3\2\2\2\u0193\u0194\t\7\2\2\u0194s\3\2\2\2\u0195\u0196\t\b\2\2\u0196"+
		"u\3\2\2\2\u0197\u0198\7^\2\2\u0198\u0199\t\t\2\2\u0199w\3\2\2\2\u019a"+
		"\u019c\t\n\2\2\u019b\u019d\t\13\2\2\u019c\u019b\3\2\2\2\u019c\u019d\3"+
		"\2\2\2\u019d\u019f\3\2\2\2\u019e\u01a0\5r9\2\u019f\u019e\3\2\2\2\u01a0"+
		"\u01a1\3\2\2\2\u01a1\u019f\3\2\2\2\u01a1\u01a2\3\2\2\2\u01a2y\3\2\2\2"+
		"\u01a3\u01a5\7b\2\2\u01a4\u01a6\n\f\2\2\u01a5\u01a4\3\2\2\2\u01a6\u01a7"+
		"\3\2\2\2\u01a7\u01a5\3\2\2\2\u01a7\u01a8\3\2\2\2\u01a8\u01a9\3\2\2\2\u01a9"+
		"\u01aa\7b\2\2\u01aa\u01ab\3\2\2\2\u01ab\u01ac\b=\4\2\u01ac{\3\2\2\2\u01ad"+
		"\u01af\t\6\2\2\u01ae\u01ad\3\2\2\2\u01af\u01b0\3\2\2\2\u01b0\u01ae\3\2"+
		"\2\2\u01b0\u01b1\3\2\2\2\u01b1\u01b2\3\2\2\2\u01b2\u01b3\b>\3\2\u01b3"+
		"}\3\2\2\2\37\2\3\u011b\u0120\u0126\u012c\u0131\u0137\u0139\u0141\u0145"+
		"\u014a\u014c\u0155\u0157\u015e\u0160\u0167\u0169\u016b\u0173\u0177\u017a"+
		"\u0184\u018f\u019c\u01a1\u01a7\u01b0\5\7\3\2\b\2\2\6\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}