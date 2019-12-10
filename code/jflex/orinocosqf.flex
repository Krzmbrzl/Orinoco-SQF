package arma.orinocosqf.lexer;

import arma.orinocosqf.preprocessing.PreProcessorMacro;
import org.jetbrains.annotations.NotNull;
// import org.jetbrains.annotations.Nullable;
import arma.orinocosqf.preprocessing.MacroSet;
import arma.orinocosqf.util.HashableCharSequence;
import arma.orinocosqf.util.CommandSet;
import arma.orinocosqf.util.LightweightStringBuilder;
import java.io.IOException;

%%

%ctorarg MacroSet macroSet

%init{
	this.macroSet = macroSet;
%init}


%public %class OrinocoJFlexLexer
// %implements
%unicode
%line
%function advance
%type TokenType
%eof{
    return;
%eof}

%include orinocosqf_javaheader

%state MACRO_ARGS
%state PREPROCESSOR_CMD
%state PREPROCESSOR_CMD_ML_COMMENT

LETTER = [a-zA-Z_] //can't start with $ because of hex numbers
LETTER_DIGIT = [$a-zA-Z_$0-9]
WORD = {LETTER} {LETTER_DIGIT}*
GLUED_WORD = ("##")? {LETTER_DIGIT}+ (("##") {LETTER_DIGIT}+)+ ("##")?

LINE_TERMINATOR = \n|\r\n|\r
INPUT_CHARACTER = [^\r\n]

WHITE_SPACE_CHAR = {LINE_TERMINATOR} | [ \t\f]
WHITE_SPACE = {WHITE_SPACE_CHAR}+

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
CMD_DEFINE = "#define"
CMD_INCLUDE = "#include"
CMD_IFDEF = "#ifdef"
CMD_IFNDEF = "#ifndef"
CMD_ELSE = "#else"
CMD_ENDIF = "#endif"
CMD_UNDEF = "#undef"

%%

