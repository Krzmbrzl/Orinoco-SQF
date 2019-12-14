package arma.orinocosqf.exceptions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import arma.orinocosqf.preprocessing.bodySegments.BodySegment;

/**
 * An exception thrown when a macro is tried to be expanded with too many arguments
 * 
 * @author Raven
 *
 */
public class WrongMacroArgumentCountException extends OrinocoPreprocessorException {
	private static final long serialVersionUID = -5533878061182209134L;

	/**
	 * @param macroName The name of the macro that was tried to be expanded with too many arguments
	 * @param expectedArgCount The amount of arguments this macro takes
	 * @param actualArgCount The amount of arguments that were actually supplied to it
	 * @param context The {@link BodySegment} in whose context this exception occurred
	 */
	public WrongMacroArgumentCountException(@NotNull String macroName, int expectedArgCount, int actualArgCount,
			@Nullable BodySegment context) {
		super("Trying to expand the macro \"" + macroName + "\" with " + actualArgCount + " args, but it requires " + expectedArgCount,
				context);
	}

}
