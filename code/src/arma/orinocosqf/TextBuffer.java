package arma.orinocosqf;

import org.jetbrains.annotations.NotNull;

public interface TextBuffer extends CharSequence {
	/**
	 * Gets text from this buffer
	 * 
	 * @param offset The offset of the first character of the text that is to be extracted
	 * @param length The length of the text that is to be extracted
	 * 
	 * @return The requested text area
	 * 
	 * @throws IndexOutOfBoundsException When the requested text-area is not contained in this buffer
	 */
	@NotNull
	public String getText(int offset, int length) throws IndexOutOfBoundsException;

	/**
	 * Gets text from this buffer and writes it into the given char-buffer
	 * 
	 * @param buffer The buffer to write into (must at least be of length <code>boffset + length</code>)
	 * @param boffset The offset inside the provided buffer at which the first retrieved character is written
	 * @param offset The offset of the first character of the text that is to be extracted
	 * @param length The length of the text that is to be extracted
	 * 
	 * @throws IndexOutOfBoundsException When the requested text-area is not contained in this buffer or if the provided char[]-buffer is
	 *         not big enough to hold all of the requested text
	 */
	public void getText(char[] buffer, int boffset, int offset, int length) throws IndexOutOfBoundsException;
}
