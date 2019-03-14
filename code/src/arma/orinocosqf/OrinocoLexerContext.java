package arma.orinocosqf;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import arma.orinocosqf.exceptions.UnknownIdException;

/**
 * An interface describing a lexer-context-object that can be used to "make sense" of the information the orinoco lexer provides. For
 * instance it can transform command/variable-IDs into the String-representation of that command/variable and it can also be asked for the
 * {@link TextBuffer} that holds all the text that has been processed inside the lexer so far (either the unpreprocessed or the preprocessed
 * version of it).
 * 
 * @author Raven
 *
 */
public interface OrinocoLexerContext {
	/**
	 * Converts the given command-id back into the String representation of the corresponding command
	 * 
	 * @param id The Id to convert
	 * @return The corresponding String representation
	 * 
	 * @throws UnknownIdException If the given id can't be associated with a command
	 */
	@NotNull
	public String getCommand(int id) throws UnknownIdException;

	/**
	 * Converts the given variable-id back into the String representation of the corresponding variable
	 * 
	 * @param id The Id to convert
	 * @return The corresponding String representation or <code>null</code> if no variable with such an Id exists
	 */
	@Nullable
	public String getVariable(int id);

	/**
	 * @return Whether text-buffering has been enabled during lexing. If this method returns <code>true</code> then {@link #getTextBuffer()}
	 *         and {@link #getTextBufferPreprocessed()} won't return <code>null</code>
	 */
	public boolean isTextBufferingEnabled();

	/**
	 * Gets the {@link TextBuffer} holding all text that has been read from the original input that has been read in the lexer up until this
	 * point. The returned buffer will reflect the original input as is - no preprocessing applied.
	 * 
	 * @return The respective buffer or <code>null</code> if buffering is disabled
	 * @see #isTextBufferingEnabled()
	 */
	@Nullable
	public TextBuffer getTextBuffer();

	/**
	 * Gets the {@link TextBuffer} holding all text that has been read and preprocessed by the lexer/preprocessor up until this point.
	 * 
	 * @return The respective buffer or <code>null</code> if buffering is disabled
	 * @see #isTextBufferingEnabled()
	 */
	@Nullable
	public TextBuffer getTextBufferPreprocessed();
}
