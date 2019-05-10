package arma.orinocosqf.problems;

import org.jetbrains.annotations.NotNull;

public class Error_EmptyInput extends Problem.Error {

	@Override
	public @NotNull String getDisplayName() {
		return "EmptyInputError";
	}

	@Override
	public @NotNull String getDescription() {
		return "An empty input has been encountered in a position where a non-empty input has been expected. "
				+ "Whitespace (WS) might also be considered as empty input.";
	}

	@Override
	public int getId() {
		return 5;
	}

}
