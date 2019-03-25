package arma.orinocosqf.problems;

import org.jetbrains.annotations.NotNull;

/**
 * An interface describing a ProblemListener that can be atached to anything that might encounter problems and that should be notified about
 * every encountered Problem
 *
 * @author Raven
 */
public interface ProblemListener {
	/**
	 * Gets called whenever a {@link Problem} has been encountered
	 *
	 * @param problem The encountered problem
	 * @param The conrete problem's message
	 * @param offset The offset of the problem area in the original input or <code>-1</code> if not applicable
	 * @param length The length of the problem area in the original input or <code>-1</code> if not applicable
	 */
	void problemEncoutnered(@NotNull Problem problem, @NotNull String msg, int offset, int length);
}
