package arma.orinocosqf;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.junit.BeforeClass;
import org.junit.Test;

import arma.orinocosqf.helpers.VirtualArmaFileSystem;
import arma.orinocosqf.lexer.OrinocoLexer;
import arma.orinocosqf.preprocessing.OrinocoPreProcessor;
import arma.orinocosqf.problems.Problem;
import arma.orinocosqf.problems.ProblemListener;
import arma.orinocosqf.problems.Problems;
import arma.orinocosqf.tokenprocessing.OutputTokenProcessor;

public class PreProcessingErrorTest {

	static class TestProblemListener implements ProblemListener {
		public List<EncounteredProblem> problems;

		public TestProblemListener() {
			problems = new ArrayList<>();
		}

		@Override
		public void problemEncountered(@NotNull Problem problem, @NotNull String msg, int offset, int length, int line) {
			problems.add(new EncounteredProblem(problem, msg, offset, length, line));
		}
	}

	static class EncounteredProblem {
		Problem problem;
		String msg;
		int offset;
		int length;
		int line;

		public EncounteredProblem(@NotNull Problem problem, int offset, int length, int line) {
			this(problem, "<The message is not subject of the test>", offset, length, line);
		}

		public EncounteredProblem(@NotNull Problem problem, @NotNull String msg, int offset, int length, int line) {
			this.problem = problem;
			this.msg = msg;
			this.offset = offset;
			this.length = length;
			this.line = line;
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof EncounteredProblem)) {
				return false;
			}

			EncounteredProblem other = (EncounteredProblem) o;

