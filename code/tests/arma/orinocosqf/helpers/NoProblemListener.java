package arma.orinocosqf.helpers;

import static org.junit.Assert.fail;

import org.jetbrains.annotations.NotNull;

import arma.orinocosqf.problems.Problem;
import arma.orinocosqf.problems.ProblemListener;

/**
 * An implementation of {@link ProblemListener} that fails the test-case if any problem is being reported
 * 
 * @author Raven
 *
 */
public class NoProblemListener implements ProblemListener {

	@Override
	public void problemEncountered(@NotNull Problem problem, @NotNull String msg, int offset, int length, int line) {
		fail("Asserted to get no problems but got \"" + msg + "\" (" + problem + "). Offset= " + offset + "; length=" + length + "; line="
				+ line);
	}

}
