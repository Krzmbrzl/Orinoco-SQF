package arma.orinocosqf.problems;

import org.jetbrains.annotations.NotNull;

/**
 * An error describing that a macro has been used with improper amount of arguments
 * 
 * @author Raven
 *
 */
public class Error_WrongMacroArgumentCount extends Error_PreprocessorError {

	@Override
	public @NotNull String getDisplayName() {
		return "WrongMacroArgumentCountError";
	}

	@Override
	public @NotNull String getDescription() {
		return "A macro has been used with the wrong amount of arguments";
	}

	@Override
	public int getId() {
		return 10;
	}
}
