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
		WS=57, ESQL_BODY=58, ESQL_WS=59;
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
			"RPAREN", "LBRACE", "RBRACE", "LBRACKET", "RBRACKET", "INTEGER_LITERAL", 
			"DECIMAL_LITERAL", "QUOTED_STRING", "UPPER_IDENT", "LOWER_IDENT", "LINE_COMMENT", 
			"MULTILINE_COMMENT", "WS", "DIGIT", "LETTER", "ESCAPE_SEQUENCE", "EXPONENT", 
			"ESQL_BODY", "ESQL_WS"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2=\u01bc\b\1\b\1\4"+
		"\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n"+
		"\4\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t"+
		"+\4,\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64"+
		"\t\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t"+
		"=\4>\t>\4?\t?\4@\t@\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\4\3\4\3\4\3\5\3\5\3"+
		"\5\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\t\3\t"+
		"\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\13\3\f"+
		"\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16"+
		"\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3\21"+
		"\3\21\3\21\3\21\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3\24\3\24\3\24\3\25"+
		"\3\25\3\26\3\26\3\26\3\27\3\27\3\27\3\30\3\30\3\30\3\31\3\31\3\31\3\32"+
		"\3\32\3\32\3\33\3\33\3\33\3\34\3\34\3\34\3\35\3\35\3\35\3\36\3\36\3\37"+
		"\3\37\3 \3 \3!\3!\3\"\3\"\3#\3#\3$\3$\3%\3%\3&\3&\3\'\3\'\3(\3(\3)\3)"+
		"\3*\3*\3+\3+\3,\3,\3-\3-\3.\3.\3/\3/\3\60\3\60\3\61\3\61\3\62\3\62\3\63"+
		"\6\63\u0122\n\63\r\63\16\63\u0123\3\64\6\64\u0127\n\64\r\64\16\64\u0128"+
		"\3\64\3\64\6\64\u012d\n\64\r\64\16\64\u012e\3\64\3\64\6\64\u0133\n\64"+
		"\r\64\16\64\u0134\3\64\6\64\u0138\n\64\r\64\16\64\u0139\3\64\3\64\6\64"+
		"\u013e\n\64\r\64\16\64\u013f\5\64\u0142\n\64\3\64\3\64\3\64\3\64\6\64"+
		"\u0148\n\64\r\64\16\64\u0149\3\64\3\64\5\64\u014e\n\64\3\65\3\65\3\65"+
		"\7\65\u0153\n\65\f\65\16\65\u0156\13\65\3\65\3\65\3\66\3\66\3\66\3\66"+
		"\7\66\u015e\n\66\f\66\16\66\u0161\13\66\3\67\3\67\3\67\3\67\7\67\u0167"+
		"\n\67\f\67\16\67\u016a\13\67\3\67\3\67\3\67\3\67\6\67\u0170\n\67\r\67"+
		"\16\67\u0171\5\67\u0174\n\67\38\38\38\38\78\u017a\n8\f8\168\u017d\138"+
		"\38\58\u0180\n8\38\58\u0183\n8\38\38\39\39\39\39\79\u018b\n9\f9\169\u018e"+
		"\139\39\39\39\39\39\3:\6:\u0196\n:\r:\16:\u0197\3:\3:\3;\3;\3<\3<\3=\3"+
		"=\3=\3>\3>\5>\u01a5\n>\3>\6>\u01a8\n>\r>\16>\u01a9\3?\3?\6?\u01ae\n?\r"+
		"?\16?\u01af\3?\3?\3?\3?\3@\6@\u01b7\n@\r@\16@\u01b8\3@\3@\3\u018c\2A\4"+
		"\3\6\4\b\5\n\6\f\7\16\b\20\t\22\n\24\13\26\f\30\r\32\16\34\17\36\20 \21"+
		"\"\22$\23&\24(\25*\26,\27.\30\60\31\62\32\64\33\66\348\35:\36<\37> @!"+
		"B\"D#F$H%J&L\'N(P)R*T+V,X-Z.\\/^\60`\61b\62d\63f\64h\65j\66l\67n8p9r:"+
		"t;v\2x\2z\2|\2~<\u0080=\4\2\3\r\6\2\f\f\17\17$$^^\3\2C\\\3\2c|\4\2\f\f"+
		"\17\17\5\2\13\f\17\17\"\"\3\2\62;\4\2C\\c|\7\2$$^^ppttvv\4\2GGgg\4\2-"+
		"-//\3\2bb\2\u01d6\2\4\3\2\2\2\2\6\3\2\2\2\2\b\3\2\2\2\2\n\3\2\2\2\2\f"+
		"\3\2\2\2\2\16\3\2\2\2\2\20\3\2\2\2\2\22\3\2\2\2\2\24\3\2\2\2\2\26\3\2"+
		"\2\2\2\30\3\2\2\2\2\32\3\2\2\2\2\34\3\2\2\2\2\36\3\2\2\2\2 \3\2\2\2\2"+
		"\"\3\2\2\2\2$\3\2\2\2\2&\3\2\2\2\2(\3\2\2\2\2*\3\2\2\2\2,\3\2\2\2\2.\3"+
		"\2\2\2\2\60\3\2\2\2\2\62\3\2\2\2\2\64\3\2\2\2\2\66\3\2\2\2\28\3\2\2\2"+
		"\2:\3\2\2\2\2<\3\2\2\2\2>\3\2\2\2\2@\3\2\2\2\2B\3\2\2\2\2D\3\2\2\2\2F"+
		"\3\2\2\2\2H\3\2\2\2\2J\3\2\2\2\2L\3\2\2\2\2N\3\2\2\2\2P\3\2\2\2\2R\3\2"+
		"\2\2\2T\3\2\2\2\2V\3\2\2\2\2X\3\2\2\2\2Z\3\2\2\2\2\\\3\2\2\2\2^\3\2\2"+
		"\2\2`\3\2\2\2\2b\3\2\2\2\2d\3\2\2\2\2f\3\2\2\2\2h\3\2\2\2\2j\3\2\2\2\2"+
		"l\3\2\2\2\2n\3\2\2\2\2p\3\2\2\2\2r\3\2\2\2\2t\3\2\2\2\3~\3\2\2\2\3\u0080"+
		"\3\2\2\2\4\u0082\3\2\2\2\6\u0086\3\2\2\2\b\u0089\3\2\2\2\n\u008c\3\2\2"+
		"\2\f\u008f\3\2\2\2\16\u0094\3\2\2\2\20\u0099\3\2\2\2\22\u009e\3\2\2\2"+
		"\24\u00a4\3\2\2\2\26\u00a9\3\2\2\2\30\u00af\3\2\2\2\32\u00b7\3\2\2\2\34"+
		"\u00be\3\2\2\2\36\u00c4\3\2\2\2 \u00c9\3\2\2\2\"\u00ce\3\2\2\2$\u00d2"+
		"\3\2\2\2&\u00d5\3\2\2\2(\u00d9\3\2\2\2*\u00dc\3\2\2\2,\u00de\3\2\2\2."+
		"\u00e1\3\2\2\2\60\u00e4\3\2\2\2\62\u00e7\3\2\2\2\64\u00ea\3\2\2\2\66\u00ed"+
		"\3\2\2\28\u00f0\3\2\2\2:\u00f3\3\2\2\2<\u00f6\3\2\2\2>\u00f8\3\2\2\2@"+
		"\u00fa\3\2\2\2B\u00fc\3\2\2\2D\u00fe\3\2\2\2F\u0100\3\2\2\2H\u0102\3\2"+
		"\2\2J\u0104\3\2\2\2L\u0106\3\2\2\2N\u0108\3\2\2\2P\u010a\3\2\2\2R\u010c"+
		"\3\2\2\2T\u010e\3\2\2\2V\u0110\3\2\2\2X\u0112\3\2\2\2Z\u0114\3\2\2\2\\"+
		"\u0116\3\2\2\2^\u0118\3\2\2\2`\u011a\3\2\2\2b\u011c\3\2\2\2d\u011e\3\2"+
		"\2\2f\u0121\3\2\2\2h\u014d\3\2\2\2j\u014f\3\2\2\2l\u0159\3\2\2\2n\u0173"+
		"\3\2\2\2p\u0175\3\2\2\2r\u0186\3\2\2\2t\u0195\3\2\2\2v\u019b\3\2\2\2x"+
		"\u019d\3\2\2\2z\u019f\3\2\2\2|\u01a2\3\2\2\2~\u01ab\3\2\2\2\u0080\u01b6"+
		"\3\2\2\2\u0082\u0083\7n\2\2\u0083\u0084\7g\2\2\u0084\u0085\7v\2\2\u0085"+
		"\5\3\2\2\2\u0086\u0087\7k\2\2\u0087\u0088\7p\2\2\u0088\7\3\2\2\2\u0089"+
		"\u008a\7h\2\2\u008a\u008b\7p\2\2\u008b\t\3\2\2\2\u008c\u008d\7k\2\2\u008d"+
		"\u008e\7h\2\2\u008e\13\3\2\2\2\u008f\u0090\7v\2\2\u0090\u0091\7j\2\2\u0091"+
		"\u0092\7g\2\2\u0092\u0093\7p\2\2\u0093\r\3\2\2\2\u0094\u0095\7g\2\2\u0095"+
		"\u0096\7n\2\2\u0096\u0097\7u\2\2\u0097\u0098\7g\2\2\u0098\17\3\2\2\2\u0099"+
		"\u009a\7v\2\2\u009a\u009b\7t\2\2\u009b\u009c\7w\2\2\u009c\u009d\7g\2\2"+
		"\u009d\21\3\2\2\2\u009e\u009f\7h\2\2\u009f\u00a0\7c\2\2\u00a0\u00a1\7"+
		"n\2\2\u00a1\u00a2\7u\2\2\u00a2\u00a3\7g\2\2\u00a3\23\3\2\2\2\u00a4\u00a5"+
		"\7p\2\2\u00a5\u00a6\7w\2\2\u00a6\u00a7\7n\2\2\u00a7\u00a8\7n\2\2\u00a8"+
		"\25\3\2\2\2\u00a9\u00aa\7o\2\2\u00aa\u00ab\7c\2\2\u00ab\u00ac\7v\2\2\u00ac"+
		"\u00ad\7e\2\2\u00ad\u00ae\7j\2\2\u00ae\27\3\2\2\2\u00af\u00b0\7s\2\2\u00b0"+
		"\u00b1\7w\2\2\u00b1\u00b2\7g\2\2\u00b2\u00b3\7t\2\2\u00b3\u00b4\7{\2\2"+
		"\u00b4\u00b5\3\2\2\2\u00b5\u00b6\b\f\2\2\u00b6\31\3\2\2\2\u00b7\u00b8"+
		"\7u\2\2\u00b8\u00b9\7r\2\2\u00b9\u00ba\7c\2\2\u00ba\u00bb\7y\2\2\u00bb"+
		"\u00bc\7p\2\2\u00bc\u00bd\7#\2\2\u00bd\33\3\2\2\2\u00be\u00bf\7u\2\2\u00bf"+
		"\u00c0\7r\2\2\u00c0\u00c1\7c\2\2\u00c1\u00c2\7y\2\2\u00c2\u00c3\7p\2\2"+
		"\u00c3\35\3\2\2\2\u00c4\u00c5\7u\2\2\u00c5\u00c6\7g\2\2\u00c6\u00c7\7"+
		"p\2\2\u00c7\u00c8\7f\2\2\u00c8\37\3\2\2\2\u00c9\u00ca\7y\2\2\u00ca\u00cb"+
		"\7j\2\2\u00cb\u00cc\7g\2\2\u00cc\u00cd\7p\2\2\u00cd!\3\2\2\2\u00ce\u00cf"+
		"\7w\2\2\u00cf\u00d0\7u\2\2\u00d0\u00d1\7g\2\2\u00d1#\3\2\2\2\u00d2\u00d3"+
		"\7c\2\2\u00d3\u00d4\7u\2\2\u00d4%\3\2\2\2\u00d5\u00d6\7r\2\2\u00d6\u00d7"+
		"\7c\2\2\u00d7\u00d8\7t\2\2\u00d8\'\3\2\2\2\u00d9\u00da\7f\2\2\u00da\u00db"+
		"\7q\2\2\u00db)\3\2\2\2\u00dc\u00dd\7a\2\2\u00dd+\3\2\2\2\u00de\u00df\7"+
		"~\2\2\u00df\u00e0\7@\2\2\u00e0-\3\2\2\2\u00e1\u00e2\7~\2\2\u00e2\u00e3"+
		"\7~\2\2\u00e3/\3\2\2\2\u00e4\u00e5\7(\2\2\u00e5\u00e6\7(\2\2\u00e6\61"+
		"\3\2\2\2\u00e7\u00e8\7/\2\2\u00e8\u00e9\7@\2\2\u00e9\63\3\2\2\2\u00ea"+
		"\u00eb\7?\2\2\u00eb\u00ec\7?\2\2\u00ec\65\3\2\2\2\u00ed\u00ee\7#\2\2\u00ee"+
		"\u00ef\7?\2\2\u00ef\67\3\2\2\2\u00f0\u00f1\7>\2\2\u00f1\u00f2\7?\2\2\u00f2"+
		"9\3\2\2\2\u00f3\u00f4\7@\2\2\u00f4\u00f5\7?\2\2\u00f5;\3\2\2\2\u00f6\u00f7"+
		"\7>\2\2\u00f7=\3\2\2\2\u00f8\u00f9\7@\2\2\u00f9?\3\2\2\2\u00fa\u00fb\7"+
		"-\2\2\u00fbA\3\2\2\2\u00fc\u00fd\7/\2\2\u00fdC\3\2\2\2\u00fe\u00ff\7,"+
		"\2\2\u00ffE\3\2\2\2\u0100\u0101\7\61\2\2\u0101G\3\2\2\2\u0102\u0103\7"+
		"\'\2\2\u0103I\3\2\2\2\u0104\u0105\7#\2\2\u0105K\3\2\2\2\u0106\u0107\7"+
		"\60\2\2\u0107M\3\2\2\2\u0108\u0109\7.\2\2\u0109O\3\2\2\2\u010a\u010b\7"+
		"<\2\2\u010bQ\3\2\2\2\u010c\u010d\7=\2\2\u010dS\3\2\2\2\u010e\u010f\7?"+
		"\2\2\u010fU\3\2\2\2\u0110\u0111\7(\2\2\u0111W\3\2\2\2\u0112\u0113\7~\2"+
		"\2\u0113Y\3\2\2\2\u0114\u0115\7*\2\2\u0115[\3\2\2\2\u0116\u0117\7+\2\2"+
		"\u0117]\3\2\2\2\u0118\u0119\7}\2\2\u0119_\3\2\2\2\u011a\u011b\7\177\2"+
		"\2\u011ba\3\2\2\2\u011c\u011d\7]\2\2\u011dc\3\2\2\2\u011e\u011f\7_\2\2"+
		"\u011fe\3\2\2\2\u0120\u0122\5v;\2\u0121\u0120\3\2\2\2\u0122\u0123\3\2"+
		"\2\2\u0123\u0121\3\2\2\2\u0123\u0124\3\2\2\2\u0124g\3\2\2\2\u0125\u0127"+
		"\5v;\2\u0126\u0125\3\2\2\2\u0127\u0128\3\2\2\2\u0128\u0126\3\2\2\2\u0128"+
		"\u0129\3\2\2\2\u0129\u012a\3\2\2\2\u012a\u012c\7\60\2\2\u012b\u012d\5"+
		"v;\2\u012c\u012b\3\2\2\2\u012d\u012e\3\2\2\2\u012e\u012c\3\2\2\2\u012e"+
		"\u012f\3\2\2\2\u012f\u014e\3\2\2\2\u0130\u0132\7\60\2\2\u0131\u0133\5"+
		"v;\2\u0132\u0131\3\2\2\2\u0133\u0134\3\2\2\2\u0134\u0132\3\2\2\2\u0134"+
		"\u0135\3\2\2\2\u0135\u014e\3\2\2\2\u0136\u0138\5v;\2\u0137\u0136\3\2\2"+
		"\2\u0138\u0139\3\2\2\2\u0139\u0137\3\2\2\2\u0139\u013a\3\2\2\2\u013a\u0141"+
		"\3\2\2\2\u013b\u013d\7\60\2\2\u013c\u013e\5v;\2\u013d\u013c\3\2\2\2\u013e"+
		"\u013f\3\2\2\2\u013f\u013d\3\2\2\2\u013f\u0140\3\2\2\2\u0140\u0142\3\2"+
		"\2\2\u0141\u013b\3\2\2\2\u0141\u0142\3\2\2\2\u0142\u0143\3\2\2\2\u0143"+
		"\u0144\5|>\2\u0144\u014e\3\2\2\2\u0145\u0147\7\60\2\2\u0146\u0148\5v;"+
		"\2\u0147\u0146\3\2\2\2\u0148\u0149\3\2\2\2\u0149\u0147\3\2\2\2\u0149\u014a"+
		"\3\2\2\2\u014a\u014b\3\2\2\2\u014b\u014c\5|>\2\u014c\u014e\3\2\2\2\u014d"+
		"\u0126\3\2\2\2\u014d\u0130\3\2\2\2\u014d\u0137\3\2\2\2\u014d\u0145\3\2"+
		"\2\2\u014ei\3\2\2\2\u014f\u0154\7$\2\2\u0150\u0153\5z=\2\u0151\u0153\n"+
		"\2\2\2\u0152\u0150\3\2\2\2\u0152\u0151\3\2\2\2\u0153\u0156\3\2\2\2\u0154"+
		"\u0152\3\2\2\2\u0154\u0155\3\2\2\2\u0155\u0157\3\2\2\2\u0156\u0154\3\2"+
		"\2\2\u0157\u0158\7$\2\2\u0158k\3\2\2\2\u0159\u015f\t\3\2\2\u015a\u015e"+
		"\5x<\2\u015b\u015e\5v;\2\u015c\u015e\7a\2\2\u015d\u015a\3\2\2\2\u015d"+
		"\u015b\3\2\2\2\u015d\u015c\3\2\2\2\u015e\u0161\3\2\2\2\u015f\u015d\3\2"+
		"\2\2\u015f\u0160\3\2\2\2\u0160m\3\2\2\2\u0161\u015f\3\2\2\2\u0162\u0168"+
		"\t\4\2\2\u0163\u0167\5x<\2\u0164\u0167\5v;\2\u0165\u0167\7a\2\2\u0166"+
		"\u0163\3\2\2\2\u0166\u0164\3\2\2\2\u0166\u0165\3\2\2\2\u0167\u016a\3\2"+
		"\2\2\u0168\u0166\3\2\2\2\u0168\u0169\3\2\2\2\u0169\u0174\3\2\2\2\u016a"+
		"\u0168\3\2\2\2\u016b\u016f\7a\2\2\u016c\u0170\5x<\2\u016d\u0170\5v;\2"+
		"\u016e\u0170\7a\2\2\u016f\u016c\3\2\2\2\u016f\u016d\3\2\2\2\u016f\u016e"+
		"\3\2\2\2\u0170\u0171\3\2\2\2\u0171\u016f\3\2\2\2\u0171\u0172\3\2\2\2\u0172"+
		"\u0174\3\2\2\2\u0173\u0162\3\2\2\2\u0173\u016b\3\2\2\2\u0174o\3\2\2\2"+
		"\u0175\u0176\7\61\2\2\u0176\u0177\7\61\2\2\u0177\u017b\3\2\2\2\u0178\u017a"+
		"\n\5\2\2\u0179\u0178\3\2\2\2\u017a\u017d\3\2\2\2\u017b\u0179\3\2\2\2\u017b"+
		"\u017c\3\2\2\2\u017c\u017f\3\2\2\2\u017d\u017b\3\2\2\2\u017e\u0180\7\17"+
		"\2\2\u017f\u017e\3\2\2\2\u017f\u0180\3\2\2\2\u0180\u0182\3\2\2\2\u0181"+
		"\u0183\7\f\2\2\u0182\u0181\3\2\2\2\u0182\u0183\3\2\2\2\u0183\u0184\3\2"+
		"\2\2\u0184\u0185\b8\3\2\u0185q\3\2\2\2\u0186\u0187\7\61\2\2\u0187\u0188"+
		"\7,\2\2\u0188\u018c\3\2\2\2\u0189\u018b\13\2\2\2\u018a\u0189\3\2\2\2\u018b"+
		"\u018e\3\2\2\2\u018c\u018d\3\2\2\2\u018c\u018a\3\2\2\2\u018d\u018f\3\2"+
		"\2\2\u018e\u018c\3\2\2\2\u018f\u0190\7,\2\2\u0190\u0191\7\61\2\2\u0191"+
		"\u0192\3\2\2\2\u0192\u0193\b9\3\2\u0193s\3\2\2\2\u0194\u0196\t\6\2\2\u0195"+
		"\u0194\3\2\2\2\u0196\u0197\3\2\2\2\u0197\u0195\3\2\2\2\u0197\u0198\3\2"+
		"\2\2\u0198\u0199\3\2\2\2\u0199\u019a\b:\3\2\u019au\3\2\2\2\u019b\u019c"+
		"\t\7\2\2\u019cw\3\2\2\2\u019d\u019e\t\b\2\2\u019ey\3\2\2\2\u019f\u01a0"+
		"\7^\2\2\u01a0\u01a1\t\t\2\2\u01a1{\3\2\2\2\u01a2\u01a4\t\n\2\2\u01a3\u01a5"+
		"\t\13\2\2\u01a4\u01a3\3\2\2\2\u01a4\u01a5\3\2\2\2\u01a5\u01a7\3\2\2\2"+
		"\u01a6\u01a8\5v;\2\u01a7\u01a6\3\2\2\2\u01a8\u01a9\3\2\2\2\u01a9\u01a7"+
		"\3\2\2\2\u01a9\u01aa\3\2\2\2\u01aa}\3\2\2\2\u01ab\u01ad\7b\2\2\u01ac\u01ae"+
		"\n\f\2\2\u01ad\u01ac\3\2\2\2\u01ae\u01af\3\2\2\2\u01af\u01ad\3\2\2\2\u01af"+
		"\u01b0\3\2\2\2\u01b0\u01b1\3\2\2\2\u01b1\u01b2\7b\2\2\u01b2\u01b3\3\2"+
		"\2\2\u01b3\u01b4\b?\4\2\u01b4\177\3\2\2\2\u01b5\u01b7\t\6\2\2\u01b6\u01b5"+
		"\3\2\2\2\u01b7\u01b8\3\2\2\2\u01b8\u01b6\3\2\2\2\u01b8\u01b9\3\2\2\2\u01b9"+
		"\u01ba\3\2\2\2\u01ba\u01bb\b@\3\2\u01bb\u0081\3\2\2\2\37\2\3\u0123\u0128"+
		"\u012e\u0134\u0139\u013f\u0141\u0149\u014d\u0152\u0154\u015d\u015f\u0166"+
		"\u0168\u016f\u0171\u0173\u017b\u017f\u0182\u018c\u0197\u01a4\u01a9\u01af"+
		"\u01b8\5\7\3\2\b\2\2\6\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}