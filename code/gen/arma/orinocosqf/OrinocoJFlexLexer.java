/* The following code was generated by JFlex 1.7.0 */

package arma.orinocosqf;

import org.jetbrains.annotations.NotNull;
import arma.orinocosqf.preprocessing.MacroSet;


/**
 * This class is a scanner generated by 
 * <a href="http://www.jflex.de/">JFlex</a> 1.7.0
 * from the specification file <tt>/home/bumbi/IdeaProjects/Orinoco-SQF/code/jflex/orinocosqf.flex</tt>
 */
public class OrinocoJFlexLexer {

  /** This character denotes the end of file */
  public static final int YYEOF = -1;

  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int YYINITIAL = 0;
  public static final int MACRO_CALL = 2;

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   *                  at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int ZZ_LEXSTATE[] = { 
     0,  0,  1, 1
  };

  /** 
   * Translates characters to character classes
   */
  private static final String ZZ_CMAP_PACKED = 
    "\11\0\1\6\1\4\1\56\1\6\1\5\22\0\1\6\1\34\1\15"+
    "\1\3\1\1\1\41\1\37\1\16\1\44\1\45\1\20\1\42\1\52"+
    "\1\11\1\7\1\17\1\12\11\2\1\55\1\53\1\36\1\33\1\35"+
    "\1\54\1\0\4\14\1\10\1\14\21\1\1\13\2\1\1\50\1\21"+
    "\1\51\1\43\1\1\1\0\2\14\1\27\1\22\1\23\1\24\2\1"+
    "\1\25\2\1\1\30\1\1\1\26\4\1\1\32\1\1\1\31\2\1"+
    "\1\13\2\1\1\46\1\40\1\47\7\0\1\56\u1fa2\0\1\56\1\56"+
    "\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\udfe6\0";

  /** 
   * Translates characters to character classes
   */
  private static final char [] ZZ_CMAP = zzUnpackCMap(ZZ_CMAP_PACKED);

