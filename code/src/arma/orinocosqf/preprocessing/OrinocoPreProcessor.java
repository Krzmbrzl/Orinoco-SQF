package arma.orinocosqf.preprocessing;

import arma.orinocosqf.OrinocoLiteralType;
import arma.orinocosqf.OrinocoReader;
import arma.orinocosqf.OrinocoTokenProcessor;
import arma.orinocosqf.configuration.OrinocoPreprocessorConfiguration;
import arma.orinocosqf.exceptions.InvalidPathException;
import arma.orinocosqf.exceptions.MissingMacroArgumentException;
import arma.orinocosqf.exceptions.NoMacroArgumentsGivenException;
import arma.orinocosqf.exceptions.OrinocoPreprocessorException;
import arma.orinocosqf.lexer.OrinocoLexer;
import arma.orinocosqf.lexer.OrinocoLexerContext;
import arma.orinocosqf.lexer.OrinocoTokenDelegator;
import arma.orinocosqf.preprocessing.bodySegments.BodySegment;
import arma.orinocosqf.preprocessing.bodySegments.BodySegmentParser;
import arma.orinocosqf.preprocessing.bodySegments.BodySegmentSequence;
import arma.orinocosqf.preprocessing.bodySegments.TextSegment;
import arma.orinocosqf.problems.Problems;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A {@link OrinocoTokenDelegator} implementation that fully preprocesses tokens.
 *
 * @author Raven
 * @since 02/20/2019
 */
public class OrinocoPreProcessor implements OrinocoTokenDelegator {
	/**
	 * The lexer that is being used to feed tokens into this preprocessor
	 */
	protected OrinocoLexer lexer;
	/**
	 * The {@link OrinocoTokenProcessor} to delegate method calls to
	 */
	protected OrinocoTokenProcessor processor;

	/**
	 * The {@link MacroSet} of the current preprocessing run
	 */
	protected MacroSet macroSet;
	/**
	 * The {@link BodySegmentParser} used to parse define-bodies or other preprocessor-structures
	 */
	protected BodySegmentParser segmentParser;
	/**
	 * The {@link ArmaFilesystem} used to handle includes
	 */
	protected ArmaFilesystem fileSystem;
	/**
	 * A dummy macro used for preprocessing a sequence of text (outside a macro body). It is needed for providing the macro set for the
	 * preprocessing
	 */
	protected PreProcessorMacro dummyMacro;
	/**
	 * The {@link OrinocoPreprocessorConfiguration} holding all preferences for this preprocessor
	 */
	protected OrinocoPreprocessorConfiguration configuration;


	public OrinocoPreProcessor(@NotNull OrinocoTokenProcessor processor, @NotNull ArmaFilesystem fileSystem) {
		this(processor, fileSystem, new OrinocoPreprocessorConfiguration());
	}

	public OrinocoPreProcessor(@NotNull OrinocoTokenProcessor processor, @NotNull ArmaFilesystem fileSystem,
			@NotNull OrinocoPreprocessorConfiguration configuration) {
		this.processor = processor;
		this.configuration = configuration;
		this.macroSet = new MacroSet();
		this.segmentParser = new BodySegmentParser(lexer);
		this.dummyMacro = new PreProcessorMacro(macroSet, "__________________", Collections.emptyList(), new TextSegment(""));

		setFileSystem(fileSystem);
	}

	@Override
	public void setLexer(@NotNull OrinocoLexer lexer) {
		this.lexer = lexer;
	}

	@Override
	public boolean skipPreProcessing() {
		return false;
	}

	@Override
	public void acceptPreProcessorCommand(@NotNull PreProcessorCommand command, @NotNull char[] bufReadOnly, int offset, int length) {
		if (offset + length > bufReadOnly.length) {
			throw new IndexOutOfBoundsException("Offset+length are too big for the given buffer");
		}

		switch (command) {
			case IfDef:
				handleIfDef(bufReadOnly, offset, length);
				break;
			case Define:
				handleDefine(bufReadOnly, offset, length);
				break;
			case Else:
				handleElse(bufReadOnly, offset, length);
				break;
			case EndIf:
				handleEndIf(bufReadOnly, offset, length);
				break;
			case IfNDef:
				handleIfnDef(bufReadOnly, offset, length);
				break;
			case Include:
				handleInclude(bufReadOnly, offset, length);
				break;
			case Undef:
				handleUndef(bufReadOnly, offset, length);
				break;
			default:
				throw new IllegalStateException("Reached unreachable code in OrinocoPreprocesor!");
		}
	}

