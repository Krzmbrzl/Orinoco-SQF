package arma.orinocosqf.problems;

import org.jetbrains.annotations.NotNull;

/**
 * An error about an unclosed String
 * 
 * @author Raven
 *
 */
public class Error_UnclosedString extends Problem.Error {

	@Override
	public @NotNull String getDisplayName() {
		return "UnclosedStringError";
	}

	@Override
	public @NotNull String getDescription() {
		return "A string has been opened (e.g. via ' or \") but was never closed. "
				+ "Fix bei either removing the opening quotation mark or add another one at an appropriate position";
	}

	@Override
	public int getId() {
		return 2;
	}

}