			// Don't compare the message as this might be implementation specific and/or subject to localization
			return this.problem.equals(other.problem) && this.offset == other.offset && this.length == other.length
					&& this.line == other.line;
		}

		@Override
		public String toString() {
			return problem.toString() + " at offset " + offset + "-" + (offset + length) + " in line " + line + ": \"" + msg + "\"";
		}
	}

	static OrinocoPreProcessor preprocessor;
	static OutputTokenProcessor processor;
	static VirtualArmaFileSystem virtualFs;
	static OrinocoLexer lexer;
	static TestProblemListener problemListener;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		problemListener = new TestProblemListener();

		virtualFs = new VirtualArmaFileSystem();
		processor = new OutputTokenProcessor();
		preprocessor = new OrinocoPreProcessor(processor, virtualFs);
		lexer = new OrinocoLexer(preprocessor, problemListener);

		processor.setOutputWriter(new OutputStreamWriter(new OutputStream() {

			@Override
			public void write(int b) throws IOException {
				// discard
			}
		}));

		lexer.enableTextBuffering(true);
	}

	void assertProblemEquals(EncounteredProblem expected, EncounteredProblem actual) {
		if (!expected.equals(actual)) {
			assertEquals("The encountered problem doesn't match the expected one", expected.toString(), actual.toString());

			// fail-safe
			fail("Problems aren't equal but their String-representations are");
		}
	}

	void performTest(String input, EncounteredProblem[] expectedProblems) {
		problemListener.problems.clear();

		lexer.start(OrinocoReader.fromCharSequence(input));

		if (expectedProblems.length != 0 && problemListener.problems.size() == 0) {
			fail("Expected problems to be reported, but none were");
		}

		for (int i = 0; i < expectedProblems.length; i++) {
			EncounteredProblem expectedProblem = expectedProblems[i];

			if (i >= problemListener.problems.size()) {
				fail("Expected more problems than were reported.\nUnmatched problem: " + expectedProblem.toString());
			}

			EncounteredProblem actualProblem = problemListener.problems.get(i);

			assertProblemEquals(expectedProblem, actualProblem);
		}

		if (expectedProblems.length < problemListener.problems.size()) {
			StringBuilder errorMsg = new StringBuilder();
			errorMsg.append("Got more errors than expected:\n");

			for (int i = expectedProblems.length; i < problemListener.problems.size(); i++) {
				errorMsg.append("  ");
				errorMsg.append(i - expectedProblems.length + 1);
				errorMsg.append(". ");
				errorMsg.append(problemListener.problems.get(i).toString());
				errorMsg.append("\n");
			}

			fail(errorMsg.toString());
		}
	}

	@Test
	public void preprocessorWhitespaceErrors() {
		performTest("#define TEST(arg1, arg2)", new EncounteredProblem[] { new EncounteredProblem(Problems.ERROR_LEADING_WS, 18, 1, 1) });
		performTest("#define TEST(arg1 ,arg2)", new EncounteredProblem[] { new EncounteredProblem(Problems.ERROR_TRAILING_WS, 17, 1, 1) });
		performTest("#define TEST()", new EncounteredProblem[] { new EncounteredProblem(Problems.ERROR_EMPTY, 12, 1, 1) });
		performTest("#define TEST(   )", new EncounteredProblem[] { new EncounteredProblem(Problems.ERROR_EMPTY, 13, 3, 1) });
		performTest("#define TEST( )", new EncounteredProblem[] { new EncounteredProblem(Problems.ERROR_EMPTY, 13, 1, 1) });
		performTest("#define TEST(a,)", new EncounteredProblem[] { new EncounteredProblem(Problems.ERROR_EMPTY, 14, 1, 1) });
		performTest("#define TEST(a,   )", new EncounteredProblem[] { new EncounteredProblem(Problems.ERROR_EMPTY, 15, 3, 1) });
		performTest("#define TEST(a, )", new EncounteredProblem[] { new EncounteredProblem(Problems.ERROR_EMPTY, 15, 1, 1) });
	}

	@Test
	public void macroArgumentCountErrors() {
		// No argument provided and no parenthesis present
		// Note that the arma preprocessor doesn't throw an error in this kind of situation and simply doesn't expand the respective macro
		// essentially treating it as a global variable. However we don't know if this is still an error that is simply hidden behind the
		// scenes
		// and even if it is not, this is very bad practice to do. Thus (for now) we throw an error
		performTest("#define TEST(arg1,arg2) arg1 arg2\nTEST",
				new EncounteredProblem[] { new EncounteredProblem(Problems.ERROR_WRONG_ARGUMENT_COUNT, 34, 4, 2) });
		// No argument provided
		performTest("#define TEST(arg1,arg2) arg1 arg2\nTEST()",
				new EncounteredProblem[] { new EncounteredProblem(Problems.ERROR_WRONG_ARGUMENT_COUNT, 34, 6, 2) });
		// Only one argument provided
		performTest("#define TEST(arg1,arg2) arg1 arg2\nTEST(a)",
				new EncounteredProblem[] { new EncounteredProblem(Problems.ERROR_WRONG_ARGUMENT_COUNT, 34, 7, 2) });
		// One argument too much
		performTest("#define TEST(arg1,arg2) arg1 arg2\nTEST(a,b,c)",
				new EncounteredProblem[] { new EncounteredProblem(Problems.ERROR_WRONG_ARGUMENT_COUNT, 34, 11, 2) });
		// Three arguments too much
		performTest("#define TEST(arg1,arg2) arg1 arg2\nTEST(a,b,c,d,e)",
				new EncounteredProblem[] { new EncounteredProblem(Problems.ERROR_WRONG_ARGUMENT_COUNT, 34, 15, 2) });
	}

	@Test
	public void parenthesisError() {
		// Unclosed parenthesis
		performTest("#define TEST(a This is a",
				new EncounteredProblem[] { new EncounteredProblem(Problems.ERROR_UNCLOSED_PARENTHESIS, 12, 1, 1) });
		// Nested parenthesis
		performTest("#define a((b))", new EncounteredProblem[] { new EncounteredProblem(Problems.ERROR_INVALID_CHARACTER, 10, 1, 1)});
	}

}
