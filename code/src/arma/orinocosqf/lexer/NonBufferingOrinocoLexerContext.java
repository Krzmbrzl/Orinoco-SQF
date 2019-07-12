package arma.orinocosqf.lexer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import arma.orinocosqf.TextBuffer;
import arma.orinocosqf.exceptions.UnknownIdException;
import arma.orinocosqf.sqf.SQFCommands;

/**
 * An implementation of a {@link OrinocoLexerContext} which will have text-buffering disabled
 * 
 * @author Raven
 *
 */
public class NonBufferingOrinocoLexerContext implements OrinocoLexerContext {
	/**
	 * The lexer associated to this buffer
	 */
	protected OrinocoLexer lexer;


	/**
	 * 
	 * @param lexer The lexer this context is being instantiated for
	 */
	public NonBufferingOrinocoLexerContext(@NotNull OrinocoLexer lexer) {
		this.lexer = lexer;
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
		return false;
	}

	@Override
	@Nullable
	public TextBuffer getTextBuffer() {
		return null;
	}

	@Override
	@Nullable
	public TextBuffer getTextBufferPreprocessed() {
		return null;
	}

}
