package arma.orinocosqf.problems;

import org.jetbrains.annotations.NotNull;

/**
 * A syntax error problem for when there are too many operands (1+1 1 for example)
 *
 * @author K
 * @since 3/26/20
 */
public class Error_SyntaxTooManyOperands extends Error_Syntax {

	public Error_SyntaxTooManyOperands() {
	}

	@Override
	public @NotNull String getDisplayName() {
		return "Syntax Error: Too many operands";
	}

	@Override
	public @NotNull String getDescription() {
		return "This error occurs when there is a syntax error due to too many values ('operands').";
	}

	@Override
	public int getId() {
		return 3002;
	}
}
