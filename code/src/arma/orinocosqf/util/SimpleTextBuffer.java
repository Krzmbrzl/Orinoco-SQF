package arma.orinocosqf.util;

import arma.orinocosqf.TextBuffer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author K
 * @since 5/13/19
 */
public class SimpleTextBuffer implements TextBuffer {
	private final StringBuilder sb;

	public SimpleTextBuffer(@NotNull CharSequence cs) {
		sb = new StringBuilder(cs.length());
		sb.append(cs);
	}

	public SimpleTextBuffer() {
		this.sb = new StringBuilder();
	}

	@Override
	@NotNull
	public String getText(int offset, int length) throws IndexOutOfBoundsException {
		return sb.substring(offset, offset + length);
	}

	@Override
	public void getText(char[] buffer, int boffset, int offset, int length) throws IndexOutOfBoundsException {
		for (int i = 0; i < length; i++) {
			buffer[boffset + i] = sb.charAt(i + offset);
		}
	}

	@Override
	public void append(char[] buffer, int offset, int length) throws IndexOutOfBoundsException, IOException {
		sb.append(buffer, offset, length);
	}

	@Override
	public int length() {
		return sb.length();
	}

	@Override
	public char charAt(int i) {
		return sb.charAt(i);
	}

	@Override
	public CharSequence subSequence(int i, int i1) {
		return sb.subSequence(i, i1);
	}

	@Override
	public Appendable append(CharSequence charSequence) throws IOException {
		sb.append(charSequence);
		return sb;
	}

	@Override
	public Appendable append(CharSequence charSequence, int i, int i1) throws IOException {
		sb.append(charSequence, i, i1);
		return sb;
	}

	@Override
	public Appendable append(char c) throws IOException {
		sb.append(c);
		return sb;
	}

	@Override
	public @NotNull String getText() {
		return sb.toString();
	}
}
