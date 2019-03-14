package arma.orinocosqf.problems;

import org.jetbrains.annotations.NotNull;

/**
 * Base class of all problem-objects that is being used for the error-reporting mechanism inside Orinoco
 * 
 * @author Raven
 *
 */
public abstract class Problem {
	/**
	 * @return The display name of this problem
	 */
	@NotNull
	abstract String getDisplayName();

	/**
	 * @return The general description of this problem
	 */
	@NotNull
	abstract String getDescription();

	/**
	 * @return The {@link Severity} of this problem
	 */
	@NotNull
	abstract Severity getSeverity();

	/**
	 * @return The unique Id of this problem
	 */
	abstract int getId();


	/**
	 * An enum for problem severities
	 * 
	 * @author Raven
	 *
	 */
	enum Severity {
		INFO, WARNING, ERROR
	}

	// Base classes of all concrete problems
	public abstract class Warning extends Problem {
	}

	public abstract class Error extends Problem {
	}

	public abstract class Info extends Problem {
	}
}
