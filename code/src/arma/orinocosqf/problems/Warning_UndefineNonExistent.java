package arma.orinocosqf.problems;

import org.jetbrains.annotations.NotNull;

/**
 * A warning for undefining non-existing things
 * 
 * @author Raven
 *
 */
public class Warning_UndefineNonExistent extends Problem.Warning {

	@Override
	public @NotNull String getDisplayName() {
		return "UndefineNonExistentWarning";
	}

	@Override
	public @NotNull String getDescription() {
		return "Something that doesn't exist is requested to be undefined.";
	}

	@Override
	public int getId() {
		return 7;
	}

}
