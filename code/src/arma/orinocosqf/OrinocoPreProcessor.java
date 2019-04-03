package arma.orinocosqf;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

import arma.orinocosqf.bodySegments.BodySegment;
import arma.orinocosqf.bodySegments.BodySegmentParser;
import arma.orinocosqf.bodySegments.GlueSegment;
import arma.orinocosqf.bodySegments.StringifySegment;

/**
 * A {@link OrinocoLexerStream} implementation that fully preprocesses tokens.
 *
 * @author K
 * @since 02/20/2019
 */
public class OrinocoPreProcessor implements OrinocoLexerStream {
	private OrinocoLexer lexer;
	private final OrinocoTokenProcessor processor;
	protected MacroSet macroSet;
	protected BodySegmentParser segmentParser;

	public OrinocoPreProcessor(@NotNull OrinocoTokenProcessor p) {
		this.processor = p;

		macroSet = new MacroSet();
		segmentParser = new BodySegmentParser();
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

	}

	@Override
	public void acceptComment(int originalOffset, int originalLength, int preprocessedOffset, int preprocessedLength,
			@NotNull OrinocoLexerContext ctx) {
		// TODO: extract NLs and feed them back to lexer if comments aren't kept anways
	}

	/**
	 * Checks whether the given character can be part of a macro name (or macro-argument-name)
	 * 
	 * @param c The character to check
	 * @return Whether or not it can be a macro name part
	 */
	protected boolean isMacroNamePart(char c, boolean isFirstLetter) {
		return Character.isLetter(c) || c == '_' || (!isFirstLetter && Character.isDigit(c));
	}

	protected MacroSet getMacroSet() {
		return this.macroSet;
	}

	protected void handleDefine(@NotNull char[] readOnlyBuf, int startOffset, int length) {
		int maxOffset = startOffset + length;
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
					params.add(currentArg.toString());
					currentArg.setLength(0);

					if (c == ')') {
						i++; // move pointer after paren
						break;
					}

					if (c != ',') {
						// something's wrong
						// TODO: produce error/warning
						System.err.println("Detected invalid character while collecting macro arguments: '" + c + "'");
					}
				}
			}
		}

		if (currentArg.length() > 0) {
			// unclosed paren
			// TODO: Error
			System.err.println("Unclosed paren in macro-argument definition");
		}

		while (i < maxOffset && readOnlyBuf[i] == ' ') {
			// skip all spaces before the macro body
			i++;
		}

		StringBuilder macroBody = new StringBuilder();

		int remainingLength = maxOffset - i;
		// TODO: make NL-keeping toggleable
		for (int k = 0; k < remainingLength; k++) {
			macroBody.append(readOnlyBuf[i + k]);

			// Check for any
			switch (readOnlyBuf[k + 1]) {
				case '\r':
					// handle CRLF
					if (k + 1 < remainingLength && readOnlyBuf[k + 1 + i] == '\n') {
						k++;
						lexer.acceptPreProcessedText("\r\n");
					}
					// ignore lonely \r
					break;
				case '\n':
					// keep newline
					lexer.acceptPreProcessedText("\n");
					break;

				default:
					break;
			}
		}

		System.out.println(macroBody.toString());

		BodySegment body = segmentParser.parseSegments(readOnlyBuf, i, remainingLength, params,
				(c, isFirstLetter) -> isMacroNamePart(c, isFirstLetter));

		System.out.println(body);
		System.out.println(body.toStringNoPreProcessing());

		PreProcessorMacro macro = new PreProcessorMacro(getMacroSet(), macroName.toString(), params, body);

		getMacroSet().put(macroName.toString(), macro);
	}

	public static class SegmentList extends ArrayList<BodySegment> {

		private static final long serialVersionUID = -3483333718792570123L;
		boolean inGlueSegment;
		boolean inStringifySegment;


		@Override
		public boolean add(BodySegment segment) {
			if (inGlueSegment) {
				BodySegment left = this.size() > 0 ? this.remove(this.size() - 1) : null;

				if (inStringifySegment) {
					// Such a situation could arise from something like this: #define TEST this###test
					// StringifySegment is right argument of GlueSegment
					super.add(new GlueSegment(left, new StringifySegment(segment)));
				} else {
					super.add(new GlueSegment(left, segment));
				}
			} else if (inStringifySegment) {
				super.add(new StringifySegment(segment));
			}

			if (!inGlueSegment && !inStringifySegment) {
				super.add(segment);
			}

			inStringifySegment = false;
			inGlueSegment = false;

			return true;
		}

		/**
		 * Sets this list to produce a GlueSegment, the next time {@link #add(BodySegment)} is invoked
		 */
		public void inGlueSegment() {
			if (inGlueSegment) {
				inGlueSegment = false;

				BodySegment left = this.size() > 0 ? this.remove(this.size() - 1) : null;
				this.add(new GlueSegment(left, null));
			}
			if (inStringifySegment) {
				// This should be unreachable as any consecutive hashtags should result in glue-segments
				// TODO: issue error -> this is unexpected behaviour
				throw new IllegalStateException("Unexpected program flow");
			}

			inGlueSegment = true;
		}

		/**
		 * Sets this list to produce a StringifySegment, the next time {@link #add(BodySegment)} is invoked
		 */
		public void inStringifySegment() {
			if (inStringifySegment) {
				// This should be unreachable as any consecutive hashtags should result in glue-segments
				// TODO: issue error -> this is unexpected behaviour
				throw new IllegalStateException("Unexpected program flow");
			}

			inStringifySegment = true;
		}

		/**
		 * If there still is a pending Glue- and/or StringifySegment, this method will create them with null-arguments as there won't be
		 * another call to {@link #add(BodySegment)}
		 */
		public void listDone() {
			if (inStringifySegment) {
				super.add(new StringifySegment(null));
			}
			if (inGlueSegment) {
				BodySegment left = this.size() > 0 ? this.remove(this.size() - 1) : null;
				super.add(new GlueSegment(left, null));
			}
			inStringifySegment = false;
			inGlueSegment = false;
		}
	}

	public static void main(String[] args) {
		OrinocoPreProcessor p = new OrinocoPreProcessor(null);

		// TODO: This should only stringify the opening paren, not the whole ParenSegment
		String content = "#define myMacro(Some,body)  #(a,(,body,))";

		p.acceptPreProcessorCommand(PreProcessorCommand.Define, content.toCharArray(), 0, content.length());
	}


}
