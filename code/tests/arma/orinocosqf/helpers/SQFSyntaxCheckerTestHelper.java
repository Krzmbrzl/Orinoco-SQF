package arma.orinocosqf.helpers;

import arma.orinocosqf.OrinocoReader;
import arma.orinocosqf.OrinocoTokenInstanceProcessor;
import arma.orinocosqf.OrinocoTokenProcessor;
import arma.orinocosqf.lexer.OrinocoLexer;
import arma.orinocosqf.lexer.OrinocoTokenDelegator;
import arma.orinocosqf.parsing.postfix.SQFInfixToPostfixProcessor;
import arma.orinocosqf.preprocessing.ArmaFilesystem;
import arma.orinocosqf.preprocessing.OrinocoPreProcessor;
import arma.orinocosqf.problems.Problem;
import arma.orinocosqf.problems.ProblemListener;
import arma.orinocosqf.problems.ProblemListenerPanicImplementation;
import arma.orinocosqf.type.ValueType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Contains helper methods for SQF syntax checking related tests
 *
 * @author Kayler
 * @since 7/25/2019
 */
public abstract class SQFSyntaxCheckerTestHelper {

	@NotNull
	private TestCaseHelper parseText(@NotNull String text) {
		SQFInfixToPostfixProcessor postfixProcessor = new SQFInfixToPostfixProcessor();
		MyProblemListener problemListener = new MyProblemListener();
		postfixProcessor.setProblemListener(problemListener);

		OrinocoTokenProcessor processor = new OrinocoTokenInstanceProcessor.ToInstanceTranslator(postfixProcessor);
		OrinocoTokenDelegator delegator = new OrinocoPreProcessor(processor, new ArmaFilesystem(new File("").toPath(), new ArrayList<>()));
		OrinocoLexer lexer = new OrinocoLexer(delegator, new ProblemListenerPanicImplementation());

		lexer.start(OrinocoReader.fromCharSequence(text));

		TestCaseHelper helper = new TestCaseHelper();
		helper.problemListener = problemListener;
		helper.processor = postfixProcessor;

		return helper;
	}

	/**
	 * Asserts that the problems detected are > 0
	 *
	 * @param text the SQF code
	 * @see #assertNoProblems(String)
	 */
	public void assertHasProblems(@NotNull String text) {
		TestCaseHelper helper = parseText(text);
		MyProblemListener problemListener = helper.problemListener;
		SQFInfixToPostfixProcessor processor = helper.processor;
		assertFalse("Expected there to be problems. Return Type: " + processor.getReturnType(), problemListener.problems.isEmpty());
	}

	/**
	 * Asserts that the problems detected are equal to 0.
	 *
	 * @param text the SQF code
	 * @see #assertNoProblems(String)
	 */
	public void assertNoProblems(@NotNull String text) {
		MyProblemListener problemListener = parseText(text).problemListener;
		doAssertNoProblems(problemListener);
	}

	private void doAssertNoProblems(@NotNull MyProblemListener problemListener) {
		StringBuilder probs = new StringBuilder();
		probs.append('\n');
		for (List<String> prob : problemListener.problems) {
			probs.append('\t');
			probs.append(prob.toString());
			probs.append('\n');
		}
		assertEquals("Expected no problems, got: " + probs, 0, problemListener.problems.size());
	}

	/**
	 * Parses the given text then returns the last {@link ValueType} of all the statements.
	 *
	 * @param text SQF code to parse
	 * @return the sqf return value
	 */
	@NotNull
	public ValueType getExitTypeForText(@NotNull String text) {
		SQFInfixToPostfixProcessor processor = parseText(text).processor;
		return processor.getReturnType();
	}

	/**
	 * This will then assert that the return types match and that there were no problems reported
	 *
	 * @param text SQF code to parse
	 * @param expectedRetType expected return type
	 */
	public void assertExitTypeAndNoProblems(@NotNull String text,
											@Nullable ValueType expectedRetType) {
		TestCaseHelper helper = parseText(text);

		doAssertNoProblems(helper.problemListener);
		assertEquals(expectedRetType, helper.processor.getReturnType());
	}

	private static class MyProblemListener implements ProblemListener {

		private final List<List<String>> problems = new ArrayList<>();

		@Override
		public void problemEncountered(@NotNull Problem problem, @NotNull String msg, int offset, int length, int line) {
			List<String> prob = new ArrayList();
			prob.add(problem.getDisplayName());
			prob.add(msg);
			problems.add(prob);
		}
	}

	private static class TestCaseHelper {
		MyProblemListener problemListener;
		SQFInfixToPostfixProcessor processor;
	}

}
