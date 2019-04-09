package arma.orinocosqf;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
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
	private final RootTokenNode rootTokenNode = new RootTokenNode(this);
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
		rootTokenNode.children.add(new MultilineCommentTokenNode(this));
		rootTokenNode.children.add(new SingleLineCommentTokenNode(this));
		rootTokenNode.children.add(new WhitespaceTokenNode(this, true));
		rootTokenNode.children.add(new PreProcessorCommandTokenNode(this));

		rootTokenNode.children.trimToSize();

		TokenNode cursor = rootTokenNode;
		while (ocs.hasAvailable()) {
			char read = ocs.read();
			if (read == '\n') {
				lineNumber++;
			}
			TokenNode next = cursor.accept(read);
			if (!cursor.isPreProcessing()) {
				originalLength++;
			}
			currentToken.append(read);
			preprocessedOffset++;
			if (cursor.isDone()) {
				cursor.finish();
				cursor = next;
				currentToken.setLength(0);
			}
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

	private void makePreProcessorCommand() {
		//lexerStream.acceptPreProcessorCommand();
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

	private static class PreProcessorCommandTokenNode extends TokenNode {
		private boolean preprocessing = false;

		public PreProcessorCommandTokenNode(@NotNull OrinocoLexer lexer) {
			super(lexer);
		}

		@Override
		public boolean isPreProcessing() {
			return preprocessing;
		}

		@Override
		@NotNull
		public TokenNode accept(char c) {
			return null;
		}

		@Override
		public boolean isDone() {
			return false;
		}

		@Override
		public void finish() {

		}
	}

	private static class WhitespaceTokenNode extends TokenNode {
		private final boolean makeWhitespaceToken;
		private boolean working = false;
		private boolean done = false;

		public WhitespaceTokenNode(@NotNull OrinocoLexer lexer, boolean makeWhitespaceToken) {
			super(lexer);
			this.makeWhitespaceToken = makeWhitespaceToken;
		}

		@Override
		public boolean isPreProcessing() {
			return false;
		}

		@Override
		@NotNull
		public TokenNode accept(char c) {
			if (Character.isWhitespace(c)) {
				working = true;
			} else {
				if (working) {
					working = false;
					done = true;
				}
			}
			return this;
		}

		@Override
		public boolean isDone() {
			return done;
		}

		@Override
		public void finish() {
			if (!done) {
				throw new IllegalStateException();
			}
			done = false;
			if (makeWhitespaceToken) {
				lexer.makeWhitespace();
			}
		}
	}

	private static class SingleLineCommentTokenNode extends TokenNode {

		private final int STATE1_NEED_SLASH = 0;
		private final int STATE2_NEED_SECOND_SLASH = 1;
		private final int STATE3_MADE_COMMENT = 2;

		private int state = STATE1_NEED_SLASH;

		public SingleLineCommentTokenNode(@NotNull OrinocoLexer lexer) {
			super(lexer);
		}

		@Override
		public boolean isPreProcessing() {
			return false;
		}

		@NotNull
		@Override
		public TokenNode accept(char c) {
			switch (state) {
				case STATE1_NEED_SLASH: {
					if (c == '/') {
						state = STATE2_NEED_SECOND_SLASH;
					}
					break;
				}
				case STATE2_NEED_SECOND_SLASH: {
					if (c == '/') {
						state = STATE3_MADE_COMMENT;
					} else {
						state = STATE1_NEED_SLASH;
					}
					break;
				}
			}
			return this;
		}

		@Override
		public boolean isDone() {
			return state == STATE3_MADE_COMMENT;
		}

		@Override
		public void finish() {
			if (state != STATE3_MADE_COMMENT) {
				throw new IllegalStateException();
			}
			state = STATE1_NEED_SLASH;
			this.lexer.makeComment();
		}
	}

	private static class MultilineCommentTokenNode extends TokenNode {

		private final int STATE1_NEED_SLASH = 0;
		private final int STATE2_NEED_STAR = 1;
		private final int STATE3_NEED_SECOND_STAR = 2;
		private final int STATE4_NEED_SECOND_SLASH = 3;
		private final int STATE5_MADE_COMMENT = 4;
		private final TokenNode gotoWhenCommentMade;

		private int state = STATE1_NEED_SLASH;

		public MultilineCommentTokenNode(@NotNull OrinocoLexer lexer) {
			super(lexer);
			gotoWhenCommentMade = this;
		}

		@Override
		public boolean isPreProcessing() {
			return false;
		}

		public MultilineCommentTokenNode(@NotNull OrinocoLexer lexer, @NotNull TokenNode gotoWhenCommentMade) {
			super(lexer);
			this.gotoWhenCommentMade = gotoWhenCommentMade;
		}

		@NotNull
		@Override
		public TokenNode accept(char c) {
			switch (state) {
				case STATE1_NEED_SLASH: {
					if (c == '/') {
						state = STATE2_NEED_STAR;
					}
					break;
				}
				case STATE2_NEED_STAR: {
					if (c == '*') {
						state = STATE3_NEED_SECOND_STAR;
					} else {
						state = STATE1_NEED_SLASH;
					}
					break;
				}
				case STATE3_NEED_SECOND_STAR: {
					if (c == '*') {
						state = STATE4_NEED_SECOND_SLASH;
					}
					break;
				}
				case STATE4_NEED_SECOND_SLASH: {
					if (c == '/') {
						state = STATE5_MADE_COMMENT;
					} else {
						state = STATE1_NEED_SLASH;
					}
					break;
				}
				default: {

				}
			}
			return gotoWhenCommentMade;
		}

		@Override
		public boolean isDone() {
			return state == STATE5_MADE_COMMENT;
		}

		@Override
		public void finish() {
			if (state != STATE5_MADE_COMMENT) {
				throw new IllegalStateException();
			}
			state = STATE1_NEED_SLASH;
			this.lexer.makeComment();
		}
	}

	private abstract static class TokenNode {

		protected final OrinocoLexer lexer;

		public TokenNode(@NotNull OrinocoLexer lexer) {
			this.lexer = lexer;
		}

		public abstract boolean isPreProcessing();

		@NotNull
		public abstract TokenNode accept(char c);

		public abstract boolean isDone();

		public abstract void finish();

	}

	private static class RootTokenNode extends TokenNode {

		@NotNull
		public final ArrayList<TokenNode> children = new ArrayList<>();
		private boolean childMatched = false;

		public RootTokenNode(@NotNull OrinocoLexer lexer) {
			super(lexer);
		}

		@Override
		public boolean isPreProcessing() {
			return false;
		}

		@NotNull
		@Override
		public TokenNode accept(char c) {
			for (TokenNode child : children) {
				TokenNode accept = child.accept(c);
				if (child.isDone()) {
					childMatched = true;
					if (accept != child) {
						return accept;
					}
				}
			}
			return this;
		}

		@Override
		public boolean isDone() {
			return childMatched;
		}

		@Override
		public void finish() {
			if (!childMatched) {
				throw new IllegalStateException();
			}
			childMatched = false;
		}
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
