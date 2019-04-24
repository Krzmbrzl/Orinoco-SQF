package arma.orinocosqf;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static arma.orinocosqf.ASCIITextHelper.toLowerCase;

/**
 * @author K
 * @since 4/24/19
 */
public class SQFTokenNodes {
	public static class SQFRootTokenNode extends OrinocoLexer.RootTokenNode {
		private final OrinocoLexer.TokenNode[] nodes = new OrinocoLexer.TokenNode[6];
		private final LinkedList<Integer> usableNodes = new LinkedList<>();

		private final int MULTILINE_COMMENT = 0;
		private final int SINGLELINE_COMMENT = 1;
		private final int PREPROCESSOR_COMMAND = 2;
		private final int WHITESPACE = 3;
		private final int WORD = 4;

		public SQFRootTokenNode(@NotNull OrinocoLexer lexer) {
			super(lexer);
			nodes[MULTILINE_COMMENT] = new MultilineCommentTokenNode(lexer);
			nodes[SINGLELINE_COMMENT] = new SingleLineCommentTokenNode(lexer);
			nodes[PREPROCESSOR_COMMAND] = new PreProcessorCommandTokenNode(lexer);
			nodes[WHITESPACE] = new WhitespaceTokenNode(lexer, true);
			nodes[WORD] = new WordTokenNode(lexer);
			for (int i = 0; i < nodes.length; i++) {
				usableNodes.add(i);
			}
		}

		@Override
		public void accept(char c) {

		}

		@Override
		public boolean isActive() {
			return false;
		}

		@Override
		public boolean hasCompletedWork() {
			return false;
		}

		@Override
		public void submitWork() {

		}

		@Override
		public void errorIncompleteState() {

		}

		@Override
		public void reset() {

		}

		@Override
		public void noMoreTokens() {

		}
	}

	public static class WordTokenNode extends OrinocoLexer.TokenNode {

		private final int STATE1_NEED_CHAR = 0;
		private final int STATE2_NEED_WHITESPACE = 1;
		private final int STATE3_DONE = 2;

		private int state = STATE1_NEED_CHAR;

		public WordTokenNode(@NotNull OrinocoLexer lexer) {
			super(lexer);
		}

		@Override
		public void accept(char c) {
			switch (state) {
				case STATE1_NEED_CHAR: {
					if (Character.isWhitespace(c)) {
						break;
					}
					state = STATE2_NEED_WHITESPACE;
					break;
				}
				case STATE2_NEED_WHITESPACE: {
					if (Character.isWhitespace(c)) {
						state = STATE3_DONE;
						break;
					}
					break;
				}
				case STATE3_DONE: {
					break;
				}
			}
		}

		@Override
		public boolean isActive() {
			return state > STATE1_NEED_CHAR && state < STATE3_DONE;
		}

		@Override
		public boolean hasCompletedWork() {
			return state == STATE3_DONE;
		}

		@Override
		public void submitWork() {
			// todo
			reset();
		}

		@Override
		public void errorIncompleteState() {

		}

		@Override
		public void reset() {
			state = STATE1_NEED_CHAR;
		}
	}

	public static class PreProcessorCommandTokenNode extends OrinocoLexer.TokenNode {
		private final int STATE1_NEED_HASH = 0;
		private final int STATE2_WORD = 1;
		private final int STATE3_WHITESPACE = 2;
		private final int STATE4_BODY = 3;
		private final int STATE5_NEXT_LINE = 4; //used when \ is encountered in body
		private final int STATE6_DONE = 5;
		private int state = STATE1_NEED_HASH;
		private final MultilineCommentTokenNode commentNode;
		private boolean inComment = false;
		private PreProcessorCommand command = null;
		private final StringBuilder commandName = new StringBuilder();
		private final List<Integer> tokenRanges = new ArrayList<>();
		private int currentIndex;
		private int endIndexOfLastComment;

		public PreProcessorCommandTokenNode(@NotNull OrinocoLexer lexer) {
			super(lexer);
			commentNode = new MultilineCommentTokenNode(lexer);
			reset();
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
								reset();
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
					// todo handle single line comments
					if (commentNode.isActive()) {
						if (!inComment) {
							if (endIndexOfLastComment == -1) {
								tokenRanges.add(0);
								tokenRanges.add(currentIndex);
							} else {
								tokenRanges.add(endIndexOfLastComment + 1);
								tokenRanges.add(currentIndex);
							}
						}
						inComment = true;
						break;
					}
					if (inComment) {
						if (commentNode.hasCompletedWork()) {
							inComment = false;
							endIndexOfLastComment = currentIndex;
						}
						break;
					}

					if (c == '\\') {
						state = STATE5_NEXT_LINE;
						break;
					}
					if (c == '\n') {
						state = STATE6_DONE;
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
			currentIndex++;
		}

		@Override
		public boolean isActive() {
			return state > STATE1_NEED_HASH && state < STATE6_DONE;
		}

		@Override
		public boolean hasCompletedWork() {
			return state == STATE6_DONE;
		}

		@Override
		public void submitWork() {
			if (state != STATE6_DONE) {
				throw new IllegalStateException();
			}
			if (command == null) {
				throw new IllegalStateException();
			}
			if (tokenRanges.size() == 0) {
				tokenRanges.add(0);
				tokenRanges.add(currentIndex);
			} else if (tokenRanges.size() % 2 != 0) {
				tokenRanges.add(currentIndex);
			}
			makePreProcessorCommand(command, tokenRanges);
			reset();
		}

		@Override
		public void reset() {
			state = STATE1_NEED_HASH;
			commandName.setLength(0);
			command = null;
			currentIndex = 0;
			tokenRanges.clear();
			endIndexOfLastComment = -1;
		}

		@Override
		public void errorIncompleteState() {
			// todo
		}
	}

	public static class WhitespaceTokenNode extends OrinocoLexer.TokenNode {
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
				makeWhitespace();
			}
			reset();
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

	public static class SingleLineCommentTokenNode extends OrinocoLexer.TokenNode {

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
						reset();
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
			this.makeComment();
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

	public static class MultilineCommentTokenNode extends OrinocoLexer.TokenNode {

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
			this.makeComment();
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
}
