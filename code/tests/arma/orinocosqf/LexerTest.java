package arma.orinocosqf;

import static org.junit.Assert.*;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;

public class LexerTest {

	/**
	 * An implementation that will simply check whether or not the respective methods get called in the expected order with the expected
	 * arguments
	 * 
	 * @author Raven
	 *
	 */
	static class TestStream implements OrinocoLexerStream {

		boolean hasBegun;
		boolean hasEnded;
		boolean isLexerSet;

		ArrayDeque<OrinocoToken> expectedTokens;

		public TestStream() {
			expectedTokens = new ArrayDeque<>();
		}

		/**
		 * Resets all parameters and internal states
		 */
		public void reset() {
			hasBegun = false;
			hasEnded = false;
			isLexerSet = false;
			expectedTokens.clear();
		}

		public void setExpectedTokens(Collection<OrinocoToken> tokens) {
			expectedTokens.addAll(tokens);
		}

		public void addExpectedToken(OrinocoToken token) {
			expectedTokens.add(token);
		}

		public void testEnded() {
			assertTrue("Test ended before begin()-method has been called", hasBegun);
			assertTrue("Test ended before end()-method has been called", hasEnded);
			assertTrue("Test ended before lexer has been set", isLexerSet);
			assertTrue("Not all expected tokens have been encountered", expectedTokens.isEmpty());
		}

		@Override
		public void begin() {
			assertFalse("Method called after end()-method has been called", hasEnded);
			assertTrue("Method called before lexer has been set", isLexerSet);

			hasBegun = true;
		}

		@Override
		public void acceptCommand(int id, int preprocessedOffset, int originalOffset, int originalLength) {
			assertTrue("Method called before begin()-method has been called", hasBegun);
			assertFalse("Method called after end()-method has been called", hasEnded);
			assertTrue("Method called before lexer has been set", isLexerSet);
			assertFalse("No further tokens expected", expectedTokens.isEmpty());

			OrinocoToken expectedToken = expectedTokens.removeFirst();

			assertEquals("Wrong token type!", OrinocoSQFTokenType.Command, expectedToken.getTokenType());
			assertEquals("Wrong command-Id", expectedToken.getId(), id);
			assertEquals("Wrong preprocessed offset", expectedToken.getPreprocessedOffset(), preprocessedOffset);
			assertEquals("Wrong original offset", expectedToken.getOriginalOffset(), originalOffset);
			assertEquals("Wrong original length", expectedToken.getOriginalLength(), originalLength);
		}

		@Override
		public void acceptLocalVariable(int id, int preprocessedOffset, int originalOffset, int originalLength) {
			assertTrue("Method called before begin()-method has been called", hasBegun);
			assertFalse("Method called after end()-method has been called", hasEnded);
			assertTrue("Method called before lexer has been set", isLexerSet);
			assertFalse("No further tokens expected", expectedTokens.isEmpty());

			OrinocoToken expectedToken = expectedTokens.removeFirst();

			assertEquals("Wrong token type!", OrinocoSQFTokenType.LocalVariable, expectedToken.getTokenType());
			assertEquals("Wrong command-Id", expectedToken.getId(), id);
			assertEquals("Wrong preprocessed offset", expectedToken.getPreprocessedOffset(), preprocessedOffset);
			assertEquals("Wrong original offset", expectedToken.getOriginalOffset(), originalOffset);
			assertEquals("Wrong original length", expectedToken.getOriginalLength(), originalLength);

		}

		@Override
		public void acceptGlobalVariable(int id, int preprocessedOffset, int originalOffset, int originalLength) {
			assertTrue("Method called before begin()-method has been called", hasBegun);
			assertFalse("Method called after end()-method has been called", hasEnded);
			assertTrue("Method called before lexer has been set", isLexerSet);
			assertFalse("No further tokens expected", expectedTokens.isEmpty());

			OrinocoToken expectedToken = expectedTokens.removeFirst();

			assertEquals("Wrong token type!", OrinocoSQFTokenType.GlobalVariable, expectedToken.getTokenType());
			assertEquals("Wrong command-Id", expectedToken.getId(), id);
			assertEquals("Wrong preprocessed offset", expectedToken.getPreprocessedOffset(), preprocessedOffset);
			assertEquals("Wrong original offset", expectedToken.getOriginalOffset(), originalOffset);
			assertEquals("Wrong original length", expectedToken.getOriginalLength(), originalLength);

		}

