package arma.orinocosqf.problems;

import org.jetbrains.annotations.NotNull;

/**
 * A generic syntax error problem
 *
 * @author K
 * @since 3/26/20
 */
public class Error_Syntax extends Problem.Error {

	public Error_Syntax() {
	}

	@Override
	public @NotNull String getDisplayName() {
		return "Syntax Error";
	}

	@Override
	public @NotNull String getDescription() {
		return "This error occurs when there is a syntax error.";
	}

	@Override
	public int getId() {
		return 3000;
	}
}
