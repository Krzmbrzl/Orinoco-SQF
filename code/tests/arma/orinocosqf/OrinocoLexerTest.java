package arma.orinocosqf;

import arma.orinocosqf.exceptions.UnknownIdException;
import arma.orinocosqf.helpers.TokenExpector;
import arma.orinocosqf.lexer.OrinocoLexer;
import arma.orinocosqf.lexer.OrinocoLexerSQFLiteralType;
import arma.orinocosqf.lexer.SimpleOrinocoLexerContext;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OrinocoLexerTest {
	private TokenExpector expector;
	private TokenExpector.AcceptedTokenFactory tokenFactory;
	private OrinocoLexer lexer;


	private void lexerFromText(@NotNull String text) {
		expector = new TokenExpector(true);
		lexer = new OrinocoLexer(OrinocoReader.fromCharSequence(text), expector);
		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(text)));
		tokenFactory = new TokenExpector.AcceptedTokenFactory();
	}

	private IdTransformer<String> getVariableTransformer() {
		return lexer.getIdTransformer();
	}

	private IdTransformer<String> getCommandTransformer() {
		return SQFCommands.instance;
	}

	@Test
	public void emptyInput() {
		String input = "";
		lexerFromText(input);

		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
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
	public void literal_string_doubleQuotes_escapedQuotes() {
		String input = "\"\"\"\"";
		lexerFromText(input);

		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.String, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "\"With \"\"input\"\"\"";
		lexerFromText(input);

		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.String, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "\"With \"\"\"\"input\"\"\"\"\"";
		lexerFromText(input);

		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.String, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
	}

	@Test
	public void literal_string_doubleQuotes_containingSingeQuotes() {
		String input = "\"'\"";
		lexerFromText(input);

		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.String, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "\"With 'input'\"";
		lexerFromText(input);

		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.String, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "\"With ''input''\"";
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
	public void literal_string_singleQuotes_escapedQuotes() {
		String input = "''''";
		lexerFromText(input);

		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.String, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "'With ''input'''";
		lexerFromText(input);

		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.String, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "'With ''''input'''''";
		lexerFromText(input);

		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.String, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
	}

	@Test
	public void literal_string_singleQuotes_containingDoubleQuotes() {
		String input = "'\"'";
		lexerFromText(input);

		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.String, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "'With \"input\"'";
		lexerFromText(input);

		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.String, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "'With \"\"input\"\"'";
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

	@Test
	public void globalVariables() throws UnknownIdException {
		String input = "test";
		lexerFromText(input);

		tokenFactory.acceptGlobalVariable(getVariableTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "otherTest";
		lexerFromText(input);

		tokenFactory.acceptGlobalVariable(getVariableTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
	}

	@Test
	public void globalVariables_caseInsensitive() throws UnknownIdException {
		String input;
		{
			input = "test";
			lexerFromText(input);
			int testId = getVariableTransformer().toId("test");

			// precondition: Used id-transformer is case-insensitive
			assertEquals("The used variable transformer appears to be case-sensitive", getVariableTransformer().toId("test"),
					getVariableTransformer().toId("TeSt"));

			tokenFactory.acceptGlobalVariable(testId, 0, input.length(), 0, input.length(), lexer.getContext());
			expector.addExpectedTokens(tokenFactory.getTokens());
			lexer.start();
			expector.assertTokensMatch();
		}

		{
			input = "Test";
			lexerFromText(input);
			int testId = getVariableTransformer().toId("test");

			tokenFactory.acceptGlobalVariable(testId, 0, input.length(), 0, input.length(), lexer.getContext());
			expector.addExpectedTokens(tokenFactory.getTokens());
			lexer.start();
			expector.assertTokensMatch();
		}


		{
			input = "tesT";
			lexerFromText(input);
			int testId = getVariableTransformer().toId("test");

			tokenFactory.acceptGlobalVariable(testId, 0, input.length(), 0, input.length(), lexer.getContext());
			expector.addExpectedTokens(tokenFactory.getTokens());
			lexer.start();
			expector.assertTokensMatch();
		}


		{
			input = "TEST";
			lexerFromText(input);
			int testId = getVariableTransformer().toId("test");

			tokenFactory.acceptGlobalVariable(testId, 0, input.length(), 0, input.length(), lexer.getContext());
			expector.addExpectedTokens(tokenFactory.getTokens());
			lexer.start();
			expector.assertTokensMatch();
		}


		{
			input = "tEsT";
			lexerFromText(input);
			int testId = getVariableTransformer().toId("test");

			tokenFactory.acceptGlobalVariable(testId, 0, input.length(), 0, input.length(), lexer.getContext());
			expector.addExpectedTokens(tokenFactory.getTokens());
			lexer.start();
			expector.assertTokensMatch();
		}


		{
			input = "second_Test";
			lexerFromText(input);
			int secondTestId = getVariableTransformer().toId("second_Test");

			tokenFactory.acceptGlobalVariable(secondTestId, 0, input.length(), 0, input.length(), lexer.getContext());
			expector.addExpectedTokens(tokenFactory.getTokens());
			lexer.start();
			expector.assertTokensMatch();
		}


		{
			input = "second_test";
			lexerFromText(input);
			int secondTestId = getVariableTransformer().toId("second_Test");

			tokenFactory.acceptGlobalVariable(secondTestId, 0, input.length(), 0, input.length(), lexer.getContext());
			expector.addExpectedTokens(tokenFactory.getTokens());
			lexer.start();
			expector.assertTokensMatch();
		}


		{
			input = "Second_TesT";
			lexerFromText(input);
			int secondTestId = getVariableTransformer().toId("second_Test");

			tokenFactory.acceptGlobalVariable(secondTestId, 0, input.length(), 0, input.length(), lexer.getContext());
			expector.addExpectedTokens(tokenFactory.getTokens());
			lexer.start();
			expector.assertTokensMatch();
		}


		{
			input = "SECOND_TEST";
			lexerFromText(input);
			int secondTestId = getVariableTransformer().toId("second_Test");

			tokenFactory.acceptGlobalVariable(secondTestId, 0, input.length(), 0, input.length(), lexer.getContext());
			expector.addExpectedTokens(tokenFactory.getTokens());
			lexer.start();
			expector.assertTokensMatch();
		}


		{
			input = "sEcOnD_tEsT";
			lexerFromText(input);
			int secondTestId = getVariableTransformer().toId("second_Test");

			tokenFactory.acceptGlobalVariable(secondTestId, 0, input.length(), 0, input.length(), lexer.getContext());
			expector.addExpectedTokens(tokenFactory.getTokens());
			lexer.start();
			expector.assertTokensMatch();
		}
	}

	@Test
	public void globalVariables_withNumbers() throws UnknownIdException {
		String input = "test123";
		lexerFromText(input);

		tokenFactory.acceptGlobalVariable(getVariableTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "other456Test";
		lexerFromText(input);

		tokenFactory.acceptGlobalVariable(getVariableTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "o7the8rTe9st10";
		lexerFromText(input);

		tokenFactory.acceptGlobalVariable(getVariableTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
	}

	@Test
	public void globalVariables_withUnderscores() throws UnknownIdException {
		String input = "test_";
		lexerFromText(input);

		tokenFactory.acceptGlobalVariable(getVariableTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "other_Test";
		lexerFromText(input);

		tokenFactory.acceptGlobalVariable(getVariableTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "o_the_rTe_st__";
		lexerFromText(input);

		tokenFactory.acceptGlobalVariable(getVariableTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "o_the______rTe_st__";
		lexerFromText(input);

		tokenFactory.acceptGlobalVariable(getVariableTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
	}

	@Test
	public void globalVariables_withNumbersAndUnderscores() throws UnknownIdException {
		String input = "test_123";
		lexerFromText(input);

		tokenFactory.acceptGlobalVariable(getVariableTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "test_123_";
		lexerFromText(input);

		tokenFactory.acceptGlobalVariable(getVariableTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "other_456_Test";
		lexerFromText(input);

		tokenFactory.acceptGlobalVariable(getVariableTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "o_7___the_8rTe____9____st_10____";
		lexerFromText(input);

		tokenFactory.acceptGlobalVariable(getVariableTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
	}

	@Test
	public void localVariables() throws UnknownIdException {
		String input = "_test";
		lexerFromText(input);

		tokenFactory.acceptLocalVariable(getVariableTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "_otherTest";
		lexerFromText(input);

		tokenFactory.acceptLocalVariable(getVariableTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
	}

	@Test
	public void localVariables_caseInsensitive() throws UnknownIdException {
		String input;
		{
			input = "_test";
			lexerFromText(input);

			// precondition: Used id-transformer is case-insensitive
			assertEquals("The used variable transformer appears to be case-sensitive", getVariableTransformer().toId("_test"),
					getVariableTransformer().toId("_TeSt"));

			int testId = getVariableTransformer().toId("_test");

			tokenFactory.acceptLocalVariable(testId, 0, input.length(), 0, input.length(), lexer.getContext());
			expector.addExpectedTokens(tokenFactory.getTokens());
			lexer.start();
			expector.assertTokensMatch();
		}

		{
			input = "_Test";
			lexerFromText(input);
			int testId = getVariableTransformer().toId("_test");

			tokenFactory.acceptLocalVariable(testId, 0, input.length(), 0, input.length(), lexer.getContext());
			expector.addExpectedTokens(tokenFactory.getTokens());
			lexer.start();
			expector.assertTokensMatch();
		}


		{
			input = "_tesT";
			lexerFromText(input);
			int testId = getVariableTransformer().toId("_test");

			tokenFactory.acceptLocalVariable(testId, 0, input.length(), 0, input.length(), lexer.getContext());
			expector.addExpectedTokens(tokenFactory.getTokens());
			lexer.start();
			expector.assertTokensMatch();
		}


		{
			input = "_TEST";
			lexerFromText(input);
			int testId = getVariableTransformer().toId("_test");

			tokenFactory.acceptLocalVariable(testId, 0, input.length(), 0, input.length(), lexer.getContext());
			expector.addExpectedTokens(tokenFactory.getTokens());
			lexer.start();
			expector.assertTokensMatch();
		}


		{
			input = "_tEsT";
			lexerFromText(input);
			int testId = getVariableTransformer().toId("_test");

			tokenFactory.acceptLocalVariable(testId, 0, input.length(), 0, input.length(), lexer.getContext());
			expector.addExpectedTokens(tokenFactory.getTokens());
			lexer.start();
			expector.assertTokensMatch();
		}


		{
			input = "_second_Test";
			lexerFromText(input);
			int secondTestId = getVariableTransformer().toId("_second_Test");

			tokenFactory.acceptLocalVariable(secondTestId, 0, input.length(), 0, input.length(), lexer.getContext());
			expector.addExpectedTokens(tokenFactory.getTokens());
			lexer.start();
			expector.assertTokensMatch();
		}


		{
			input = "_second_test";
			lexerFromText(input);
			int secondTestId = getVariableTransformer().toId("_second_Test");

			tokenFactory.acceptLocalVariable(secondTestId, 0, input.length(), 0, input.length(), lexer.getContext());
			expector.addExpectedTokens(tokenFactory.getTokens());
			lexer.start();
			expector.assertTokensMatch();
		}


		{
			input = "_Second_TesT";
			lexerFromText(input);
			int secondTestId = getVariableTransformer().toId("_second_Test");

			tokenFactory.acceptLocalVariable(secondTestId, 0, input.length(), 0, input.length(), lexer.getContext());
			expector.addExpectedTokens(tokenFactory.getTokens());
			lexer.start();
			expector.assertTokensMatch();
		}


		{
			input = "_SECOND_TEST";
			lexerFromText(input);
			int secondTestId = getVariableTransformer().toId("_second_Test");

			tokenFactory.acceptLocalVariable(secondTestId, 0, input.length(), 0, input.length(), lexer.getContext());
			expector.addExpectedTokens(tokenFactory.getTokens());
			lexer.start();
			expector.assertTokensMatch();
		}


		{
			input = "_sEcOnD_tEsT";
			lexerFromText(input);
			int secondTestId = getVariableTransformer().toId("_second_Test");

			tokenFactory.acceptLocalVariable(secondTestId, 0, input.length(), 0, input.length(), lexer.getContext());
			expector.addExpectedTokens(tokenFactory.getTokens());
			lexer.start();
			expector.assertTokensMatch();
		}
	}

	@Test
	public void localVariables_withNumbers() throws UnknownIdException {
		String input = "_test123";
		lexerFromText(input);

		tokenFactory.acceptLocalVariable(getVariableTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "_other456Test";
		lexerFromText(input);

		tokenFactory.acceptLocalVariable(getVariableTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "_o7the8rTe9st10";
		lexerFromText(input);

		tokenFactory.acceptLocalVariable(getVariableTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
	}

	@Test
	public void localVariables_withUnderscores() throws UnknownIdException {
		String input = "_test_";
		lexerFromText(input);

		tokenFactory.acceptLocalVariable(getVariableTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "_other_Test";
		lexerFromText(input);

		tokenFactory.acceptLocalVariable(getVariableTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "_o_the_rTe_st__";
		lexerFromText(input);

		tokenFactory.acceptLocalVariable(getVariableTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "_o_the______rTe_st__";
		lexerFromText(input);

		tokenFactory.acceptLocalVariable(getVariableTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
	}

	@Test
	public void localVariables_withNumbersAndUnderscores() throws UnknownIdException {
		String input = "_test_123";
		lexerFromText(input);

		tokenFactory.acceptLocalVariable(getVariableTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "_test_123_";
		lexerFromText(input);

		tokenFactory.acceptLocalVariable(getVariableTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "_other_456_Test";
		lexerFromText(input);

		tokenFactory.acceptLocalVariable(getVariableTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "_o_7___the_8rTe____9____st_10____";
		lexerFromText(input);

		tokenFactory.acceptLocalVariable(getVariableTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
	}

	@Test
	public void comments_singleLine() {
		String input = "//";
		lexerFromText(input);

		tokenFactory.acceptComment(0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "// I am a comment";
		lexerFromText(input);

		tokenFactory.acceptComment(0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
	}

	@Test
	public void comments_singleLine_weirdCharacters() {
		String input = "//#`}^°<>~,;.:-_äöü";
		lexerFromText(input);

		tokenFactory.acceptComment(0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "// ĀǢŒÞ¢ǿ"; // unicode
		lexerFromText(input);

		tokenFactory.acceptComment(0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
	}

	@Test
	public void comments_singleLine_followedByNL() {
		String input = "// I am a comment\n";
		lexerFromText(input);

		tokenFactory.acceptComment(0, input.length() - 1, 0, input.length() - 1, lexer.getContext());
		tokenFactory.acceptWhitespace(input.length() - 1, 1, input.length() - 1, 1, lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
	}

	@Test
	public void comments_multiLine() {
		String input = "/**/";
		lexerFromText(input);

		tokenFactory.acceptComment(0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "/* I am a comment */";
		lexerFromText(input);

		tokenFactory.acceptComment(0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
	}

	@Test
	public void comments_multiLine_withNL() {
		String input = "/*\n*/";
		lexerFromText(input);

		tokenFactory.acceptComment(0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "/* I am \na \ncomment */";
		lexerFromText(input);

		tokenFactory.acceptComment(0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
	}

	@Test
	public void comments_multiLine_withNL_withCarriageReturn() {
		String input = "/*\r\n*/";
		lexerFromText(input);

		tokenFactory.acceptComment(0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "/* I am \r\na \r\ncomment */";
		lexerFromText(input);

		tokenFactory.acceptComment(0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
	}

	@Test
	public void comments_multiLine_withStars() {
		String input = "/***/";
		lexerFromText(input);

		tokenFactory.acceptComment(0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "/* I am *a *comment */";
		lexerFromText(input);

		tokenFactory.acceptComment(0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
	}

	@Test
	public void comments_multiLine_withStars_withNL() {
		String input = "/*\n*\n*/";
		lexerFromText(input);

		tokenFactory.acceptComment(0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "/* I am \n*a *com\nment */";
		lexerFromText(input);

		tokenFactory.acceptComment(0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
	}

	@Test
	public void comments_multiLine_nestedStartSequence() {
		String input = "/*/**/";
		lexerFromText(input);

		tokenFactory.acceptComment(0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "/* I am /*a \n/*comment */";
		lexerFromText(input);

		tokenFactory.acceptComment(0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
	}

	@Test
	public void comments_multiLine_almostEndSequence() {
		String input = "/** /*/";
		lexerFromText(input);

		tokenFactory.acceptComment(0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "/* I am *\n/a *\t/ * /comment */";
		lexerFromText(input);

		tokenFactory.acceptComment(0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
	}

	@Test
	public void commands_sqfkeywords() throws UnknownIdException {
		String input = "hint";
		lexerFromText(input);

		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "format";
		lexerFromText(input);

		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "call";
		lexerFromText(input);

		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "if";
		lexerFromText(input);

		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "createUnit";
		lexerFromText(input);

		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "addAction";
		lexerFromText(input);

		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
		
		
		input = "atan2";
		lexerFromText(input);

		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
	}

	@Test
	public void commands_sqfkeywords_caseInsensitive() throws UnknownIdException {
		// precondition: the used transformer is case-insensitive
		assertEquals("The used command-transformer appears to be case-sensitive", getCommandTransformer().toId("createUnit"),
				getCommandTransformer().toId("createunit"));

		int createUnitId = getCommandTransformer().toId("createUnit");
		int addActionId = getCommandTransformer().toId("addAction");
		int callId = getCommandTransformer().toId("call");

		String input = "createUnit";
		lexerFromText(input);

		tokenFactory.acceptCommand(createUnitId, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "createunit";
		lexerFromText(input);

		tokenFactory.acceptCommand(createUnitId, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "CreAteUnit";
		lexerFromText(input);

		tokenFactory.acceptCommand(createUnitId, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "CREATEUNIT";
		lexerFromText(input);

		tokenFactory.acceptCommand(createUnitId, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "CreateuniT";
		lexerFromText(input);

		tokenFactory.acceptCommand(createUnitId, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "addAction";
		lexerFromText(input);

		tokenFactory.acceptCommand(addActionId, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "addaction";
		lexerFromText(input);

		tokenFactory.acceptCommand(addActionId, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "AdDAction";
		lexerFromText(input);

		tokenFactory.acceptCommand(addActionId, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "ADDACTION";
		lexerFromText(input);

		tokenFactory.acceptCommand(addActionId, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "AddactioN";
		lexerFromText(input);

		tokenFactory.acceptCommand(addActionId, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "call";
		lexerFromText(input);

		tokenFactory.acceptCommand(callId, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "CALL";
		lexerFromText(input);

		tokenFactory.acceptCommand(callId, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "CaLl";
		lexerFromText(input);

		tokenFactory.acceptCommand(callId, 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
	}
	
	@Test
	public void commands_operators() throws UnknownIdException {
		String input = "+";
		lexerFromText(input);

		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "-";
		lexerFromText(input);

		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "*";
		lexerFromText(input);

		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "/";
		lexerFromText(input);

		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "^";
		lexerFromText(input);

		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
		
		
		input = "%";
		lexerFromText(input);

		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();


		input = "#";
		lexerFromText(input);

		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
		
		
		input = "(";
		lexerFromText(input);

		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
		
		
		input = ")";
		lexerFromText(input);

		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
		
		
		input = "[";
		lexerFromText(input);

		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
		
		
		input = "]";
		lexerFromText(input);

		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
		
		
		input = "{";
		lexerFromText(input);

		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
		
		
		input = "}";
		lexerFromText(input);

		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
		
		
		input = ",";
		lexerFromText(input);

		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
		
		
		input = ";";
		lexerFromText(input);

		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
		
		
		input = "!";
		lexerFromText(input);

		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
		
		
		input = "&&";
		lexerFromText(input);

		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
		
		
		input = "||";
		lexerFromText(input);

		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
		
		
		input = ":";
		lexerFromText(input);

		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
		
		
		input = "=";
		lexerFromText(input);

		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
		
		
		input = "==";
		lexerFromText(input);

		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
		
		
		input = "!=";
		lexerFromText(input);

		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
		
		
		input = "<";
		lexerFromText(input);

		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
		
		
		input = "<=";
		lexerFromText(input);

		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
		
		
		input = ">";
		lexerFromText(input);

		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
		
		
		input = ">=";
		lexerFromText(input);

		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
		
		
		input = ">>";
		lexerFromText(input);

		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start();
		expector.assertTokensMatch();
	}
}
