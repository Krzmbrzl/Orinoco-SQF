package arma.orinocosqf;

import arma.orinocosqf.exceptions.UnknownIdException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		return SQFCommands.instance.getId(command);
	}

	private static final Pattern pattern_ifdef = Pattern.compile("^#(ifdef|ifndef) ([a-zA-Z0-9_$]+)");

	private OrinocoLexerContext context = new OrinocoLexerContext() {
		@Override
		public @NotNull String getCommand(int id) throws UnknownIdException {
			String c = SQFCommands.instance.getCommandById(id);
			if (c == null) {
				throw new UnknownIdException(id + "");
			}
			return c;
		}

		@Override
		public @Nullable String getVariable(int id) {
			return null;
		}

		@Override
		public boolean isTextBufferingEnabled() {
			return false;
		}

		@Override
		public @Nullable TextBuffer getTextBuffer() {
			return null;
		}

		@Override
		public @Nullable TextBuffer getTextBufferPreprocessed() {
			return null;
		}
	};

	private final OrinocoLexerStream lexerStream;
	private int originalOffset = 0;
	private int originalLength = 0;
	private int preprocessedOffset = 0;
	private int preprocessedLength = 0;
	private final OrinocoJFlexLexer jFlexLexer;

	private enum PreProcessorState {
		IfDef, IfNDef, ElseIfDef, ElseIfNDef
	}

	@NotNull
	private Stack<PreProcessorState> preProcessorState = new Stack<>();

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
			doStart();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void doStart() throws IOException {
		while (true) {
			OrinocoJFlexLexer.TokenType type = jFlexLexer.advance();
			if (type == null) {
				throw new IllegalStateException(); //?
			}
			if (type == OrinocoJFlexLexer.TokenType.EOF) {
				return;
			}
			if (!preProcessorState.isEmpty()) {
				if (type == OrinocoJFlexLexer.TokenType.CMD_ENDIF) {
					preProcessorState.pop();
					return;
				}
				switch (preProcessorState.peek()) {
					case IfDef: { //read tokens until an #else comes along
						if (type == OrinocoJFlexLexer.TokenType.CMD_ELSE) {
							preProcessorState.pop();
							preProcessorState.push(PreProcessorState.ElseIfDef);
							return;
						}
						break;
					}
					case IfNDef: { //skip tokens until an #else comes along or endif
						if (type == OrinocoJFlexLexer.TokenType.CMD_ELSE) {
							preProcessorState.pop();
							preProcessorState.push(PreProcessorState.ElseIfNDef);
						}
						return;
					}
					case ElseIfDef: {
						return; //skip tokens
					}
					case ElseIfNDef: {
						break;
					}
					default: {
						throw new IllegalStateException(); // ???
					}
				}
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
				case CMD_IFDEF: //fall
				case CMD_IFNDEF: {
					Matcher m = pattern_ifdef.matcher(jFlexLexer.yytext());
					if (m.find()) {
						String name = m.group(2);
						MacroSet macroSet = lexerStream.getMacroSet();
						if (macroSet.containsKey(name)) {
							if (type == OrinocoJFlexLexer.TokenType.CMD_IFDEF) {
								preProcessorState.push(PreProcessorState.IfDef);
							} else {
								preProcessorState.push(PreProcessorState.IfNDef);
							}
						}
					}
					break;
				}
				case CMD_DEFINE: {
					makePreProcessorCommand(PreProcessorCommand.Define);
					break;
				}
				case CMD_INCLUDE: {
					makePreProcessorCommand(PreProcessorCommand.Include);
					break;
				}
				case CMD_ELSE: {
					//todo report uneeded #else
					makePreProcessorCommand(PreProcessorCommand.Else);
					break;
				}
				case CMD_ENDIF: {
					//todo report uneeded #endif
					makePreProcessorCommand(PreProcessorCommand.EndIf);
					break;
				}
				case CMD_UNDEF: {
					makePreProcessorCommand(PreProcessorCommand.Undef);
					break;
				}
				case BLOCK_COMMENT:
				case INLINE_COMMENT: {
					makeComment();
					break;
				}
				case HEX_LITERAL: //fall
				case INTEGER_LITERAL: //fall
				case DEC_LITERAL: {
					makeLiteral(OrinocoLexerSQFLiteralType.Number);
					break;
				}
				case STRING_LITERAL: {
					makeLiteral(OrinocoLexerSQFLiteralType.String);
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
	}

	private void makeLiteral(@NotNull OrinocoLexerSQFLiteralType number) {
		lexerStream.acceptLiteral(number, preprocessedOffset, preprocessedLength, originalOffset, originalLength, context);
		updateOffsetsAfterMake();
	}

	private void makePreProcessorCommand(@NotNull PreProcessorCommand command) {
		lexerStream.acceptPreProcessorCommand(command, jFlexLexer.getBuffer(), originalOffset, originalLength);
		updateOffsetsAfterMake();
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
		lexerStream.acceptCommand(jFlexLexer.getLatestCommandId(), preprocessedOffset, preprocessedLength, originalOffset, originalLength, context);
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
		return context;
	}

	public OrinocoLexerStream getLexerStream() {
		return lexerStream;
	}


}
