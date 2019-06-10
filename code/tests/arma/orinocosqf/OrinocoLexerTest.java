package arma.orinocosqf;

import arma.orinocosqf.exceptions.UnknownIdException;
import arma.orinocosqf.helpers.TokenExpector;
import arma.orinocosqf.lexer.OrinocoLexer;
import arma.orinocosqf.lexer.OrinocoLexerSQFLiteralType;
import arma.orinocosqf.lexer.SimpleOrinocoLexerContext;
import arma.orinocosqf.sqf.SQFCommands;
import arma.orinocosqf.util.SimpleTextBuffer;

import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OrinocoLexerTest {
	static TokenExpector expector;
	static TokenExpector.AcceptedTokenFactory tokenFactory;
	static OrinocoLexer lexer;

	@BeforeClass
	public static void setup() {
		expector = new TokenExpector(true);
		lexer = new OrinocoLexer(expector);
		tokenFactory = new TokenExpector.AcceptedTokenFactory();
	}
	
	@Before
	public void reset() {
		expector.reset();
		tokenFactory.reset();
		lexer.reset();
	}

	void test(@NotNull String text) {
		expector.addExpectedTokens(tokenFactory.getTokens());
		lexer.start(OrinocoReader.fromCharSequence(text), false);
		expector.assertTokensMatch();

		reset();
	}


	/*
	 * private void lexerFromText(@NotNull String text) { expector = new TokenExpector(true); lexer = new
	 * OrinocoLexer(OrinocoReader.fromCharSequence(text), expector); lexer.setContext(new SimpleOrinocoLexerContext(lexer, new
	 * SimpleTextBuffer(text))); tokenFactory = new TokenExpector.AcceptedTokenFactory(); }
	 */

	private IdTransformer<String> getVariableTransformer() {
		return lexer.getIdTransformer();
	}

	private IdTransformer<String> getCommandTransformer() {
		return SQFCommands.instance;
	}

	@Test
	public void emptyInput() {
		test("");
	}

	@Test
	public void literal_string_doubleQuotes() {
		String input = "\"\"";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.String, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "\"With input\"";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.String, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);
	}

	@Test
	public void literal_string_doubleQuotes_escapedQuotes() {
		String input = "\"\"\"\"";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.String, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "\"With \"\"input\"\"\"";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.String, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "\"With \"\"\"\"input\"\"\"\"\"";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.String, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);
	}

	@Test
	public void literal_string_doubleQuotes_containingSingeQuotes() {
		String input = "\"'\"";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.String, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "\"With 'input'\"";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.String, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "\"With ''input''\"";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.String, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);
	}

	@Test
	public void literal_string_singleQuotes() {
		String input = "''";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.String, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "'With input'";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.String, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);
	}

	@Test
	public void literal_string_singleQuotes_escapedQuotes() {
		String input = "''''";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.String, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "'With ''input'''";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.String, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "'With ''''input'''''";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.String, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);
	}

	@Test
	public void literal_string_singleQuotes_containingDoubleQuotes() {
		String input = "'\"'";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.String, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "'With \"input\"'";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.String, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "'With \"\"input\"\"'";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.String, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);
	}

	@Test
	public void literal_number_int() {
		String input = "5";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "33576";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);
	}

	@Test
	public void literal_number_float() {
		String input = "0.5";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "0.33576";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);
	}

	@Test
	public void literal_number_float_noLeadingZero() {
		String input = ".5";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = ".33576";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);
	}

	@Test
	public void literal_number_scientific_intFactor() {
		String input = "2e5";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "23e12";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "2E5";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "23E12";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);
	}

	@Test
	public void literal_number_scientific_floatFactor() {
		String input = "2.3e5";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "23.456e12";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "2.3E5";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "23.456E12";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);
	}

	@Test
	public void literal_number_scientific_intFactor_negativeExponent() {
		String input = "2e-5";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "23e-12";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "2E-5";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "23E-12";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);
	}

	@Test
	public void literal_number_scientific_floatFactor_negativeExponent() {
		String input = "2.3e-5";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "23.456e-12";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "2.3E-5";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "23.456E-12";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);
	}

	@Test
	public void literal_number_scientific_intFactor_implicitExponent() {
		String input = "2e";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "23e";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "2E";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "23E";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);
	}

	@Test
	public void literal_number_scientific_floatFactor_implicitExponent() {
		String input = "2.3e";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "23.456e";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "2.3E";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "23.456E";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);
	}

	@Test
	public void literal_number_hex_0x() {
		String input = "0x1";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "0xFF";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "0xABCDEF1234567890";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);
	}

	@Test
	public void literal_number_hex_dollar() {
		String input = "$1";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "$FF";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "$ABCDEF1234567890";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLiteral(OrinocoLexerSQFLiteralType.Number, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);
	}

	@Test
	public void globalVariables() throws UnknownIdException {
		String input = "test";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptGlobalVariable(getVariableTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "otherTest";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptGlobalVariable(getVariableTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);
	}

	@Test
	public void globalVariables_caseInsensitive() throws UnknownIdException {
		String input;
		{
			input = "test";
			lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
			int testId = getVariableTransformer().toId("test");

			// precondition: Used id-transformer is case-insensitive
			assertEquals("The used variable transformer appears to be case-sensitive", getVariableTransformer().toId("test"),
					getVariableTransformer().toId("TeSt"));

			tokenFactory.acceptGlobalVariable(testId, 0, input.length(), 0, input.length(), lexer.getContext());
			test(input);
		}

		{
			input = "Test";
			lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
			int testId = getVariableTransformer().toId("test");

			tokenFactory.acceptGlobalVariable(testId, 0, input.length(), 0, input.length(), lexer.getContext());
			test(input);
		}


		{
			input = "tesT";
			lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
			int testId = getVariableTransformer().toId("test");

			tokenFactory.acceptGlobalVariable(testId, 0, input.length(), 0, input.length(), lexer.getContext());
			test(input);
		}


		{
			input = "TEST";
			lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
			int testId = getVariableTransformer().toId("test");

			tokenFactory.acceptGlobalVariable(testId, 0, input.length(), 0, input.length(), lexer.getContext());
			test(input);
		}


		{
			input = "tEsT";
			lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
			int testId = getVariableTransformer().toId("test");

			tokenFactory.acceptGlobalVariable(testId, 0, input.length(), 0, input.length(), lexer.getContext());
			test(input);
		}


		{
			input = "second_Test";
			lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
			int secondTestId = getVariableTransformer().toId("second_Test");

			tokenFactory.acceptGlobalVariable(secondTestId, 0, input.length(), 0, input.length(), lexer.getContext());
			test(input);
		}


		{
			input = "second_test";
			lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
			int secondTestId = getVariableTransformer().toId("second_Test");

			tokenFactory.acceptGlobalVariable(secondTestId, 0, input.length(), 0, input.length(), lexer.getContext());
			test(input);
		}


		{
			input = "Second_TesT";
			lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
			int secondTestId = getVariableTransformer().toId("second_Test");

			tokenFactory.acceptGlobalVariable(secondTestId, 0, input.length(), 0, input.length(), lexer.getContext());
			test(input);
		}


		{
			input = "SECOND_TEST";
			lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
			int secondTestId = getVariableTransformer().toId("second_Test");

			tokenFactory.acceptGlobalVariable(secondTestId, 0, input.length(), 0, input.length(), lexer.getContext());
			test(input);
		}


		{
			input = "sEcOnD_tEsT";
			lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
			int secondTestId = getVariableTransformer().toId("second_Test");

			tokenFactory.acceptGlobalVariable(secondTestId, 0, input.length(), 0, input.length(), lexer.getContext());
			test(input);
		}
	}

	@Test
	public void globalVariables_withNumbers() throws UnknownIdException {
		String input = "test123";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptGlobalVariable(getVariableTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "other456Test";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptGlobalVariable(getVariableTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "o7the8rTe9st10";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptGlobalVariable(getVariableTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);
	}

	@Test
	public void globalVariables_withUnderscores() throws UnknownIdException {
		String input = "test_";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptGlobalVariable(getVariableTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "other_Test";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptGlobalVariable(getVariableTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "o_the_rTe_st__";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptGlobalVariable(getVariableTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "o_the______rTe_st__";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptGlobalVariable(getVariableTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);
	}

	@Test
	public void globalVariables_withNumbersAndUnderscores() throws UnknownIdException {
		String input = "test_123";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptGlobalVariable(getVariableTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "test_123_";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptGlobalVariable(getVariableTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "other_456_Test";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptGlobalVariable(getVariableTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "o_7___the_8rTe____9____st_10____";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptGlobalVariable(getVariableTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);
	}

	@Test
	public void localVariables() throws UnknownIdException {
		String input = "_test";

		// TODO: The var-IDs are messed up
		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLocalVariable(getVariableTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "_otherTest";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLocalVariable(getVariableTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);
	}

	@Test
	public void localVariables_caseInsensitive() throws UnknownIdException {
		String input;
		{
			input = "_test";

			lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
			// precondition: Used id-transformer is case-insensitive
			assertEquals("The used variable transformer appears to be case-sensitive", getVariableTransformer().toId("_test"),
					getVariableTransformer().toId("_TeSt"));

			int testId = getVariableTransformer().toId("_test");

			tokenFactory.acceptLocalVariable(testId, 0, input.length(), 0, input.length(), lexer.getContext());
			test(input);
		}

		{
			input = "_Test";
			lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
			int testId = getVariableTransformer().toId("_test");

			tokenFactory.acceptLocalVariable(testId, 0, input.length(), 0, input.length(), lexer.getContext());
			test(input);
		}


		{
			input = "_tesT";
			lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
			int testId = getVariableTransformer().toId("_test");

			tokenFactory.acceptLocalVariable(testId, 0, input.length(), 0, input.length(), lexer.getContext());
			test(input);
		}


		{
			input = "_TEST";
			lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
			int testId = getVariableTransformer().toId("_test");

			tokenFactory.acceptLocalVariable(testId, 0, input.length(), 0, input.length(), lexer.getContext());
			test(input);
		}


		{
			input = "_tEsT";
			lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
			int testId = getVariableTransformer().toId("_test");

			tokenFactory.acceptLocalVariable(testId, 0, input.length(), 0, input.length(), lexer.getContext());
			test(input);
		}


		{
			input = "_second_Test";
			lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
			int secondTestId = getVariableTransformer().toId("_second_Test");

			tokenFactory.acceptLocalVariable(secondTestId, 0, input.length(), 0, input.length(), lexer.getContext());
			test(input);
		}


		{
			input = "_second_test";
			lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
			int secondTestId = getVariableTransformer().toId("_second_Test");

			tokenFactory.acceptLocalVariable(secondTestId, 0, input.length(), 0, input.length(), lexer.getContext());
			test(input);
		}


		{
			input = "_Second_TesT";
			lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
			int secondTestId = getVariableTransformer().toId("_second_Test");

			tokenFactory.acceptLocalVariable(secondTestId, 0, input.length(), 0, input.length(), lexer.getContext());
			test(input);
		}


		{
			input = "_SECOND_TEST";
			lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
			int secondTestId = getVariableTransformer().toId("_second_Test");

			tokenFactory.acceptLocalVariable(secondTestId, 0, input.length(), 0, input.length(), lexer.getContext());
			test(input);
		}


		{
			input = "_sEcOnD_tEsT";
			lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
			int secondTestId = getVariableTransformer().toId("_second_Test");

			tokenFactory.acceptLocalVariable(secondTestId, 0, input.length(), 0, input.length(), lexer.getContext());
			test(input);
		}
	}

	@Test
	public void localVariables_withNumbers() throws UnknownIdException {
		String input = "_test123";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLocalVariable(getVariableTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "_other456Test";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLocalVariable(getVariableTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "_o7the8rTe9st10";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLocalVariable(getVariableTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);
	}

	@Test
	public void localVariables_withUnderscores() throws UnknownIdException {
		String input = "_test_";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLocalVariable(getVariableTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "_other_Test";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLocalVariable(getVariableTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "_o_the_rTe_st__";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLocalVariable(getVariableTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "_o_the______rTe_st__";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLocalVariable(getVariableTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);
	}

	@Test
	public void localVariables_withNumbersAndUnderscores() throws UnknownIdException {
		String input = "_test_123";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLocalVariable(getVariableTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "_test_123_";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLocalVariable(getVariableTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "_other_456_Test";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLocalVariable(getVariableTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "_o_7___the_8rTe____9____st_10____";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptLocalVariable(getVariableTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);
	}

	@Test
	public void comments_singleLine() {
		String input = "//";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptComment(0, input.length(), 0, input.length(), lexer.getContext(), 0);
		test(input);


		input = "// I am a comment";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptComment(0, input.length(), 0, input.length(), lexer.getContext(), 0);
		test(input);
	}

	@Test
	public void comments_singleLine_weirdCharacters() {
		String input = "//#`}^°<>~,;.:-_äöü";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptComment(0, input.length(), 0, input.length(), lexer.getContext(), 0);
		test(input);


		input = "// ĀǢŒÞ¢ǿ"; // unicode

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptComment(0, input.length(), 0, input.length(), lexer.getContext(), 0);
		test(input);
	}

	@Test
	public void comments_singleLine_followedByNL() {
		String input = "// I am a comment\n";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptComment(0, input.length() - 1, 0, input.length() - 1, lexer.getContext(), 0);
		tokenFactory.acceptWhitespace(input.length() - 1, 1, input.length() - 1, 1, lexer.getContext());
		test(input);
	}

	@Test
	public void comments_multiLine() {
		String input = "/**/";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptComment(0, input.length(), 0, input.length(), lexer.getContext(), 0);
		test(input);


		input = "/* I am a comment */";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptComment(0, input.length(), 0, input.length(), lexer.getContext(), 0);
		test(input);
	}

	@Test
	public void comments_multiLine_withNL() {
		String input = "/*\n*/";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptComment(0, input.length(), 0, input.length(), lexer.getContext(), 1);
		test(input);


		input = "/* I am \na \ncomment */";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptComment(0, input.length(), 0, input.length(), lexer.getContext(), 2);
		test(input);
	}

	@Test
	public void comments_multiLine_withNL_withCarriageReturn() {
		String input = "/*\r\n*/";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptComment(0, input.length(), 0, input.length(), lexer.getContext(), 1);
		test(input);


		input = "/* I am \r\na \r\ncomment */";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptComment(0, input.length(), 0, input.length(), lexer.getContext(), 2);
		test(input);
	}

	@Test
	public void comments_multiLine_withStars() {
		String input = "/***/";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptComment(0, input.length(), 0, input.length(), lexer.getContext(), 0);
		test(input);


		input = "/* I am *a *comment */";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptComment(0, input.length(), 0, input.length(), lexer.getContext(), 0);
		test(input);
	}

	@Test
	public void comments_multiLine_withStars_withNL() {
		String input = "/*\n*\n*/";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptComment(0, input.length(), 0, input.length(), lexer.getContext(), 2);
		test(input);


		input = "/* I am \n*a *com\nment */";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptComment(0, input.length(), 0, input.length(), lexer.getContext(), 2);
		test(input);
	}

	@Test
	public void comments_multiLine_nestedStartSequence() {
		String input = "/*/**/";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptComment(0, input.length(), 0, input.length(), lexer.getContext(), 0);
		test(input);


		input = "/* I am /*a \n/*comment */";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptComment(0, input.length(), 0, input.length(), lexer.getContext(), 1);
		test(input);
	}

	@Test
	public void comments_multiLine_almostEndSequence() {
		String input = "/** /*/";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptComment(0, input.length(), 0, input.length(), lexer.getContext(), 0);
		test(input);


		input = "/* I am *\n/a *\t/ * /comment */";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptComment(0, input.length(), 0, input.length(), lexer.getContext(), 1);
		test(input);
	}

	@Test
	public void commands_sqfkeywords() throws UnknownIdException {
		String input = "hint";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "format";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "call";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "if";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "createUnit";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "addAction";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "atan2";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);
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

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptCommand(createUnitId, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "createunit";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptCommand(createUnitId, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "CreAteUnit";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptCommand(createUnitId, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "CREATEUNIT";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptCommand(createUnitId, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "CreateuniT";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptCommand(createUnitId, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "addAction";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptCommand(addActionId, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "addaction";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptCommand(addActionId, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "AdDAction";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptCommand(addActionId, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "ADDACTION";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptCommand(addActionId, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "AddactioN";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptCommand(addActionId, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "call";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptCommand(callId, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "CALL";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptCommand(callId, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "CaLl";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptCommand(callId, 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);
	}

	@Test
	public void commands_operators() throws UnknownIdException {
		String input = "+";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "-";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "*";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "/";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "^";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "%";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "#";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "(";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = ")";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "[";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "]";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "{";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "}";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = ",";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = ";";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "!";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "&&";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "||";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = ":";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "=";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "==";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "!=";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "<";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = "<=";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = ">";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = ">=";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);


		input = ">>";

		lexer.setContext(new SimpleOrinocoLexerContext(lexer, new SimpleTextBuffer(input)));
		tokenFactory.acceptCommand(getCommandTransformer().toId(input), 0, input.length(), 0, input.length(), lexer.getContext());
		test(input);
	}
}
