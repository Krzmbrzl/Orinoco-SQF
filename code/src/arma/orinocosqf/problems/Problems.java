package arma.orinocosqf.problems;

/**
 * A utility class holding an instance of every available {@link Problem} implementation
 * @author Raven
 *
 */
public class Problems {
	public static final Error_EmptyInput EMPTY = new Error_EmptyInput();
	public static final Error_Generic GENERIC = new Error_Generic();
	public static final Error_Internal INTERNAL = new Error_Internal();
	public static final Error_InvalidCharacter INVALID_CHARACTER = new Error_InvalidCharacter();
	public static final Error_UnclosedParenthesis UNCLOSED_PARENTHESIS = new Error_UnclosedParenthesis();
	public static final Error_UnclosedString UNCLOSED_STRING = new Error_UnclosedString();
}
