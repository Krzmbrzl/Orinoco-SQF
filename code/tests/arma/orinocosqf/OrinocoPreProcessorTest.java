package arma.orinocosqf;

import arma.orinocosqf.exceptions.UnknownIdException;
import arma.orinocosqf.helpers.TestOrinocoLexer;
import arma.orinocosqf.helpers.TokenExpector;
import arma.orinocosqf.preprocessing.ArmaFilesystem;
import arma.orinocosqf.preprocessing.OrinocoPreProcessor;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class OrinocoPreProcessorTest {

	private TokenExpector expector;
	private OrinocoPreProcessor preProcessor;
	private TestOrinocoLexer lexer;
	private ArmaFilesystem fs = new ArmaFilesystem(new File(System.getProperty("user.home")).toPath(), new ArrayList<>());
	private TokenExpector.AcceptedTokenFactory tokenFactory;

	private void lexerFromText(@NotNull String text, @NotNull Consumer<CharSequence> preprocessTextCb) {
		expector = new TokenExpector();
		preProcessor = new OrinocoPreProcessor(expector, fs);
		lexer = new TestOrinocoLexer(OrinocoReader.fromCharSequence(text), preProcessor, preprocessTextCb);
		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(text)));
		tokenFactory = new TokenExpector.AcceptedTokenFactory();
	}

	@Test
	public void noPreProcessing_command() {
		Consumer<CharSequence> cb = s -> fail("Expected no text to preprocess. Got " + s);
		lexerFromText("format", cb);
		final int formatId = OrinocoLexer.getCommandId("format");
		tokenFactory.acceptCommand(formatId, 0, 6, 0, 6, lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
	}

	@Test
	public void noPreProcessing_globalVariable() throws Exception {
		Consumer<CharSequence> cb = s -> fail("Expected no text to preprocess. Got " + s);
		String text = "text1";
		lexerFromText(text, cb);
		final int varId = lexer.getIdTransformer().toId(text);
		tokenFactory.acceptGlobalVariable(varId, 0, 5, 0, 5, lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
	}

	@Test
	public void noPreProcessing_localVariable() {
		Consumer<CharSequence> cb = s -> fail("Expected no text to preprocess. Got " + s);
		lexerFromText("_text1", cb);
		tokenFactory.acceptLocalVariable(0, 0, 6, 0, 6, lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
	}

	@Test
	public void noPreProcessing_macroUnmatched() throws UnknownIdException {
		Consumer<CharSequence> cb = s -> fail("Expected no text to preprocess. Got " + s);

		String define = "#define Macro(arg,arg2) arg=arg2";
		String text = "MACRO(v,z)";
		String all = define + "\n" + text;
		lexerFromText(all, cb);

		final int varId_v = lexer.getIdTransformer().toId("v");
		final int varId_z = lexer.getIdTransformer().toId("z");
		final int varId_MACRO = lexer.getIdTransformer().toId("MACRO");

		final int textStart = define.length() + 1; // +1 for \n
		final int lparenStart = textStart + text.indexOf('(');
		final int vStart = textStart + text.indexOf('v');
		final int commaStart = textStart + text.indexOf(',');
		final int zStart = textStart + text.indexOf('z');
		final int rparenStart = textStart + text.indexOf(')');

		final int lparenId = OrinocoLexer.getCommandId("(");
		final int rparenId = OrinocoLexer.getCommandId(")");
		final int commaId = OrinocoLexer.getCommandId(",");

		tokenFactory.acceptGlobalVariable(varId_MACRO, textStart, 5, textStart, 5, lexer.getContext());
		tokenFactory.acceptCommand(lparenId, lparenStart, 1, lparenStart, 1, lexer.getContext());
		tokenFactory.acceptGlobalVariable(varId_v, vStart, 1, vStart, 1, lexer.getContext());
		tokenFactory.acceptCommand(commaId, commaStart, 1, commaStart, 1, lexer.getContext());
		tokenFactory.acceptGlobalVariable(varId_z, zStart, 1, zStart, 1, lexer.getContext());
		tokenFactory.acceptCommand(rparenId, rparenStart, 1, rparenStart, 1, lexer.getContext());

		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
	}

	@Test
	public void simpleParamMacro() throws UnknownIdException {
		// This test is for a simple macro with parameters
		final String expected = "v=z";
		Consumer<CharSequence> cb = s -> assertEquals(expected, s.toString());

		String define = "#define MACRO(arg,arg2) arg=arg2";
		String text = "MACRO(v,z)";
		String all = define + "\n" + text;
		lexerFromText(all, cb);

		final int varId_v = lexer.getIdTransformer().toId("v");
		final int varId_z = lexer.getIdTransformer().toId("z");

		final int textStart = define.length() + 1; // +1 for \n

		final int eqId = OrinocoLexer.getCommandId("=");

		final int vInd = textStart + expected.indexOf('v');
		final int eqInd = textStart + expected.indexOf('=');
		final int zInd = textStart + expected.indexOf('z');

		tokenFactory.acceptGlobalVariable(varId_v, vInd, 1, textStart, text.length(), lexer.getContext());
		tokenFactory.acceptCommand(eqId, eqInd, 1, textStart, text.length(), lexer.getContext());
		tokenFactory.acceptGlobalVariable(varId_z, zInd, 1, textStart, text.length(), lexer.getContext());

		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		lexer.assertDidPreProcessing();
		expector.assertTokensMatch();
	}

	@Test
	public void noPreProcessing_matchButMissingHashHash() {
		// this test creates valid defines that get matched, but arma's preprocessor doesn't
		// allow for replacing text inbetween text unless there is a ##
		Consumer<CharSequence> cb = s -> fail("Expected no text to preprocess. Got " + s);

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

		Consumer<CharSequence> cb = s -> assertEquals(expected[expectedInd[0]++], s.toString());

		String[] lines = {"#define ARG a", "#define ARG2 b", "#define ARG3 c", "ARG = 1 + ARG2 + ARG3"};

		lexerFromText(String.join("\n", lines), cb);

		lexer.start();
		lexer.assertDidPreProcessing();
	}

	@Test
	public void simpleDefineWithParam() {
		// This test is for a simple macro with a single parameter

		Consumer<CharSequence> cb = s -> assertEquals("number 0", s.toString());

		String[] lines = {"#define N(NUMBER) number NUMBER", "Hello N(0)",};

		lexerFromText(String.join("\n", lines), cb);

		lexer.start();
		lexer.assertDidPreProcessing();
	}

	@Test
	public void simpleDefineWithParams() {
		// This test is for a simple macro with multiple parameters

		Consumer<CharSequence> cb = s -> assertEquals("car setVelocity [0,0,5];", s.toString());

		String[] lines = {"#define BLASTOFF(UNIT,RATE) UNIT setVelocity [0,0,RATE];", "disableSerialization; BLASTOFF(car,5)",};

		lexerFromText(String.join("\n", lines), cb);

		lexer.start();
		lexer.assertDidPreProcessing();
	}

	@Test
	public void simpleDefineWithParamMissingGlue() {
		// test for missing ## in body of macro that should come after a nested macro invocation, but preprocessing
		// happens anyways
		Consumer<CharSequence> cb = s -> assertEquals("Hello number 0word", s.toString());

		String[] lines = {"#define N(NUMBER) number NUMBER", "#define MAC Hello N(0)word", "MAC"};

		lexerFromText(String.join("\n", lines), cb);

		lexer.start();
		lexer.assertDidPreProcessing();
	}

	@Test
	public void simpleDefineWithParamWithGlue() {
		// This test is for a simple macro with a single parameter,
		// and there is a ## between the macro and a word.

		Consumer<CharSequence> cb = s -> assertEquals("Hello number 0word", s.toString());

		String[] lines = {"#define N(NUMBER) number NUMBER", "#define MAC Hello N(0)##word", "MAC"};

		lexerFromText(String.join("\n", lines), cb);

		lexer.start();
		lexer.assertDidPreProcessing();
	}

	@Test
	public void multilineDefine() {
		// This test is for a macro spanning multiple lines

		String[] expected = {"Hello \nworld"};
		int[] expectedInd = {0};
		Consumer<CharSequence> cb = s -> assertEquals(expected[expectedInd[0]++], s.toString());

		String[] lines = {"#define HELLO Hello \\\nworld", "HELLO"};

		lexerFromText(String.join("\n", lines), cb);

		lexer.start();
		lexer.assertDidPreProcessing();
	}

	@Test
	public void multilineDefine_withSingleLineComment() {
		// This test is for a macro spanning multiple lines
		// and containging a single line comment in its body

		String[] expected = {"Hello"};
		int[] expectedInd = {0};
		Consumer<CharSequence> cb = s -> assertEquals(expected[expectedInd[0]++], s.toString());

		String[] lines = {"#define HELLO Hello// comment here\\\nworld", "HELLO"};

		lexerFromText(String.join("\n", lines), cb);

		lexer.start();
		lexer.assertDidPreProcessing();
	}

	@Test
	public void multilineDefine_withMultiLineComment() {
		// This test is for a macro spanning multiple lines

		String[] expected = {"Hello\n\nworld"};
		int[] expectedInd = {0};
		Consumer<CharSequence> cb = s -> assertEquals(expected[expectedInd[0]++], s.toString());

		String[] lines = {"#define HELLO Hello/* Multiline\ncomment here*/\\\nworld", "HELLO"};

		lexerFromText(String.join("\n", lines), cb);

		lexer.start();
		lexer.assertDidPreProcessing();
	}

	@Test
	public void glueInMacroBody() {
		// This test is for checking glue (##) inside a macro body
		// and containging a multi line comment in its body

		Consumer<CharSequence> cb = s -> assertEquals("20", s.toString());

		String[] lines = {"#define TWO 2", "#define twenty ##TWO##0", "hint str twenty;", // outputs "20"
		};

		lexerFromText(String.join("\n", lines), cb);

		lexer.start();
		lexer.assertDidPreProcessing();
	}

	@Test
	public void glueParameters() {
		// This test is for glueing 2 macro parameters together

		Consumer<CharSequence> cb = s -> assertEquals("123456", s.toString());

		String[] lines = {"#define GLUE(g1,g2) g1##g2", "GLUE(123,456)",};

		lexerFromText(String.join("\n", lines), cb);

		lexer.start();
		lexer.assertDidPreProcessing();
	}

	@Test
	public void stringify() {
		// This test is for stringify

		String[] expected = {"\"123\";", "\"FOO\";"};
		int[] expectedInd = {0};
		Consumer<CharSequence> cb = s -> assertEquals(expected[expectedInd[0]++], s.toString());

		String[] lines = {"#define STRINGIFY(s) #s;", "test1 = STRINGIFY(123)", "test2 = STRINGIFY(FOO)"};

		lexerFromText(String.join("\n", lines), cb);

		lexer.start();
		lexer.assertDidPreProcessing();
	}

	@Test
	public void stringifyWhitespaceParameters() {
		// This test is for stringify

		Consumer<CharSequence> cb = s -> assertEquals("\"Is it me youre looking for\"", s.toString());

		String[] lines = {"#define HELLO(s) #s", "HELLO(Is it me youre looking for)",};

		lexerFromText(String.join("\n", lines), cb);

		lexer.start();
		lexer.assertDidPreProcessing();
	}

	@Test
	public void stringifyWhitespaceParametersAndCommas() {
		// This test is for stringify and if whitespace between paramters are properly preprocessed.
		// This test is in-game tested in Arma 3 SQF

		Consumer<CharSequence> cb = s -> assertEquals("Hello, \" Is it me youre looking for\"?", s.toString());

		String[] lines = {"#define HELLO(hi,s,end) hi, #s##end", // ## is so we can put ? right after "
				"HELLO(Hello, Is it me youre looking for,?)",};

		lexerFromText(String.join("\n", lines), cb);

		lexer.start();
		lexer.assertDidPreProcessing();
	}

	@Test
	public void embodiedMacro() {
		// This test is for testing if a macro inside the body of another macro will be properly invoked

		Consumer<CharSequence> cb = s -> assertEquals("Foo=1", s.toString());

		String[] lines = {"#define ASSIGN(NAME,VAL) NAME=VAL", "#define SET_TO_ONE(NAME) ASSIGN(NAME,1)", "SET_TO_ONE(Foo);"};

		lexerFromText(String.join("\n", lines), cb);

		lexer.start();
		lexer.assertDidPreProcessing();

		// This test is to make sure that there are different instances of NAME across different macro definitions.
		// Notice how in this test, ASSIGN uses KEY rather than NAME.
		String[] lines2 = {"#define ASSIGN(KEY,VAL) KEY=VAL", "#define SET_TO_ONE(NAME) ASSIGN(NAME,1)", "SET_TO_ONE(Foo);"};

		lexerFromText(String.join("\n", lines2), cb);

		lexer.start();
		lexer.assertDidPreProcessing();
	}

	@Test
	public void macroDefineOrderDoesntMatter() {
		// This test is for making sure that no matter the order of the #defines, the embodied macros are still properly handled

		Consumer<CharSequence> cb = s -> assertEquals("Something", s.toString());

		String[] lines = {"#define ONE TWO", // notice how ONE is dependent on TWO, but TWO is defined after ONE
				"#define TWO Something", "ONE",};

		lexerFromText(String.join("\n", lines), cb);

		lexer.start();
		lexer.assertDidPreProcessing();
	}

	@Test
	public void undef() {
		// This test is for a simple macro that becomes undefined

		String[] expected = {"a"};
		int[] expectedInd = {0};
		Consumer<CharSequence> cb = s -> assertEquals(expected[expectedInd[0]++], s.toString());

		String[] lines = {"#define ARG a", "ARG", "#undef ARG", "ARG",};

		lexerFromText(String.join("\n", lines), cb);
		lexer.start();
		lexer.assertDidPreProcessing();
	}

	@Test
	public void undefThenRedef() {
		// This test is for a simple macro that becomes undefined and then redefined

		String[] expected = {"a", "BB"};
		int[] expectedInd = {0};
		Consumer<CharSequence> cb = s -> assertEquals(expected[expectedInd[0]++], s.toString());

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
		lexer.assertDidPreProcessing();
	}

	@Test
	public void ifdef() {
		// This test is for a simple macro that becomes defined inside an ifdef

		String[] expected = {"def", "def"};
		int[] expectedInd = {0};
		Consumer<CharSequence> cb = s -> assertEquals(expected[expectedInd[0]++], s.toString());

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
		lexer.assertDidPreProcessing();
	}

	@Test
	public void ifdefElse() {
		// This test is for #else for #ifdef

		String[] expected = {"beg"};
		int[] expectedInd = {0};
		Consumer<CharSequence> cb = s -> assertEquals(expected[expectedInd[0]++], s.toString());

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
		lexer.assertDidPreProcessing();
	}

	@Test
	public void ifdefIgnoreElse() {
		// This test is for #else for #ifdef, but the #else block is skipped because #ifdef is true

		String[] expected = {"def", "a"};
		int[] expectedInd = {0};
		Consumer<CharSequence> cb = s -> assertEquals(expected[expectedInd[0]++], s.toString());

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
		lexer.assertDidPreProcessing();
	}

	@Test
	public void ifndef() {
		// This test is for a simple macro that becomes defined inside an ifndef

		String[] expected = {"def", "def"};
		int[] expectedInd = {0};
		Consumer<CharSequence> cb = s -> assertEquals(expected[expectedInd[0]++], s.toString());

		String[] lines = {"#ifndef ARG", "#define DEF def", "DEF", "#endif", "DEF",};

		lexerFromText(String.join("\n", lines), cb);
		lexer.start();
		lexer.assertDidPreProcessing();
	}

	@Test
	public void ifndefElse() {
		// This test is for #else for #ifndef

		String[] expected = {"a", "beg"};
		int[] expectedInd = {0};
		Consumer<CharSequence> cb = s -> assertEquals(expected[expectedInd[0]++], s.toString());

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
		lexer.assertDidPreProcessing();
	}

	@Test
	public void ifndefIgnoreElse() {
		// This test is for #else for #ifndef but #else block is skipped because #ifndef is true

		Consumer<CharSequence> cb = s -> assertEquals("def", s.toString());

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
		lexer.assertDidPreProcessing();
	}
//
//	@Test
//	public void includeVirtual() {
//		// TODO: reimplement properly
//		// This test is for a simple include where the included handler returns hard coded text
//
//		String[] expected = {"a", "included"};
//		int[] expectedInd = {0};
//		Consumer<CharSequence> cb = s -> assertEquals(expected[expectedInd[0]++], s.toString());
//
//		String[] lines = { "#define ARG a", "ARG", "#include \"test include\"", // doesn't matter what is included as include handler is
//																				// hard coded
//				"INCLUDE_MACRO" };
//
//		lexerFromText(String.join("\n", lines), cb);
//
//		lexer.start();
//		lexer.assertDidPreProcessing();
//	}


}
