package arma.orinocosqf.exceptions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import arma.orinocosqf.preprocessing.bodySegments.BodySegment;

/**
 * An exception thrown whenever a macro requiring arguments is tried to be expanded without supplying any arguments for it
 * 
 * @author Raven
 *
 */
public class NoMacroArgumentsGivenException extends OrinocoPreprocessorException {
	private static final long serialVersionUID = 5157521157374609614L;

	/**
	 * @param macroName The name of the macro that was tried to be expanded without arguments although it does require arguments
	 * @param context The {@link BodySegment} in whose context this exception occurred
	 */
	public NoMacroArgumentsGivenException(@NotNull String macroName, @Nullable BodySegment context) {
		super("Trying to expand the macro \"" + macroName + "\" without providing the required macro arguments", context);
	}
}
