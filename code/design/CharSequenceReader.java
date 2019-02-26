import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * An implementation of a {@link Reader} that works on a {@link CharSequence} as
 * an input. This prevents the need to use a {@link ByteArrayInputStream}
 * alongside with an {@link InputStreamReader}.
 * 
 * @author K
 * @since 02/20/2019
 */
public class CharSequenceReader extends Reader {
	private final CharSequence cs;
	private int cursor = 0;

	public CharSequenceReader(@NotNull CharSequence cs) {
		this.cs = cs;
	}

	@Override
	public int read(@NotNull char[] cbuf, int off, int len) {
		int count = 0;
		for (int i = off; i < len && cursor < cs.length(); i++) {
			cbuf[i] = cs.charAt(cursor++);
			count++;
		}
		return count;
	}

	@Override
	public void close() {
		// do nothing
	}

}
