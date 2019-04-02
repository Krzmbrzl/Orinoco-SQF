package arma.orinocosqf;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.jetbrains.annotations.NotNull;

import arma.orinocosqf.bodySegments.BodySegment;
import arma.orinocosqf.bodySegments.BodySegmentSequence;
import arma.orinocosqf.bodySegments.GlueSegment;
import arma.orinocosqf.bodySegments.MacroArgumentSegment;
import arma.orinocosqf.bodySegments.ParenSegment;
import arma.orinocosqf.bodySegments.StringifySegment;
import arma.orinocosqf.bodySegments.TextSegment;
import arma.orinocosqf.bodySegments.WordSegment;

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

	public OrinocoPreProcessor(@NotNull OrinocoTokenProcessor p) {
		this.processor = p;

		macroSet = new MacroSet();
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

		BodySegment body = parseSegments(readOnlyBuf, i, remainingLength, params);

		System.out.println(body);
		System.out.println(body.toStringNoPreProcessing());

		PreProcessorMacro macro = new PreProcessorMacro(getMacroSet(), macroName.toString(), params, body);

		getMacroSet().put(macroName.toString(), macro);
	}

	protected BodySegment parseSegments(@NotNull char[] readOnlyBuf, int offset, int length, List<String> params) {
		int maxOffset = offset + length;

		Stack<SegmentList> segmentLists = new Stack<>();
		segmentLists.push(new SegmentList());

		StringBuilder currentSequence = new StringBuilder();

		boolean inWord = false;
		boolean inText = false;
		int parenLevel = 0;

		for (int i = offset; i < maxOffset; i++) {
			char c = readOnlyBuf[i];

			if (isMacroNamePart(c, currentSequence.length() == 0)) {
				if (inText) {
					// text ended here
					inText = false;
					segmentLists.peek().add(new TextSegment(currentSequence.toString()));
					currentSequence.setLength(0);
				}
				inWord = true;
				currentSequence.append(c);
			} else {
				if (inWord) {
					// word was ended here
					inWord = false;

					// could either be a Word- or a MacroArgumentSegment
					if (params.contains(currentSequence.toString())) {
						segmentLists.peek()
								.add(new MacroArgumentSegment(currentSequence.toString(), params.indexOf(currentSequence.toString())));
					} else {
						// its a normal word
						segmentLists.peek().add(new WordSegment(currentSequence.toString()));
					}

					currentSequence.setLength(0);
				}

				if (c == '#' || (parenLevel == 0 && c == '(') || (parenLevel == 1 && (c == ')') || (parenLevel > 0 && c == ','))) {
					// only allow opening parens to open new ParenSegment outside of a ParenSegment -> No nested ParenSegments
					// only allow the outermost closing paren to be processed here (to finish off the started ParenSegment
					// only check for closing paren and comma if inside a parenSegment
					// only check for comma in a ParenSegment
					if (inText) {
						inText = false;
						segmentLists.peek().add(new TextSegment(currentSequence.toString()));
						currentSequence.setLength(0);
					}

					// TODO: watch for Strings in paren-segments -> They can even contain commas, but have to use a balanced number of
					// quotes. Quote escaping is not supported

					switch (c) {
						case '#':
							if (i + 1 < maxOffset && readOnlyBuf[i + 1] == '#') {
								i++;
								// glue segment
								segmentLists.peek().inGlueSegment();
							} else {
								// stringify segment
								segmentLists.peek().inStringifySegment();
							}
							break;
						case '(':
							// paren-segment start
							parenLevel++;
							segmentLists.push(new SegmentList());
							break;
						case ')':
							// paren-segment end
							parenLevel--;
							SegmentList list = segmentLists.pop();
							list.listDone();

							if (i > offset && readOnlyBuf[i - 1] == ',') {
								// check if character right before was a comma
								// if yes, an empty "argument" has to be appended to the ParenSegment
								list.add(new TextSegment(""));
							}

							segmentLists.peek().add(new ParenSegment(list));
							break;
						case ',':
							// paren-segment separator
							if (i > offset && (readOnlyBuf[i - 1] == ',' || (parenLevel == 1 && readOnlyBuf[i - 1] == '('))) {
								// check if character right before was a comma
								// or if it was an opening paren that opened the ParenSegment (all other ones are considered TextSegments on their own)
								// if yes, an empty "argument" has to be appended to the ParenSegment
								segmentLists.peek().add(new TextSegment(""));
							}
							break;
					}
				} else {
					if (c == '(') {
						// nested parens -> Don't create nested paren-Segments but keep track of nesting
						parenLevel++;
					} else if (c == ')') {
						if (parenLevel == 0) {
							// TODO: error about unopened paren
							System.err.println("Encountered unopened paren");
						} else {
							parenLevel--;
						}
					}
					inText = true;
					currentSequence.append(c);
				}
			}
		}

		// add last segment
		if (inText) {
			segmentLists.peek().add(new TextSegment(currentSequence.toString()));
		} else if (inWord) {
			if (params.contains(currentSequence.toString())) {
				segmentLists.peek().add(new MacroArgumentSegment(currentSequence.toString(), params.indexOf(currentSequence.toString())));
			} else {
				segmentLists.peek().add(new WordSegment(currentSequence.toString()));
			}
		}

		if (parenLevel > 0) {
			// TODO: this could also be checked by segmentLists.size() > 1
			// TODO: Error about unclosed paren (need to be searched first though)
			System.err.println("Encountered unclosed paren");
		}

		SegmentList list = segmentLists.pop();
		list.listDone();

		return new BodySegmentSequence(list);
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