	@Override
	public void preProcessToken(@NotNull char[] bufReadOnly, int offset, int length) {
		BodySegment segment = segmentParser.parseSegments(bufReadOnly, offset, length, Collections.emptyList(),
				(c, isFirstLetter) -> this.isMacroNamePart(c, isFirstLetter));

		segment.setOwner(dummyMacro);

		try {
			this.lexer.acceptPreProcessedText(segment.applyArguments(Collections.emptyList()));
		} catch (OrinocoPreprocessorException e) {
			int[] location = getContextLocation(segment, e.getContext());

			int contextOffset, contextLength;

			if (location != null) {
				contextOffset = location[0];
				contextLength = location[1];
			} else {
				// The context couldn't be found -> use the whole segment as a location
				contextOffset = 0;
				contextLength = segment.toStringNoPreProcessing().length();
			}

			if (e instanceof NoMacroArgumentsGivenException) {
				lexer.problemEncountered(Problems.ERROR_NO_MACRO_ARGUMENTS_PROVIDED, e.getMessage(), contextOffset, contextLength, -1);
			} else if (e instanceof MissingMacroArgumentException) {
				lexer.problemEncountered(Problems.ERROR_WRONG_ARGUMENT_COUNT, e.getMessage(), contextOffset, contextLength, -1);
			} else {
				lexer.problemEncountered(Problems.ERROR_PREPROCESSOR, e.getMessage(), contextOffset, contextLength, -1);
			}
		}
	}

	/**
	 * Gets the location of the context segment inside the rootsegment
	 * 
	 * @param rootSegment The root segment to search in
	 * @param context The context segment to search for
	 * @return An int-array of length two {offset, length} or <code>null</code> if it couldn't be found
	 */
	@Nullable
	private static int[] getContextLocation(BodySegment rootSegment, BodySegment context) {
		int offset = 0;
		int length = context.toStringNoPreProcessing().length();

		if (rootSegment == context) {
			return new int[] { offset, length };
		} else {
			if (rootSegment instanceof BodySegmentSequence) {
				BodySegmentSequence seq = (BodySegmentSequence) rootSegment;

				for (BodySegment currentSegment : seq) {
					int[] result = getContextLocation(currentSegment, context);

					if (result != null) {
						result[0] += offset;
						return result;
					} else {
						offset += currentSegment.toStringNoPreProcessing().length();
					}
				}
			}
		}

		// not found
		return null;
	}

	@Override
	public void begin(@NotNull OrinocoLexerContext ctx) {
		this.processor.begin(ctx);
	}

	@Override
	public void acceptCommand(int id, int preprocessedOffset, int preprocessedLength, int originalOffset, int originalLength,
			@NotNull OrinocoLexerContext ctx) {
		this.processor.acceptCommand(id, preprocessedOffset, preprocessedLength, originalOffset, originalLength, ctx);
	}

	@Override
	public void acceptLocalVariable(int id, int preprocessedOffset, int preprocessedLength, int originalOffset, int originalLength,
			@NotNull OrinocoLexerContext ctx) {
		this.processor.acceptLocalVariable(id, preprocessedOffset, preprocessedLength, originalOffset, originalLength, ctx);
	}

	@Override
	public void acceptGlobalVariable(int id, int preprocessedOffset, int preprocessedLength, int originalOffset, int originalLength,
			@NotNull OrinocoLexerContext ctx) {
		this.processor.acceptGlobalVariable(id, preprocessedOffset, preprocessedLength, originalOffset, originalLength, ctx);
	}

	@Override
	public void acceptLiteral(@NotNull OrinocoLiteralType type, int preprocessedOffset, int preprocessedLength, int originalOffset,
							  int originalLength, @NotNull OrinocoLexerContext ctx) {
		this.processor.acceptLiteral(type, preprocessedOffset, preprocessedLength, originalOffset, originalLength, ctx);
	}

	@Override
	public void preProcessorTokenSkipped(int offset, int length, @NotNull OrinocoLexerContext ctx) {

	}

