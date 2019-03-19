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

		Consumer<String> cb = s -> assertEquals("number 0", s);

		String[] lines = {
				"#define N(NUMBER) number NUMBER",
				"Hello N(0)",
		};

		lexerFromText(String.join("\n", lines), cb);

		lexer.start();
	}

	@Test
	public void simpleDefineWithParams() {
		// This test is for a simple macro with multiple parameters

		Consumer<String> cb = s -> assertEquals("car setVelocity [0,0,5];", s);

		String[] lines = {
				"#define BLASTOFF(UNIT,RATE) UNIT setVelocity [0,0,RATE];",
				"disableSerialization; BLASTOFF(car,5)",
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

		String[] expected = {"Hello number 0word"};
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

		String[] expected = {"a", "a2", "c"};
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

	@Test
	public void glueInMacroBody() {
		// This test is for checking glue (##) inside a macro body

		Consumer<String> cb = s -> assertEquals("20", s);

		String[] lines = {
				"#define TWO 2",
				"#define twenty ##TWO##0",
				"hint str twenty;", // outputs "20"
		};

		lexerFromText(String.join("\n", lines), cb);

		lexer.start();
	}

	@Test
	public void glueParameters() {
		// This test is for glueing 2 macro parameters together

		Consumer<String> cb = s -> assertEquals("123456", s);

		String[] lines = {
				"#define GLUE(g1,g2) g1##g2",
				"GLUE(123,456)",
		};

		lexerFromText(String.join("\n", lines), cb);

		lexer.start();
	}

	@Test
	public void stringify() {
		// This test is for stringify

		String[] expected = {"\"123\";", "\"456\";"};
		int[] expectedInd = {0};
		Consumer<String> cb = s -> assertEquals(expected[expectedInd[0]++], s);

		String[] lines = {
				"#define STRINGIFY(s) #s;",
				"test1 = STRINGIFY(123)",
				"test2 = STRINGIFY(FOO)"
		};

		lexerFromText(String.join("\n", lines), cb);

		lexer.start();
	}

	@Test
	public void embodiedMacro() {
		// This test is for testing if a macro inside the body of another macro will be properly invoked

		Consumer<String> cb = s -> assertEquals("Foo=1;", s);

		String[] lines = {
				"#define ASSIGN(NAME,VAL) NAME=VAL",
				"#define SET_TO_ONE(NAME) ASSIGN(NAME,1)",
				"SET_TO_ONE(Foo);"
		};

		lexerFromText(String.join("\n", lines), cb);

		lexer.start();

		// This test is to make sure that there are different instances of NAME across different macro definitions.
		// Notice how in this test, ASSIGN uses KEY rather than NAME.
		String[] lines2 = {
				"#define ASSIGN(KEY,VAL) KEY=VAL",
				"#define SET_TO_ONE(NAME) ASSIGN(NAME,1)",
				"SET_TO_ONE(Foo);"
		};

		lexerFromText(String.join("\n", lines2), cb);

		lexer.start();
	}

	@Test
	public void macroDefineOrderDoesntMatter() {
		// This test is for making sure that no matter the order of the #defines, the embodied macros are still properly handled

		Consumer<String> cb = s -> assertEquals("Something", s);

		String[] lines = {
				"#define ONE TWO", //notice how ONE is dependent on TWO, but TWO is defined after ONE
				"#define TWO Something",
				"ONE",
		};

		lexerFromText(String.join("\n", lines), cb);

		lexer.start();
	}

	@Test
	public void undef() {
		// This test is for a simple macro that becomes undefined

		String[] expected = {"a"};
		int[] expectedInd = {0};
		Consumer<String> cb = s -> assertEquals(expected[expectedInd[0]++], s);

		String[] lines = {
				"#define ARG a",
				"ARG",
				"#undef ARG",
				"ARG",
		};

		lexerFromText(String.join("\n", lines), cb);
		lexer.start();
	}

	@Test
	public void undefThenRedef() {
		// This test is for a simple macro that becomes undefined and then redefined

		String[] expected = {"a", "BB"};
		int[] expectedInd = {0};
		Consumer<String> cb = s -> assertEquals(expected[expectedInd[0]++], s);

		String[] lines = {
				"#define ARG a",
				"ARG",
				"#undef ARG",
				"ARG",
				"#define ARG BB",
				"ARG",
		};

		lexerFromText(String.join("\n", lines), cb);
		lexer.start();
	}

	@Test
	public void ifdef() {
		// This test is for a simple macro that becomes defined inside an ifdef

		String[] expected = {"def", "def"};
		int[] expectedInd = {0};
		Consumer<String> cb = s -> assertEquals(expected[expectedInd[0]++], s);

		String[] lines = {
				"#define ARG a",
				"#ifdef ARG",
				"#define DEF def",
				"DEF",
				"#endif",
				"DEF",
		};

		lexerFromText(String.join("\n", lines), cb);
		lexer.start();
	}

	@Test
	public void ifdefElse() {
		// This test is for #else for #ifdef

		String[] expected = {"beg"};
		int[] expectedInd = {0};
		Consumer<String> cb = s -> assertEquals(expected[expectedInd[0]++], s);

		String[] lines = {
				"#ifdef ARG",
				"#define DEF def",
				"DEF", //this is here to ensure the #define is skipped
				"#else",
				"#define BEG beg",
				"#endif",
				"DEF",
				"ARG",
				"BEG",
		};

		lexerFromText(String.join("\n", lines), cb);
		lexer.start();
	}

	@Test
	public void ifdefIgnoreElse() {
		// This test is for #else for #ifdef, but the #else block is skipped because #ifdef is true

		String[] expected = {"def", "a"};
		int[] expectedInd = {0};
		Consumer<String> cb = s -> assertEquals(expected[expectedInd[0]++], s);

		String[] lines = {
				"#define ARG a",
				"#ifdef ARG",
				"#define DEF def",
				"#else",
				"#define BEG beg",
				"#endif",
				"DEF",
				"ARG",
				"BEG",
		};

		lexerFromText(String.join("\n", lines), cb);
		lexer.start();
	}

	@Test
	public void ifndef() {
		// This test is for a simple macro that becomes defined inside an ifndef

		String[] expected = {"def", "def"};
		int[] expectedInd = {0};
		Consumer<String> cb = s -> assertEquals(expected[expectedInd[0]++], s);

		String[] lines = {
				"#ifndef ARG",
				"#define DEF def",
				"DEF",
				"#endif",
				"DEF",
		};

		lexerFromText(String.join("\n", lines), cb);
		lexer.start();
	}

	@Test
	public void ifndefElse() {
		// This test is for #else for #ifndef

		String[] expected = {"a", "beg"};
		int[] expectedInd = {0};
		Consumer<String> cb = s -> assertEquals(expected[expectedInd[0]++], s);

		String[] lines = {
				"#define ARG a",
				"#ifndef ARG",
				"#define DEF def",
				"DEF", //this is here to ensure the #define is skipped
				"#else",
				"#define BEG beg",
				"#endif",
				"DEF",
				"ARG",
				"BEG",
		};

		lexerFromText(String.join("\n", lines), cb);
		lexer.start();
	}

	@Test
	public void ifndefIgnoreElse() {
		// This test is for #else for #ifndef but #else block is skipped because #ifndef is true

		String[] expected = {"def"};
		int[] expectedInd = {0};
		Consumer<String> cb = s -> assertEquals(expected[expectedInd[0]++], s);

		String[] lines = {
				"#ifndef ARG",
				"#define DEF def",
				"#else",
				"#define BEG beg",
				"#endif",
				"DEF",
				"ARG",
				"BEG",
		};

		lexerFromText(String.join("\n", lines), cb);
		lexer.start();
	}

	//todo include: discuss how lexer handles working directories for preprocessor include

}