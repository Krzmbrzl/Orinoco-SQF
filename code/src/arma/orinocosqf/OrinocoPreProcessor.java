package arma.orinocosqf;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

import arma.orinocosqf.bodySegments.BodySegment;
import arma.orinocosqf.bodySegments.BodySegmentParser;
import arma.orinocosqf.problems.Problems;

/**
 * A {@link OrinocoLexerStream} implementation that fully preprocesses tokens.
 *
 * @author Raven
 * @since 02/20/2019
 */
public class OrinocoPreProcessor implements OrinocoLexerStream {
	/**
	 * The lexer that is being used to feed tokens into this preprocessor
	 */
	private OrinocoLexer lexer;
	/**
	 * The {@link OrinocoTokenProcessor} to delegate method calls to
	 */
	private final OrinocoTokenProcessor processor;
	/**
	 * The {@link MacroSet} of the current preprocessing run
	 */
	protected MacroSet macroSet;
	/**
	 * The {@link BodySegmentParser} used to parse define-bodies or other preprocessor-structures
	 */
	protected BodySegmentParser segmentParser;


	public OrinocoPreProcessor(@NotNull OrinocoTokenProcessor p) {
		this.processor = p;

		macroSet = new MacroSet();
		segmentParser = new BodySegmentParser(lexer);
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
				break;
			case Define:
				handleDefine(bufReadOnly, offset, length);
				break;
			case Else:
				break;
			case EndIf:
				break;
			case IfNDef:
				break;
			case Include:
				break;
			case Undef:
				break;
			default:
				break;
		}
	}

	@Override
	public void preProcessToken(@NotNull char[] bufReadOnly, int offset, int length) {

	}

	@Override
	public void begin() {
		this.processor.begin();
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
	public void acceptLiteral(@NotNull OrinocoLexerLiteralType type, int preprocessedOffset, int preprocessedLength, int originalOffset,
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
	public void end() {
		this.processor.end();
	}

	@Override
	public void acceptWhitespace(int originalOffset, int originalLength, int preprocessedOffset, int preprocessedLength,
			@NotNull OrinocoLexerContext ctx) {
		// search for NLs
		ctx.getTextBuffer().getText(originalOffset, originalLength);
	}

	@Override
	public void acceptComment(int originalOffset, int originalLength, int preprocessedOffset, int preprocessedLength,
			@NotNull OrinocoLexerContext ctx) {
		// TODO: extract NLs and feed them back to lexer if comments aren't kept anyways
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

	protected MacroSet getMacroSet() {
		return this.macroSet;
	}

	/**
	 * Processes a #define statement
	 * @param readOnlyBuf The buffer containing the statement
	 * @param startOffset The offset at which the statement starts
	 * @param length The length of the statement
	 */
	protected void handleDefine(@NotNull char[] readOnlyBuf, int startOffset, int length) {
		int maxOffset = startOffset + length;
		
		// skip the #define itself
		startOffset += "#define".length();

		StringBuilder macroName = new StringBuilder();

		int i;
		// get the name of the defined macro
		for (i = startOffset; i < maxOffset; i++) {
			char c = readOnlyBuf[i];

			if (Character.isWhitespace(c) && macroName.length() == 0) {
				// skip WS between #define and the macro name
				continue;
			}

			if (isMacroNamePart(c, i == startOffset)) {
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
				// extract arguments
				if (isMacroNamePart(c, currentArg.length() == 0)) {
					currentArg.append(c);
				} else {
					// trim whitespace away
					int originalLength = currentArg.length();
					String strCurrentArg = currentArg.toString().trim();
					currentArg.setLength(0);

					if (currentArg.length() == 0) {
						lexer.problemEncountered(Problems.EMPTY, "Empty (or pure WS) macro argument in definition",
								startOffset + i - 1 - originalLength, originalLength, -1);
					}

					params.add(strCurrentArg);

					if (c == ')') {
						i++; // move pointer after paren
						break;
					}

					if (c != ',') {
						// something's wrong -> report
						lexer.problemEncountered(Problems.INVALID_CHARACTER,
								"Detected invalid character while collecting macro arguments: '" + c
										+ "' (Expected comma or closing paren)",
								startOffset + i, 1, -1);
					}
				}
			}
		}

		if (currentArg.length() > 0) {
			// unclosed paren -> issue an error
			lexer.problemEncountered(Problems.UNCLOSED_PARENTHESIS, "Unclosed paren in macro-argument definition", i, 1, -1);
			
			// add the argument nonetheless
			int originalLength = currentArg.length();
			String strCurrentArg = currentArg.toString().trim();
			currentArg.setLength(0);

			if (currentArg.length() == 0) {
				lexer.problemEncountered(Problems.EMPTY, "Empty (or pure WS) macro argument in definition",
						startOffset + i - 1 - originalLength, originalLength, -1);
			}

			params.add(strCurrentArg);
		}

		while (i < maxOffset && readOnlyBuf[i] == ' ') {
			// skip all spaces before the macro body
			i++;
		}

		StringBuilder macroBody = new StringBuilder();

		int remainingLength = maxOffset - i;
		boolean usesCRLF = false;
		int NLCount = 0;
		
		for (int k = 0; k < remainingLength; k++) {
			macroBody.append(readOnlyBuf[i + k]);

			// Check for any
			switch (readOnlyBuf[k + 1]) {
				case '\r':
					usesCRLF = true;
					break;
				case '\n':
					NLCount++;
				default:
					break;
			}
		}

		BodySegment body = segmentParser.parseSegments(readOnlyBuf, i, remainingLength, params,
				(c, isFirstLetter) -> isMacroNamePart(c, isFirstLetter));


		PreProcessorMacro macro = new PreProcessorMacro(getMacroSet(), macroName.toString(), params, body);

		getMacroSet().put(macroName.toString(), macro);
		
		// Feed Newlines from macro body back to the lexer in order to preserve them
		// TODO: make NL-keeping toggleable
		String nl = usesCRLF ? "\r\n" : "\n";
		for (int k=0; k < NLCount; k++) {
			lexer.acceptPreProcessedText(nl);
		}
	}
}