	@Override
	public void preProcessorCommandSkipped(int offset, int length, @NotNull OrinocoLexerContext ctx) {

	}

	@Override
	public void end(@NotNull OrinocoLexerContext ctx) {
		this.processor.end(ctx);
	}

	@Override
	public void acceptWhitespace(int originalOffset, int originalLength, int preprocessedOffset, int preprocessedLength,
			@NotNull OrinocoLexerContext ctx) {
		// search for NLs
		// ctx.getTextBuffer().getText(originalOffset, originalLength);
	}

	@Override
	public void acceptComment(int originalOffset, int originalLength, int preprocessedOffset, int preprocessedLength,
			@NotNull OrinocoLexerContext ctx, int newlineCount) {
		if (configuration.keepComments()) {
			// TODO: feed comments back to special method in lexer to prevent endless loop
		} else {
			if (configuration.preserveNewlines()) {
				lexer.acceptPreservedNewlines(newlineCount);
			}
		}
	}

	/**
	 * Checks whether the given character can be part of a macro name (or macro-argument-name)
	 *
	 * @param c The character to check
	 * @return Whether or not it can be a macro name part
	 */
	protected boolean isMacroNamePart(char c, boolean isFirstLetter) {
		return (c > 'Z' ? c <= 'z' && c >= 'a' : c <= 'Z' && c >= 'A') || c == '_' || (!isFirstLetter && Character.isDigit(c));
	}

	@Override
	@NotNull
	public MacroSet getMacroSet() {
		return this.macroSet;
	}

	/**
	 * Sets the {@link ArmaFilesystem} used by this preprocessor in order to resolve includes
	 * 
	 * @param system The new system to use form now on
	 */
	public void setFileSystem(@NotNull ArmaFilesystem system) {
		this.fileSystem = system;
	}

	/**
	 * Processes a #define statement
	 *
	 * @param readOnlyBuf The buffer containing the statement
	 * @param startOffset The offset at which the statement starts
	 * @param length The length of the statement
	 */
	protected void handleDefine(@NotNull char[] readOnlyBuf, int startOffset, int length) {
		int maxOffset = startOffset + length;

		// skip the #define itself
		startOffset += "#define".length();

		StringBuilder macroName = new StringBuilder();
		int macroNameStartOffset = -1;

		int i;
		// get the name of the defined macro
		for (i = startOffset; i < maxOffset; i++) {
			char c = readOnlyBuf[i];

			if (Character.isWhitespace(c) && macroName.length() == 0) {
				// skip WS between #define and the macro name
				continue;
			}

			if (isMacroNamePart(c, i == startOffset)) {
				if (macroNameStartOffset == -1) {
					macroNameStartOffset = i;
				}

				macroName.append(c);
			} else {
				break;
			}
		}

		List<String> params = new ArrayList<>();

		// check for macro args
		boolean firstIteration = true;
		StringBuilder currentArg = new StringBuilder();
		for (; i < maxOffset; i++) {
			char c = readOnlyBuf[i];

			if (firstIteration) {
				firstIteration = false;

				if (c != '(') {
					// there are no macro arguments
					break;
				}
			} else {
				boolean removedLeadingWS = false;
				if (currentArg.length() == 0) {
					// Make sure there is no leading whitespace
					int begin = i;
					while (i + 1 < maxOffset && Character.isWhitespace(c)) {
						i++;
						c = readOnlyBuf[i];
						removedLeadingWS = true;
					}

					if (removedLeadingWS && isMacroNamePart(c, true)) {
						// report error about leading WS for macro argument
						lexer.problemEncountered(Problems.ERROR_LEADING_WS, "Leading whitespace before macro argument", begin,
								i - begin, -1);
					}
					// If we removed all WS but c is no macro name part, the error about pure WS argument will trigger below
				}
				// extract arguments
				if (isMacroNamePart(c, currentArg.length() == 0)) {
					currentArg.append(c);
				} else {
					// trim whitespace away
					int originalLength = currentArg.length();
					String strCurrentArg = currentArg.toString().trim();
					currentArg.setLength(0);

					if (strCurrentArg.length() == 0) {
						lexer.problemEncountered(Problems.ERROR_EMPTY, "Empty (or pure WS) macro argument in definition",
								 i - 1 - originalLength, originalLength, -1);
					}

					params.add(strCurrentArg);

					if (c == ')') {
						i++; // move pointer after paren
						break;
					}

					if (c != ',') {
						// something's wrong -> report
						lexer.problemEncountered(Problems.ERROR_INVALID_CHARACTER,
								"Detected invalid character while collecting macro arguments: '" + c
										+ "' (Expected comma or closing paren)",
								i, 1, -1);
					}
				}
			}
		}

		if (currentArg.length() > 0) {
			// unclosed paren -> issue an error
			lexer.problemEncountered(Problems.ERROR_UNCLOSED_PARENTHESIS, "Unclosed paren in macro-argument definition", i, 1, -1);

			// add the argument nonetheless
			int originalLength = currentArg.length();
			String strCurrentArg = currentArg.toString().trim();
			currentArg.setLength(0);

			if (currentArg.length() == 0) {
				lexer.problemEncountered(Problems.ERROR_EMPTY, "Empty (or pure WS) macro argument in definition",
						i - 1 - originalLength, originalLength, -1);
			}

			params.add(strCurrentArg);
		}

		while (i < maxOffset && readOnlyBuf[i] == ' ') {
			// skip all spaces before the macro body
			i++;
		}

		int remainingLength = maxOffset - i;

		if (configuration.preserveNewlines()) {
			// Feed back preserved newlines if enabled
			int NLCount = 0;
			for (int k = 0; k < remainingLength; k++) {
				if (readOnlyBuf[k + 1] == '\n') {
					NLCount++;
				}
			}

			if (NLCount > 0) {
				lexer.acceptPreservedNewlines(NLCount);
			}
		}

		BodySegment body = segmentParser.parseSegments(readOnlyBuf, i, remainingLength, params,
				(c, isFirstLetter) -> isMacroNamePart(c, isFirstLetter));


		PreProcessorMacro macro = new PreProcessorMacro(getMacroSet(), macroName.toString(), params, body);

		if (getMacroSet().containsKey(macroName.toString())) {
			// There already is a macro with that name -> warn about overwriting it
			lexer.problemEncountered(Problems.WARNING_OVERWRITE,
					"A macro with the name " + macroName + " does already exist and is being overwritten by this declaration.",
					macroNameStartOffset, macroName.length(), -1);
		}

		getMacroSet().put(macroName.toString(), macro);
	}

