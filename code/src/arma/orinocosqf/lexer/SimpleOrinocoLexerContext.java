package arma.orinocosqf.lexer;

import arma.orinocosqf.TextBuffer;
import arma.orinocosqf.exceptions.UnknownIdException;
import arma.orinocosqf.sqf.SQFCommands;
import arma.orinocosqf.util.SimpleTextBuffer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author K
 * @since 5/13/19
 */
public class SimpleOrinocoLexerContext implements OrinocoLexerContext {
	private final OrinocoLexer lexer;
	private TextBuffer originalText;
	private final SimpleTextBuffer preprocessedBuffer = new SimpleTextBuffer();

	public SimpleOrinocoLexerContext(@NotNull OrinocoLexer lexer, @NotNull TextBuffer originalText) {
		this.lexer = lexer;
		this.originalText = originalText;
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

}
