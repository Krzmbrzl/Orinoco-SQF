package arma.orinocosqf;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

%%

%init{
	// constructor things in here
%init}


%public %class OrinocoJFlexLexer
// %implements
%unicode
%function advance
%type TokenType
%eof{
    return;
%eof}

%state MACRO_CALL

%{
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
	}
%}

%{
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
%}

WORD = [:jletter:] ([:jletterdigit:])*
GLUED_WORD = ("##")? [:jletter:] ("##" [:jletterdigit:] | [:jletterdigit:])* ("##")?

LINE_TERMINATOR = \n|\r\n|\r
INPUT_CHARACTER = [^\r\n]

WHITE_SPACE = ({LINE_TERMINATOR} | [ \t\f])+

DIGIT = [0-9]
DIGITS = {DIGIT}+

INTEGER_LITERAL = {DIGITS}
DEC_SIGNIFICAND = "." {DIGITS} | {DIGITS} "." {DIGIT}+
DEC_EXPONENT = ({DEC_SIGNIFICAND} | {INTEGER_LITERAL}) [Ee] [+-]? {DIGIT}*
DEC_LITERAL = ({DEC_SIGNIFICAND} | {DEC_EXPONENT})

HEX_LITERAL = [0] [xX] [0]* {HEX_DIGIT} {1,8}
HEX_DIGIT   = [0-9a-fA-F]

STRING_LITERAL = ("\"\""|"\""([^\"]+|\"\")+"\"") | ("''" | "'"([^']+|'')+"'")

BLOCK_COMMENT = "/*" ~"*/"
INLINE_COMMENT = "//" {INPUT_CHARACTER}*

MACRO_CHARACTER = [^\r\n] | (("\\\n" | "\\\r\n" | "\\\r") [ \t\f]*)
MACRO_TEXT = {MACRO_CHARACTER}+
CMD_DEFINE = "#define" {MACRO_TEXT}?
CMD_INCLUDE = "#include" {MACRO_TEXT}?
CMD_IFDEF = "#ifdef" {MACRO_TEXT}?
CMD_IFNDEF = "#ifndef" {MACRO_TEXT}?
CMD_ELSE = "#else" {MACRO_TEXT}?
CMD_ENDIF = "#endif" {MACRO_TEXT}?
CMD_UNDEF = "#undef" {MACRO_TEXT}?

%%


<YYINITIAL> {

	{WHITE_SPACE} { return TokenType.WHITE_SPACE; }
	{CMD_DEFINE} { return TokenType.CMD_DEFINE; }
	{CMD_INCLUDE} { return TokenType.CMD_INCLUDE; }
	{CMD_IFDEF} { return TokenType.CMD_IFDEF; }
	{CMD_IFNDEF} { return TokenType.CMD_IFNDEF; }
	{CMD_ELSE} { return TokenType.CMD_ELSE; }
	{CMD_ENDIF} { return TokenType.CMD_ENDIF; }
	{CMD_UNDEF} { return TokenType.CMD_UNDEF; }

	{BLOCK_COMMENT} { return TokenType.BLOCK_COMMENT; }
	{INLINE_COMMENT} { return TokenType.INLINE_COMMENT; }

	{HEX_LITERAL} { return TokenType.HEX_LITERAL; }
	{INTEGER_LITERAL} { return TokenType.INTEGER_LITERAL; }
	{DEC_LITERAL} { return TokenType.DEC_LITERAL; }
	{STRING_LITERAL} { return TokenType.STRING_LITERAL; }

	{GLUED_WORD} {
		return TokenType.GLUED_WORD;
	}

	{WORD} {
        if(yytextIsCommand()) {
            return TokenType.COMMAND;
        }



        return TokenType.WORD;
    }

		"==" { latestCommandId = EQEQ_id; return TokenType.EQEQ; }
    	"!=" { latestCommandId = NE_id; return TokenType.NE; }
    	">>" { latestCommandId = GTGT_id; return TokenType.GTGT; }
    	"<=" { latestCommandId = LE_id; return TokenType.LE; }
    	">=" { latestCommandId = GE_id; return TokenType.GE; }
    	"&&" { latestCommandId = AMPAMP_id; return TokenType.AMPAMP; }
    	"||" { latestCommandId = BARBAR_id; return TokenType.BARBAR; }

    	"*" { latestCommandId = ASTERISK_id; return TokenType.ASTERISK; }
    	"=" { latestCommandId = EQ_id; return TokenType.EQ; }
    	"%" { latestCommandId = PERC_id; return TokenType.PERC; }
    	"+" { latestCommandId = PLUS_id; return TokenType.PLUS; }
    	"-" { latestCommandId = MINUS_id; return TokenType.MINUS; }
    	"/" { latestCommandId = FSLASH_id; return TokenType.FSLASH; }
    	"^" { latestCommandId = CARET_id; return TokenType.CARET; }

    	"#" { latestCommandId = HASH_id; return TokenType.HASH; }

    	"<" { latestCommandId = LT_id; return TokenType.LT; }
    	">" { latestCommandId = GT_id; return TokenType.GT; }

    	"!" { latestCommandId = EXCL_id; return TokenType.EXCL; }

    	"(" { latestCommandId = LPAREN_id; return TokenType.LPAREN; }
    	")" { latestCommandId = RPAREN_id; return TokenType.RPAREN; }
    	"{" { latestCommandId = L_CURLY_BRACE_id; return TokenType.L_CURLY_BRACE; }
    	"}" { latestCommandId = R_CURLY_BRACE_id; return TokenType.R_CURLY_BRACE; }
    	"[" { latestCommandId = L_SQ_BRACKET_id; return TokenType.L_SQ_BRACKET; }
    	"]" { latestCommandId = R_SQ_BRACKET_id; return TokenType.R_SQ_BRACKET; }
    	"," { latestCommandId = COMMA_id; return TokenType.COMMA; }
    	";" { latestCommandId = SEMICOLON_id; return TokenType.SEMICOLON; }

    	"?" { latestCommandId = QUEST_id; return TokenType.QUEST; }
    	":" { latestCommandId = COLON_id; return TokenType.COLON; }

	. { return TokenType.BAD_CHARACTER; }
}

<<EOF>> {
      	if (!yymoreStreams()) { return TokenType.EOF;}
		yypopStream();
  }