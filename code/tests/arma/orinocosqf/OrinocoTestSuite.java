package arma.orinocosqf;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ArmaFilesystemTest.class, ArrayValueHolderTest.class, BodySegmentExpansionTest.class, BodySegmentParserTest.class,
		OrinocoLexerTest.class, OrinocoPreProcessorTest.class, ProblemImplementationTest.class, ValueTypeEquivalenceTest.class,
		PreProcessingTest.class, SQFSyntaxCheckerTest.class})
public class OrinocoTestSuite {

}
