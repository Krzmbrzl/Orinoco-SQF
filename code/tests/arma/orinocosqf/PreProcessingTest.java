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
import arma.orinocosqf.problems.ProblemListenerPanicImplementation;
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
		lexer = new OrinocoLexer(preprocessor, new ProblemListenerPanicImplementation());

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
		performTest(new String[] { "#define MACRO ", "MACRO" }, new String[] { "", "" });
		performTest(new String[] { "#define MACRO", "MACRO" }, new String[] { "", "" });
		performTest(new String[] { "#define MACRO Hello there ", "MACRO" }, new String[] { "", "Hello there " });
		performTest(new String[] { "#define MACRO     Hello there ", "MACRO" }, new String[] { "", "Hello there " });
	}

	@Test
	public void nestedMacroExpansionTest() {
		performTest(new String[] { "#define ONE Hello", "#define TWO ONE World", "TWO" }, new String[] { "", "", "Hello World" });
		performTest(new String[] { "#define ONE Hello", "#define TWO  ONE World", "TWO" }, new String[] { "", "", "Hello World" });
		performTest(new String[] { "#define ONE Hello", "#define TWO     ONE World", "TWO" }, new String[] { "", "", "Hello World" });
		performTest(new String[] { "#define ONE Hello", "#define TWO ONE World", "#define THREE Test: TWO and more", "THREE" },
				new String[] { "", "", "", "Test: Hello World and more" });

	}

	@Test
	public void macroArgumentExpansionTest() {
		performTest(new String[] { "#define MACRO(arg) Hello arg", "MACRO(world)" }, new String[] { "", "Hello world" });
		performTest(new String[] { "#define MACRO(arg) Hello", "MACRO(world)" }, new String[] { "", "Hello" });
		performTest(new String[] { "#define MACRO(arg) Hello arg", "MACRO(some longer test)" },
				new String[] { "", "Hello some longer test" });
		performTest(new String[] { "#define MACRO(arg1,arg2) Hello arg1 and arg2", "MACRO(world,me)" },
				new String[] { "", "Hello world and me" });
		performTest(new String[] { "#define MACRO(arg1,arg2) Hello arg1 and arg2", "MACRO(world,)" },
				new String[] { "", "Hello world and " });
		performTest(new String[] { "#define MACRO(arg1,arg2) Hello arg1 and arg2", "MACRO(,me)" }, new String[] { "", "Hello  and me" });
		performTest(new String[] { "#define MACRO(arg1,arg2) Hello arg1 and arg2", "MACRO(beatiful world,magnificient me)" },
				new String[] { "", "Hello beatiful world and magnificient me" });
	}

	@Test
	public void nestedMacroArgumentExpansionTest() {
		performTest(new String[] { "#define A test", "#define MACRO(arg) arg", "MACRO(A)" }, new String[] { "", "", "test" });
		performTest(new String[] { "#define A(a) test a", "#define MACRO(arg) arg", "MACRO(A(miau))" },
				new String[] { "", "", "test miau" });
		performTest(new String[] { "#define A test", "#define MACRO(A) A", "MACRO(Hello)" }, new String[] { "", "", "Hello" });
		performTest(new String[] { "#define A test", "#define MACRO(A) A", "MACRO(A)" }, new String[] { "", "", "test" });
		performTest(new String[] { "#define A(a,b) a and b", "#define MACRO(arg) arg", "MACRO(A(one,two))" },
				new String[] { "", "", "one and two" });
		performTest(new String[] { "#define A(a,b) a and b", "#define MACRO(A) A", "MACRO(A(one,two))" },
				new String[] { "", "", "one and two" });

	}

	@Test
	public void concatenationTest() {
		performTest(new String[] { "#define MACRO(a,b) a##b", "MACRO(Hello,World)" }, new String[] { "", "HelloWorld" });
		performTest(new String[] { "#define MACRO(a) a##Miau", "MACRO(Hello)" }, new String[] { "", "HelloMiau" });
		performTest(new String[] { "#define MACRO(a,b) a##b", "MACRO(Hello,)" }, new String[] { "", "Hello" });
		performTest(new String[] { "#define MACRO(a,b) a##b", "MACRO(,World)" }, new String[] { "", "World" });
		performTest(new String[] { "#define MACRO(a,b) a##b", "MACRO(Hello, World)" }, new String[] { "", "Hello World" });
	}

	@Test
	public void stringificationTest() {
		performTest(new String[] { "#define QUOTE(a) #a", "QUOTE(test)" }, new String[] { "", "\"test\"" });
		performTest(new String[] { "#define QUOTE(a) #a", "QUOTE(test here)" }, new String[] { "", "\"test here\"" });
		performTest(new String[] { "#define QUOTE(a) #a", "#define ONE miau", "QUOTE(ONE)" }, new String[] { "", "", "\"miau\"" });
		performTest(new String[] { "#define QUOTE #a", "QUOTE" }, new String[] { "", "\"a\"" });
	}

}
