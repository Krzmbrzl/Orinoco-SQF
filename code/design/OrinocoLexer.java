import org.jetbrains.annotations.NotNull;

/**
 * A lexer that tokenizes text separated by whitespace ("words" or "tokens") and submits each token to a {@link OrinocoLexerStream}.
 * This lexer also has a cyclic dependency on a preprocessor. Due to the fact that each token may have a macro inside it, the lexer stores a
 * set of macro's as a reference to know when the preprocessor is needed. When a preprocessor is needed for a token, it submits the token to
 * {@link OrinocoLexerStream#preProcessToken(char[], int, int)}.
 * Subsequently, the preprocessed result re-enters the lexer for re-lexing via {@link #acceptPreProcessedText(String)}. This
 * preprocessed result may need further preprocessing after the re-lex because the preprocessor doesn't handle tokenizing the text (see example 1).
 *
 * Example 1:
 * <code>
 * #define ONE 1
 * #define ASSIGN(VAR) VAR = ONE;
 * ASSIGN(hello) //begin lexing here
 *
 * // The lexer sees ASSIGN(hello), which matches a macro. It feeds "ASSIGN(hello)" to the preprocessor.
 * // The preprocessor then goes to the body of ASSIGN and replaces VAR with hello, thus yielding "hello = ONE;".
 * // The lexer then receives that text via {@link #acceptPreProcessedText(String)}. The lexer lexes "hello" and "=" and submits
 * // them to the proper {@link OrinocoLexerStream} accept method that doesn't involve preprocessing.
 * // The lexer then lexes ONE and it matches a macro. It then feeds that to the preprocessor, preprocessor spits out "1" to the lexer.
 * // Lexer lexes 1, submits to {@link OrinocoLexerStream} without further preprocessing,
 * // and then finally ";" is lexed from the first {@link #acceptPreProcessedText(String)}
 * </code>
 *
 * @author K
 * @since 02/20/2019
 */
public abstract class OrinocoLexer {
	private final OrinocoReader r;
	private final OrinocoLexerStream lexerStream;

	public OrinocoLexer(@NotNull OrinocoReader r, @NotNull OrinocoLexerStream lexerStream) {
		this.r = r;
		this.lexerStream = lexerStream;
	}

	/**
	 * Starts the lexing process.
	 */
	public abstract void start();

	/**
	 * Accepts partially or fully preprocessed text (see Example 1 in class level doc) from the {@link OrinocoLexerStream}.
	 *
	 * @param text the preprocessed, untokenized text
	 */
	abstract void acceptPreProcessedText(@NotNull String text);
}
