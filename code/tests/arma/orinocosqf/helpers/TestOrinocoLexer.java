package arma.orinocosqf.helpers;

import arma.orinocosqf.OrinocoLexer;
import arma.orinocosqf.OrinocoLexerStream;
import arma.orinocosqf.OrinocoReader;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * A {@link OrinocoLexer} instance made for testing. This class listens to {@link OrinocoLexer#acceptPreProcessedText(CharSequence)} via a {@link Consumer}
 * callback and submits the text through it for assertions.
 *
 * @author K
 * @since 3/11/19
 */
public class TestOrinocoLexer extends OrinocoLexer {
	private final Consumer<CharSequence> preprocessedTextCallback;

	public TestOrinocoLexer(@NotNull OrinocoReader r,
							@NotNull OrinocoLexerStream lexerStream,
							@NotNull Consumer<CharSequence> preprocessedTextCallback) {
		super(r, lexerStream);
		this.preprocessedTextCallback = preprocessedTextCallback;
	}

	@Override
	public void acceptPreProcessedText(@NotNull CharSequence text) {
		// do this first to stop further preprocessing before assertions
		preprocessedTextCallback.accept(text);

		super.acceptPreProcessedText(text);
	}
}