  /** 
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\2\0\1\1\1\2\1\3\1\4\1\5\1\1\1\6"+
    "\1\3\2\1\1\7\1\10\1\11\1\12\1\13\1\14"+
    "\2\1\1\15\1\16\1\17\1\20\1\21\1\22\1\23"+
    "\1\24\1\25\1\26\1\27\1\30\1\31\1\0\1\32"+
    "\1\0\1\33\5\0\1\33\1\32\1\0\1\34\1\0"+
    "\1\34\1\35\1\0\1\36\1\37\1\40\1\41\1\42"+
    "\1\43\1\44\1\32\2\33\6\0\1\33\2\45\10\0"+
    "\1\45\1\46\2\0\1\47\4\0\1\45\1\0\1\50"+
    "\1\47\1\51\2\0\1\52\1\45\1\53\1\50\1\47"+
    "\1\51\1\54\1\0\1\52\1\45\1\53\1\50\1\51"+
    "\1\54\1\55\1\52\1\45\1\53\1\54\1\55\1\45"+
    "\1\55\1\45";

  private static int [] zzUnpackAction() {
    int [] result = new int[116];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /** 
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\57\0\57\0\136\0\215\0\274\0\353\0\u011a"+
    "\0\57\0\u0149\0\u0178\0\u01a7\0\u01d6\0\57\0\u0205\0\u0234"+
    "\0\u0263\0\u0292\0\u02c1\0\u02f0\0\57\0\57\0\57\0\57"+
    "\0\57\0\57\0\57\0\57\0\57\0\57\0\57\0\57"+
    "\0\57\0\u031f\0\u034e\0\u011a\0\u037d\0\u03ac\0\u03db\0\u040a"+
    "\0\u0439\0\u0468\0\u0497\0\u04c6\0\u0178\0\u04f5\0\u01a7\0\u0524"+
    "\0\u0553\0\u0582\0\57\0\57\0\57\0\57\0\57\0\57"+
    "\0\57\0\u03ac\0\u05b1\0\u05e0\0\u060f\0\u063e\0\u066d\0\u069c"+
    "\0\u06cb\0\u06fa\0\u0729\0\u0758\0\u04c6\0\u0787\0\u07b6\0\u07e5"+
    "\0\u0814\0\u0843\0\u0872\0\u08a1\0\u08d0\0\u08ff\0\57\0\u092e"+
    "\0\u095d\0\u098c\0\u09bb\0\u09ea\0\u0a19\0\u0a48\0\u0a77\0\u0aa6"+
    "\0\u0ad5\0\u0b04\0\u0b33\0\u0b62\0\u0b91\0\u0bc0\0\u0bef\0\u0c1e"+
    "\0\u0c4d\0\u0c7c\0\u0cab\0\u0cda\0\u0d09\0\u0d38\0\u0d67\0\u0d96"+
    "\0\u0dc5\0\u0df4\0\u0e23\0\u0e52\0\u0e81\0\u0eb0\0\u0edf\0\u0f0e"+
    "\0\u0f3d\0\u0f6c\0\u0f9b\0\u034e";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[116];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /** 
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpackTrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\1\3\1\4\1\5\1\6\3\7\1\10\1\4\1\11"+
    "\1\12\2\4\1\13\1\14\1\15\1\16\1\3\11\4"+
    "\1\17\1\20\1\21\1\22\1\23\1\24\1\25\1\26"+
    "\1\27\1\30\1\31\1\32\1\33\1\34\1\35\1\36"+
    "\1\37\1\40\1\41\61\0\2\4\1\42\4\0\1\4"+
    "\1\0\3\4\5\0\11\4\25\0\1\43\1\5\1\42"+
    "\3\0\1\44\1\45\1\0\1\5\2\43\5\0\1\43"+
    "\1\45\7\43\27\0\1\46\16\0\1\47\1\50\1\0"+
    "\1\51\3\0\1\52\31\0\3\7\52\0\1\53\7\0"+
    "\1\53\45\0\1\43\1\5\1\42\3\0\1\44\1\45"+
    "\1\0\1\5\1\54\1\43\5\0\1\43\1\45\7\43"+
    "\24\0\15\55\1\56\41\55\16\57\1\60\40\57\17\0"+
    "\1\61\1\62\71\0\1\63\56\0\1\64\56\0\1\65"+
    "\1\0\1\66\54\0\1\67\62\0\1\70\57\0\1\71"+
    "\21\0\1\72\54\0\2\43\1\42\4\0\1\43\1\0"+
    "\3\43\5\0\11\43\25\0\1\43\1\73\1\42\4\0"+
    "\1\43\1\74\1\73\2\43\5\0\11\43\7\0\1\74"+
    "\15\0\2\43\5\0\1\43\1\0\3\43\5\0\11\43"+
    "\47\0\1\75\61\0\1\76\1\0\1\77\52\0\1\100"+
    "\1\0\1\101\56\0\1\102\32\0\1\53\5\0\1\103"+
    "\1\0\1\53\10\0\1\103\34\0\1\43\1\104\1\42"+
    "\4\0\1\104\1\0\1\105\1\43\1\104\5\0\3\104"+
    "\2\43\1\104\3\43\41\0\1\55\57\0\1\57\40\0"+
    "\4\61\2\0\51\61\20\62\1\106\36\62\1\0\1\43"+
    "\1\73\1\42\4\0\1\43\1\0\1\73\2\43\5\0"+
    "\11\43\26\0\1\74\7\0\1\74\70\0\1\107\54\0"+
    "\1\110\66\0\1\111\46\0\1\112\3\0\1\113\57\0"+
    "\1\114\51\0\1\115\36\0\1\74\6\0\2\74\27\0"+
    "\1\74\15\0\1\43\1\116\1\42\4\0\1\116\1\0"+
    "\1\116\1\43\1\116\5\0\3\116\2\43\1\116\3\43"+
    "\24\0\17\62\1\117\1\106\36\62\25\0\1\120\56\0"+
    "\1\121\54\0\1\122\56\0\1\123\55\0\1\124\64\0"+
    "\1\125\51\0\1\126\34\0\1\43\1\127\1\42\4\0"+
    "\1\127\1\0\1\127\1\43\1\127\5\0\3\127\2\43"+
    "\1\127\3\43\52\0\1\130\54\0\1\131\32\0\4\122"+
    "\2\0\13\122\1\132\35\122\24\0\1\133\55\0\1\134"+
    "\64\0\1\135\51\0\1\136\33\0\1\43\1\137\1\42"+
    "\4\0\1\137\1\0\1\137\1\43\1\137\5\0\3\137"+
    "\2\43\1\137\3\43\47\0\1\140\33\0\4\131\2\0"+
    "\13\131\1\141\35\131\5\122\1\142\13\122\1\132\35\122"+
    "\4\133\2\0\13\133\1\143\35\133\24\0\1\144\54\0"+
    "\1\145\34\0\4\136\2\0\13\136\1\146\35\136\1\0"+
    "\1\43\1\147\1\42\4\0\1\147\1\0\1\147\1\43"+
    "\1\147\5\0\3\147\2\43\1\147\3\43\24\0\4\140"+
    "\2\0\13\140\1\150\35\140\5\131\1\151\13\131\1\141"+
    "\35\131\5\122\1\0\13\122\1\132\35\122\5\133\1\152"+
    "\13\133\1\143\35\133\4\144\2\0\13\144\1\153\35\144"+
    "\23\0\1\154\33\0\5\136\1\155\13\136\1\146\35\136"+
    "\1\0\1\43\1\156\1\42\4\0\1\156\1\0\1\156"+
    "\1\43\1\156\5\0\3\156\2\43\1\156\3\43\24\0"+
    "\5\140\1\157\13\140\1\150\35\140\5\131\1\0\13\131"+
    "\1\141\35\131\5\133\1\0\13\133\1\143\35\133\5\144"+
    "\1\160\13\144\1\153\35\144\4\154\2\0\13\154\1\161"+
    "\35\154\5\136\1\0\13\136\1\146\35\136\1\0\1\43"+
    "\1\162\1\42\4\0\1\162\1\0\1\162\1\43\1\162"+
    "\5\0\3\162\2\43\1\162\3\43\24\0\5\140\1\0"+
    "\13\140\1\150\35\140\5\144\1\0\13\144\1\153\35\144"+
    "\5\154\1\163\13\154\1\161\35\154\1\0\1\43\1\164"+
    "\1\42\4\0\1\164\1\0\1\164\1\43\1\164\5\0"+
    "\3\164\2\43\1\164\3\43\24\0\5\154\1\0\13\154"+
    "\1\161\35\154";

  private static int [] zzUnpackTrans() {
    int [] result = new int[4042];
    int offset = 0;
    offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackTrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /* error codes */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;

