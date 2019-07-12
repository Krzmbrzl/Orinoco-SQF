package arma.orinocosqf.helpers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import arma.orinocosqf.TextBuffer;
import arma.orinocosqf.lexer.OrinocoLexer;
import arma.orinocosqf.lexer.BufferingOrinocoLexerContext;
import arma.orinocosqf.util.SimpleTextBuffer;

public class PrefilledLexerContext extends BufferingOrinocoLexerContext {

	protected TextBuffer originalInputBuffer;
	protected TextBuffer preprocessedInputBuffer;

	public PrefilledLexerContext(@NotNull OrinocoLexer lexer, @NotNull CharSequence originalInput,
			@NotNull CharSequence preprocessedInput) {
		super(lexer);

		originalInputBuffer = new SimpleTextBuffer(originalInput);
		preprocessedInputBuffer = new SimpleTextBuffer(preprocessedInput);
	}

	public PrefilledLexerContext(@NotNull OrinocoLexer lexer, @NotNull CharSequence originalInput) {
		// Assume that the preporcessed input equals the original input
		this(lexer, originalInput, originalInput);
	}

	@Override
	public boolean isTextBufferingEnabled() {
		return true;
	}

	@Override
	public @Nullable TextBuffer getTextBuffer() {
		return originalInputBuffer;
	}

	@Override
	public @Nullable TextBuffer getTextBufferPreprocessed() {
		return preprocessedInputBuffer;
	}

}
