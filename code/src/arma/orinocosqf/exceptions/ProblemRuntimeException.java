package arma.orinocosqf.exceptions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import arma.orinocosqf.problems.Problem;

/**
 * A {@link OrinocoRuntimeException} caused by a {@link Problem}
 * 
 * @author Raven
 *
 */
public class ProblemRuntimeException extends OrinocoRuntimeException {

	private static final long serialVersionUID = -5841761759068279801L;

	/**
	 * @param problem The problem associated with this exception
	 */
	public ProblemRuntimeException(@NotNull Problem problem) {
		this(problem, null, null);
	}

	/**
	 * @param problem The problem associated with this exception
	 * @param message An explicit error message
	 */
	public ProblemRuntimeException(@NotNull Problem problem, @Nullable String message) {
		this(problem, message, null);
	}

	/**
	 * @param problem The problem associated with this exception
	 * @param message An explicit error message
	 * @param context An object used as further context of this problem. The object's toString method will be part of the error message
	 */
	public ProblemRuntimeException(@NotNull Problem problem, @Nullable String message, @Nullable Object context) {
		super("Encountered problem: " + problem.toString() + (message != null ? "\nMessage: " + message : "")
				+ (context != null ? "\nContext: " + context : ""));
	}
}
