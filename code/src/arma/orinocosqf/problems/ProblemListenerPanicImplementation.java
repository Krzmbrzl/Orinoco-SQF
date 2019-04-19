package arma.orinocosqf.problems;

import org.jetbrains.annotations.NotNull;

import arma.orinocosqf.exceptions.ProblemRuntimeException;

/**
 * An implementation of {@link ProblemListener} that will immediately throw a {@link ProblemRuntimeException} whenever
 * {@link #problemEncountered(Problem, String, int, int, int)} is being invoked. It is mainly intended for testing purposes as normally
 * there should be a more rigorous error-handling approach.
 * 
 * @author Raven
 *
 */
public class ProblemListenerPanicImplementation implements ProblemListener {

	static class ErrorPosition {
		int offset, length, line;

		public ErrorPosition(int offset, int length, int line) {
			this.offset = offset;
			this.length = length;
			this.line = line;
		}

		@Override
		public String toString() {
			return "ErrorPosition{offset=" + offset + "; length=" + length + "; line=" + line + "}";
		}
	}

	public ProblemListenerPanicImplementation() {
	}

	@Override
	public void problemEncountered(@NotNull Problem problem, @NotNull String msg, int offset, int length, int line) {
		throw new ProblemRuntimeException(problem, "Problem-panic: " + msg, new ErrorPosition(offset, length, line));
	}

}
