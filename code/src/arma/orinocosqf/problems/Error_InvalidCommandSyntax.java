package arma.orinocosqf.problems;

import org.jetbrains.annotations.NotNull;

/**
 * A syntax error problem for when a command syntax doesn't exist for the provided types
 *
 * @author K
 * @since 3/26/20
 */
public class Error_InvalidCommandSyntax extends Problem.Error {

	public Error_InvalidCommandSyntax() {
	}

	@Override
	public @NotNull String getDisplayName() {
		return "Type Error";
	}

	@Override
	public @NotNull String getDescription() {
		return "This error occurs when a command is provided invalid operands.";
	}

	@Override
	public int getId() {
		return 3003;
	}
}
