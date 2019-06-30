package arma.orinocosqf;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public interface TextBuffer extends CharSequence, Appendable {
	/**
	 * Gets text from this buffer
	 *
	 * @param offset The offset of the first character of the text that is to be extracted
	 * @param length The length of the text that is to be extracted
	 * @return The requested text area
	 * @throws IndexOutOfBoundsException When the requested text-area is not contained in this buffer
	 */
	@NotNull
	String getText(int offset, int length) throws IndexOutOfBoundsException;

	/**
	 * @return All text buffered in this object
	 */
	@NotNull
	String getText();

	/**
	 * Gets text from this buffer and writes it into the given char-buffer
	 *
	 * @param buffer The buffer to write into (must at least be of length <code>offset + length</code>)
	 * @param boffset The offset inside the provided buffer at which the first retrieved character is written
	 * @param offset The offset of the first character of the text that is to be extracted
	 * @param length The length of the text that is to be extracted
	 * @throws IndexOutOfBoundsException When the requested text-area is not contained in this buffer or if the provided char[]-buffer is
	 *         not big enough to hold all of the requested text
	 */
	void getText(char[] buffer, int boffset, int offset, int length) throws IndexOutOfBoundsException;

	/**
	 * Appends text from the given buffer to this object
	 * 
	 * @param buffer The buffer to read the text from
	 * @param offset The start.offset from which on text should be transferred to this object
	 * @param length The length of the text area to be transferred
	 * @throws IndexOutOfBoundsException
	 * @throws IOException
	 */
	void append(char[] buffer, int offset, int length) throws IndexOutOfBoundsException, IOException;
}
