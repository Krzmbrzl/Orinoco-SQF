package arma.orinocosqf.bodySegments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A parser that is able to transform macro bodies into a {@link BodySegment}-representation
 * 
 * @author Raven
 *
 */
public class BodySegmentParser {

	protected static final BodySegment EMPTY_SEGMENT = new TextSegment("");

	/**
	 * Checks whether the given character is contained in the given char-array
	 * 
	 * @param c The character to search for
	 * @param array The array to search in
	 * @return Whether or not the character is contained
	 */
	private static boolean isInCharArray(char c, char[] array) {
		for (char current : array) {
			if (current == c) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Helper-interface for detecting word-parts. It is used in {@link SegmentReader}
	 * 
	 * @author Raven
	 *
	 */
	public static interface WordPartDetector {
		public boolean isWordPart(char c, boolean isFirstLetter);
	}

	/**
	 * A helper reader-like class that facilitates the extraction of relevant parts from an input
	 * 
	 * @author Raven
	 *
	 */
	protected abstract static class SegmentReader {
		/**
		 * An implementation of a {@link WordPartDetector} that will be used to determine what characters will be part of a word
		 */
		WordPartDetector detector;


		/**
		 * @param detector An implementation of a {@link WordPartDetector} that will be used to determine what characters will be part of a
		 *        word
		 */
		public SegmentReader(@NotNull WordPartDetector detector) {
			this.detector = detector;
		}

		/**
		 * @return The next character in the input or <code>(char) -1</code> if there is no next character
		 */
		public abstract char nextChar();

		/**
		 * @return The character in the input to the left of the character that has been read the last time {@link #nextChar()} was called
		 *         or <code>(char) -1</code> if there is no such character. A call to this method doesn't affect the state of this reader in
		 *         any way.
		 */
		public abstract char previousChar();

		/**
		 * @return The character {@link #nextChar()} returned the last time it was invoked or <code>(char) -1</code> if there is no such
		 *         character. A call to this method doesn't affect the state of this reader in any way.
		 */
		public abstract char currentChar();

		/**
		 * @return How many characters are remaining to be read in this reader
		 */
		public abstract int charsRemaining();

		/**
		 * Rewinds the char-pointer by one position so that the next call to {@link #nextChar()} yields the same character as the previous
		 * call to it. Therefore this function mimics a basic unread-functionality
		 */
		public abstract void rewindChar();

		/**
		 * Reads a all characters of a word from the current position of this reader onwards
		 * 
		 * @param targetBuf The target buffer in which the word should be read
		 * @return The length of the read word
		 * @throws IOException This method itself doesn't throw this exception. The only case such an exception might arise is if the given
		 *         targetBuf's append-method throws it.
		 */
		public int readWord(@NotNull Appendable targetBuf) throws IOException {
			int letters = 0;
			boolean isFirstLetter = true;
			char c = nextChar();

			while (c != (char) -1 && detector.isWordPart(c, isFirstLetter)) {
				targetBuf.append(c);
				letters++;

				isFirstLetter = false;
				c = nextChar();
			}

			// "unread" last character as this one wasn't part of the word anymore
			rewindChar();

			return letters;
		}

		/**
		 * Reads a sequence of text. A sequence of text is a sequence of characters that can't be part of a word. Additionally this sequence
		 * may be delimited by the given breakChars.
		 * 
		 * @param targetBuf The target buffer to read the sequence into
		 * @param breakChars An array of characters that will serve as additional delimiters for the sequence
		 * @return The amount of read characters.
		 * @throws IOException This method itself doesn't throw this exception. The only case such an exception might arise is if the given
		 *         targetBuf's append-method throws it.
		 */
		public int readSequence(@NotNull Appendable targetBuf, @NotNull char[] breakChars) throws IOException {
			int letters = 0;

			char c = nextChar();

			// read sequence
			while (c != (char) -1) {
				if (detector.isWordPart(c, true) || isInCharArray(c, breakChars)) {
					// exit loop as sequence ends here
					break;
				}

				letters++;
				targetBuf.append(c);
				c = nextChar();
			}

			// "unread" last character as this one wasn't part of the word anymore
			rewindChar();

			return letters;
		}

		/**
		 * Reads a word or a sequence of text. If the first character read can be a word-start (determined by {@link #detector}), the full
		 * word will be read (ignoring the given breakChars). Otherwise a sequence is read.
		 * 
		 * @param targetBuf The target buffer to read the word/sequence into
		 * @param breakChars An array of characters that will serve as additional delimiters for the sequence
		 * @return The amount of read characters.
		 * @throws IOException This method itself doesn't throw this exception. The only case such an exception might arise is if the given
		 *         targetBuf's append-method throws it.
		 * 
		 * @see #readWord(Appendable)
		 * @see #readSequence(Appendable, char[])
		 */
		public int readWordOrSequence(@NotNull Appendable targetBuf, @NotNull char[] breakChars) throws IOException {
			// peek at next character
			char c = nextChar();
			rewindChar();

			if (c == (char) -1) {
				return 0;
			}

			if (detector.isWordPart(c, true)) {
				// read a word
				return readWord(targetBuf);
			} else {
				// read a sequence
				return readSequence(targetBuf, breakChars);
			}
		}

		/**
		 * Reads a String delimited by the given character from the input into the given buffer. The String is read into the buffer
		 * including the quotes. If the character next read by {@link #nextChar()} isn't the starting delimiter, this method doesn't read
		 * anything.
		 * 
		 * @param targetBuf The buffer to read the string into
		 * @param stringDelimiter The delimiter of the string to read (typically single or double quoteI
		 * @return The amount of read characters
		 * @throws IOException This method itself doesn't throw this exception. The only case such an exception might arise is if the given
		 *         targetBuf's append-method throws it.
		 */
		public int readString(@NotNull Appendable targetBuf, char stringDelimiter) throws IOException {
			int letters = 0;

			char c = nextChar();

			if (c != stringDelimiter) {
				rewindChar();
				return 0;
			}

			letters++;
			targetBuf.append(c);

			c = nextChar();

			while (c != stringDelimiter && c != (char) -1) {
				letters++;
				targetBuf.append(c);

				c = nextChar();
			}

			if (c != stringDelimiter) {
				// unclosed string -> Error
				// TODO: report error
				System.err.println("Unclosed string in macro body (delimiter: " + stringDelimiter + ")");
			} else {
				targetBuf.append(c);
				letters++;
			}

			return letters;
		}
	}

	/**
	 * An implementation of a {@link SegmentReader} that uses a char-array as input
	 * 
	 * @author Raven
	 *
	 */
	protected static class CharBuffReader extends SegmentReader {
		/**
		 * The input char-array
		 */
		char[] buf;
		/**
		 * The offset at which the first character (that should be considered) in the char-array is located. All characters before this
		 * index won't be accessible by this reader
		 */
		int startOffset;
		/**
		 * The length of the input. All characters after <code>startOffset + length</code> in the char-array won't be accessible by this
		 * reader
		 */
		int length;
		/**
		 * The amount of characters that have been read from the input so far
		 */
		int readCharacters;


		/**
		 * @param detector An implementation of a {@link WordPartDetector} that will be used to determine what characters will be part of a
		 *        word
		 * @param buf The char-array used as an input
		 * @param startOffset The offset at which the first character (that should be considered) in the char-array is located. All
		 *        characters before this index won't be accessible by this reader.
		 * @param length The length of the input. All characters after <code>startOffset + length</code> in the char-array won't be
		 *        accessible by this reader
		 */
		public CharBuffReader(@NotNull WordPartDetector detector, @NotNull char[] buf, int startOffset, int length) {
			super(detector);

			if (length + startOffset > buf.length) {
				throw new IllegalArgumentException("Specified offset+length exceeds the given buffer's size!");
			}

			this.buf = buf;
			this.startOffset = startOffset;
			this.length = length;
			readCharacters = 0;
		}

		@Override
		public char nextChar() {
			if (readCharacters >= length) {
				readCharacters++;
				// There aren't any more characters
				return (char) -1;
			}

			return buf[startOffset + readCharacters++];
		}

		@Override
		public void rewindChar() {
			if (readCharacters > 0) {
				readCharacters--;
			}
		}

		@Override
		public int charsRemaining() {
			return length - readCharacters;
		}

		@Override
		public char previousChar() {
			// -2 as the pointer is designed to point to the next char already
			return readCharacters < 2 ? (char) -1 : buf[startOffset + readCharacters - 2];
		}

		@Override
		public char currentChar() {
			return readCharacters == 0 ? (char) -1 : buf[startOffset + readCharacters - 1];
		}

	}

	protected char[] textDelimiters;
	protected char[] textDelimitersInParenexpressions;

	public BodySegmentParser() {
		textDelimiters = new char[] { '(', ')', '#', '"' };
		textDelimitersInParenexpressions = new char[] { '(', ')', '#', '"', ',' };
	}

	/**
	 * Parses the segments of a macro body from a char-array
	 * 
	 * @param bufReadOnly The char-array to use as an input
	 * @param offset The offset inside the given array at which the first character of the macro body is to be found
	 * @param length The length of the macro body
	 * @param params A List containing the names of the parameter the macro, whose body should be parsed, takes.
	 * @param detector An implementation of a {@link WordPartDetector} that will be used to determine what characters will be part of a word
	 * @return The {@link BodySegment} fully describing the complete macro body
	 */
	@NotNull
	public BodySegment parseSegments(@NotNull char[] bufReadOnly, int offset, int length, List<String> params, WordPartDetector detector) {
		return doParseSegments(new CharBuffReader(detector, bufReadOnly, offset, length), params, false);
	}

	/**
	 * Performs the actual segment parsing by looping and recursive calling this function again.
	 * 
	 * @param reader The {@link SegmentReader} that is used to access the input
	 * @param params A List containing the names of the parameter the macro, whose body should be parsed, takes.
	 * @param insideParenSegment Whether the inside of a {@link ParenSegment} is currently processed. If in doubt, set to <code>false</code>
	 * @return The {@link BodySegment} fully describing the complete macro body
	 */
	@NotNull
	protected BodySegment doParseSegments(@NotNull SegmentReader reader, @NotNull List<String> params, boolean inParenExpression) {
		if (reader.charsRemaining() == 0) {
			return EMPTY_SEGMENT;
		}

		List<BodySegment> segmentContainer = new ArrayList<>();

		while (reader.charsRemaining() > 0) {
			parseSegmentSequence(reader, segmentContainer, params, inParenExpression ? textDelimitersInParenexpressions : textDelimiters);

			try {
				char c = reader.nextChar();

				switch (c) {
					case '(':
						// create new paren-expression
						List<BodySegment> parenSegments = new ArrayList<>();

						BodySegment nextSegment = doParseSegments(reader, params, true);
						if (reader.nextChar() == ')' && nextSegment == EMPTY_SEGMENT) {
							// special case for empty parens
							segmentContainer.add(new ParenSegment(parenSegments));
							break;
						} else {
							reader.rewindChar();
						}

						parenSegments.add(nextSegment);

						char nextC = reader.nextChar();

						while (nextC == ',') {
							// further elements in the list
							parenSegments.add(doParseSegments(reader, params, true));

							nextC = reader.nextChar();
						}

						if (nextC != ')') {
							// TODO: improve error handling
							throw new IllegalStateException("Unclosed paren-Expression");
						}

						segmentContainer.add(new ParenSegment(parenSegments));
						break;

					case '#':
						nextC = reader.nextChar();

						if (nextC != '#') {
							// read char back, so that nextStandardSegment can account for it
							reader.rewindChar();
						}

						StringBuilder segmentBuilder = new StringBuilder();
						nextSegment = nextStandardSegment(reader, segmentBuilder, params,
								inParenExpression ? textDelimitersInParenexpressions : textDelimiters);

						if (nextC == '#') {
							// glue segment
							BodySegment leftArg = segmentContainer.size() > 0 ? segmentContainer.remove(segmentContainer.size() - 1) : null;
							segmentContainer.add(new GlueSegment(leftArg, nextSegment));
						} else {
							// stringify segment
							if (nextSegment instanceof TextSegment) {
								// text-segments can't be stringified
								segmentContainer.add(new StringifySegment(null));
								segmentContainer.add(nextSegment);
							} else {
								segmentContainer.add(new StringifySegment(nextSegment));
							}
						}
						break;

					case '"':
						reader.rewindChar();
						StringBuilder stringContent = new StringBuilder();
						reader.readString(stringContent, '"');

						appendText(stringContent.toString(), segmentContainer);
						break;

					case ')':
					case ',':
						if (inParenExpression) {
							// the current paren-element is terminated -> the creation of this ParenSegemnt is done higher in the recursion
							// hierarchy
							reader.rewindChar();
							if (segmentContainer.size() == 0) {
								return EMPTY_SEGMENT;
							} else {
								return segmentContainer.size() > 1 ? new BodySegmentSequence(segmentContainer) : segmentContainer.get(0);
							}
						} else {
							// this is to be treated as normal text
							appendText(String.valueOf(c), segmentContainer);
						}
						break;

					case (char) -1:
						// EOI
						break;

					default:
						// TODO: improve error handling
						throw new IllegalStateException("Encountered unexpected character '" + c + "'");
				}
			} catch (IOException e) {
				// Should be unreachable
				e.printStackTrace();
			}
		}

		if (inParenExpression) {
			return new ParenSegment(segmentContainer);
		} else {
			if (segmentContainer.size() == 0) {
				// TODO: improve error handling
				throw new IllegalStateException("Trying to return empty element");
			}
			return segmentContainer.size() > 1 ? new BodySegmentSequence(segmentContainer) : segmentContainer.get(0);
		}
	}

	/**
	 * Parses a sequence of standard segments. If there are no such segments at the current input position, the method returns without doing
	 * anything.
	 * 
	 * @param reader The {@link SegmentReader} that is used to access the input
	 * @param segmentContainer The list to add the parsed segments to
	 * @param params A List containing the names of the parameter the macro, whose body should be parsed, takes.
	 * @param textDelimiters The characters delimiting text-segments
	 */
	protected void parseSegmentSequence(@NotNull SegmentReader reader, @NotNull List<BodySegment> segmentContainer,
			@NotNull List<String> params, @NotNull char[] textDelimiters) {
		BodySegment currentSegment = null;

		StringBuilder segmentBuilder = new StringBuilder();

		do {
			try {
				currentSegment = nextStandardSegment(reader, segmentBuilder, params, textDelimiters);

				if (currentSegment != null) {
					if (currentSegment instanceof TextSegment && segmentContainer.size() > 0
							&& segmentContainer.get(segmentContainer.size() - 1) instanceof TextSegment) {
						// append the text rather than creating a new segment
						((TextSegment) segmentContainer.get(segmentContainer.size() - 1)).append(((TextSegment) currentSegment).text);
					} else {
						segmentContainer.add(currentSegment);
					}
				}
			} catch (IOException e) {
				// Should be unreachable
				e.printStackTrace();
			}
		} while (currentSegment != null);
	}

	/**
	 * Appends the given text to the given list of {@link BodySegment}s. If the previous segment in the list is a {@link TextSegment}, the
	 * text will be appended to it. Otherwise a new TextSegment will be created and added to the list
	 * 
	 * @param text The text to add
	 * @param list The list to add to
	 */
	protected void appendText(@NotNull String text, @NotNull List<BodySegment> list) {
		if (list.size() > 0 && list.get(list.size() - 1) instanceof TextSegment) {
			// merge with previous TextSegment
			((TextSegment) list.get(list.size() - 1)).append(text);
		} else {
			// add as new segment
			list.add(new TextSegment(text));
		}
	}

	/**
	 * Reads the next standard segment in the input. A standard segment is either a word or a sequence
	 * 
	 * @param reader The reader to use to access the input
	 * @param buffer The buffer to read into. On a successful match, it will be cleared of its contents.
	 * @param params A list containing the names of the parameter the currently processed macro takes
	 * @param breakChars The characters to break a sequence at
	 * @return The {@link BodySegment} representing the read segment or <code>null</code> if none was read.
	 * @throws IOException This method itself doesn't throw this exception. The only case such an exception might arise is if the given
	 *         buffer's append-method throws it.
	 * 
	 * @see {@link SegmentReader#readWord(Appendable)}
	 * @see {@link SegmentReader#readSequence(Appendable, char[])}
	 */
	@Nullable
	protected BodySegment nextStandardSegment(SegmentReader reader, StringBuilder buffer, List<String> params, char[] breakChars)
			throws IOException {
		if (reader.readWord(buffer) > 0) {
			// a word has been read
			String strSegment = buffer.toString();
			buffer.setLength(0);

			if (params.contains(strSegment)) {
				// it's a macro argument
				return new MacroArgumentSegment(strSegment, params.indexOf(strSegment));
			} else {
				// it's just a word
				return new WordSegment(strSegment);
			}
		} else if (reader.readSequence(buffer, breakChars) > 0) {
			// a sequence has been read
			String strBuf = buffer.toString();
			buffer.setLength(0);
			return new TextSegment(strBuf);
		}

		return null;
	}
}