  /* error messages for the codes above */
  private static final String ZZ_ERROR_MSG[] = {
    "Unknown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\1\0\1\10\1\11\5\1\1\11\4\1\1\11\6\1"+
    "\15\11\1\0\1\1\1\0\1\1\5\0\2\1\1\0"+
    "\1\1\1\0\2\1\1\0\7\11\3\1\6\0\3\1"+
    "\10\0\1\1\1\11\2\0\1\1\4\0\1\1\1\0"+
    "\3\1\2\0\7\1\1\0\17\1";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[116];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** the input device */
  private java.io.Reader zzReader;

  /** the current state of the DFA */
  private int zzState;

  /** the current lexical state */
  private int zzLexicalState = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private char zzBuffer[] = new char[ZZ_BUFFERSIZE];

  /** the textposition at the last accepting state */
  private int zzMarkedPos;
  
  /** the current text position in the buffer */
  private int zzCurrentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int zzStartRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int zzEndRead;

  /** number of newlines encountered up to the start of the matched text */
  private int yyline;

  /** the number of characters up to the start of the matched text */
  private int yychar;

  /**
   * the number of characters from the last newline up to the start of the 
   * matched text
   */
  private int yycolumn;

  /** 
   * zzAtBOL == true iff the scanner is currently at the beginning of a line
   */
  private boolean zzAtBOL = true;

  /** zzAtEOF == true iff the scanner is at the EOF */
  private boolean zzAtEOF;

  /** denotes if the user-EOF-code has already been executed */
  private boolean zzEOFDone;
  
  /** 
   * The number of occupied positions in zzBuffer beyond zzEndRead.
   * When a lead/high surrogate has been read from the input stream
   * into the final zzBuffer position, this will have a value of 1;
   * otherwise, it will have a value of 0.
   */
  private int zzFinalHighSurrogate = 0;

  /** the stack of open (nested) input streams to read from */
  private java.util.Stack<ZzFlexStreamInfo> zzStreams
    = new java.util.Stack<ZzFlexStreamInfo>();

  /**
   * inner class used to store info for nested
   * input streams
   */
  private static final class ZzFlexStreamInfo {
    java.io.Reader zzReader;
    int zzEndRead;
    int zzStartRead;
    int zzCurrentPos;
    int zzMarkedPos;
    int yyline;
    int yychar;
    int yycolumn;
    char [] zzBuffer;
    boolean zzAtBOL;
    boolean zzAtEOF;
    boolean zzEOFDone;
    int zzFinalHighSurrogate;

    /** sets all values stored in this class */
    ZzFlexStreamInfo(java.io.Reader zzReader, int zzEndRead, int zzStartRead,
                  int zzCurrentPos, int zzMarkedPos, char [] zzBuffer, 
                  boolean zzAtBOL, boolean zzAtEOF, boolean zzEOFDone,
                  int zzFinalHighSurrogate, int yyline, int yychar, int yycolumn) {
      this.zzReader      = zzReader;
      this.zzEndRead     = zzEndRead;
      this.zzStartRead   = zzStartRead;
      this.zzCurrentPos  = zzCurrentPos;
      this.zzMarkedPos   = zzMarkedPos;
      this.zzBuffer      = zzBuffer;
      this.zzAtBOL       = zzAtBOL;
      this.zzAtEOF       = zzAtEOF;
      this.zzEOFDone     = zzEOFDone;
      this.zzFinalHighSurrogate = zzFinalHighSurrogate;
      this.yyline        = yyline;
      this.yychar        = yychar;
      this.yycolumn      = yycolumn;
    }
  }

  /* user code: */
	private final MacroSet macroSet;
	private CommandSet commands;
  	private final YYTextCharSequence yytextCharSequence = new YYTextCharSequence();
	private int latestCommandId = -1;

	private int EQEQ_id;
	private int NE_id;
	private int GTGT_id;
	private int LE_id;
	private int GE_id;
	private int AMPAMP_id;
	private int BARBAR_id;
	private int ASTERISK_id;
	private int EQ_id;
	private int PERC_id;
	private int PLUS_id;
	private int MINUS_id;
	private int FSLASH_id;
	private int CARET_id;
	private int HASH_id;
	private int LT_id;
	private int GT_id;
	private int EXCL_id;
	private int LPAREN_id;
	private int RPAREN_id;
	private int L_CURLY_BRACE_id;
	private int R_CURLY_BRACE_id;
	private int L_SQ_BRACKET_id;
	private int R_SQ_BRACKET_id;
	private int COMMA_id;
	private int SEMICOLON_id;
	private int QUEST_id;
	private int COLON_id;


	public int getLatestCommandId() {
		return latestCommandId;
	}

  	public void setCommandSet(@NotNull CommandSet commands) {
		this.commands = commands;

		{
			EQEQ_id = commands.getId("==");
			NE_id = commands.getId("!=");
			GTGT_id = commands.getId(">>");
			LE_id = commands.getId("<=");
			GE_id = commands.getId(">=");
			AMPAMP_id = commands.getId("&&");
			BARBAR_id = commands.getId("||");
			ASTERISK_id = commands.getId("*");
			EQ_id = commands.getId("=");
			PERC_id = commands.getId("%");
			PLUS_id = commands.getId("+");
			MINUS_id = commands.getId("-");
			FSLASH_id = commands.getId("/");
			CARET_id = commands.getId("^");
			HASH_id = commands.getId("#");
			LT_id = commands.getId("<");
			GT_id = commands.getId(">");
			EXCL_id = commands.getId("!");
			LPAREN_id = commands.getId("(");
			RPAREN_id = commands.getId(")");
			L_CURLY_BRACE_id = commands.getId("{");
			R_CURLY_BRACE_id = commands.getId("}");
			L_SQ_BRACKET_id = commands.getId("[");
			R_SQ_BRACKET_id = commands.getId("]");
			COMMA_id = commands.getId(",");
			SEMICOLON_id = commands.getId(";");
			QUEST_id = commands.getId("?");
			COLON_id = commands.getId(":");
		}

	}

	private boolean yytextIsCommand() {
		latestCommandId = commands.getId(yytextCharSequence);
  		return latestCommandId >= 0;
	}

	private class YYTextCharSequence implements CharSequence{
		public int length() {
			return zzMarkedPos - zzStartRead;
		}
        public char charAt(int i) {
			return zzBuffer[zzStartRead + i];
        }

        public CharSequence subSequence(int startinc, int endex) {
			return new String(zzBuffer, zzStartRead + startinc, zzStartRead + endex);
        }

        @NotNull
        @Override
		public String toString() {
			return new String(zzBuffer, zzStartRead, length());
		}
	}
	public enum TokenType {
		  WHITE_SPACE,

          CMD_DEFINE,
          CMD_INCLUDE,
          CMD_IFDEF,
          CMD_IFNDEF,
          CMD_ELSE,
          CMD_ENDIF,
          CMD_UNDEF,

          BLOCK_COMMENT,
          INLINE_COMMENT,

          HEX_LITERAL,
          INTEGER_LITERAL,
          DEC_LITERAL,
          STRING_LITERAL,

          COMMAND(true),

          MACRO,
          LOCAL_VAR,
          GLUED_WORD,
          WORD,
          EQEQ(true),
          NE(true),
          GTGT(true),
          LE(true),
          GE(true),
          AMPAMP(true),
          BARBAR(true),

          ASTERISK(true),
          EQ(true),
          PERC(true),
          PLUS(true),
          MINUS(true),
          FSLASH(true),
          CARET(true),

          HASH(true),

          LT(true),
          GT(true),

          EXCL(true),

          LPAREN(true),
          RPAREN(true),
          L_CURLY_BRACE(true),
          R_CURLY_BRACE(true),
          L_SQ_BRACKET(true),
          R_SQ_BRACKET(true),
          COMMA(true),
          SEMICOLON(true),

          QUEST(true),
          COLON(true),

          BAD_CHARACTER,
          EOF;

		  public final boolean isCommand;

		  TokenType() {
		  		isCommand = false;
		  }

		  TokenType(boolean isCommand) {
				this.isCommand = isCommand;
		  }
	}


  /**
   * Creates a new scanner
   *
   * @param   in  the java.io.Reader to read input from.
   */
  public OrinocoJFlexLexer(java.io.Reader in, MacroSet macroSet) {
  	this.macroSet = macroSet;
    this.zzReader = in;
  }


  /** 
   * Unpacks the compressed character translation table.
   *
   * @param packed   the packed character translation table
   * @return         the unpacked character translation table
   */
  private static char [] zzUnpackCMap(String packed) {
    char [] map = new char[0x110000];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < 174) {
      int  count = packed.charAt(i++);
      char value = packed.charAt(i++);
      do map[j++] = value; while (--count > 0);
    }
    return map;
  }


