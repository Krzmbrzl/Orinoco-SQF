package arma.orinocosqf;

import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import arma.orinocosqf.helpers.VirtualArmaFileSystem;
import arma.orinocosqf.lexer.OrinocoLexer;
import arma.orinocosqf.preprocessing.OrinocoPreProcessor;
import arma.orinocosqf.tokenprocessing.OutputTokenProcessor;

public class PreProcessingTest {

	static OrinocoPreProcessor preprocessor;
	static OutputTokenProcessor processor;
	static VirtualArmaFileSystem virtualFs;
	static OrinocoLexer lexer;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		virtualFs = new VirtualArmaFileSystem();
		processor = new OutputTokenProcessor();
		preprocessor = new OrinocoPreProcessor(processor, virtualFs);
		lexer = new OrinocoLexer(preprocessor); // TODO: Hope that this will soon be valid
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		String[] inputLinesines = { "#define TEST some Test", "I am TEST and I am proud of it", "hint str 3;" };
		String[] expectedOutputLines = { "", "I am some Test and I am proud of it", "hint str 3;" };

		// TODO: Actually implement the test
		fail("Not yet implemented");
	}

}
