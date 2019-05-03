package arma.orinocosqf;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

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
	private OrinocoLexerContext context;

	public static int getCommandId(@NotNull String command) {
		return 0; //todo
	}

	private final OrinocoLexerStream lexerStream;
	private int originalOffset = 0;
	private int originalLength = 0;
	private int preprocessedOffset = 0;
	private int preprocessedLength = 0;
	private final OrinocoJFlexLexer jFlexLexer;

	public OrinocoLexer(@NotNull OrinocoReader r, @NotNull OrinocoLexerStream lexerStream) {
		this.lexerStream = lexerStream;
		lexerStream.setLexer(this);
		jFlexLexer = new OrinocoJFlexLexer(r);
	}

	/**
	 * Starts the lexing process.
	 */
	public void start() {
		try {
			while (true) {
				OrinocoJFlexLexer.TokenType type = jFlexLexer.advance();
				if (type == null) {
					throw new IllegalStateException(); //?
				}
				if (type == OrinocoJFlexLexer.TokenType.EOF) {
					return;
				}
				if (type.isCommand) {
					makeCommand();
					continue;
				}
				switch (type) {
					case WHITE_SPACE: {
						makeWhitespace();
						break;
					}
					case CMD_DEFINE: {
						break;
					}
					case CMD_INCLUDE: {
						break;
					}
					case CMD_IFDEF: {
						break;
					}
					case CMD_IFNDEF: {
						break;
					}
					case CMD_ELSE: {
						break;
					}
					case CMD_ENDIF: {
						break;
					}
					case CMD_UNDEF: {
						break;
					}
					case BLOCK_COMMENT:
					case INLINE_COMMENT: {
						makeComment();
						break;
					}
					case HEX_LITERAL: {
						break;
					}
					case INTEGER_LITERAL: {
						break;
					}
					case DEC_LITERAL: {
						break;
					}
					case STRING_LITERAL: {
						break;
					}
					case GLUED_WORD: {
						break;
					}
					case WORD: {
						break;
					}
					case BAD_CHARACTER: {
						break;
					}
					default: {
						throw new IllegalStateException(); // ?
					}
				}
			}
		} catch (IOException ignore) {

		}

	}

	private void updateOffsetsAfterMake() {
		originalOffset += originalLength;
		originalLength = 0;
		preprocessedOffset += preprocessedLength;
		preprocessedLength = 0;
	}

	private void makeWhitespace() {
		lexerStream.acceptWhitespace(originalOffset, originalLength, preprocessedOffset, preprocessedLength, context);
		updateOffsetsAfterMake();
	}

	private void makeComment() {
		lexerStream.acceptComment(originalOffset, originalLength, preprocessedOffset, preprocessedLength, context);
		updateOffsetsAfterMake();
	}

	private void makeLocalVariable() {
		//lexerStream.acceptLocalVariable();
		updateOffsetsAfterMake();
	}

	private void makeGlobalVariable() {
		//lexerStream.acceptGlobalVariable();
		updateOffsetsAfterMake();
	}

	private void makeCommand() {
		//lexerStream.acceptGlobalVariable();
		updateOffsetsAfterMake();
	}

	private void makePreProcessedText() {
		//lexerStream.preProcessToken();
		updateOffsetsAfterMake();
	}

	/**
	 * Accepts partially or fully preprocessed text (see Example 1 in class level doc) from the {@link OrinocoLexerStream}.
	 *
	 * @param text the preprocessed, untokenized text
	 */
	public void acceptPreProcessedText(@NotNull CharSequence text) {
		acceptIncludedReader(OrinocoReader.fromCharSequence(text));
	}

	/**
	 * Pushes the current {@link OrinocoReader} on a stack and lexes this reader wholely before then popping the stack and continuing
	 * lexing.
	 *
	 * @param reader the reader to immediately begin lexing
	 */
	public void acceptIncludedReader(@NotNull OrinocoReader reader) {
		jFlexLexer.yypushStream(reader);
	}

	/**
	 * @return The current {@link OrinocoLexerContext} of this lexer
	 */
	@NotNull
	public OrinocoLexerContext getContext() {
		// TODO
		throw new UnsupportedOperationException("Get context not yet implemented!");
	}

	public OrinocoLexerStream getLexerStream() {
		return lexerStream;
	}


}
