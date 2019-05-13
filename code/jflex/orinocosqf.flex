package arma.orinocosqf;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import arma.orinocosqf.preprocessing.MacroSet;
import arma.orinocosqf.HashableCharSequence;

%%

%ctorarg MacroSet macroSet

%init{
	this.macroSet = macroSet;
%init}


%public %class OrinocoJFlexLexer
// %implements
%unicode
%function advance
%type TokenType
%eof{
    return;
%eof}

%include orinocosqf_javaheader

LETTER = [a-zA-Z] //can't start with $ because of hex numbers
LETTER_DIGIT = [$a-zA-Z_$0-9]
WORD = {LETTER} {LETTER_DIGIT}*
GLUED_WORD = ("##")? {LETTER_DIGIT} ("##" {LETTER_DIGIT} | {LETTER_DIGIT})* ("##")?

LINE_TERMINATOR = \n|\r\n|\r
INPUT_CHARACTER = [^\r\n]

WHITE_SPACE = ({LINE_TERMINATOR} | [ \t\f])+

DIGIT = [0-9]
DIGITS = {DIGIT}+

INTEGER_LITERAL = {DIGITS}
DEC_SIGNIFICAND = "." {DIGITS} | {DIGITS} "." {DIGIT}+
DEC_EXPONENT = ({DEC_SIGNIFICAND} | {INTEGER_LITERAL}) [Ee] [+-]? {DIGIT}*
DEC_LITERAL = ({DEC_SIGNIFICAND} | {DEC_EXPONENT})

HEX_LITERAL = ([0] [xX] [0]* {HEX_DIGIT}+) | "$" {HEX_DIGIT}+
HEX_DIGIT   = [0-9a-fA-F]

STRING_LITERAL = ("\"\""|"\""([^\"]+|\"\")+"\"") | ("''" | "'"([^']+|'')+"'")

BLOCK_COMMENT = "/*" ~"*/"
INLINE_COMMENT = "//" {INPUT_CHARACTER}*

MACRO_NEXT_LINE = (("\\\n" | "\\\r\n" | "\\\r") [ \t\f]*)
MACRO_CHARACTER = [^\r\n] | {MACRO_NEXT_LINE}
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

	{WORD} {
		if(macroSet.containsKey(yytextHashableCharSequence)) {
			return TokenType.MACRO;
		}
		if(yytextIsCommand()) {
			return TokenType.COMMAND;
		}

		return TokenType.WORD;

    }

	{GLUED_WORD} {
		return TokenType.GLUED_WORD;
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