package arma.orinocosqf;

import org.jetbrains.annotations.NotNull;

/**
 * @author K
 * @since 5/13/19
 */
public class SimpleTextBuffer implements TextBuffer {
	private final CharSequence cs;

	public SimpleTextBuffer(@NotNull CharSequence cs) {
		this.cs = cs;
	}

	@Override
	@NotNull
	public String getText(int offset, int length) throws IndexOutOfBoundsException {
		char[] chars = new char[length];
		System.out.println("SimpleTextBuffer.getText cs.length()=" + cs.length());
		System.out.println("SimpleTextBuffer.getText cs=" + cs);
		System.out.println("SimpleTextBuffer.getText offset=" + offset);
		System.out.println("SimpleTextBuffer.getText length=" + length);
		for (int i = 0; i < length; i++) {
			System.out.println(cs.charAt(i + offset));
			chars[i] = cs.charAt(i + offset);
		}

		return new String(chars);
	}

	@Override
	public void getText(char[] buffer, int boffset, int offset, int length) throws IndexOutOfBoundsException {
		for (int i = 0; i < length; i++) {
			buffer[boffset + i] = cs.charAt(i + offset);
		}
	}

	@Override
	public int length() {
		return cs.length();
	}

	@Override
	public char charAt(int i) {
		return cs.charAt(i);
	}

	@Override
	public CharSequence subSequence(int i, int i1) {
		return cs.subSequence(i, i1);
	}
}
