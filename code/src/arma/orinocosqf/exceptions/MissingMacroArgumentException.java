package arma.orinocosqf.exceptions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import arma.orinocosqf.preprocessing.bodySegments.BodySegment;

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
	 * @param context The {@link BodySegment} in whose context this exception occurred
	 */
	public MissingMacroArgumentException(@NotNull String argumentName, @Nullable BodySegment context) {
		super("Trying to expand macro without providing a value for the argument \"" + argumentName + "\"", context);
	}
}
