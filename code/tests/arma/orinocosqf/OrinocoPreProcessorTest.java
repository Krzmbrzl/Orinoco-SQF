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
}