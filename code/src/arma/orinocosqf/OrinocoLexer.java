package arma.orinocosqf;

import arma.orinocosqf.exceptions.UnknownIdException;
import arma.orinocosqf.preprocessing.MacroSet;
import arma.orinocosqf.preprocessing.PreProcessorCommand;
import arma.orinocosqf.problems.Problem;
import arma.orinocosqf.problems.ProblemListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Writer;
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
 * Example 1:
 *
 * <pre>
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
public class OrinocoLexer implements ProblemListener {
	public static int getCommandId(@NotNull String command) {
		return SQFCommands.instance.getId(command);
	}

	private static final Pattern pattern_ifdef = Pattern.compile("^#(ifdef|ifndef) ([a-zA-Z0-9_$]+)");

	private OrinocoLexerContext context = new DefaultLexerContext();

	private final OrinocoLexerStream lexerStream;
	private int originalOffset = 0;
	private int originalLength = 0;
	private int preprocessedOffset = 0;
	private int preprocessedLength = 0;
	private final OrinocoJFlexLexer jFlexLexer;
	private static final CaseInsensitiveHashSet<SQFVariable> globalVarSet = new CaseInsensitiveHashSet<>();
	private static int nextGlobalVarId = 0;
	private final CaseInsensitiveHashSet<SQFVariable> localVarSet = new CaseInsensitiveHashSet<>();
	private int nextLocalVarId = 0;
	/**
	 * @see #getIdTransformer()
	 */
	private final VariableIdTransformer varIdTransformer = new MyVariableIdTransformer();
	/**
	 * @see #setPreprocessedResultWriter(Writer)
	 */
	private Writer preprocessedResultWriter;

	private enum PreProcessorIfDefState {
		IfDef, IfNDef, ElseIfDef, ElseIfNDef
	}

	@NotNull
	private final Stack<PreProcessorIfDefState> preProcessorIfDefState = new Stack<>();

	public OrinocoLexer(@NotNull OrinocoReader r, @NotNull OrinocoLexerStream lexerStream) {
		this.lexerStream = lexerStream;
		lexerStream.setLexer(this);
		jFlexLexer = new OrinocoJFlexLexer(r, lexerStream.getMacroSet());
		jFlexLexer.setCommandSet(SQFCommands.instance);
	}

	/**
	 * @return an id transformer for both local and global variables
	 */
	@NotNull
	public VariableIdTransformer getIdTransformer() {
		return varIdTransformer;
	}

	/**
	 * If non null, all preprocessed text will be written to the specified writer. If null, preprocessed text will be discarded and only
	 * tokenized versions will be sent through the {@link OrinocoLexerStream}
	 *
	 * @param writer writer to use
	 */
	protected void setPreprocessedResultWriter(@Nullable Writer writer) {
		this.preprocessedResultWriter = writer;
	}