  /**
   * Refills the input buffer.
   *
   * @return      <code>false</code>, iff there was new input.
   * 
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  private boolean zzRefill() throws java.io.IOException {

    /* first: make room (if you can) */
    if (zzStartRead > 0) {
      zzEndRead += zzFinalHighSurrogate;
      zzFinalHighSurrogate = 0;
      System.arraycopy(zzBuffer, zzStartRead,
                       zzBuffer, 0,
                       zzEndRead-zzStartRead);

      /* translate stored positions */
      zzEndRead-= zzStartRead;
      zzCurrentPos-= zzStartRead;
      zzMarkedPos-= zzStartRead;
      zzStartRead = 0;
    }

    /* is the buffer big enough? */
    if (zzCurrentPos >= zzBuffer.length - zzFinalHighSurrogate) {
      /* if not: blow it up */
      char newBuffer[] = new char[zzBuffer.length * 2];
      System.arraycopy(zzBuffer, 0, newBuffer, 0, zzBuffer.length);
      zzBuffer = newBuffer;
      zzEndRead += zzFinalHighSurrogate;
      zzFinalHighSurrogate = 0;
    }

    /* fill the buffer with new input */
    int requested = zzBuffer.length - zzEndRead;
    int numRead = zzReader.read(zzBuffer, zzEndRead, requested);

