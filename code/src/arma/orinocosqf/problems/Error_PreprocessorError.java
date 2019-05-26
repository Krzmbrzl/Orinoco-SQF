package arma.orinocosqf.problems;

import org.jetbrains.annotations.NotNull;

/**
 * Error about something having gone wrong during preprocessing. This error should only be used, if the exact source of the problem is
 * unclear
 * 
 * @author Raven
 *
 */
public class Error_PreprocessorError extends Problem.Error {

	@Override
	public @NotNull String getDisplayName() {
		return "PreprocessorError";
	}

	@Override
	public @NotNull String getDescription() {
		return "Something has gone wrong during preprocessing";
	}

	@Override
	public int getId() {
		return 9;
	}

}
