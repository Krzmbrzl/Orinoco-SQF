package arma.orinocosqf;

import org.jetbrains.annotations.NotNull;

/**
 * A lightweight version of {@link StringBuilder}. This implementation has a non-shrinkable buffer
 * and direct access to the underlying char[] buffer ({@link #getCharsReadOnly()}) to prevent unneeded {@link #toString()} invocations.
 * @author K
 * @since 5/14/19
 */
public class LightweightStringBuilder implements CharSequence {
	private char[] chars = new char[256];
	private int cursor = 0;

	private void grow() {
		char[] copy = new char[chars.length * 2];
		System.arraycopy(chars, 0, copy, 0, copy.length);
		chars = copy;
	}

	public void append(@NotNull CharSequence cs) {
		while (cursor + cs.length() > chars.length) {
			grow();
		}
		for (int i = 0; i < cs.length(); i++) {
			chars[cursor++] = cs.charAt(i);
		}
	}

	public void setLength(int l) {
		cursor = l;
	}

	public char[] getCharsReadOnly() {
		return chars;
	}

	@Override
	@NotNull
	public String toString() {
		return new String(chars, 0, cursor);
	}

	@Override
	public int length() {
		return cursor;
	}

	@Override
	public char charAt(int i) {
		return chars[i];
	}

	@Override
	public CharSequence subSequence(int i, int endInd) {
		return new String(chars, i, endInd - i);
	}
}
