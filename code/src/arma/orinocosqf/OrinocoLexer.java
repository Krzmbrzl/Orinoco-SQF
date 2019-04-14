package arma.orinocosqf;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import static arma.orinocosqf.ASCIITextHelper.toLowerCase;

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
			rootTokenNode.accept(read);
			currentToken.append(read);
			preprocessedOffset++;
			if (ocs.isUsingOriginalReader()) {
				originalLength++;
			}
			if (rootTokenNode.hasCompletedWork()) {
				rootTokenNode.submitWork();
				currentToken.setLength(0);
			}
		}
		rootTokenNode.noMoreTokens();
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

	private void makePreProcessorCommand(@NotNull PreProcessorCommand command) {
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
		private final int STATE1_NEED_HASH = 0;
		private final int STATE2_WORD = 1;
		private final int STATE3_WHITESPACE = 2;
		private final int STATE4_BODY = 3;
		private final int STATE5_NEXT_LINE = 4; //used when \ is encountered in body
		private final int STATE5_DONE = 5;
		private int state = STATE1_NEED_HASH;
		private final MultilineCommentTokenNode commentNode;
		private boolean inComment = false;
		private PreProcessorCommand command = null;
		private final StringBuilder commandName = new StringBuilder();

		public PreProcessorCommandTokenNode(@NotNull OrinocoLexer lexer) {
			super(lexer);
			commentNode = new MultilineCommentTokenNode(lexer);
		}

		@Override
		public void accept(char c) {
			// #word body
			// or
			// #word body \
			// more body
			switch (state) {
				case STATE1_NEED_HASH: {
					if (c == '#') {
						state = STATE2_WORD;
					}
					break;
				}
				case STATE2_WORD: {
					if (Character.isWhitespace(c)) {
						state = STATE3_WHITESPACE;
						for (PreProcessorCommand command : PreProcessorCommand.values()) {
							if (commandName.length() != command.name().length()) {
								continue;
							}
							boolean match = true;
							for (int i = 0; i < commandName.length(); i++) {
								char cn = toLowerCase(commandName.charAt(i));
								if (cn != toLowerCase(command.name().charAt(i))) {
									match = false;
									break;
								}
							}
							if (match) {
								this.command = command;
							} else {
								state = STATE1_NEED_HASH;
							}
						}
						break;
					}
					commandName.append(c);
					break;
				}
				case STATE3_WHITESPACE: {
					if (!Character.isWhitespace(c)) {
						state = STATE4_BODY;
					}
					break;
				}
				case STATE4_BODY: {
					commentNode.accept(c);
					if (commentNode.isActive()) {
						inComment = true;
						break;
					}
					if (inComment) {
						if (commentNode.hasCompletedWork()) {
							inComment = false;
						}
						break;
					}

					if (c == '\\') {
						state = STATE5_NEXT_LINE;
						break;
					}
					if (c == '\n') {
						state = STATE5_DONE;
					}

					break;
				}
				case STATE5_NEXT_LINE: {
					if (c == '\n') {
						state = STATE4_BODY;
					}
					break;
				}
			}
		}

		@Override
		public boolean isActive() {
			return state > STATE1_NEED_HASH && state < STATE5_DONE;
		}

		@Override
		public boolean hasCompletedWork() {
			return state == STATE5_DONE;
		}

		@Override
		public void submitWork() {
			if (state != STATE5_DONE) {
				throw new IllegalStateException();
			}
			if (command == null) {
				throw new IllegalStateException();
			}
			lexer.makePreProcessorCommand(command);
			reset();
		}

		@Override
		public void reset() {
			state = STATE1_NEED_HASH;
			commandName.setLength(0);
			command = null;
		}

		@Override
		public void errorIncompleteState() {
			// todo
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
		public void accept(char c) {
			if (Character.isWhitespace(c)) {
				working = true;
			} else {
				if (working) {
					working = false;
					done = true;
				}
			}
		}

		@Override
		public boolean isActive() {
			return working;
		}

		@Override
		public boolean hasCompletedWork() {
			return done;
		}

		@Override
		public void submitWork() {
			if (!done) {
				throw new IllegalStateException();
			}
			if (makeWhitespaceToken) {
				lexer.makeWhitespace();
			}
		}

		@Override
		public void reset() {
			done = false;
		}

		@Override
		public void errorIncompleteState() {
			// do nothing because there is no incomplete state for this
		}
	}

	private static class SingleLineCommentTokenNode extends TokenNode {

		private final int STATE1_NEED_SLASH = 0;
		private final int STATE2_NEED_SECOND_SLASH = 1;
		private final int STATE3_NEED_NEWLINE = 2;
		private final int STATE4_MADE_COMMENT = 3;

		private int state = STATE1_NEED_SLASH;

		public SingleLineCommentTokenNode(@NotNull OrinocoLexer lexer) {
			super(lexer);
		}

		@Override
		public void accept(char c) {
			switch (state) {
				case STATE1_NEED_SLASH: {
					if (c == '/') {
						state = STATE2_NEED_SECOND_SLASH;
					}
					break;
				}
				case STATE2_NEED_SECOND_SLASH: {
					if (c == '/') {
						state = STATE3_NEED_NEWLINE;
					} else {
						state = STATE1_NEED_SLASH;
					}
					break;
				}
				case STATE3_NEED_NEWLINE: {
					if (c == '\n') {
						state = STATE4_MADE_COMMENT;
					}
					break;
				}
			}
		}

		@Override
		public boolean isActive() {
			return state > STATE1_NEED_SLASH && state < STATE4_MADE_COMMENT;
		}

		@Override
		public boolean hasCompletedWork() {
			return state == STATE4_MADE_COMMENT;
		}

		@Override
		public void submitWork() {
			if (state != STATE4_MADE_COMMENT) {
				throw new IllegalStateException();
			}
			reset();
			this.lexer.makeComment();
		}

		@Override
		public void reset() {
			state = STATE1_NEED_SLASH;
		}

		@Override
		public void errorIncompleteState() {
			// todo tell lexer what is missing to complete a comment
		}
	}

	private static class MultilineCommentTokenNode extends TokenNode {

		private final int STATE1_NEED_SLASH = 0;
		private final int STATE2_NEED_STAR = 1;
		private final int STATE3_NEED_SECOND_STAR = 2;
		private final int STATE4_NEED_SECOND_SLASH = 3;
		private final int STATE5_MADE_COMMENT = 4;

		private int state = STATE1_NEED_SLASH;

		public MultilineCommentTokenNode(@NotNull OrinocoLexer lexer) {
			super(lexer);
		}

		@Override
		public void accept(char c) {
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
			}
		}

		@Override
		public boolean hasCompletedWork() {
			return state == STATE5_MADE_COMMENT;
		}

		@Override
		public void submitWork() {
			if (state != STATE5_MADE_COMMENT) {
				throw new IllegalStateException();
			}
			reset();
		}

		@Override
		public void reset() {
			state = STATE1_NEED_SLASH;
			this.lexer.makeComment();
		}

		@Override
		public void errorIncompleteState() {
			// todo tell lexer what is missing to complete a comment
		}

		@Override
		public boolean isActive() {
			return state > STATE1_NEED_SLASH && state < STATE5_MADE_COMMENT;
		}
	}

	private abstract static class TokenNode {

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

	}

	private static class RootTokenNode extends TokenNode {

		@NotNull
		public final ArrayList<TokenNode> children = new ArrayList<>();
		private boolean childMatched = false;

		public RootTokenNode(@NotNull OrinocoLexer lexer) {
			super(lexer);
		}

		@Override
		public void accept(char c) {
			TokenNode finished = null;

			int activeCount = 0;
			for (TokenNode child : children) {
				child.accept(c);
				if (child.isActive()) {
					activeCount++;
				}
				if (child.hasCompletedWork()) {
					childMatched = true;
					finished = child;
				}
			}
			if (activeCount == 0 && finished != null) {
				finished.submitWork();
			}
		}

		@Override
		public boolean isActive() {
			for (TokenNode child : children) {
				if (child.isActive()) {
					return true;
				}
			}
			return false;
		}

		@Override
		public boolean hasCompletedWork() {
			return childMatched && !isActive();
		}

		@Override
		public void submitWork() {
			if (!childMatched) {
				throw new IllegalStateException();
			}
			reset();
		}

		@Override
		public void reset() {
			childMatched = false;
		}

		@Override
		public void errorIncompleteState() {
			for (TokenNode child : children) {
				if (child.isActive()) {
					child.errorIncompleteState();
					break;
				}
			}
		}

		public void noMoreTokens() {
			if (!isActive()) {
				return;
			}
			boolean didSomething = false;
			for (TokenNode child : children) {
				if (child.hasCompletedWork()) {
					child.submitWork();
					didSomething = true;
					break;
				}
			}
			if (!didSomething) {
				errorIncompleteState();
			}
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
