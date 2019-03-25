package arma.orinocosqf;

import arma.orinocosqf.helpers.TokenExpector;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;

public class OrinocoLexerTest {
	private TokenExpector expector;
	private TokenExpector.AcceptedTokenFactory tokenFactory;
	private OrinocoLexer lexer;

	@Before
	public void setUp() throws Exception {
		expector = new TokenExpector(true);
		tokenFactory = new TokenExpector.AcceptedTokenFactory();
	}

	private void lexerFromText(@NotNull String text) {
		lexer = new OrinocoLexer(OrinocoReader.fromCharSequence(text), expector);
	}

	private void lexerFromFile(@NotNull File f) throws FileNotFoundException {
		lexer = new OrinocoLexer(OrinocoReader.fromStream(new FileInputStream(f), StandardCharsets.UTF_8), expector);
	}

	@Test
	public void literal_string_doubleQuotes() {
		String input = "\"\"";
		lexerFromText(input);

		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.String, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "\"With input\"";
		lexerFromText(input);

		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.String, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
	}

	@Test
	public void literal_string_singleQuotes() {
		String input = "''";
		lexerFromText(input);

		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.String, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "'With input'";
		lexerFromText(input);

		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.String, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
	}

	@Test
	public void literal_number_int() {
		String input = "5";
		lexerFromText(input);

		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "33576";
		lexerFromText(input);

		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
	}

	@Test
	public void literal_number_float() {
		String input = "0.5";
		lexerFromText(input);

		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "0.33576";
		lexerFromText(input);

		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
	}

	@Test
	public void literal_number_float_noLeadingZero() {
		String input = ".5";
		lexerFromText(input);

		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = ".33576";
		lexerFromText(input);

		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
	}

	@Test
	public void literal_number_scientific_intFactor() {
		String input = "2e5";
		lexerFromText(input);

		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "23e12";
		lexerFromText(input);

		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "2E5";
		lexerFromText(input);

		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "23E12";
		lexerFromText(input);

		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
	}

	@Test
	public void literal_number_scientific_floatFactor() {
		String input = "2.3e5";
		lexerFromText(input);

		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "23.456e12";
		lexerFromText(input);

		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "2.3E5";
		lexerFromText(input);

		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "23.456E12";
		lexerFromText(input);

		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
	}

	@Test
	public void literal_number_scientific_intFactor_negativeExponent() {
		String input = "2e-5";
		lexerFromText(input);

		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "23e-12";
		lexerFromText(input);

		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "2E-5";
		lexerFromText(input);

		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "23E-12";
		lexerFromText(input);

		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
	}

	@Test
	public void literal_number_scientific_floatFactor_negativeExponent() {
		String input = "2.3e-5";
		lexerFromText(input);

		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "23.456e-12";
		lexerFromText(input);

		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "2.3E-5";
		lexerFromText(input);

		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "23.456E-12";
		lexerFromText(input);

		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
	}

	@Test
	public void literal_number_scientific_intFactor_implicitExponent() {
		String input = "2e";
		lexerFromText(input);

		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "23e";
		lexerFromText(input);

		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "2E";
		lexerFromText(input);

		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "23E";
		lexerFromText(input);

		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
	}

	@Test
	public void literal_number_scientific_floatFactor_implicitExponent() {
		String input = "2.3e";
		lexerFromText(input);

		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "23.456e";
		lexerFromText(input);

		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "2.3E";
		lexerFromText(input);

		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "23.456E";
		lexerFromText(input);

		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
	}

	@Test
	public void literal_number_hex_0x() {
		String input = "0x1";
		lexerFromText(input);

		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "0xFF";
		lexerFromText(input);

		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "0xABCDEF1234567890";
		lexerFromText(input);

		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
	}

	@Test
	public void literal_number_hex_dollar() {
		String input = "$1";
		lexerFromText(input);

		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "$FF";
		lexerFromText(input);

		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "$ABCDEF1234567890";
		lexerFromText(input);

		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
	}
}
