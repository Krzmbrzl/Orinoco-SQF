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

%{
	private CommandSet commands;
  	private final YYTextCharSequence yytextCharSequence = new YYTextCharSequence();

  	public void setCommandSet(@NotNull CommandSet commands) {
		this.commands = commands;
	}

	public boolean yytextIsCommand() {
  		return commands.getId(yytextCharSequence) >= 0;
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

LINE_TERMINATOR = \r|\n|\r\n
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

	"==" { return TokenType.EQEQ; }
	"!=" { return TokenType.NE; }
	">>" { return TokenType.GTGT; }
	"<=" { return TokenType.LE; }
	">=" { return TokenType.GE; }
	"&&" { return TokenType.AMPAMP; }
	"||" { return TokenType.BARBAR; }

	"*" { return TokenType.ASTERISK; }
	"=" { return TokenType.EQ; }
	"%" { return TokenType.PERC; }
	"+" { return TokenType.PLUS; }
	"-" { return TokenType.MINUS; }
	"/" { return TokenType.FSLASH; }
	"^" { return TokenType.CARET; }

	"#" { return TokenType.HASH; }

	"<" { return TokenType.LT; }
	">" { return TokenType.GT; }

	"!" { return TokenType.EXCL; }

	"("   { return TokenType.LPAREN; }
	")"   { return TokenType.RPAREN; }
	"{"   { return TokenType.L_CURLY_BRACE; }
	"}"   { return TokenType.R_CURLY_BRACE; }
	"["   { return TokenType.L_SQ_BRACKET; }
	"]"   { return TokenType.R_SQ_BRACKET; }
	","   { return TokenType.COMMA; }
	";"   { return TokenType.SEMICOLON; }

	"?" { return TokenType.QUEST; }
	":" { return TokenType.COLON; }

	. { return TokenType.BAD_CHARACTER; }
}

<<EOF>> {
      	if (!yymoreStreams()) { return TokenType.EOF;}
		yypopStream();
  }