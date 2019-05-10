package arma.orinocosqf.problems;

import org.jetbrains.annotations.NotNull;

/**
 * An interface describing a ProblemListener that can be attached to anything that might encounter problems and that should be notified
 * about every encountered Problem
 *
 * @author Raven
 */
public interface ProblemListener {
	/**
	 * Gets called whenever a {@link Problem} has been encountered
	 *
	 * @param problem The encountered problem
	 * @param msg The concrete problem's message
	 * @param offset The offset of the problem area in the original input or <code>-1</code> if not applicable
	 * @param length The length of the problem area in the original input or <code>-1</code> if not applicable
	 * @param line The line in which the problem occurred. If the problem spans multiple lines this is the first line in which it occurs.
	 *        This is set to <code>-1</code> if the line is unknown or a line number is not applicable
	 */
	void problemEncountered(@NotNull Problem problem, @NotNull String msg, int offset, int length, int line);
}
