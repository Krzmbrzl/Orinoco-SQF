package arma.orinocosqf;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Stack;

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

	private final Stack<LexerState> stateStack = new Stack<>();
	private final OrinocoLexerStream lexerStream;
	private int originalOffset = 0;
	private int originalLength = 0;
	private int preprocessedOffset = 0;
	private int preprocessedLength = 0;
	private int lineNumber = 1;
	private boolean allowPreProcessorCommand = true;
	private static final char[] NON_WHITESPACE_DELIMS = {'+', '-', '/', '*', '!', '(', ')', '{', '}', '[', ']', '?', '<', '>', '#'};

	public OrinocoLexer(@NotNull OrinocoReader r, @NotNull OrinocoLexerStream lexerStream) {
		this.lexerStream = lexerStream;
		lexerStream.setLexer(this);
		stateStack.push(new LexerState(r, true));
	}

	/**
	 * Starts the lexing process.
	 */
	public void start() {
		try {
			lexCurrentReader();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void lexCurrentReader() throws IOException {
		while (!stateStack.isEmpty()) {
			LexerState lexState = stateStack.peek();
			readAllIntoBuffer();

			if (lexState.buffEnd == 0) {
				stateStack.pop();
				continue;
			}
			lexState.bufInd = 0;

			do {
				@NotNull TokenType expectedTokenType = getTokenType(lexState);

				switch (expectedTokenType) {
					case Whitespace: {
						readAllWhitespace(lexState.buffEnd);
						break;
					}
					case Text: {
						readAllOfWord(lexState.buffEnd);
						break;
					}
					case MultilineComment: {
						break;
					}
					case SingleLineComment: {
						break;
					}
					default: {
						throw new IllegalStateException();
					}
				}
				allowPreProcessorCommand = false;
			} while (lexState.bufInd < lexState.buffEnd);
		}
	}

	private @NotNull TokenType getTokenType(@NotNull LexerState lexState) {
		@NotNull TokenType expectedTokenType;
		char[] buffer = lexState.buffer;
		if (buffer.length >= 2 && buffer[lexState.bufInd] == '/' && buffer[lexState.bufInd + 1] == '*') {
			expectedTokenType = TokenType.MultilineComment;
		} else if (buffer.length >= 2 && buffer[lexState.bufInd] == '/' && buffer[lexState.bufInd + 1] == '/') {
			expectedTokenType = TokenType.SingleLineComment;
		} else {
			expectedTokenType = Character.isWhitespace(buffer[lexState.bufInd]) ? TokenType.Whitespace : TokenType.Text;
		}
		return expectedTokenType;
	}

	private void readAllWhitespace(int cap) {
		LexerState lexState = stateStack.peek();
		char[] buffer = lexState.buffer;
		int start = lexState.bufInd;
		for (; lexState.bufInd < cap; lexState.bufInd++) {
			char c = buffer[lexState.bufInd];
			if (c == '\n') {
				lineNumber++;
				allowPreProcessorCommand = true;
			}
			if (!Character.isWhitespace(c)) {
				lexState.bufInd--;
				break;
			}
		}
		int read = lexState.bufInd - start;
		preprocessedLength += read;
		if (lexState.isFirstState) {
			originalLength += read;
		}

		lexerStream.acceptWhitespace(originalOffset, originalLength, preprocessedOffset, preprocessedLength, context);
	}

	private void readAllOfWord(int cap) {
		LexerState lexState = stateStack.peek();
		char[] buffer = lexState.buffer;
		int start = lexState.bufInd;
		boolean readingPreProcessorCommand = false;
		boolean maybeComment = false;
		for (; lexState.bufInd < cap; lexState.bufInd++) {
			char c = buffer[lexState.bufInd];
			if (c == '/') {
				if (maybeComment) {
					lexState.bufInd--;
					break;
				}
				maybeComment = true;
			} else if (c == '*') {
				if (maybeComment) {
					if (readingPreProcessorCommand) {
						//todo read all of comment
						continue;
					}
					lexState.bufInd--;
					break;
				}
				maybeComment = true;
			} else {
				if (c == '"') {

				}
				maybeComment = false;
			}
			if (readingPreProcessorCommand) {
				if (c == '\n') {
					lexState.bufInd--;
					break;
				}
			} else {
				if (allowPreProcessorCommand) {
					if (c == '#') {
						readingPreProcessorCommand = true;
						continue;
					}
				}

				if (Character.isWhitespace(c)) {
					lexState.bufInd--;
					break;
				}
			}
			// todo binary search
			// todo determine if String
			// todo operators are also delimiters, not just whitespace (1+1 for example)
		}
		int read = lexState.bufInd - start;
		preprocessedLength += read;
		if (lexState.isFirstState) {
			originalLength += read;
		}
	}

	private void readAllIntoBuffer() throws IOException {
		LexerState state = stateStack.peek();
		int start = 0;
		int end = state.buffer.length;
		int read;
		do {
			read = state.reader.read(state.buffer, start, end);
			state.buffEnd = read;
			if (read > 0) {
				start = state.buffer.length;
				state.growBuffer();
				end = state.buffer.length - start;
			}
		} while (read > 0);
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
		stateStack.push(new LexerState(reader));
	}

	/**
	 * @return The current {@link OrinocoLexerContext} of this lexer
	 */
	@NotNull
	public OrinocoLexerContext getContext() {
		// TODO
		throw new UnsupportedOperationException("Get context not yet implemented!");
	}

	private enum TokenType {
		Whitespace,
		SingleLineComment,
		MultilineComment,
		Text;
	}

	private class LexerState {
		public char[] buffer = new char[256];
		public int buffEnd = 0;
		public int bufInd = 0;
		@NotNull
		public final OrinocoReader reader;
		public final boolean isFirstState;

		private LexerState(@NotNull OrinocoReader reader) {
			this.reader = reader;
			this.isFirstState = false;
		}

		public LexerState(@NotNull OrinocoReader reader, boolean isFirstState) {
			this.reader = reader;
			this.isFirstState = isFirstState;
		}

		public void growBuffer() {
			char[] newBuffer = new char[buffer.length * 2];
			System.arraycopy(buffer, 0, newBuffer, 0, newBuffer.length);
			this.buffer = newBuffer;
		}
	}
}
