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

	public static final Warning_OverwriteExisting WARNING_OVERWRITE = new Warning_OverwriteExisting();
	public static final Warning_UndefineNonExistent WARNING_UNDEFINE_NONEXISTENT = new Warning_UndefineNonExistent();
}
