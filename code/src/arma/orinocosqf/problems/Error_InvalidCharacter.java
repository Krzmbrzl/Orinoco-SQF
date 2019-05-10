package arma.orinocosqf.problems;

import org.jetbrains.annotations.NotNull;

/**
 * An error about an invalid character
 * @author Raven
 *
 */
public class Error_InvalidCharacter extends Problem.Error {

	@Override
	public @NotNull String getDisplayName() {
		return "InvalidCharacterError";
	}

	@Override
	public @NotNull String getDescription() {
		return "A character has been encountered that was either unexpected or simply invalid in the encountered context. "
				+ "Fix by deleting the respective character";
	}

	@Override
	public int getId() {
		return 3;
	}

}
