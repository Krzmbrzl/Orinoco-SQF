package arma.orinocosqf.exceptions;

import org.jetbrains.annotations.NotNull;

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
	 * @param offset The offset at which this exception occured
	 * @param length The length of the affected area
	 */
	public NoMacroArgumentsGivenException(@NotNull String macroName, int offset, int length) {
		super("Trying to expand the macro \"" + macroName + "\" without providing the required macro arguments", offset, length);
	}
}
