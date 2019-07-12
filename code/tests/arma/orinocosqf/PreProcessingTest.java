package arma.orinocosqf;

import static org.junit.Assert.assertEquals;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

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
		lexer = new OrinocoLexer(preprocessor);

		lexer.enableTextBuffering(true);
	}

	void performTest(String[] input, String[] expected) {
		// Test with LF
		performTest(String.join("\n", input), String.join("\n", expected), true);

		// Test with CRLF
		performTest(String.join("\r\n", input), String.join("\r\n", expected), false);
	}

	void performTest(String input, String expected, boolean unixLF) {
		ByteArrayOutputStream os = new ByteArrayOutputStream(expected.length());
		OutputStreamWriter outWriter = new OutputStreamWriter(os);

		processor.setOutputWriter(outWriter);

		lexer.start(OrinocoReader.fromCharSequence(input));

		try {
			outWriter.flush();
			outWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String result = new String(os.toByteArray());

		String expectedSingleLine = expected.replaceAll("\\r?\\n", "");
		String resultSingleLine = result.replaceAll("\\r?\\n", "");

		if (!expectedSingleLine.equals(resultSingleLine)) {
			// the difference is not WS-only -> Explicitly perform assertion in order to make the diff available
			assertEquals("Preprocessed result differs from expected one", expected, result);
		} else {
			// The content without any newlines is the same -> Let's check the newlines then
			assertEquals("Preprocessed result has different amount of NLs than the expected one", expected.replace("\r", ""),
					result.replace("\r", ""));

			// They also got the same amount of NLs -> check the kind of NLs as well
			assertEquals("The NLs in the preprocessed result differs from the ones in the expected one (LF=" + (unixLF ? "UNIX" : "WINDOWS")
					+ ")", expected, result);
		}
	}

	@Test
	public void basicMacroExpansion() {
		performTest(new String[] { "#define MACRO test", "MACRO" }, new String[] { "", "test" });
	}

}
