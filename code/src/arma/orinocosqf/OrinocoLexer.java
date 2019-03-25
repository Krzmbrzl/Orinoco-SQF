package arma.orinocosqf;

import org.jetbrains.annotations.NotNull;

/**
 * A lexer that tokenizes text (into "words" or "tokens") and submits each token to a {@link OrinocoLexerStream}. This lexer also has a
 * cyclic dependency on a preprocessor (in shape of a {@link OrinocoLexerStream}). Due to the fact that each token may have a macro inside
 * it, the lexer stores a set of macro's as a reference to know when the preprocessor is needed. When a preprocessor is needed for a token,
 * it submits the token to {@link OrinocoLexerStream#preProcessToken(char[], int, int)}. Subsequently, the preprocessed result re-enters the
 * lexer for re-lexing via {@link #acceptPreProcessedText(CharSequence)}.
 *
 * Example 1: <pre>
 * #define ONE 1
 * #define ASSIGN(VAR) VAR = ONE;
 * ASSIGN(hello) //begin lexing here
 *
 * // The lexer sees ASSIGN(hello), which matches a macro. It feeds "ASSIGN(hello)" to the preprocessor.
 * // The preprocessor then fully preprocesses ASSIGN(hello), thus yielding "hello = 1;".
 * // The lexer then receives that text via {@link #acceptPreProcessedText(CharSequence)}. The lexer lexes "hello", "=", "1", and ";" and
 * // submits them to the proper {@link OrinocoLexerStream} accept method that doesn't involve preprocessing.
 * </pre>
 *
 * @author K
 * @since 02/20/2019
 */
public class OrinocoLexer {
	public static int getCommandId(@NotNull String command) {
		return 0; //todo
	}

	/**
	 * The current {@link OrinocoReader} instance
	 */
	private @NotNull OrinocoReader reader;
	private final OrinocoLexerStream lexerStream;
	/**
	 * The offset of tokens after preprocessing
	 */
	private int preprocessedOffset;

	public OrinocoLexer(@NotNull OrinocoReader r, @NotNull OrinocoLexerStream lexerStream) {
		this.reader = r;
		this.lexerStream = lexerStream;
		lexerStream.setLexer(this);
	}

	/**
	 * Starts the lexing process.
	 */
	public void start() {

	}

	/**
	 * Accepts partially or fully preprocessed text (see Example 1 in class level doc) from the {@link OrinocoLexerStream}.
	 *
	 * @param text the preprocessed, untokenized text
	 */
	public void acceptPreProcessedText(@NotNull CharSequence text) {

	}

	/**
	 * Pushes the current {@link OrinocoReader} on a stack and lexes this reader wholely before then popping the stack and continuing
	 * lexing.
	 *
	 * @param reader the reader to immediately begin lexing
	 */
	public void acceptIncludedReader(@NotNull OrinocoReader reader) {

	}

	/**
	 * @return The current {@link OrinocoLexerContext} of this lexer
	 */
	@NotNull
	public OrinocoLexerContext getContext() {
		// TODO
		throw new UnsupportedOperationException("Get context not yet implemented!");
	}
}
