package arma.orinocosqf.problems;

import org.jetbrains.annotations.NotNull;

public class Error_Generic extends Problem.Error {

	@Override
	public @NotNull String getDisplayName() {
		return "GenericError";
	}

	@Override
	public @NotNull String getDescription() {
		return "A generic error has been encountered that doesn't fit to any more precice error type.";
	}

	@Override
	public int getId() {
		return 0;
	}

}
