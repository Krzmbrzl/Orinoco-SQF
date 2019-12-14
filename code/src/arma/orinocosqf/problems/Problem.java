package arma.orinocosqf.problems;

import org.jetbrains.annotations.NotNull;

/**
 * Base class of all problem-objects that is being used for the error-reporting mechanism inside Orinoco
 *
 * @author Raven
 */
public abstract class Problem {
	/**
	 * @return The display name of this problem
	 */
	@NotNull
	public abstract String getDisplayName();

	/**
	 * @return The general description of this problem
	 */
	@NotNull
	public abstract String getDescription();

	/**
	 * @return The {@link Severity} of this problem
	 */
	@NotNull
	public abstract Severity getSeverity();

	/**
	 * @return The unique Id of this problem
	 */
	public abstract int getId();

	@Override
	public String toString() {
		return getDisplayName() + "{" + "severity=" + getSeverity() + "; id=" + getId() + "}";
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Problem)) {
			return false;
		}

		Problem other = (Problem) o;

		return this.getDisplayName().equals(other.getDisplayName()) && this.getDescription().equals(other.getDescription())
				&& this.getSeverity().equals(other.getSeverity()) && this.getId() == other.getId();
	}


	/**
	 * An enum for problem severities
	 *
	 * @author Raven
	 */
	public static enum Severity {
		INFO, WARNING, ERROR
	}

	// Base classes of all concrete problems
	public abstract static class Warning extends Problem {
		@Override
		public Severity getSeverity() {
			return Severity.WARNING;
		}
	}

	public abstract static class Error extends Problem {
		@Override
		public Severity getSeverity() {
			return Severity.ERROR;
		}
	}

	public abstract static class Info extends Problem {
		@Override
		public Severity getSeverity() {
			return Severity.INFO;
		}
	}
}
