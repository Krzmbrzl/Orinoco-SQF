package arma.orinocosqf.problems;

import org.jetbrains.annotations.NotNull;

/**
 * A warning about overwriting something that already exists
 * 
 * @author Raven
 *
 */
public class Warning_OverwriteExisting extends Problem.Warning {

	@Override
	public @NotNull String getDisplayName() {
		return "OverwriteExistingWarning";
	}

	@Override
	public @NotNull String getDescription() {
		return "Something that already exists is being overwritten by something else. This may be intentional or by accident.";
	}

	@Override
	public int getId() {
		return 6;
	}

}
