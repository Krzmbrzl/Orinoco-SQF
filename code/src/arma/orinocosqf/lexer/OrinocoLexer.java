package arma.orinocosqf.lexer;

import arma.orinocosqf.OrinocoLiteralType;
import arma.orinocosqf.OrinocoReader;
import arma.orinocosqf.Resettable;
import arma.orinocosqf.TextBuffer;
import arma.orinocosqf.preprocessing.MacroSet;
import arma.orinocosqf.preprocessing.PreProcessorCommand;
import arma.orinocosqf.problems.Problem;
import arma.orinocosqf.problems.ProblemListener;
import arma.orinocosqf.sqf.SQFCommands;
import arma.orinocosqf.sqf.SQFVariable;
import arma.orinocosqf.util.CaseInsensitiveHashSet;
import arma.orinocosqf.util.HashableCharSequence;
import arma.orinocosqf.util.LightweightStringBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A lexer that tokenizes text (into "words" or "tokens") and submits each token to a {@link OrinocoTokenDelegator}. This lexer also has a
 * cyclic dependency on a preprocessor (in shape of a {@link OrinocoTokenDelegator}). Due to the fact that each token may have a macro
 * inside it, the lexer stores a set of macro's as a reference to know when the preprocessor is needed. When a preprocessor is needed for a
 * token, it submits the token to {@link OrinocoTokenDelegator#preProcessToken(char[], int, int)}. Subsequently, the preprocessed result
 * re-enters the lexer for re-lexing via {@link #acceptPreProcessedText(CharSequence)}.
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
 * // submits them to the proper {@link OrinocoTokenDelegator} accept method that doesn't involve preprocessing.
 * </pre>
 *
 * @author K
 * @since 02/20/2019
 */
public class OrinocoLexer implements ProblemListener, Resettable {
	public static int getCommandId(@NotNull String command) {
		return SQFCommands.instance.getId(command);
	}

	protected static final Pattern pattern_ifdef = Pattern.compile("#if[n]?def ([a-zA-Z0-9_$]+)");

	protected OrinocoLexerContext context;
	protected OrinocoLexerContextFactory contextFactory;
	protected OrinocoTokenDelegator tokenDelegator;

	protected ProblemListener problemListener;

	protected int originalOffset = 0;
	protected int originalLength = 0;
	protected int preprocessedOffset = 0;
	protected int preprocessedLength = 0;
	protected int previousOriginalOffset = 0;
	protected int previousOriginalLength = 0;
	protected int previousPreprocessedOffset = 0;
	protected int previousPreprocessedLength = 0;

	protected OrinocoJFlexLexer jFlexLexer;
	protected static CaseInsensitiveHashSet<SQFVariable> globalVarSet = new CaseInsensitiveHashSet<>();
	protected static int nextGlobalVarId = 0;
	protected CaseInsensitiveHashSet<SQFVariable> localVarSet = new CaseInsensitiveHashSet<>();
	protected int nextLocalVarId = 0;
	/**
	 * @see #getIdTransformer()
	 */
	protected VariableIdTransformer varIdTransformer;

	protected enum PreProcessorIfDefState {
		/** This state occurs when #ifdef of #ifndef results in a true condition and everything before #else is lexed */
		LexToElse,
		/**
		 * This state occurs when the condition of #ifdef or #ifndef results in a true condition and everything after to #else should be
		 * skipped
		 */
		SkipElseBlock,
		/**
		 * This method should occurs when the condition of #ifdef or #ifndef results in a false condition and everything before #else should
		 * be skipped
		 */
		SkipToElse,
		/**
		 * This state happens when an #else block has been discovered and the current state is {@link #SkipToElse}. All tokens between #else
		 * and #endif will be lexed
		 */
		LexToEndIf
	}

	@NotNull
	protected final Stack<PreProcessorIfDefState> preProcessorIfDefState = new Stack<>();


	/**
	 * Creates an instance of this lexer
	 * 
	 * @param tokenDelegator The {@link OrinocoTokenDelegator} used to process all encountered tokens
	 * @param problemListener The {@link ProblemListener} all problems encountered during lexing or preprocessing are delegated to
	 * @param contextFactory The {@link OrinocoLexerContextFactory} used to produce {@link OrinocoLexerContext}s with the desired properties
	 * @param enableTextBuffering Whether to enable text-buffering for the context used by this lexer
	 */
	public OrinocoLexer(@NotNull OrinocoTokenDelegator tokenDelegator, @NotNull ProblemListener problemListener,
			@NotNull OrinocoLexerContextFactory contextFactory, boolean enableTextBuffering) {
		this.tokenDelegator = tokenDelegator;
		tokenDelegator.setLexer(this);

		this.contextFactory = contextFactory;
		this.context = contextFactory.produce(this, enableTextBuffering);

		this.setProblemListener(problemListener);

		this.varIdTransformer = new MyVariableIdTransformer();
	}

	/**
	 * Creates an instance of this lexer. The instanciated lexer will have text-buffering disabled by default.
	 * 
	 * @param tokenDelegator The {@link OrinocoTokenDelegator} used to process all encountered tokens
	 * @param problemListener The {@link ProblemListener} all problems encountered during lexing or preprocessing are delegated to
	 * @param contextFactory The {@link OrinocoLexerContextFactory} used to produce {@link OrinocoLexerContext}s with the desired properties
	 */
	public OrinocoLexer(@NotNull OrinocoTokenDelegator tokenDelegator, @NotNull ProblemListener problemListener,
			@NotNull OrinocoLexerContextFactory contextFactory) {
		this(tokenDelegator, problemListener, contextFactory, false);
	}

	/**
	 * Creates an instance of this lexer. The instantiated lexer will use default LexerContext objects by default.
	 * 
	 * @param tokenDelegator The {@link OrinocoTokenDelegator} used to process all encountered tokens
	 * @param problemListener The {@link ProblemListener} all problems encountered during lexing or preprocessing are delegated to
	 * @param enableTextBuffering Whether to enable text-buffering for the context used by this lexer
	 * 
	 * @see BufferingOrinocoLexerContext
	 * @see NonBufferingOrinocoLexerContext
	 */
	public OrinocoLexer(@NotNull OrinocoTokenDelegator tokenDelegator, @NotNull ProblemListener problemListener,
			boolean enableTextBuffering) {
		this(tokenDelegator, problemListener, new OrinocoLexerContextFactory() {

			@Override
			public OrinocoLexerContext produce(@NotNull OrinocoLexer lexer, boolean textBufferingEnabled) {
				return textBufferingEnabled ? new BufferingOrinocoLexerContext(lexer) : new NonBufferingOrinocoLexerContext(lexer);
			}
		}, enableTextBuffering);
	}

	/**
	 * Creates an instance of this lexer. The instantiated lexer will have text-buffering disabled and it will use default LexerContext
	 * objects by default.
	 * 
	 * @param tokenDelegator The {@link OrinocoTokenDelegator} used to process all encountered tokens
	 * @param problemListener The {@link ProblemListener} all problems encountered during lexing or preprocessing are delegated to
	 *
	 * @see BufferingOrinocoLexerContext
	 * @see NonBufferingOrinocoLexerContext
	 */
	public OrinocoLexer(@NotNull OrinocoTokenDelegator tokenDelegator, @NotNull ProblemListener problemListener) {
		this(tokenDelegator, problemListener, false);
	}


	/**
	 * @return an id transformer for both local and global variables
	 */
	@NotNull
	public VariableIdTransformer getIdTransformer() {
		return varIdTransformer;
	}

	/**
	 * Enables or disables text-buffering by switching out this lexer's context object.
	 * 
	 * @param enabled Whether text-buffering should be enabled
	 */
	public void enableTextBuffering(boolean enabled) {
		if ((enabled && !isTextBufferingEnabled() || (!enabled && isTextBufferingEnabled()))) {
			// The current context doesn't fulfill the requirements of the user -> switch it out by one that does
			this.setContext(contextFactory.produce(this, enabled));
		}
	}

	/**
	 * @return Whether the current context of this lexer supports text-buffering
	 */
	public boolean isTextBufferingEnabled() {
		return getContext().isTextBufferingEnabled();
	}

	/**
	 * Sets the context for the lexer. If null, a default instance will be used that has no text buffering.
	 *
	 * @param context context to use, or null to use default instance
	 */
	public void setContext(@Nullable OrinocoLexerContext context) {
		if (context == null) {
			context = contextFactory.produce(this, false);
		}
		this.context = context;
	}
	
	/**
	 * @param problemListener The {@link ProblemListener} all problems encountered during lexing or preprocessing are delegated to
	 */
	public void setProblemListener(@NotNull ProblemListener problemListener) {
		this.problemListener = problemListener;
	}

	/**
	 * Starts the lexing process.
	 *
	 * @param inputReader The {@link OrinocoReader} for accessing the input
	 */
	public void start(@NotNull OrinocoReader inputReader) {
		this.start(inputReader, true);
	}

	/**
	 * Starts the lexing process.
	 *
	 * @param inputReader The {@link OrinocoReader} for accessing the input
	 * @param reset A flag indicating whether to reset the lexer before starting
	 */
	public void start(@NotNull OrinocoReader inputReader, boolean reset) {
		if (reset) {
			this.reset();
		}

		tokenDelegator.setLexer(this);

		jFlexLexer = new OrinocoJFlexLexer(inputReader, tokenDelegator.getMacroSet());
		jFlexLexer.setCommandSet(SQFCommands.instance);

		tokenDelegator.begin(getContext());

		try {
			doStart();
		} catch (IOException e) {
			e.printStackTrace();
		}

		tokenDelegator.end(getContext());
	}

	protected void doStart() throws IOException {
		while (true) {
			jFlexLexer.resetTokenOffsets();
			OrinocoJFlexLexer.TokenType type = jFlexLexer.advance();
			if (type == null) {
				throw new IllegalStateException(); // ?
			}
			if (type == OrinocoJFlexLexer.TokenType.EOF) {
				return;
			}
			if (!jFlexLexer.yymoreStreams()) {
				originalLength = jFlexLexer.originalLength();
				preprocessedLength = jFlexLexer.preprocessedLength();
			} else {
				preprocessedLength += jFlexLexer.preprocessedLength();
			}
			if (!preProcessorIfDefState.isEmpty()) {
				if (type == OrinocoJFlexLexer.TokenType.CMD_ENDIF) {
					preProcessorIfDefState.pop();
					continue;
				}
				switch (preProcessorIfDefState.peek()) {
					case LexToElse: { // read tokens until an #else comes along
						if (type == OrinocoJFlexLexer.TokenType.CMD_ELSE) {
							preProcessorIfDefState.pop();
							preProcessorIfDefState.push(PreProcessorIfDefState.SkipElseBlock);
							continue;
						}
						break;
					}
					case SkipElseBlock: {
						continue;
					}
					case SkipToElse: {
						if (type != OrinocoJFlexLexer.TokenType.CMD_ELSE) {
							continue;
						}
						preProcessorIfDefState.pop();
						preProcessorIfDefState.push(PreProcessorIfDefState.LexToEndIf);
						break;
					}
					case LexToEndIf: {
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
				case CMD_IFDEF: // fall
				case CMD_IFNDEF: {
					if (tokenDelegator.skipPreProcessing()) {
						tokenDelegator.preProcessorCommandSkipped(originalOffset, originalLength, context);
						break;
					}
					Matcher m = pattern_ifdef.matcher(jFlexLexer.getPreProcessorCommand());
					if (m.find()) {
						String name = m.group(1);
						MacroSet macroSet = tokenDelegator.getMacroSet();
						if (macroSet.containsKey(name)) {
							if (type == OrinocoJFlexLexer.TokenType.CMD_IFDEF) {
								preProcessorIfDefState.push(PreProcessorIfDefState.LexToElse);
							} else {
								preProcessorIfDefState.push(PreProcessorIfDefState.SkipToElse);
							}
						} else {
							if (type == OrinocoJFlexLexer.TokenType.CMD_IFDEF) {
								preProcessorIfDefState.push(PreProcessorIfDefState.SkipToElse);
							} else {
								preProcessorIfDefState.push(PreProcessorIfDefState.LexToElse);
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
					// todo report uneeded #else
					makePreProcessorCommandIfPreProcessingEnabled(PreProcessorCommand.Else);
					break;
				}
				case CMD_ENDIF: {
					// todo report uneeded #endif
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
				case HEX_LITERAL: // fall
				case INTEGER_LITERAL: // fall
				case DEC_LITERAL: {
					makeLiteral(OrinocoLiteralType.Number);
					break;
				}
				case STRING_LITERAL: {
					makeLiteral(OrinocoLiteralType.String);
					break;
				}
				case GLUED_WORD: // fall
				case MACRO: {
					if (tokenDelegator.skipPreProcessing()) {
						if (context.getTextBufferPreprocessed() != null) {
							context.getTextBufferPreprocessed().append(jFlexLexer.getBuffer(), jFlexLexer.yystart(), jFlexLexer.yylength());
						}
						tokenDelegator.preProcessorTokenSkipped(originalOffset, originalLength, context);
					} else {
						if (jFlexLexer.macroHasArgs()) {
							LightweightStringBuilder macroWithArgs = jFlexLexer.getMacroWithArgs();
							char[] chars = macroWithArgs.getCharsReadOnly();
							tokenDelegator.preProcessToken(chars, 0, macroWithArgs.length());
						} else {
							tokenDelegator.preProcessToken(jFlexLexer.getBuffer(), jFlexLexer.yystart(), jFlexLexer.yylength());
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
	protected void updateOffsetsAfterMake() {
		previousOriginalOffset = originalOffset;
		previousOriginalLength = originalLength;
		previousPreprocessedOffset = preprocessedOffset;
		previousPreprocessedLength = preprocessedLength;

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
	 * @throws IOException because of {@link TextBuffer}
	 * @param type literal type
	 * @see OrinocoTokenDelegator#acceptLiteral
	 */
	protected void makeLiteral(@NotNull OrinocoLiteralType type) throws IOException {
		if (context.getTextBuffer() != null) {
			context.getTextBuffer().append(jFlexLexer.getBuffer(), jFlexLexer.yystart(), jFlexLexer.yylength());
		}
		if (context.getTextBufferPreprocessed() != null) {
			context.getTextBufferPreprocessed().append(jFlexLexer.getBuffer(), jFlexLexer.yystart(), jFlexLexer.yylength());
		}
		tokenDelegator.acceptLiteral(type, preprocessedOffset, preprocessedLength, originalOffset, originalLength, context);
		updateOffsetsAfterMake();
	}

	/**
	 * Makes a preprocessor command token only if {@link OrinocoTokenDelegator#skipPreProcessing()} returns false. This method will always
	 * invoke {@link #updateOffsetsAfterMake()} after token is made or token is skipped.
	 *
	 * @param command command to use
	 * @see OrinocoTokenDelegator#acceptPreProcessorCommand(PreProcessorCommand, char[], int, int)
	 * @see OrinocoTokenDelegator#preProcessorCommandSkipped(int, int, OrinocoLexerContext)
	 */
	protected void makePreProcessorCommandIfPreProcessingEnabled(@NotNull PreProcessorCommand command) throws IOException {
		LightweightStringBuilder cmd = jFlexLexer.getPreProcessorCommand();
		if (context.getTextBuffer() != null) {
			context.getTextBuffer().append(cmd.getCharsReadOnly(), 0, cmd.length());
		}

		if (tokenDelegator.skipPreProcessing()) {
			if (context.getTextBufferPreprocessed() != null) {
				context.getTextBufferPreprocessed().append(cmd.getCharsReadOnly(), 0, cmd.length());
			}

			tokenDelegator.preProcessorCommandSkipped(originalOffset, originalLength, context);
			updateOffsetsAfterMake();
			return;
		}
		tokenDelegator.acceptPreProcessorCommand(command, cmd.getCharsReadOnly(), 0, cmd.length());
		updateOffsetsAfterMake();
	}

	/**
	 * Makes a whitespace token and then invokes {@link #updateOffsetsAfterMake()}
	 *
	 * @throws IOException because of {@link TextBuffer}
	 * @see OrinocoTokenDelegator#acceptWhitespace(int, int, int, int, OrinocoLexerContext)
	 */
	protected void makeWhitespace() throws IOException {
		if (context.getTextBuffer() != null) {
			context.getTextBuffer().append(jFlexLexer.getBuffer(), jFlexLexer.yystart(), jFlexLexer.yylength());
		}
		if (context.getTextBufferPreprocessed() != null) {
			context.getTextBufferPreprocessed().append(jFlexLexer.getBuffer(), jFlexLexer.yystart(), jFlexLexer.yylength());
		}
		tokenDelegator.acceptWhitespace(originalOffset, originalLength, preprocessedOffset, preprocessedLength, context);
		updateOffsetsAfterMake();
	}

	/**
	 * Makes a comment token and then invokes {@link #updateOffsetsAfterMake()}
	 *
	 * @throws IOException because of {@link TextBuffer}
	 * @see OrinocoTokenDelegator#acceptComment(int, int, int, int, OrinocoLexerContext, int)
	 */
	protected void makeComment() throws IOException {
		char[] buffer = jFlexLexer.getBuffer();
		if (context.getTextBuffer() != null) {
			context.getTextBuffer().append(buffer, jFlexLexer.yystart(), jFlexLexer.yylength());
		}
		if (context.getTextBufferPreprocessed() != null) {
			context.getTextBufferPreprocessed().append(buffer, jFlexLexer.yystart(), jFlexLexer.yylength());
		}
		int count = 0;
		final int start = jFlexLexer.yystart();
		for (int i = 0; i < jFlexLexer.yylength(); i++) {
			if (buffer[start + i] == '\n') {
				count++;
			}
		}
		tokenDelegator.acceptComment(originalOffset, originalLength, preprocessedOffset, preprocessedLength, context, count);
		updateOffsetsAfterMake();
	}

	/**
	 * Makes a local variable token and then invokes {@link #updateOffsetsAfterMake()}
	 *
	 * @throws IOException because of {@link TextBuffer}
	 * @param id the local variable id
	 * @see OrinocoTokenDelegator#acceptLocalVariable(int, int, int, int, int, OrinocoLexerContext)
	 */
	protected void makeLocalVariable(int id) throws IOException {
		if (context.getTextBuffer() != null) {
			context.getTextBuffer().append(jFlexLexer.getBuffer(), jFlexLexer.yystart(), jFlexLexer.yylength());
		}
		if (context.getTextBufferPreprocessed() != null) {
			context.getTextBufferPreprocessed().append(jFlexLexer.getBuffer(), jFlexLexer.yystart(), jFlexLexer.yylength());
		}
		tokenDelegator.acceptLocalVariable(id, preprocessedOffset, preprocessedLength, originalOffset, originalLength, context);
		updateOffsetsAfterMake();
	}

	/**
	 * Makes a global variable token and then invokes {@link #updateOffsetsAfterMake()}
	 *
	 * @throws IOException because of {@link TextBuffer}
	 * @param id global variable id
	 */
	protected void makeGlobalVariable(int id) throws IOException {
		if (context.getTextBuffer() != null) {
			context.getTextBuffer().append(jFlexLexer.getBuffer(), jFlexLexer.yystart(), jFlexLexer.yylength());
		}
		if (context.getTextBufferPreprocessed() != null) {
			context.getTextBufferPreprocessed().append(jFlexLexer.getBuffer(), jFlexLexer.yystart(), jFlexLexer.yylength());
		}
		tokenDelegator.acceptGlobalVariable(id, preprocessedOffset, preprocessedLength, originalOffset, originalLength, context);
		updateOffsetsAfterMake();
	}

	/**
	 * Makes a command token and then invokes {@link #updateOffsetsAfterMake()}
	 *
	 * @throws IOException because of {@link TextBuffer}
	 * @see OrinocoTokenDelegator#acceptCommand(int, int, int, int, int, OrinocoLexerContext)
	 */
	protected void makeCommand() throws IOException {
		if (context.getTextBuffer() != null) {
			context.getTextBuffer().append(jFlexLexer.getBuffer(), jFlexLexer.yystart(), jFlexLexer.yylength());
		}
		if (context.getTextBufferPreprocessed() != null) {
			context.getTextBufferPreprocessed().append(jFlexLexer.getBuffer(), jFlexLexer.yystart(), jFlexLexer.yylength());
		}
		tokenDelegator.acceptCommand(jFlexLexer.getLatestCommandId(), preprocessedOffset, preprocessedLength, originalOffset,
				originalLength, context);
		updateOffsetsAfterMake();
	}

	/**
	 * Accepts partially or fully preprocessed text (see Example 1 in class level doc) from the {@link OrinocoTokenDelegator}.
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
		preprocessedLength = 0; // reset the length because something took the place of the most recent token
	}

	/**
	 * Accepts newlines that are being preserved (by the preprocessor). Preserving in this contexts simply means, that the newlines are read
	 * back into the input
	 *
	 * @param amount The amount of newlines to preserve
	 * @throws IllegalArgumentException if amount < 0
	 */
	public void acceptPreservedNewlines(int amount) {
		if (amount < 0) {
			throw new IllegalArgumentException(amount + "");
		}

		preprocessedOffset -= previousPreprocessedLength; // undo the previous preprocessed offset

		int oldOffset = originalOffset;
		int oldLength = originalLength;

		if (amount > 0) {
			String newline = "\n";
			String OS = System.getProperty("os.name").toUpperCase();
			if (OS.contains("WIN")) {
				newline = "\r\n";
			}

			preprocessedLength = newline.length() * amount;
			TextBuffer buffer = context.getTextBufferPreprocessed();
			if (buffer != null) {
				try {
					for (int i = 0; i < amount; i++) {
						buffer.append(newline);
					}
				} catch (IOException ignore) {

				}
			}
			tokenDelegator.acceptWhitespace(originalOffset, originalLength, preprocessedOffset, preprocessedLength, context);
		} else {
			preprocessedLength = 0;
		}

		updateOffsetsAfterMake();
		// undo updating the original length and offset because updateOffsetsAfterMake will update them
		// when they shouldn't be because the length and offset aren't to be updated from preprocessing
		originalLength = oldLength;
		originalOffset = oldOffset;
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
		// adjust offset and line to actually match the original input
		offset += originalOffset;
		if (line < 0) {
			line = this.jFlexLexer.getLine() + 1;
		}
		this.problemListener.problemEncountered(problem, msg, offset, length, line);
	}

	/**
	 * @see OrinocoLexer#getIdTransformer()
	 */
	protected class MyVariableIdTransformer extends VariableIdTransformer {

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

	@Override
	public void reset() {
		// New input comes with separate local variables
		localVarSet = new CaseInsensitiveHashSet<>();

		varIdTransformer.setLocalVars(localVarSet);

		// Reset TokenDelegator
		tokenDelegator.reset();

		// "Reset" context -> we can't actually reset it as it might be stored somewhere for later usage by the user
		this.context = contextFactory.produce(this, isTextBufferingEnabled());

		originalOffset = 0;
		originalLength = 0;
		preprocessedOffset = 0;
		preprocessedLength = 0;
		previousOriginalOffset = 0;
		previousOriginalLength = 0;
		previousPreprocessedOffset = 0;
		previousPreprocessedLength = 0;
		preProcessorIfDefState.clear();

		if (jFlexLexer != null) {
			jFlexLexer.reset();
		}

		// leave nextGlobalVarId and globalVarSet untouched in order to maintain compatibility between lex-runs
		// leave nextLocalVarId untouched in order to avoid same indices for local variables in different files (after all they are not the
		// same - in general anyways)
	}
}