	/**
	 * Processes a #ifdef statement
	 *
	 * @param readOnlyBuf The buffer containing the statement
	 * @param startOffset The offset at which the statement starts
	 * @param length The length of the statement
	 */
	protected void handleIfDef(@NotNull char[] readOnlyBuf, int startOffset, int length) {
		// This is handled by the lexer so we can ignore it here
	}

	/**
	 * Processes a #ifndef statement
	 *
	 * @param readOnlyBuf The buffer containing the statement
	 * @param startOffset The offset at which the statement starts
	 * @param length The length of the statement
	 */
	protected void handleIfnDef(@NotNull char[] readOnlyBuf, int startOffset, int length) {
		// This is handled by the lexer so we can ignore it here
	}

	/**
	 * Processes a #else statement
	 *
	 * @param readOnlyBuf The buffer containing the statement
	 * @param startOffset The offset at which the statement starts
	 * @param length The length of the statement
	 */
	protected void handleElse(@NotNull char[] readOnlyBuf, int startOffset, int length) {
		// This is handled by the lexer so we can ignore it here
	}

	/**
	 * Processes a #endif statement
	 *
	 * @param readOnlyBuf The buffer containing the statement
	 * @param startOffset The offset at which the statement starts
	 * @param length The length of the statement
	 */
	protected void handleEndIf(@NotNull char[] readOnlyBuf, int startOffset, int length) {
		// This is handled by the lexer so we can ignore it here
	}

