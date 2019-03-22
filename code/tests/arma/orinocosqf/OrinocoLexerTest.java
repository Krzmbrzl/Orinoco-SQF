package arma.orinocosqf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;

import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;

import arma.orinocosqf.exceptions.UnknownIdException;
import arma.orinocosqf.helpers.TokenExpector;

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

	private IdTransformer<String> getVariableTransformer() {
		throw new UnsupportedOperationException("Variable ID-transformer not yet implemented");
	}

	private IdTransformer<String> getCommandTransformer() {
		throw new UnsupportedOperationException("Command ID-transformer not yet implemented");
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
}
