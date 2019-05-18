package arma.orinocosqf.exceptions;

import org.jetbrains.annotations.NotNull;

/**
 * An exception thrown whenever a macro is attempted to be expanded without supplying a value for a given parameter
 * 
 * @author Raven
 *
 */
public class MissingMacroArgumentException extends OrinocoPreprocessorException {
	private static final long serialVersionUID = -8915710597428107596L;

	/**
	 * @param argumentName The name of the argument that wasn't supplied
	 */
	public MissingMacroArgumentException(@NotNull String argumentName) {
		super("Trying to expand macro without providing a value for the argument \"" + argumentName + "\"");
	}
}