	/**
	 * Processes a #undef statement
	 *
	 * @param readOnlyBuf The buffer containing the statement
	 * @param startOffset The offset at which the statement starts
	 * @param length The length of the statement
	 */
	protected void handleUndef(@NotNull char[] readOnlyBuf, int startOffset, int length) {
		int maxOffset = startOffset + length;

		// skip the #undef itself
		startOffset += "#undef".length();

		StringBuilder macroName = new StringBuilder();
		int macroNameStartOffset = -1;

		// get the name of the macro
		for (int i = startOffset; i < maxOffset; i++) {
			char c = readOnlyBuf[i];

			if (Character.isWhitespace(c) && macroName.length() == 0) {
				// skip WS between #undef and the macro name
				continue;
			}

			if (isMacroNamePart(c, i == startOffset)) {
				if (macroNameStartOffset == -1) {
					macroNameStartOffset = i;
				}

				macroName.append(c);
			} else {
				break;
			}
		}

		if (getMacroSet().containsKey(macroName.toString())) {
			getMacroSet().remove(macroName.toString());
		} else {
			// Trying to undef a non-existing macro -> Warning
			lexer.problemEncountered(Problems.WARNING_UNDEFINE_NONEXISTENT,
					"The macro \"" + macroName + "\" is asked to be undefined, but it doesn't even exist.", macroNameStartOffset,
					macroName.length(), -1);
		}
	}

	/**
	 * Processes a #include statement
	 *
	 * @param readOnlyBuf The buffer containing the statement
	 * @param startOffset The offset at which the statement starts
	 * @param length The length of the statement
	 */
	protected void handleInclude(@NotNull char[] readOnlyBuf, int startOffset, int length) {
		int maxOffset = startOffset + length;

		// skip the #include itself
		startOffset += "#include".length();

		StringBuilder includePath = new StringBuilder();
		char delimiter = (char) -1;
		boolean pathClosed = false;
		int pathStartOffset = -1;

		// get the name of the macro
		for (int i = startOffset; i < maxOffset; i++) {
			char c = readOnlyBuf[i];

			if (Character.isWhitespace(c) && includePath.length() == 0) {
				// skip WS between #undef and the macro name
				continue;
			}

			if (delimiter == (char) -1) {
				switch (c) {
					case '"':
					case '\'':
						// TODO: check if single quoted argument is valid for #include
						delimiter = c;
						break;
					case '<':
						delimiter = '>';
						break;
					default:
						// invalid path
						lexer.problemEncountered(Problems.ERROR_INVALID_CHARACTER,
								"Expected \" or ' or < to open the path specification for the include statement.", i, 1, -1);
						return;
				}

				pathStartOffset = i;
			} else {
				if (c == delimiter) {
					pathClosed = true;
					break;
				}

				includePath.append(c);
			}
		}

		if (!pathClosed) {
			// include path wasn't closed
			lexer.problemEncountered(Problems.ERROR_UNCLOSED_STRING, "Unclosed String for include-path. Expected terminating " + delimiter,
					pathStartOffset, includePath.length() + 1, -1);
		}

		try {
			ArmaFile includeFle = fileSystem.resolve(includePath.toString());

			if (includeFle == null) {
				lexer.problemEncountered(Problems.ERROR_INVALID_PATH, "Couldn't resolve path \"" + includePath + "\"", pathStartOffset,
						includePath.length() + 2, -1);
			} else {
				lexer.acceptIncludedReader(OrinocoReader.fromStream(includeFle.getStream(), Charset.forName("utf-8")));
			}
		} catch (InvalidPathException e) {
			lexer.problemEncountered(Problems.ERROR_INVALID_PATH, "Invalid path \"" + includePath.toString() + "\": " + e.getMessage(),
					pathStartOffset, includePath.length() + 2, -1);
		} catch (FileNotFoundException e) {
			lexer.problemEncountered(Problems.ERROR_INVALID_PATH,
					"Resolved include-path to \"" + includePath.toString() + "\" but it doesn't seem to exist.", pathStartOffset,
					includePath.length() + 2, -1);
		}
	}

	@Override
	public void reset() {
		// Create a new one instead of clearing the existing one as the set will be referenced in the respective macros that might still be
		// used beyond this point
		macroSet = new MacroSet();
		lexer = null;

		// "Update" the dummy owner-macro
		dummyMacro = new PreProcessorMacro(macroSet, "__________________", Collections.emptyList(), new TextSegment(""));
	}

	/**
	 * Sets the {@link OrinocoTokenProcessor} for this preprocessor
	 * 
	 * @param processor The new processor to use
	 */
	public void setProcessor(@NotNull OrinocoTokenProcessor processor) {
		this.processor = processor;
	}
}
