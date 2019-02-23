import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * A facade class for a {@link Reader} interface.
 *
 * @author K
 * @since 02/20/2019
 */
public abstract class OrinocoReader extends Reader {

	@NotNull
	public static OrinocoReader fromCharSequence(@NotNull CharSequence cs) {
		return new OrinocoReaderWrapper(new CharSequenceReader(cs));
	}

	@NotNull
	public static OrinocoReader fromFile(@NotNull File f, @NotNull Charset c) throws FileNotFoundException {
		return new OrinocoReaderWrapper(new InputStreamReader(new FileInputStream(f), c));
	}

	public static class OrinocoReaderWrapper extends OrinocoReader {
		private final Reader r;

		public OrinocoReaderWrapper(@NotNull Reader r) {
			this.r = r;
		}

		@Override
		public int read(@NotNull char[] cbuf, int off, int len) throws IOException {
			return r.read(cbuf, off, len);
		}

		@Override
		public void close() throws IOException {
			r.close();
		}

		@Override
		public int read(@NotNull CharBuffer target) throws IOException {
			return r.read(target);
		}

		@Override
		public int read() throws IOException {
			return r.read();
		}

		@Override
		public int read(@NotNull char[] cbuf) throws IOException {
			return r.read(cbuf);
		}

		@Override
		public long skip(long n) throws IOException {
			return r.skip(n);
		}

		@Override
		public boolean ready() throws IOException {
			return r.ready();
		}

		@Override
		public boolean markSupported() {
			return r.markSupported();
		}

		@Override
		public void mark(int readAheadLimit) throws IOException {
			r.mark(readAheadLimit);
		}

		@Override
		public void reset() throws IOException {
			r.reset();
		}
	}
}
