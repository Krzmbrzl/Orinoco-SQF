package arma.orinocosqf;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import arma.orinocosqf.problems.Problem;
import arma.orinocosqf.problems.Problems;

public class ProblemImplementationTest {

	List<Problem> allProblems;

	@Before
	public void setUp() throws Exception {
		allProblems = new ArrayList<>();

		// gather all problems listed in the Problems class (which should be all available ones
		for (Field currentField : Problems.class.getDeclaredFields()) {
			if (Modifier.isStatic(currentField.getModifiers())) {
				Object currentProblem = currentField.get(null);

				if (currentProblem instanceof Problem) {
					allProblems.add((Problem) currentProblem);
				}
			}
		}
	}

	@Test
	public void uniqueIDs() {
		HashMap<Integer, Problem> idMap = new HashMap<>((int) (allProblems.size() * 1.3));

		for (Problem currentProblem : allProblems) {
			int currentId = currentProblem.getId();

			if (idMap.containsKey(currentId)) {
				Problem conflictingProblem = idMap.get(currentId);

				fail("Duplicate ID " + currentId + " for " + currentProblem.getClass().getSimpleName() + " and "
						+ conflictingProblem.getClass().getSimpleName());
			}

			idMap.put(currentId, currentProblem);
		}
	}

}
