package arma.orinocosqf;

import arma.orinocosqf.helpers.TokenExpector;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;

public class OrinocoPreProcessorTest {
	private final TokenExpector expector = new TokenExpector();
	private final OrinocoPreProcessor preProcessor = new OrinocoPreProcessor(expector);
	private OrinocoLexer lexer;
	private final TokenExpector.AcceptedTokenFactory tokenFactory = new TokenExpector.AcceptedTokenFactory();

	private void lexerFromText(@NotNull String text) {
		lexer = new OrinocoLexer(OrinocoReader.fromCharSequence(text), preProcessor);
	}

	private void lexerFromFile(@NotNull File f) throws FileNotFoundException {
		lexer = new OrinocoLexer(OrinocoReader.fromStream(new FileInputStream(f), StandardCharsets.UTF_8), preProcessor);
	}

	@Test
	public void noPreProcessing_command() {
		lexerFromText("format");
		final int formatId = OrinocoLexer.getCommandId("format");
		tokenFactory.acceptCommand(formatId, 0, 0, 6);
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
	}

	@Test
	public void noPreProcessing_globalVariable() {
		lexerFromText("text1");
		tokenFactory.acceptGlobalVariable(0, 0, 0, 5);
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
	}

	@Test
	public void noPreProcessing_localVariable() {
		lexerFromText("_text1");
		tokenFactory.acceptLocalVariable(0, 0, 0, 6);
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
	}

	@Test
	public void noPreProcessing_macroUnmatched() {
		String define = "#define Macro(arg,arg2) arg=arg2";
		String text = "MACRO(v,z)";
		String all = define + "\n" + text;
		lexerFromText(all);

		final int textStart = define.length() + 1; // +1 for \n
		final int lparenStart = text.indexOf('(');
		final int vStart = text.indexOf('v');
		final int commaStart = text.indexOf(',');
		final int zStart = text.indexOf('z');
		final int rparenStart = text.indexOf(')');

		final int lparenId = OrinocoLexer.getCommandId("(");
		final int rparenId = OrinocoLexer.getCommandId(")");
		final int commaId = OrinocoLexer.getCommandId(",");

		//todo include #define in tokens?
		tokenFactory.acceptGlobalVariable(0, textStart, textStart, 5);
		tokenFactory.acceptCommand(lparenId, lparenStart, lparenStart, 1);
		tokenFactory.acceptGlobalVariable(1, vStart, vStart, 1);
		tokenFactory.acceptCommand(commaId, commaStart, commaStart, 1);
		tokenFactory.acceptGlobalVariable(2, zStart, zStart, 1);
		tokenFactory.acceptCommand(rparenId, rparenStart, rparenStart, 1);

		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
	}
}