    /* not supposed to occur according to specification of java.io.Reader */
    if (numRead == 0) {
      throw new java.io.IOException("Reader returned 0 characters. See JFlex examples for workaround.");
    }
    if (numRead > 0) {
      zzEndRead += numRead;
      /* If numRead == requested, we might have requested to few chars to
         encode a full Unicode character. We assume that a Reader would
         otherwise never return half characters. */
      if (numRead == requested) {      
        if (Character.isHighSurrogate(zzBuffer[zzEndRead - 1])) {
          --zzEndRead;
          zzFinalHighSurrogate = 1;
        }
      }
      /* potentially more input available */
      return false;
    }

    /* numRead < 0 ==> end of stream */
    return true;
  }

    
  /**
   * Closes the input stream.
   */
  public final void yyclose() throws java.io.IOException {
    zzAtEOF = true;            /* indicate end of file */
    zzEndRead = zzStartRead;  /* invalidate buffer    */

    if (zzReader != null)
      zzReader.close();
  }


  /**
   * Stores the current input stream on a stack, and
   * reads from a new stream. Lexical state, line,
   * char, and column counting remain untouched.
   *
   * The current input stream can be restored with
   * yypopStream (usually in an <<EOF>> action).
   *
   * @param reader the new input stream to read from
   *
   * @see #yypopStream()
   */
  public final void yypushStream(java.io.Reader reader) {
    zzStreams.push(
      new ZzFlexStreamInfo(zzReader, zzEndRead, zzStartRead, zzCurrentPos,
                        zzMarkedPos, zzBuffer, zzAtBOL, zzAtEOF, zzEOFDone,
                        zzFinalHighSurrogate, yyline, yychar, yycolumn)
    );
    zzAtBOL  = true;
    zzAtEOF  = false;
    zzBuffer = new char[ZZ_BUFFERSIZE];
    zzReader = reader;
    zzEndRead = zzStartRead = 0;
    zzCurrentPos = zzMarkedPos = 0;
    zzFinalHighSurrogate = 0;
    yyline = yychar = yycolumn = 0;
  }
    

  /**
   * Closes the current input stream and continues to
   * read from the one on top of the stream stack. 
   *
   * @throws java.util.EmptyStackException
   *         if there is no further stream to read from.
   *
   * @throws java.io.IOException
   *         if there was an error in closing the stream.
   *
   * @see #yypushStream(java.io.Reader)
   */
  public final void yypopStream() throws java.io.IOException {
    zzReader.close();
    ZzFlexStreamInfo s = (ZzFlexStreamInfo) zzStreams.pop();
    zzBuffer      = s.zzBuffer;
    zzReader      = s.zzReader;
    zzEndRead     = s.zzEndRead;
    zzStartRead   = s.zzStartRead;
    zzCurrentPos  = s.zzCurrentPos;
    zzMarkedPos   = s.zzMarkedPos;
    zzAtBOL       = s.zzAtBOL;
    zzAtEOF       = s.zzAtEOF;
    zzEOFDone     = s.zzEOFDone;
    zzFinalHighSurrogate = s.zzFinalHighSurrogate;
    yyline        = s.yyline;
    yychar        = s.yychar;
    yycolumn      = s.yycolumn;
  }


  /**
   * Returns true iff there are still streams left 
   * to read from on the stream stack.
   */
  public final boolean yymoreStreams() {
    return !zzStreams.isEmpty();
  }


  /**
   * Resets the scanner to read from a new input stream.
   * Does not close the old reader.
   *
   * All internal variables are reset, the old input stream 
   * <b>cannot</b> be reused (internal buffer is discarded and lost).
   * Lexical state is set to <tt>ZZ_INITIAL</tt>.
   *
   * Internal scan buffer is resized down to its initial length, if it has grown.
   *
   * @param reader   the new input stream 
   *
   * @see #yypushStream(java.io.Reader)
   * @see #yypopStream()
   */
  public final void yyreset(java.io.Reader reader) {
    zzReader = reader;
    zzAtBOL  = true;
    zzAtEOF  = false;
    zzEOFDone = false;
    zzEndRead = zzStartRead = 0;
    zzCurrentPos = zzMarkedPos = 0;
    zzFinalHighSurrogate = 0;
    yyline = yychar = yycolumn = 0;
    zzLexicalState = YYINITIAL;
    if (zzBuffer.length > ZZ_BUFFERSIZE)
      zzBuffer = new char[ZZ_BUFFERSIZE];
  }

  /**
   * Returns the buffer
   */
  public final char[] getBuffer() {
    return zzBuffer;
  }

  /**
   * Returns the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final String yytext() {
    return new String( zzBuffer, zzStartRead, zzMarkedPos-zzStartRead );
  }


  /**
   * Returns the character at position <tt>pos</tt> from the 
   * matched text. 
   * 
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch. 
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char yycharat(int pos) {
    return zzBuffer[zzStartRead+pos];
  }


  /**
   * Returns the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occured while scanning.
   *
   * In a wellformed scanner (no or only correct usage of 
   * yypushback(int) and a match-all fallback rule) this method 
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  } 


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }


  /**
   * Contains user EOF-code, which will be executed exactly once,
   * when the end of file is reached
   */
  private void zzDoEOF() {
    if (!zzEOFDone) {
      zzEOFDone = true;
        return;

    }
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public TokenType advance() throws java.io.IOException {
    int zzInput;
    int zzAction;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      // cached fields:
      int zzCurrentPosL;
      int zzMarkedPosL = zzMarkedPos;
      int zzEndReadL = zzEndRead;
      char [] zzBufferL = zzBuffer;
      char [] zzCMapL = ZZ_CMAP;

      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;
  
      zzState = ZZ_LEXSTATE[zzLexicalState];

      // set up zzAction for empty match case:
      int zzAttributes = zzAttrL[zzState];
      if ( (zzAttributes & 1) == 1 ) {
        zzAction = zzState;
      }


      zzForAction: {
        while (true) {
    
          if (zzCurrentPosL < zzEndReadL) {
            zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL, zzEndReadL);
            zzCurrentPosL += Character.charCount(zzInput);
          }
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL, zzEndReadL);
              zzCurrentPosL += Character.charCount(zzInput);
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMapL[zzInput] ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
        zzAtEOF = true;
            zzDoEOF();
              {
                if (!yymoreStreams()) { return TokenType.EOF;}
		yypopStream();
              }
      }
      else {
        switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
          case 1: 
            { return TokenType.BAD_CHARACTER;
            } 
            // fall through
          case 46: break;
          case 2: 
            { if(yytextCharSequence.charAt(0) == '_') {
			return TokenType.LOCAL_VAR;
		}
      	if(yytextIsCommand()) {
            return TokenType.COMMAND;
        }

        return TokenType.WORD;
            } 
            // fall through
          case 47: break;
          case 3: 
            { return TokenType.INTEGER_LITERAL;
            } 
            // fall through
          case 48: break;
          case 4: 
            { latestCommandId = HASH_id; return TokenType.HASH;
            } 
            // fall through
          case 49: break;
          case 5: 
            { return TokenType.WHITE_SPACE;
            } 
            // fall through
          case 50: break;
          case 6: 
            { latestCommandId = MINUS_id; return TokenType.MINUS;
            } 
            // fall through
          case 51: break;
          case 7: 
            { latestCommandId = FSLASH_id; return TokenType.FSLASH;
            } 
            // fall through
          case 52: break;
          case 8: 
            { latestCommandId = ASTERISK_id; return TokenType.ASTERISK;
            } 
            // fall through
          case 53: break;
          case 9: 
            { latestCommandId = EQ_id; return TokenType.EQ;
            } 
            // fall through
          case 54: break;
          case 10: 
            { latestCommandId = EXCL_id; return TokenType.EXCL;
            } 
            // fall through
          case 55: break;
          case 11: 
            { latestCommandId = GT_id; return TokenType.GT;
            } 
            // fall through
          case 56: break;
          case 12: 
            { latestCommandId = LT_id; return TokenType.LT;
            } 
            // fall through
          case 57: break;
          case 13: 
            { latestCommandId = PERC_id; return TokenType.PERC;
            } 
            // fall through
          case 58: break;
          case 14: 
            { latestCommandId = PLUS_id; return TokenType.PLUS;
            } 
            // fall through
          case 59: break;
          case 15: 
            { latestCommandId = CARET_id; return TokenType.CARET;
            } 
            // fall through
          case 60: break;
          case 16: 
            { latestCommandId = LPAREN_id; return TokenType.LPAREN;
            } 
            // fall through
          case 61: break;
          case 17: 
            { latestCommandId = RPAREN_id; return TokenType.RPAREN;
            } 
            // fall through
          case 62: break;
          case 18: 
            { latestCommandId = L_CURLY_BRACE_id; return TokenType.L_CURLY_BRACE;
            } 
            // fall through
          case 63: break;
          case 19: 
            { latestCommandId = R_CURLY_BRACE_id; return TokenType.R_CURLY_BRACE;
            } 
            // fall through
          case 64: break;
          case 20: 
            { latestCommandId = L_SQ_BRACKET_id; return TokenType.L_SQ_BRACKET;
            } 
            // fall through
          case 65: break;
          case 21: 
            { latestCommandId = R_SQ_BRACKET_id; return TokenType.R_SQ_BRACKET;
            } 
            // fall through
          case 66: break;
          case 22: 
            { latestCommandId = COMMA_id; return TokenType.COMMA;
            } 
            // fall through
          case 67: break;
          case 23: 
            { latestCommandId = SEMICOLON_id; return TokenType.SEMICOLON;
            } 
            // fall through
          case 68: break;
          case 24: 
            { latestCommandId = QUEST_id; return TokenType.QUEST;
            } 
            // fall through
          case 69: break;
          case 25: 
            { latestCommandId = COLON_id; return TokenType.COLON;
            } 
            // fall through
          case 70: break;
          case 26: 
            { return TokenType.GLUED_WORD;
            } 
            // fall through
          case 71: break;
          case 27: 
            { return TokenType.DEC_LITERAL;
            } 
            // fall through
          case 72: break;
          case 28: 
            { return TokenType.STRING_LITERAL;
            } 
            // fall through
          case 73: break;
          case 29: 
            { return TokenType.INLINE_COMMENT;
            } 
            // fall through
          case 74: break;
          case 30: 
            { latestCommandId = EQEQ_id; return TokenType.EQEQ;
            } 
            // fall through
          case 75: break;
          case 31: 
            { latestCommandId = NE_id; return TokenType.NE;
            } 
            // fall through
          case 76: break;
          case 32: 
            { latestCommandId = GE_id; return TokenType.GE;
            } 
            // fall through
          case 77: break;
          case 33: 
            { latestCommandId = GTGT_id; return TokenType.GTGT;
            } 
            // fall through
          case 78: break;
          case 34: 
            { latestCommandId = LE_id; return TokenType.LE;
            } 
            // fall through
          case 79: break;
          case 35: 
            { latestCommandId = AMPAMP_id; return TokenType.AMPAMP;
            } 
            // fall through
          case 80: break;
          case 36: 
            { latestCommandId = BARBAR_id; return TokenType.BARBAR;
            } 
            // fall through
          case 81: break;
          case 37: 
            { return TokenType.HEX_LITERAL;
            } 
            // fall through
          case 82: break;
          case 38: 
            { return TokenType.BLOCK_COMMENT;
            } 
            // fall through
          case 83: break;
          case 39: 
            { return TokenType.CMD_ELSE;
            } 
            // fall through
          case 84: break;
          case 40: 
            { return TokenType.CMD_ENDIF;
            } 
            // fall through
          case 85: break;
          case 41: 
            { return TokenType.CMD_IFDEF;
            } 
            // fall through
          case 86: break;
          case 42: 
            { return TokenType.CMD_UNDEF;
            } 
            // fall through
          case 87: break;
          case 43: 
            { return TokenType.CMD_DEFINE;
            } 
            // fall through
          case 88: break;
          case 44: 
            { return TokenType.CMD_IFNDEF;
            } 
            // fall through
          case 89: break;
          case 45: 
            { return TokenType.CMD_INCLUDE;
            } 
            // fall through
          case 90: break;
          default:
            zzScanError(ZZ_NO_MATCH);
        }
      }
    }
  }


}