	/**
	 * Sets the context for the lexer. If null, a default instance will be used that has no text buffering.
	 *
	 * @param context context to use, or null to use default instance
	 */
	public void setContext(@Nullable OrinocoLexerContext context) {
		if (context == null) {
			context = new DefaultLexerContext();
		}
		this.context = context;
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
			if (!jFlexLexer.yymoreStreams()) {
				preprocessedLength = jFlexLexer.yylength();
				originalLength = jFlexLexer.yylength();
			} else {
				preprocessedLength += jFlexLexer.yylength();
			}
			if (!preProcessorIfDefState.isEmpty()) {
				if (type == OrinocoJFlexLexer.TokenType.CMD_ENDIF) {
					preProcessorIfDefState.pop();
					return;
				}
				switch (preProcessorIfDefState.peek()) {
					case IfDef: { //read tokens until an #else comes along
						if (type == OrinocoJFlexLexer.TokenType.CMD_ELSE) {
							preProcessorIfDefState.pop();
							preProcessorIfDefState.push(PreProcessorIfDefState.ElseIfDef);
							return;
						}
						break;
					}
					case IfNDef: { //skip tokens until an #else comes along or endif
						if (type == OrinocoJFlexLexer.TokenType.CMD_ELSE) {
							preProcessorIfDefState.pop();
							preProcessorIfDefState.push(PreProcessorIfDefState.ElseIfNDef);
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
					if (lexerStream.skipPreProcessing()) {
						lexerStream.preProcessorCommandSkipped(originalOffset, originalLength, context);
						break;
					}
					Matcher m = pattern_ifdef.matcher(jFlexLexer.yytext());
					if (m.find()) {
						String name = m.group(2);
						MacroSet macroSet = lexerStream.getMacroSet();
						if (macroSet.containsKey(name)) {
							if (type == OrinocoJFlexLexer.TokenType.CMD_IFDEF) {
								preProcessorIfDefState.push(PreProcessorIfDefState.IfDef);
							} else {
								preProcessorIfDefState.push(PreProcessorIfDefState.IfNDef);
							}
						}
					}
					break;
				}
				case CMD_DEFINE: {
					makePreProcessorCommandIfPreProcessingEnabled(PreProcessorCommand.Define);
					break;
				}
				case CMD_INCLUDE: {
					makePreProcessorCommandIfPreProcessingEnabled(PreProcessorCommand.Include);
					break;
				}
				case CMD_ELSE: {
					//todo report uneeded #else
					makePreProcessorCommandIfPreProcessingEnabled(PreProcessorCommand.Else);
					break;
				}
				case CMD_ENDIF: {
					//todo report uneeded #endif
					makePreProcessorCommandIfPreProcessingEnabled(PreProcessorCommand.EndIf);
					break;
				}
				case CMD_UNDEF: {
					makePreProcessorCommandIfPreProcessingEnabled(PreProcessorCommand.Undef);
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
				case GLUED_WORD: //fall
				case MACRO: {
					if (lexerStream.skipPreProcessing()) {
						lexerStream.preProcessorTokenSkipped(originalOffset, originalLength, context);
					} else {
						if (jFlexLexer.macroHasArgs()) {
							MyStringBuilder macroWithArgs = jFlexLexer.getMacroWithArgs();
							char[] chars = macroWithArgs.getCharsReadOnly();
							lexerStream.preProcessToken(chars, 0, macroWithArgs.getLength());
						} else {
							lexerStream.preProcessToken(jFlexLexer.getBuffer(), jFlexLexer.yystart(), jFlexLexer.yylength());
						}
					}
					break;
				}
				case WORD: {
					CharSequence cs = jFlexLexer.getTokenCharSequence();
					if (cs.charAt(0) == '_') {
						SQFVariable var = localVarSet.getKeyForCharSequence(cs);
						if (var == null) {
							var = new SQFVariable(new String(HashableCharSequence.asChars(cs)), nextLocalVarId);
							localVarSet.put(var);
							nextLocalVarId++;
						}
						makeLocalVariable(var.getId());
					} else {
						SQFVariable var = globalVarSet.getKeyForCharSequence(cs);
						if (var == null) {
							var = new SQFVariable(new String(HashableCharSequence.asChars(cs)), nextGlobalVarId);
							globalVarSet.put(var);
							nextGlobalVarId++;
						}
						makeGlobalVariable(var.getId());
					}
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

	/**
	 * Updates in the following order:
	 * <ol>
	 * <li>{@link #originalOffset} is += {@link #originalLength} <b>if</b> the lexer isn't lexing preprocessed text</li>
	 * <li>{@link #originalLength} is set to 0 <b>if</b> the lexer isn't lexing preprocessed text</li>
	 * <li>{@link #preprocessedOffset} is += {@link #preprocessedLength}</li>
	 * <li>{@link #preprocessedLength} is set to 0</li>
	 * </ol>
	 */
	private void updateOffsetsAfterMake() {
		if (!jFlexLexer.yymoreStreams()) {
			originalOffset += originalLength;
			originalLength = 0;
		}
		preprocessedOffset += preprocessedLength;
		preprocessedLength = 0;
	}

	/**
	 * Makes a literal token and then invokes {@link #updateOffsetsAfterMake()}
	 *
	 * @param type literal type
	 * @throws IOException because of {@link #preprocessedResultWriter}
	 * @see OrinocoLexerStream#acceptLiteral(OrinocoLexerLiteralType, int, int, int, int, OrinocoLexerContext)
	 */
	private void makeLiteral(@NotNull OrinocoLexerSQFLiteralType type) throws IOException {
		if (preprocessedResultWriter != null) {
			preprocessedResultWriter.write(jFlexLexer.getBuffer(), jFlexLexer.yystart(), jFlexLexer.yylength());
		}
		lexerStream.acceptLiteral(type, preprocessedOffset, preprocessedLength, originalOffset, originalLength, context);
		updateOffsetsAfterMake();
	}

	/**
	 * Makes a preprocessor command token only if {@link OrinocoLexerStream#skipPreProcessing()} returns false. This method will always
	 * invoke {@link #updateOffsetsAfterMake()} after token is made or token is skipped.
	 *
	 * @param command command to use
	 * @see OrinocoLexerStream#acceptPreProcessorCommand(PreProcessorCommand, char[], int, int)
	 * @see OrinocoLexerStream#preProcessorCommandSkipped(int, int, OrinocoLexerContext)
	 */
	private void makePreProcessorCommandIfPreProcessingEnabled(@NotNull PreProcessorCommand command) {
		if (lexerStream.skipPreProcessing()) {
			lexerStream.preProcessorCommandSkipped(originalOffset, originalLength, context);
			updateOffsetsAfterMake();
			return;
		}
		lexerStream.acceptPreProcessorCommand(command, jFlexLexer.getBuffer(), jFlexLexer.yystart(), originalLength);
		updateOffsetsAfterMake();
	}

	/**
	 * Makes a whitespace token and then invokes {@link #updateOffsetsAfterMake()}
	 *
	 * @throws IOException because of {@link #preprocessedResultWriter}
	 * @see OrinocoLexerStream#acceptWhitespace(int, int, int, int, OrinocoLexerContext)
	 */
	private void makeWhitespace() throws IOException {
		if (preprocessedResultWriter != null) {
			preprocessedResultWriter.write(jFlexLexer.getBuffer(), jFlexLexer.yystart(), jFlexLexer.yylength());
		}
		lexerStream.acceptWhitespace(originalOffset, originalLength, preprocessedOffset, preprocessedLength, context);
		updateOffsetsAfterMake();
	}

	/**
	 * Makes a comment token and then invokes {@link #updateOffsetsAfterMake()}
	 *
	 * @throws IOException because of {@link #preprocessedResultWriter}
	 * @see OrinocoLexerStream#acceptComment(int, int, int, int, OrinocoLexerContext)
	 */
	private void makeComment() throws IOException {
		if (preprocessedResultWriter != null) {
			preprocessedResultWriter.write(jFlexLexer.getBuffer(), jFlexLexer.yystart(), jFlexLexer.yylength());
		}
		lexerStream.acceptComment(originalOffset, originalLength, preprocessedOffset, preprocessedLength, context);
		updateOffsetsAfterMake();
	}

	/**
	 * Makes a local variable token and then invokes {@link #updateOffsetsAfterMake()}
	 *
	 * @param id the local variable id
	 * @throws IOException because of {@link #preprocessedResultWriter}
	 * @see OrinocoLexerStream#acceptLocalVariable(int, int, int, int, int, OrinocoLexerContext)
	 */
	private void makeLocalVariable(int id) throws IOException {
		if (preprocessedResultWriter != null) {
			preprocessedResultWriter.write(jFlexLexer.getBuffer(), jFlexLexer.yystart(), jFlexLexer.yylength());
		}
		lexerStream.acceptLocalVariable(id, preprocessedOffset, preprocessedLength, originalOffset, originalLength, context);
		updateOffsetsAfterMake();
	}

	/**
	 * Makes a global variable token and then invokes {@link #updateOffsetsAfterMake()}
	 *
	 * @param id global variable id
	 * @throws IOException because of {@link #preprocessedResultWriter}
	 */
	private void makeGlobalVariable(int id) throws IOException {
		if (preprocessedResultWriter != null) {
			preprocessedResultWriter.write(jFlexLexer.getBuffer(), jFlexLexer.yystart(), jFlexLexer.yylength());
		}
		lexerStream.acceptGlobalVariable(id, preprocessedOffset, preprocessedLength, originalOffset, originalLength, context);
		updateOffsetsAfterMake();
	}

	/**
	 * Makes a command token and then invokes {@link #updateOffsetsAfterMake()}
	 *
	 * @throws IOException because of {@link #preprocessedResultWriter}
	 * @see OrinocoLexerStream#acceptCommand(int, int, int, int, int, OrinocoLexerContext)
	 */
	private void makeCommand() throws IOException {
		if (preprocessedResultWriter != null) {
			preprocessedResultWriter.write(jFlexLexer.getBuffer(), jFlexLexer.yystart(), jFlexLexer.yylength());
		}
		lexerStream.acceptCommand(jFlexLexer.getLatestCommandId(), preprocessedOffset, preprocessedLength, originalOffset, originalLength, context);
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

	/**
	 * This method implementation is intended for the use by the preprocessor. The included offset is relative to the char-buffer provided
	 * to it. <b>Do not call this method from outside the preprocessor</b>
	 */
	@Override
	public void problemEncountered(@NotNull Problem problem, @NotNull String msg, int offset, int length, int line) {
		// TODO process problem and delegate to the actual problem listener
	}

	/**
	 * @see OrinocoLexer#getIdTransformer()
	 */
	private class MyVariableIdTransformer extends VariableIdTransformer {

		public MyVariableIdTransformer() {
			super(localVarSet, globalVarSet);
		}

		@Override
		protected int getNextId(@NotNull String varName) {
			if (varName.charAt(0) == '_') {
				nextLocalVarId++;
				return nextLocalVarId - 1;
			}
			nextGlobalVarId++;
			return nextGlobalVarId - 1;
		}
	}

	/**
	 * @see OrinocoLexer#setContext(OrinocoLexerContext)
	 */
	private class DefaultLexerContext implements OrinocoLexerContext {

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
			try {
				return OrinocoLexer.this.getIdTransformer().fromId(id);
			} catch (UnknownIdException ignore) {
				return null;
			}
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
	}
}
