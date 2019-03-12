package arma.orinocosqf;

import arma.orinocosqf.helpers.TestOrinocoLexer;
import arma.orinocosqf.helpers.TokenExpector;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class OrinocoPreProcessorTest {
	private final TokenExpector expector = new TokenExpector();
	private final OrinocoPreProcessor preProcessor = new OrinocoPreProcessor(expector);
	private TestOrinocoLexer lexer;
	private final TokenExpector.AcceptedTokenFactory tokenFactory = new TokenExpector.AcceptedTokenFactory();

	private void lexerFromText(@NotNull String text, @NotNull Consumer<String> preprocessTextCb) {
		lexer = new TestOrinocoLexer(OrinocoReader.fromCharSequence(text), preProcessor, preprocessTextCb);
	}

	private void lexerFromFile(@NotNull File f, @NotNull Consumer<String> preprocessTextCb) throws FileNotFoundException {
		lexer = new TestOrinocoLexer(
				OrinocoReader.fromStream(new FileInputStream(f), StandardCharsets.UTF_8),
				preProcessor,
				preprocessTextCb
		);
	}

	@Test
	public void noPreProcessing_command() {
		Consumer<String> cb = s -> fail("Expected no text to preprocess. Got " + s);
		lexerFromText("format", cb);
		final int formatId = OrinocoLexer.getCommandId("format");
		tokenFactory.acceptCommand(formatId, 0, 0, 6);
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
	}

	@Test
	public void noPreProcessing_globalVariable() {
		Consumer<String> cb = s -> fail("Expected no text to preprocess. Got " + s);
		lexerFromText("text1", cb);
		tokenFactory.acceptGlobalVariable(0, 0, 0, 5);
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
	}

	@Test
	public void noPreProcessing_localVariable() {
		Consumer<String> cb = s -> fail("Expected no text to preprocess. Got " + s);
		lexerFromText("_text1", cb);
		tokenFactory.acceptLocalVariable(0, 0, 0, 6);
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
	}

	@Test
	public void noPreProcessing_macroUnmatched() {
		Consumer<String> cb = s -> fail("Expected no text to preprocess. Got " + s);

		String define = "#define Macro(arg,arg2) arg=arg2";
		String text = "MACRO(v,z)";
		String all = define + "\n" + text;
		lexerFromText(all, cb);

		final int textStart = define.length() + 1; // +1 for \n
		final int lparenStart = text.indexOf('(');
		final int vStart = text.indexOf('v');
		final int commaStart = text.indexOf(',');
		final int zStart = text.indexOf('z');
		final int rparenStart = text.indexOf(')');
		final int offset = define.length();

		final int lparenId = OrinocoLexer.getCommandId("(");
		final int rparenId = OrinocoLexer.getCommandId(")");
		final int commaId = OrinocoLexer.getCommandId(",");

		tokenFactory.acceptGlobalVariable(0, 0, textStart, 5);
		tokenFactory.acceptCommand(lparenId, lparenStart - offset, lparenStart, 1);
		tokenFactory.acceptGlobalVariable(1, vStart - offset, vStart, 1);
		tokenFactory.acceptCommand(commaId, commaStart - offset, commaStart, 1);
		tokenFactory.acceptGlobalVariable(2, zStart - offset, zStart, 1);
		tokenFactory.acceptCommand(rparenId, rparenStart - offset, rparenStart, 1);

		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
	}

	@Test
	public void simpleParamMacro() {
		// This test is for a simple macro with parameters
		final String expected = "v=z";
		Consumer<String> cb = s -> assertEquals(expected, s);

		String define = "#define MACRO(arg,arg2) arg=arg2";
		String text = "MACRO(v,z)";
		String all = define + "\n" + text;
		lexerFromText(all, cb);

		final int textStart = define.length() + 1; // +1 for \n

		final int eqId = OrinocoLexer.getCommandId("=");

		final int vInd = expected.indexOf('v');
		final int eqInd = expected.indexOf('=');
		final int zInd = expected.indexOf('z');

		tokenFactory.acceptGlobalVariable(0, vInd, textStart, text.length());
		tokenFactory.acceptCommand(eqId, eqInd, textStart, text.length());
		tokenFactory.acceptGlobalVariable(1, zInd, textStart, text.length());

		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
	}

	@Test
	public void noPreProcessing_matchButMissingHashHash() {
		// this test creates valid defines that get matched, but arma's preprocessor doesn't
		// allow for replacing text inbetween text unless there is a ##
		Consumer<String> cb = s -> fail("Expected no text to preprocess. Got " + s);

		String[] lines = {
				"#define e a",
				"#define oo a",
				"The cow jumped over the moon!"
		};

		lexerFromText(String.join("\n", lines), cb);
		lexer.start();
	}

	@Test
	public void simpleDefine() {
		// This test is for a simple macro without parameters

		String[] expected = {"a", "b", "c"};
		int[] expectedInd = {0};
		Consumer<String> cb = s -> assertEquals(expected[expectedInd[0]++], s);

		String[] lines = {
				"#define ARG a",
				"#define ARG2 b",
				"#define ARG3 c",
				"ARG = 1 + ARG2 + ARG3"
		};

		lexerFromText(String.join("\n", lines), cb);

		lexer.start();
	}

	@Test
	public void simpleDefineWithParam() {
		// This test is for a simple macro with a single parameter

		String[] expected = {"N(0)"};
		int[] expectedInd = {0};
		Consumer<String> cb = s -> assertEquals(expected[expectedInd[0]++], s);

		String[] lines = {
				"#define N(NUMBER) number NUMBER",
				"Hello N(0)",
		};

		lexerFromText(String.join("\n", lines), cb);

		lexer.start();
	}

	@Test
	public void simpleDefineWithParamMissingGlue() {
		// This test is for a simple macro with a single parameter,
		// but there is no preprocessing because of missing ##

		Consumer<String> cb = s -> fail("Expected no text to preprocess, got " + s);

		String[] lines = {
				"#define N(NUMBER) number NUMBER",
				"Hello N(0)word",
		};

		lexerFromText(String.join("\n", lines), cb);

		lexer.start();
	}

	@Test
	public void simpleDefineWithParamWithGlue() {
		// This test is for a simple macro with a single parameter,
		// and there is a ## between the macro and a word.

		String[] expected = {"N(0)##word"};
		int[] expectedInd = {0};
		Consumer<String> cb = s -> assertEquals(expected[expectedInd[0]++], s);

		String[] lines = {
				"#define N(NUMBER) number NUMBER",
				"Hello N(0)##word",
		};

		lexerFromText(String.join("\n", lines), cb);

		lexer.start();
	}

	@Test
	public void glueTwist() {
		// This test is for a simple macro without parameters with a twist: a macro is almost matched (ARG2),
		// but the ## makes it match ARG instead and then glues it with a 2

		String[] expected = {"ARG", "ARG##2", "ARG3"};
		int[] expectedInd = {0};
		Consumer<String> cb = s -> assertEquals(expected[expectedInd[0]++], s);

		String[] lines = {
				"#define ARG a",
				"#define ARG2 b",
				"#define ARG3 c",
				"ARG = 1 + ARG##2 + ARG3"
		};

		lexerFromText(String.join("\n", lines), cb);

		lexer.start();
	}

	// todo glue(g1,g2) g1##g2
	// todo model = \\OFP2\\Structures\\Various\\##FOLDER##\\##FOLDER; #define FOLDER myFolder

	@Test
	public void glueInMacroBody() {
		// This test is for checking glue (##) inside a macro body

		String[] expected = {"twenty", "##TWO##0"};
		int[] expectedInd = {0};
		Consumer<String> cb = s -> assertEquals(expected[expectedInd[0]++], s);

		String[] lines = {
				"#define TWO 2",
				"#define twenty ##TWO##0",
				"hint str twenty;", // outputs "20"
		};

		lexerFromText(String.join("\n", lines), cb);

		lexer.start();
	}
}