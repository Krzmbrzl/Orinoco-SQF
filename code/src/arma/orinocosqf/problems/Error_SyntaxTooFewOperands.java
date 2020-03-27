package arma.orinocosqf.problems;

import org.jetbrains.annotations.NotNull;

/**
 * A syntax error problem for when there are too few operands (1+1 1 for example)
 *
 * @author K
 * @since 3/26/20
 */
public class Error_SyntaxTooFewOperands extends Error_Syntax {

	public Error_SyntaxTooFewOperands() {
	}

	@Override
	public @NotNull String getDisplayName() {
		return "Syntax Error: Too few operands";
	}

	@Override
	public @NotNull String getDescription() {
		return "This error occurs when there is a syntax error due to too few values ('operands').";
	}

	@Override
	public int getId() {
		return 3004;
	}
}
