package arma.orinocosqf.lexer;

import arma.orinocosqf.TextBuffer;
import arma.orinocosqf.exceptions.UnknownIdException;
import arma.orinocosqf.sqf.SQFCommands;
import arma.orinocosqf.util.SimpleTextBuffer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Writer;

/**
 * @author K
 * @since 5/13/19
 */
public class SimpleOrinocoLexerContext implements OrinocoLexerContext {
	private final OrinocoLexer lexer;
	private TextBuffer originalText;
	private final StringBuilderWriter preprocessedWriter = new StringBuilderWriter();
	private final SimpleTextBuffer preprocessedBuffer = new SimpleTextBuffer(preprocessedWriter);

	public SimpleOrinocoLexerContext(@NotNull OrinocoLexer lexer, @NotNull TextBuffer originalText) {
		this.lexer = lexer;
		this.originalText = originalText;
		lexer.setPreprocessedResultWriter(preprocessedWriter);
	}


	@Override
	public @NotNull String getCommand(int id) throws UnknownIdException {
		String c = SQFCommands.instance.getCommandNameById(id);
		if (c == null) {
			throw new UnknownIdException(id + "");
		}
		return c;
	}

	@Override
	public @Nullable String getVariable(int id) {
		try {
			return lexer.getIdTransformer().fromId(id);
		} catch (UnknownIdException ignore) {
			return null;
		}
	}

	@Override
	public boolean isTextBufferingEnabled() {
		return true;
	}

	@Override
	@Nullable
	public TextBuffer getTextBuffer() {
		return originalText;
	}

	@Override
	@Nullable
	public TextBuffer getTextBufferPreprocessed() {
		return preprocessedBuffer;
	}

	private static class StringBuilderWriter extends Writer implements CharSequence {

		public final StringBuilder sb = new StringBuilder(1024);

		@Override
		public void write(@NotNull char[] chars, int i, int i1) throws IOException {
			sb.append(chars, i, i1);
		}

		@Override
		public void flush() throws IOException {

		}

		@Override
		public void close() throws IOException {

		}

		@Override
		public void write(int c) throws IOException {
			sb.append(c);
		}

		@Override
		public void write(@NotNull char[] cbuf) throws IOException {
			sb.append(cbuf);
		}

		@Override
		public void write(@NotNull String str) throws IOException {
			sb.append(str);
		}

		@Override
		public void write(@NotNull String str, int off, int len) throws IOException {
			sb.append(str, off, len);
		}

		@Override
		public Writer append(CharSequence csq) throws IOException {
			sb.append(csq);
			return this;
		}

		@Override
		public Writer append(CharSequence csq, int start, int end) throws IOException {
			sb.append(csq, start, end);
			return this;
		}

		@Override
		public Writer append(char c) throws IOException {
			sb.append(c);
			return this;
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
	}
}
