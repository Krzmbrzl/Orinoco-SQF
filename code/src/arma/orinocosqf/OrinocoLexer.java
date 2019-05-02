package arma.orinocosqf;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
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

	private final OrinocoCharStream ocs = new OrinocoCharStream();
	private final OrinocoLexerStream lexerStream;
	private int originalOffset = 0;
	private int originalLength = 0;
	private int preprocessedOffset = 0;
	private int preprocessedLength = 0;
	private int lineNumber = 1;
	private final StringBuilder currentToken = new StringBuilder();
	private static final String[] NON_WHITESPACE_DELIMS = {"+", "-", "/", "*", "!", "(", ")", "{", "}", "[", "]", "?", "<", ">", "#",
			"<=", ">=", "&&", "||"};

	public OrinocoLexer(@NotNull OrinocoReader r, @NotNull OrinocoLexerStream lexerStream) {
		this.lexerStream = lexerStream;
		lexerStream.setLexer(this);
		ocs.stateStack.push(new LexerState(r, true));
	}

	/**
	 * Starts the lexing process.
	 */
	public void start() {
		// todo :backtrack
		/*
			Instead of clearing currentToken, have a parameter that marks what parts have been used. if currentToken hasn't had
			all characters used, the while(ocs.hasAvailable()) will be swapped with reading from the currentToken and re-applying the chars
			to the tokenNodes
		*/
		while (ocs.hasAvailable()) {
			char read = ocs.read();
			if (read == '\n') {
				lineNumber++;
			}
			currentToken.append(read);
			preprocessedOffset++;
			if (ocs.isUsingOriginalReader()) {
				originalLength++;
			}
		}
	}

	public void backtrack(int charCount) {
		//todo :backtrack
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

	private void makePreProcessorCommand(@NotNull PreProcessorCommand command, @NotNull List<Integer> tokenRanges) {
		if (tokenRanges.size() < 2 || tokenRanges.size() % 2 != 0) {
			throw new IllegalArgumentException();
		}

		char[] bodyWithNoComments;
		{ //remove comments from the currentToken
			int length = 0;
			for (int i = 0; i < tokenRanges.size(); i += 2) {
				length += tokenRanges.get(i + 1) - tokenRanges.get(i);
			}
			char[] chars = new char[length];

			int ci = 0;
			for (int tr = 0; tr < tokenRanges.size(); tr += 2) {
				for (int i = tokenRanges.get(tr); i < tokenRanges.get(tr + 1); i++) {
					chars[ci++] = currentToken.charAt(i);
				}
			}
			bodyWithNoComments = chars;
		}

		lexerStream.acceptPreProcessorCommand(command, bodyWithNoComments, 0, bodyWithNoComments.length);
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
		ocs.acceptIncludedReader(reader);
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


	public abstract static class TokenNode {

		protected final OrinocoLexer lexer;

		public TokenNode(@NotNull OrinocoLexer lexer) {
			this.lexer = lexer;
		}

		public abstract void accept(char c);

		/**
		 * @return true if the node is both NOT done but is in a state that is still valid. Return false if done or in invalid state
		 */
		public abstract boolean isActive();

		/** @return true if the node is not active ({@link #isActive()}) but has work to be submitted */
		public abstract boolean hasCompletedWork();

		/** Submit the work to the lexer or whatever */
		public abstract void submitWork();

		/**
		 * Invoke this method when there is no more chars to accept, no {@link TokenNode} has finished work ({@link #hasCompletedWork()}),
		 * and at least 1 {@link TokenNode} is active ({@link #isActive()})
		 */
		public abstract void errorIncompleteState();

		/**
		 * Invoked after a {@link #errorIncompleteState()} has been called on any {@link TokenNode} in the lexer. This method should reset
		 * the node as if no characters have been accepted
		 */
		public abstract void reset();


		protected void makeWhitespace() {
			lexer.makeWhitespace();
		}

		protected void makeComment() {
			lexer.makeComment();
		}

		protected void makeLocalVariable() {
			lexer.makeLocalVariable();
		}

		protected void makeGlobalVariable() {
			lexer.makeGlobalVariable();
		}

		protected void makeCommand() {
			lexer.makeCommand();
		}

		protected void makePreProcessorCommand(@NotNull PreProcessorCommand command, @NotNull List<Integer> tokenRanges) {
			lexer.makePreProcessorCommand(command, tokenRanges);
		}

		protected void makePreProcessedText() {
			lexer.makePreProcessedText();
		}

		@NotNull
		protected CharSequence getCurrentToken() {
			return lexer.currentToken;
		}

	}

	public static abstract class RootTokenNode extends TokenNode {

		public RootTokenNode(@NotNull OrinocoLexer lexer) {
			super(lexer);
		}

		public abstract void noMoreTokens();
	}

	private static class OrinocoCharStream {

		private final Stack<LexerState> stateStack = new Stack<>();
		private boolean checked = false;

		public OrinocoCharStream() {
		}

		public boolean hasAvailable() {
			if (!checked) {
				checked = true;

				do {
					LexerState peek = stateStack.peek();
					try {
						if (!peek.reader.ready()) {
							stateStack.pop();
						}
					} catch (IOException ignore) {
					}
				} while (!stateStack.isEmpty());
			}
			if (stateStack.isEmpty()) {
				return false;
			}
			LexerState peek = stateStack.peek();
			try {
				return peek.reader.ready();
			} catch (IOException ignore) {
			}
			return false;
		}

		public char read() {
			if (!hasAvailable()) {
				throw new IllegalStateException();
			}
			final LexerState lexState = stateStack.peek();

			try {
				return (char) lexState.reader.read();
			} catch (IOException ignore) {

			}
			throw new IllegalStateException();
		}

		public void acceptIncludedReader(@NotNull OrinocoReader reader) {
			stateStack.push(new LexerState(reader));
		}

		public boolean isUsingOriginalReader() {
			return stateStack.size() == 1;
		}
	}

	private static class LexerState {
		@NotNull
		public final BufferedReader reader;
		public final boolean isFirstState;

		private LexerState(@NotNull OrinocoReader reader) {
			this(reader, false);
		}

		public LexerState(@NotNull OrinocoReader reader, boolean isFirstState) {
			this.reader = new BufferedReader(reader);
			this.isFirstState = isFirstState;
		}
	}

}
