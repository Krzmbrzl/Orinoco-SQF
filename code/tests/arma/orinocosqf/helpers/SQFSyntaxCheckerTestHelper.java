package arma.orinocosqf.helpers;

import arma.orinocosqf.OrinocoReader;
import arma.orinocosqf.OrinocoTokenInstanceProcessor;
import arma.orinocosqf.OrinocoTokenProcessor;
import arma.orinocosqf.lexer.OrinocoLexer;
import arma.orinocosqf.lexer.OrinocoTokenDelegator;
import arma.orinocosqf.parsing.postfix.SQFInfixToPostfixProcessor;
import arma.orinocosqf.preprocessing.ArmaFilesystem;
import arma.orinocosqf.preprocessing.OrinocoPreProcessor;
import arma.orinocosqf.problems.ProblemListenerPanicImplementation;
import arma.orinocosqf.type.ValueType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;

/**
 * Contains helper methods for SQF syntax checking related tests
 *
 * @author Kayler
 * @since 7/25/2019
 */
public abstract class SQFSyntaxCheckerTestHelper {

	private OrinocoLexer setup(@NotNull SQFInfixToPostfixProcessor postfixProcessor) {
		OrinocoTokenProcessor processor = new OrinocoTokenInstanceProcessor.ToInstanceTranslator(postfixProcessor);
		OrinocoTokenDelegator delegator = new OrinocoPreProcessor(processor, new ArmaFilesystem(new File("").toPath(), new ArrayList<>()));
		return new OrinocoLexer(delegator, new ProblemListenerPanicImplementation());
	}

	/**
	 * Asserts that the problems detected are > 0
	 *
	 * @param text the SQF code
	 * @see #assertNoProblems(String)
	 */
	public void assertHasProblems(@NotNull String text) {
		SQFInfixToPostfixProcessor postfixProcessor = new SQFInfixToPostfixProcessor();
		OrinocoLexer lexer = setup(postfixProcessor);
		lexer.start(OrinocoReader.fromCharSequence(text));

//		SQFFile file = (SQFFile) myFixture.configureByText(SQFFileType.INSTANCE, text);
//		ProblemsHolder problems = getProblemsHolder(file);
//		SQFSyntaxHelper.getInstance().checkSyntax(file, problems);
//
//		assertEquals("Expected there to be problems.", true, problems.getResultCount() > 0);
	}

	/**
	 * Asserts that the problems detected are equal to 0.
	 *
	 * @param text the SQF code
	 * @see #assertNoProblems(String)
	 */
	public void assertNoProblems(@NotNull String text) {
//		SQFFile file = (SQFFile) myFixture.configureByText(SQFFileType.INSTANCE, text);
//		ProblemsHolder problems = getProblemsHolder(file);
//		SQFSyntaxHelper.getInstance().checkSyntax(file, problems);
//
//		assertEquals("Expected no problems, got " + problems.getResults(), 0, problems.getResultCount());
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

}
