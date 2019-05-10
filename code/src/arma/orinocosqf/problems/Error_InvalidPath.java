package arma.orinocosqf.problems;

import org.jetbrains.annotations.NotNull;

/**
 * Error about an invalid path
 * 
 * @author Raven
 *
 */
public class Error_InvalidPath extends Problem.Error {

	@Override
	public @NotNull String getDisplayName() {
		return "InvalidPathError";
	}

	@Override
	public @NotNull String getDescription() {
		return "The specified path is invalid";
	}

	@Override
	public int getId() {
		return 8;
	}

}
