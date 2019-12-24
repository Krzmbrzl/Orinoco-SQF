package arma.orinocosqf.problems;

import org.jetbrains.annotations.NotNull;

/**
 * An error about invalid trailing whitespace
 * @author Raven
 *
 */
public class Error_TrailingWS extends Problem.Error{

	@Override
	public @NotNull String getDisplayName() {
		return "TrailingWhitespaceError";
	}

	@Override
	public @NotNull String getDescription() {
		return "There is invalid trailing whitespace";
	}

	@Override
	public int getId() {
		return 13;
	}

}
