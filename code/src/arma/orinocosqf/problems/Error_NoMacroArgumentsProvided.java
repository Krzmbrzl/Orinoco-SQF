package arma.orinocosqf.problems;

import org.jetbrains.annotations.NotNull;

/**
 * An error describing that a macro that does take arguments was called without providing any arguments
 * 
 * @author Raven
 *
 */
public class Error_NoMacroArgumentsProvided extends Error_PreprocessorError {

	@Override
	public @NotNull String getDisplayName() {
		return "NoMacroArgumentsProvidedError";
	}

	@Override
	public @NotNull String getDescription() {
		return "A macro that needs arguments was used without providing any";
	}

	@Override
	public int getId() {
		return 11;
	}
}
