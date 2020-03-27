package arma.orinocosqf.problems;

import org.jetbrains.annotations.NotNull;

/**
 * A syntax error problem for when there is an illegal character (]1=2 for example)
 *
 * @author K
 * @since 3/26/20
 */
public class Error_SyntaxIllegalCharacter extends Error_Syntax {

	public Error_SyntaxIllegalCharacter() {
	}

	@Override
	public @NotNull String getDisplayName() {
		return "Syntax Error: Illegal Character";
	}

	@Override
	public @NotNull String getDescription() {
		return "This error occurs when there is a syntax error and a character is illegally placed.";
	}

	@Override
	public int getId() {
		return 3001;
	}
}