		@Override
		public void acceptLiteral(@NotNull OrinocoLexerLiteralType type, @NotNull String token, int preprocessedOffset, int originalOffset,
				int originalLength) {
			assertTrue("Method called before begin()-method has been called", hasBegun);
			assertFalse("Method called after end()-method has been called", hasEnded);
			assertTrue("Method called before lexer has been set", isLexerSet);
			assertFalse("No further tokens expected", expectedTokens.isEmpty());

			OrinocoToken expectedToken = expectedTokens.removeFirst();

			assertEquals("Wrong token type!", OrinocoSQFTokenType.Literal, expectedToken.getTokenType());
			assertEquals("Wrong text", expectedToken.getText(), token);
			assertEquals("Wrong preprocessed offset", expectedToken.getPreprocessedOffset(), preprocessedOffset);
			assertEquals("Wrong original offset", expectedToken.getOriginalOffset(), originalOffset);
			assertEquals("Wrong original length", expectedToken.getOriginalLength(), originalLength);

		}

		@Override
		public void preProcessorTokenSkipped(@NotNull String token, int offset) {
			assertTrue("Method called before begin()-method has been called", hasBegun);
			assertFalse("Method called after end()-method has been called", hasEnded);
			assertTrue("Method called before lexer has been set", isLexerSet);
			// TODO Auto-generated method stub

		}

		@Override
		public void preProcessorCommandSkipped(@NotNull String command, int offset) {
			assertTrue("Method called before begin()-method has been called", hasBegun);
			assertFalse("Method called after end()-method has been called", hasEnded);
			assertTrue("Method called before lexer has been set", isLexerSet);
			// TODO Auto-generated method stub

		}

		@Override
		public void end() {
			assertTrue("Method called before begin()-method has been called", hasBegun);
			assertFalse("Method called after end()-method has been called", hasEnded);
			assertTrue("Method called before lexer has been set", isLexerSet);

			hasEnded = true;

			assertTrue("Not all expected tokens have been matched", expectedTokens.isEmpty());
		}

		@Override
		public void setLexer(@NotNull OrinocoLexer lexer) {
			assertFalse("Method called after end()-method has been called", hasEnded);
			assertFalse("Trying to set lexer after it has been set before", isLexerSet);

			isLexerSet = true;
		}

		@Override
		public boolean skipPreProcessing() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void acceptPreProcessorCommand(@NotNull PreProcessorCommand command, @NotNull char[] bufReadOnly, int offset,
				int bodyLength) {
			assertTrue("Method called before begin()-method has been called", hasBegun);
			assertFalse("Method called after end()-method has been called", hasEnded);
			assertTrue("Method called before lexer has been set", isLexerSet);
			// TODO Auto-generated method stub

		}

		@Override
		public void preProcessToken(@NotNull char[] bufReadOnly, int offset, int length) {
			assertTrue("Method called before begin()-method has been called", hasBegun);
			assertFalse("Method called after end()-method has been called", hasEnded);
			assertTrue("Method called before lexer has been set", isLexerSet);
			// TODO Auto-generated method stub

		}

		@Override
		public void acceptWhitespace(int originalOffset, int originalLength, int preprocessedOffset, int preprocessedLength) {
			assertTrue("Method called before begin()-method has been called", hasBegun);
			assertFalse("Method called after end()-method has been called", hasEnded);
			assertTrue("Method called before lexer has been set", isLexerSet);
			assertFalse("No further tokens expected", expectedTokens.isEmpty());

			OrinocoToken expectedToken = expectedTokens.removeFirst();

			assertEquals("Wrong token type!", OrinocoSQFTokenType.Whitespace, expectedToken.getTokenType());
			assertEquals("Wrong preprocessed offset", expectedToken.getPreprocessedOffset(), preprocessedOffset);
			assertEquals("Wrong original offset", expectedToken.getOriginalOffset(), originalOffset);
			assertEquals("Wrong original length", expectedToken.getOriginalLength(), originalLength);
		}

