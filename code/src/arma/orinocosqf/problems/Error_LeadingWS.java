package arma.orinocosqf.problems;

import org.jetbrains.annotations.NotNull;

/**
 * An error about invalid leading whitespace
 * @author Raven
 *
 */
public class Error_LeadingWS extends Problem.Error{

	@Override
	public @NotNull String getDisplayName() {
		return "LeadingWhitespaceError";
	}

	@Override
	public @NotNull String getDescription() {
		return "There is invalid leading whitespace";
	}

	@Override
	public int getId() {
		return 12;
	}

}