<PREPROCESSOR_CMD> {
	"/*" { updateTokenLength(false); yybegin(PREPROCESSOR_CMD_ML_COMMENT); }
    {INLINE_COMMENT} { updateTokenLength(false); yybegin(YYINITIAL); return preprocessorCommandMatched; }
    [^\r\n\\/*]+ { updateTokenLength(true); appendTextToPreProcessorCommand(); }
    [/|*] { updateTokenLength(true); appendTextToPreProcessorCommand(); }
    {MACRO_NEXT_LINE} { updateTokenLength(true); appendTextToPreProcessorCommand(); }
	{LINE_TERMINATOR} { yypushback(zzMarkedPos-zzStartRead); yybegin(YYINITIAL); return preprocessorCommandMatched; }
	<<EOF>> {
		if (yymoreStreams()) {
			yypopStream();
		}
		yybegin(YYINITIAL);
		return preprocessorCommandMatched;
	}
}

<PREPROCESSOR_CMD_ML_COMMENT> {
	"*/" { updateTokenLength(false); yybegin(PREPROCESSOR_CMD); }
	[^/*]+ { updateTokenLength(false); }
	[/|*] { updateTokenLength(false); }
	<<EOF>> {
		if (yymoreStreams()) {
      		yypopStream();
		}
		yybegin(YYINITIAL);
		return preprocessorCommandMatched;
	}
}

<MACRO_ARGS> {
	"(" { updateTokenLength(true); appendTextToMacro(); macroArgLeftParenCount++; }
    ")" {
    	updateTokenLength(true);
    	appendTextToMacro();
    	macroArgRightParenCount++;
    	if(macroArgParenCountBalanced()) {
			return TokenType.MACRO;
		}
    }
    {MACRO_NEXT_LINE} { updateTokenLength(true); appendTextToMacro(); }
    {WHITE_SPACE_CHAR} {
		if(macroArgParenCountBalanced()) {
			yypushback(1); //pushback the whitespace
			return TokenType.MACRO;
		}
		updateTokenLength(true);
    }
    [^()\\]+ { updateTokenLength(true); appendTextToMacro(); }

	<<EOF>> {
      	if (yymoreStreams()) {
			yypopStream();
		}
		if(macroArgParenCountBalanced()) {
			yybegin(YYINITIAL);
			return TokenType.MACRO;
		} else{
			yybegin(YYINITIAL);
			yypushback(macroWithArgs.length() - macroWithArgs.toString().indexOf('('));
		}
	}
}

<YYINITIAL> {

	{WHITE_SPACE} { updateTokenLength(true); return TokenType.WHITE_SPACE; }

	{CMD_DEFINE} { beginPreProcessorCommandState(TokenType.CMD_DEFINE); }
	{CMD_INCLUDE} { beginPreProcessorCommandState(TokenType.CMD_INCLUDE); }
	{CMD_IFDEF} { beginPreProcessorCommandState(TokenType.CMD_IFDEF); }
	{CMD_IFNDEF} { beginPreProcessorCommandState(TokenType.CMD_IFNDEF); }
	{CMD_ELSE} { beginPreProcessorCommandState(TokenType.CMD_ELSE); }
	{CMD_ENDIF} { beginPreProcessorCommandState(TokenType.CMD_ENDIF); }
	{CMD_UNDEF} { beginPreProcessorCommandState(TokenType.CMD_UNDEF); }

	{BLOCK_COMMENT} { updateTokenLength(true); return TokenType.BLOCK_COMMENT; }
	{INLINE_COMMENT} { updateTokenLength(true); return TokenType.INLINE_COMMENT; }

	{HEX_LITERAL} { updateTokenLength(true); return TokenType.HEX_LITERAL; }
	{INTEGER_LITERAL} { updateTokenLength(true); return TokenType.INTEGER_LITERAL; }
	{DEC_LITERAL} { updateTokenLength(true); return TokenType.DEC_LITERAL; }
	{STRING_LITERAL} { updateTokenLength(true); return TokenType.STRING_LITERAL; }

	{WORD} {
      	updateTokenLength(true);
      		macroHasArgs = false;
			PreProcessorMacro macro = macroSet.get(yytextHashableCharSequence);
			if(macro != null) {
				if(!macro.takesArguments()) {
					return TokenType.MACRO;
				}
				macroWithArgs.setLength(0);
				appendTextToMacro();
				yybegin(MACRO_ARGS);
				macroHasArgs = true;
			} else {
				if(yytextIsCommand()) {
					return TokenType.COMMAND;
				}

				return TokenType.WORD;
			}

	}

	{GLUED_WORD} {
      	updateTokenLength(true);
		return TokenType.GLUED_WORD;
	}

	"==" { updateTokenLength(true); latestCommandId = EQEQ_id; return TokenType.EQEQ; }
	"!=" { updateTokenLength(true); latestCommandId = NE_id; return TokenType.NE; }
	">>" { updateTokenLength(true); latestCommandId = GTGT_id; return TokenType.GTGT; }
	"<=" { updateTokenLength(true); latestCommandId = LE_id; return TokenType.LE; }
	">=" { updateTokenLength(true); latestCommandId = GE_id; return TokenType.GE; }
	"&&" { updateTokenLength(true); latestCommandId = AMPAMP_id; return TokenType.AMPAMP; }
	"||" { updateTokenLength(true); latestCommandId = BARBAR_id; return TokenType.BARBAR; }

	"*" { updateTokenLength(true); latestCommandId = ASTERISK_id; return TokenType.ASTERISK; }
	"=" { updateTokenLength(true); latestCommandId = EQ_id; return TokenType.EQ; }
	"%" { updateTokenLength(true); latestCommandId = PERC_id; return TokenType.PERC; }
	"+" { updateTokenLength(true); latestCommandId = PLUS_id; return TokenType.PLUS; }
	"-" { updateTokenLength(true); latestCommandId = MINUS_id; return TokenType.MINUS; }
	"/" { updateTokenLength(true); latestCommandId = FSLASH_id; return TokenType.FSLASH; }
	"^" { updateTokenLength(true); latestCommandId = CARET_id; return TokenType.CARET; }

	"#" { updateTokenLength(true); latestCommandId = HASH_id; return TokenType.HASH; }

	"<" { updateTokenLength(true); latestCommandId = LT_id; return TokenType.LT; }
	">" { updateTokenLength(true); latestCommandId = GT_id; return TokenType.GT; }

	"!" { updateTokenLength(true); latestCommandId = EXCL_id; return TokenType.EXCL; }

	"(" { updateTokenLength(true); latestCommandId = LPAREN_id; return TokenType.LPAREN; }
	")" { updateTokenLength(true); latestCommandId = RPAREN_id; return TokenType.RPAREN; }
	"{" { updateTokenLength(true); latestCommandId = L_CURLY_BRACE_id; return TokenType.L_CURLY_BRACE; }
	"}" { updateTokenLength(true); latestCommandId = R_CURLY_BRACE_id; return TokenType.R_CURLY_BRACE; }
	"[" { updateTokenLength(true); latestCommandId = L_SQ_BRACKET_id; return TokenType.L_SQ_BRACKET; }
	"]" { updateTokenLength(true); latestCommandId = R_SQ_BRACKET_id; return TokenType.R_SQ_BRACKET; }
	"," { updateTokenLength(true); latestCommandId = COMMA_id; return TokenType.COMMA; }
	";" { updateTokenLength(true); latestCommandId = SEMICOLON_id; return TokenType.SEMICOLON; }

	"?" { updateTokenLength(true); latestCommandId = QUEST_id; return TokenType.QUEST; }
	":" { updateTokenLength(true); latestCommandId = COLON_id; return TokenType.COLON; }

	. { updateTokenLength(true); return TokenType.BAD_CHARACTER; }
}

<<EOF>> {
      	if (!yymoreStreams()) { return TokenType.EOF;}
		yypopStream();
  }