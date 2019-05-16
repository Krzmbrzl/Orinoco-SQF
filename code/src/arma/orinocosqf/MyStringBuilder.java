package arma.orinocosqf;

import org.jetbrains.annotations.NotNull;

/**
 * @author K
 * @since 5/14/19
 */
class MyStringBuilder {
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

	public int getLength() {
		return cursor;
	}

	public String asString() {
		return new String(chars, 0, cursor);
	}
}
