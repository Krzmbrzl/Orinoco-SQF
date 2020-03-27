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
	private SQFSyntaxCheckerTestHelper.MyProblemListener parseText(@NotNull String text) {
		SQFInfixToPostfixProcessor postfixProcessor = new SQFInfixToPostfixProcessor();
		MyProblemListener problemListener = new MyProblemListener();
		postfixProcessor.setProblemListener(problemListener);

		OrinocoTokenProcessor processor = new OrinocoTokenInstanceProcessor.ToInstanceTranslator(postfixProcessor);
		OrinocoTokenDelegator delegator = new OrinocoPreProcessor(processor, new ArmaFilesystem(new File("").toPath(), new ArrayList<>()));
		OrinocoLexer lexer = new OrinocoLexer(delegator, new ProblemListenerPanicImplementation());

		lexer.start(OrinocoReader.fromCharSequence(text));

		return problemListener;
	}

	/**
	 * Asserts that the problems detected are > 0
	 *
	 * @param text the SQF code
	 * @see #assertNoProblems(String)
	 */
	public void assertHasProblems(@NotNull String text) {
		MyProblemListener problemListener = parseText(text);
		assertFalse("Expected there to be problems.", problemListener.problems.isEmpty());
	}

	/**
	 * Asserts that the problems detected are equal to 0.
	 *
	 * @param text the SQF code
	 * @see #assertNoProblems(String)
	 */
	public void assertNoProblems(@NotNull String text) {
		MyProblemListener problemListener = parseText(text);
		assertEquals("Expected no problems, got " + problemListener.problems.size(), 0, problemListener.problems.size());
	}

	/**
	 * Parses the given text then returns the last {@link ValueType} of all the statements.
	 *
	 * @param text SQF code to parse
	 * @return the sqf return value
	 */
	@Nullable
	public ValueType getExitTypeForText(@NotNull String text) {
//		SQFFile file = (SQFFile) myFixture.configureByText(SQFFileType.INSTANCE, text);
//		ProblemsHolder problems = getProblemsHolder(file);
//		CommandDescriptorCluster cluster = SQFSyntaxHelper.getInstance().getCommandDescriptors(file.getNode());
//		return new SQFSyntaxChecker(file.getFileScope().getChildStatements(), cluster, problems).begin();
		return null;
	}

	/**
	 * This will then assert that the return types match and that there were no problems reported
	 *
	 * @param text            SQF code to parse
	 * @param cluster         command descriptors to use
	 * @param expectedRetType expected return type
	 */
//	public void assertExitTypeAndNoProblems(@NotNull String text, @Nullable CommandDescriptorCluster cluster,
//											@Nullable ValueType expectedRetType) {
//		SQFFile file = (SQFFile) myFixture.configureByText(SQFFileType.INSTANCE, text);
//		ProblemsHolder problems = getProblemsHolder(file);
//		cluster = cluster == null ? SQFSyntaxHelper.getInstance().getCommandDescriptors(file.getNode()) : cluster;
//		ValueType ret = new SQFSyntaxChecker(file.getFileScope().getChildStatements(), cluster, problems).begin();
//		assertEquals("Expected no problems, got " + problems.getResults(), 0, problems.getResultCount());
//		assertEquals(expectedRetType, ret);
//	}

	private static class MyProblemListener implements ProblemListener {

		private final List<Problem> problems = new ArrayList<>();

		@Override
		public void problemEncountered(@NotNull Problem problem, @NotNull String msg, int offset, int length, int line) {
			problems.add(problem);
		}
	}

}
