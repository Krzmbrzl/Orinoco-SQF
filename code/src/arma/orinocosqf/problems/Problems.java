package arma.orinocosqf.problems;

/**
 * A utility class holding an instance of every available {@link Problem} implementation
 * 
 * @author Raven
 *
 */
public class Problems {
	public static final Error_EmptyInput ERROR_EMPTY = new Error_EmptyInput();
	public static final Error_Generic ERROR_GENERIC = new Error_Generic();
	public static final Error_Internal ERROR_INTERNAL = new Error_Internal();
	public static final Error_InvalidCharacter ERROR_INVALID_CHARACTER = new Error_InvalidCharacter();
	public static final Error_InvalidPath ERROR_INVALID_PATH = new Error_InvalidPath();
	public static final Error_NoMacroArgumentsProvided ERROR_NO_MACRO_ARGUMENTS_PROVIDED = new Error_NoMacroArgumentsProvided();
	public static final Error_PreprocessorError ERROR_PREPROCESSOR = new Error_PreprocessorError();
	public static final Error_UnclosedParenthesis ERROR_UNCLOSED_PARENTHESIS = new Error_UnclosedParenthesis();
	public static final Error_UnclosedString ERROR_UNCLOSED_STRING = new Error_UnclosedString();
	public static final Error_WrongMacroArgumentCount ERROR_WRONG_ARGUMENT_COUNT = new Error_WrongMacroArgumentCount();
	public static final Error_LeadingWS ERROR_LEADING_WS = new Error_LeadingWS();
	public static final Error_TrailingWS ERROR_TRAILING_WS = new Error_TrailingWS();

	public static final Error_Syntax ERROR_SYNTAX = new Error_Syntax();
	public static final Error_SyntaxIllegalCharacter ERROR_SYNTAX_ILLEGAL_TOKEN = new Error_SyntaxIllegalCharacter();
	public static final Error_SyntaxTooManyOperands ERROR_SYNTAX_TOO_MANY_OPERANDS = new Error_SyntaxTooManyOperands();
	public static final Error_SyntaxTooFewOperands ERROR_SYNTAX_TOO_FEW_OPERANDS = new Error_SyntaxTooFewOperands();
	public static final Error_InvalidCommandSyntax ERROR_INVALID_COMMAND_SYNTAX = new Error_InvalidCommandSyntax();

	public static final Warning_OverwriteExisting WARNING_OVERWRITE = new Warning_OverwriteExisting();
	public static final Warning_UndefineNonExistent WARNING_UNDEFINE_NONEXISTENT = new Warning_UndefineNonExistent();
}
