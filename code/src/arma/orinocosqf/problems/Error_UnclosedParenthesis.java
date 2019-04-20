package arma.orinocosqf.problems;

import org.jetbrains.annotations.NotNull;

/**
 * An error about an unclosed parenthesis
 * 
 * @author Raven
 *
 */
public class Error_UnclosedParenthesis extends ParenthesisError {

	@Override
	public @NotNull String getDisplayName() {
		return "UnclosedParenthesisError";
	}

	@Override
	public @NotNull String getDescription() {
		return "A parenthesis has been opened but was never closed. "
				+ "Fix by removing the respective opening parenthesis or add the closing one in an appropriate place";
	}

	@Override
	public int getId() {
		return 1;
	}

}