		@Override
		public void acceptComment(int originalOffset, int originalLength, int preprocessedOffset, int preprocessedLength) {
			assertTrue("Method called before begin()-method has been called", hasBegun);
			assertFalse("Method called after end()-method has been called", hasEnded);
			assertTrue("Method called before lexer has been set", isLexerSet);
			assertFalse("No further tokens expected", expectedTokens.isEmpty());

			OrinocoToken expectedToken = expectedTokens.removeFirst();

			assertEquals("Wrong token type!", OrinocoSQFTokenType.Comment, expectedToken.getTokenType());
			assertEquals("Wrong preprocessed offset", expectedToken.getPreprocessedOffset(), preprocessedOffset);
			assertEquals("Wrong original offset", expectedToken.getOriginalOffset(), originalOffset);
			assertEquals("Wrong original length", expectedToken.getOriginalLength(), originalLength);
		}

	}

	TestStream testStream;

	@Before
	public void setUp() throws Exception {
		testStream = new TestStream();
	}

	@Test
	public void test() {
		createLexer("test", "|<3,0>test|").start();
		testStream.testEnded();
	}


	OrinocoLexer createLexer(CharSequence input, String expectedTokenization) {
		testStream.reset();

		setExpectedTokensWithoutPreprocessing(expectedTokenization);

		return new OrinocoLexer(OrinocoReader.fromCharSequence(input), testStream) {

			@Override
			public void start() {
				// TODO Use actual implementation that will implement this method
			}

			@Override
			void acceptPreProcessedText(@NotNull String text) {
				// TODO Auto-generated method stub

			}
		};
	}

	void setExpectedTokensWithoutPreprocessing(String expectedTokenization) {
		// each token is assumed to be encapsulated by '|' with an extra starting entry
		// inside '<>' stating the token type and the ID of the token
		// Example String: "|<1,13>_localVariable|<2,-1> |<3,22>=|<4,-1>'Some content'|"

		Pattern tokenPattern = Pattern.compile("<\\d+,-?\\d+>.*", Pattern.DOTALL);

		int offset = 0;

		for (String currentTokenElement : expectedTokenization.split("\\|")) {
			if (currentTokenElement.isEmpty()) {
				continue;
			}

			if (!tokenPattern.matcher(currentTokenElement).matches()) {
				throw new IllegalArgumentException("Invalid token spec");
			}

			String text = currentTokenElement.substring(currentTokenElement.indexOf(">") + 1);
			int length = text.length();

			int intType = Integer.parseInt(currentTokenElement.substring(1, currentTokenElement.indexOf(",")).trim());
			int id = Integer
					.parseInt(currentTokenElement.substring(currentTokenElement.indexOf(",") + 1, currentTokenElement.indexOf(">")).trim());

			OrinocoTokenType type = null;

			switch (intType) {
				case 1:
					type = OrinocoSQFTokenType.Command;
					break;
				case 2:
					type = OrinocoSQFTokenType.LocalVariable;
					break;
				case 3:
					type = OrinocoSQFTokenType.GlobalVariable;
					break;
				case 4:
					type = OrinocoSQFTokenType.Literal;
					break;
				case 5:
					type = OrinocoSQFTokenType.UnPreProcessed;
					break;
				case 6:
					type = OrinocoSQFTokenType.Whitespace;
					break;
				case 7:
					type = OrinocoSQFTokenType.Command;
					break;
				default:
					throw new IllegalArgumentException("Unknown token type " + intType);
			}

			testStream.addExpectedToken(new OrinocoToken(id, type, offset, offset, length));


			offset += length;
		}
	}
}
