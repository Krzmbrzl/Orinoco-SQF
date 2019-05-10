package arma.orinocosqf.problems;

import org.jetbrains.annotations.NotNull;

/**
 * An internal error - e.g. unexpected program flow or unexpected input
 * 
 * @author Raven
 *
 */
public class Error_Internal extends Problem.Error {

	@Override
	public @NotNull String getDisplayName() {
		return "InternalError";
	}

	@Override
	public @NotNull String getDescription() {
		return "An internal error occured. You should contact the devs about this as this is definitely a bug.";
	}

	@Override
	public int getId() {
		return 4;
	}

}
