package arma.orinocosqf;

import org.jetbrains.annotations.NotNull;

import arma.orinocosqf.util.CharSequenceReader;

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

	/**
	 * Creates a new {@link OrinocoReader} from the given {@link CharSequence}
	 *
	 * @param cs The CharSequence to build the Reader from
	 * @return The instantiated Reader
	 */
	@NotNull
	public static OrinocoReader fromCharSequence(@NotNull CharSequence cs) {
		return new OrinocoReaderWrapper(new CharSequenceReader(cs));
	}

	/**
	 * Creates a new {@link OrinocoReader} from the given {@link File}
	 * (automatically opening an InputStream on it)
	 *
	 * @param f The File to build the Reader from
	 * @return The instantiated Reader
	 */
	@NotNull
	public static OrinocoReader fromFile(@NotNull File f, @NotNull Charset c) throws FileNotFoundException {
		return new OrinocoReaderWrapper(new InputStreamReader(new FileInputStream(f), c));
	}

	/**
	 * Creates a new {@link OrinocoReader} from the given {@link InputStream}
	 *
	 * @param in The InputStream to build the Reader from
	 * @return The instantiated Reader
	 */
	@NotNull
	public static OrinocoReader fromStream(@NotNull InputStream in, @NotNull Charset c) throws FileNotFoundException {
		return new OrinocoReaderWrapper(new InputStreamReader(in, c));
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
