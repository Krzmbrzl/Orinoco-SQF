package arma.orinocosqf.helpers;

import arma.orinocosqf.OrinocoLexer;
import arma.orinocosqf.OrinocoLexerContext;
import arma.orinocosqf.OrinocoLexerLiteralType;
import arma.orinocosqf.OrinocoLexerStream;
import arma.orinocosqf.preprocessing.MacroSet;
import arma.orinocosqf.preprocessing.PreProcessorCommand;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * A helper class for checking if a {@link arma.orinocosqf.OrinocoTokenProcessor} received a token via an accept method
 *
 * @author K
 * @since 3/10/19
 */
public class TokenExpector implements OrinocoLexerStream {
	private final AcceptedTokenFactory acceptFactory;
	private final List<AcceptedToken> expectedTokens;
	private boolean skipPreprocessing;
	private final MacroSet macroSet = new MacroSet();

	public TokenExpector() {
		this(new ArrayList<>(), false);
	}

	public TokenExpector(boolean skipPreprocessing) {
		this(new ArrayList<>(), skipPreprocessing);
	}

	public TokenExpector(@NotNull List<AcceptedToken> expectedTokens) {
		this(expectedTokens, false);
	}

	public TokenExpector(@NotNull List<AcceptedToken> expectedTokens, boolean skipPreprocessing) {
		this.acceptFactory = new AcceptedTokenFactory();
		this.expectedTokens = expectedTokens;
		this.skipPreprocessing = skipPreprocessing;
	}

	public void addExpectedToken(@NotNull AcceptedToken t) {
		expectedTokens.add(t);
	}

	public void assertTokensMatch() {
		Iterator<AcceptedToken> actualIter = acceptFactory.getTokens().iterator();
		for (AcceptedToken expectedToken : expectedTokens) {
			if (!actualIter.hasNext()) {
				fail("Actual ran out of tokens. Expected size: " + expectedTokens.size() + ", Actual size: " + acceptFactory.getTokens().size());
			}
			AcceptedToken actualNext = actualIter.next();
			assertEquals(expectedToken.method, actualNext.method);
			Map<String, Object> actualParams = actualNext.parameters;
			for (Map.Entry<String, Object> entry : expectedToken.parameters.entrySet()) {
				Object o = actualParams.get(entry.getKey());
				if (o == null) {
					String msg = String.format("Missing parameter %s in actual token: %s", entry.getKey(), actualNext.toString());
					fail(msg);
				}
				String msg = String.format("Expected %s for parameter %s, got %s", entry.getValue().toString(), entry.getKey(),
						o.toString());
				assertEquals(msg, entry.getValue(), o);
			}
		}
		if (actualIter.hasNext()) {
			StringBuilder left = new StringBuilder();
			while (actualIter.hasNext()) {
				AcceptedToken next = actualIter.next();
				left.append(next.toString());
				if (actualIter.hasNext()) {
					left.append('\n');
				}
			}
			fail("Too many tokens lexed. Leftovers:\n" + left.toString());
		}

		// clear stored tokens
		acceptFactory.getTokens().clear();
		expectedTokens.clear();
	}

	public void addExpectedTokens(@NotNull List<AcceptedToken> tokens) {
		expectedTokens.addAll(tokens);
	}

	@Override
	public void begin() {
		acceptFactory.begin();
	}

	@Override
	public void acceptCommand(int id, int preprocessedOffset, int preprocessedLength, int originalOffset, int originalLength,
							  @NotNull OrinocoLexerContext ctx) {
		acceptFactory.acceptCommand(id, preprocessedOffset, preprocessedLength, originalOffset, originalLength, ctx);
	}

	@Override
	public void acceptLocalVariable(int id, int preprocessedOffset, int preprocessedLength, int originalOffset, int originalLength,
									@NotNull OrinocoLexerContext ctx) {
		acceptFactory.acceptLocalVariable(id, preprocessedOffset, preprocessedLength, originalOffset, originalLength, ctx);
	}

	@Override
	public void acceptGlobalVariable(int id, int preprocessedOffset, int preprocessedLength, int originalOffset, int originalLength,
									 @NotNull OrinocoLexerContext ctx) {
		acceptFactory.acceptGlobalVariable(id, preprocessedOffset, preprocessedLength, originalOffset, originalLength, ctx);
	}

	@Override
	public void acceptLiteral(@NotNull OrinocoLexerLiteralType type, int preprocessedOffset, int preprocessedLength, int originalOffset,
							  int originalLength, @NotNull OrinocoLexerContext ctx) {
		acceptFactory.acceptLiteral(type, preprocessedOffset, preprocessedLength, originalOffset, originalLength, ctx);
	}

	@Override
	public void preProcessorTokenSkipped(int offset, int length, @NotNull OrinocoLexerContext ctx) {
		acceptFactory.preProcessorTokenSkipped(offset, length, ctx);
	}

	@Override
	public void preProcessorCommandSkipped(int offset, int length, @NotNull OrinocoLexerContext ctx) {
		acceptFactory.preProcessorCommandSkipped(offset, length, ctx);
	}

	@Override
	public void setLexer(@NotNull OrinocoLexer lexer) {
		acceptFactory.setLexer(lexer);
	}

	@Override
	public boolean skipPreProcessing() {
		return skipPreprocessing;
	}

	@Override
	public void acceptPreProcessorCommand(@NotNull PreProcessorCommand command, @NotNull char[] bufReadOnly, int offset, int bodyLength) {
		acceptFactory.acceptPreProcessorCommand(command, bufReadOnly, offset, bodyLength);
	}

	@Override
	public void preProcessToken(@NotNull char[] bufReadOnly, int offset, int length) {
		acceptFactory.preProcessToken(bufReadOnly, offset, length);
	}

	@Override
	public void acceptWhitespace(int originalOffset, int originalLength, int preprocessedOffset, int preprocessedLength,
								 @NotNull OrinocoLexerContext ctx) {
		acceptFactory.acceptWhitespace(originalOffset, originalLength, preprocessedOffset, preprocessedLength, ctx);
	}

	@Override
	public void acceptComment(int originalOffset, int originalLength, int preprocessedOffset, int preprocessedLength,
							  @NotNull OrinocoLexerContext ctx) {
		acceptFactory.acceptComment(originalOffset, originalLength, preprocessedOffset, preprocessedLength, ctx);
	}

	@Override
	public @NotNull MacroSet getMacroSet() {
		return macroSet;
	}

	@Override
	public void end() {
		acceptFactory.end();
	}

	public static class AcceptedToken {
		private final Map<String, Object> parameters = new HashMap<>();
		private final AcceptMethod method;

		public AcceptedToken(@NotNull AcceptMethod method) {
			this.method = method;
		}

		@NotNull
		public AcceptMethod getMethod() {
			return method;
		}

		@SuppressWarnings("unchecked")
		public <T> T getParameterValue(@NotNull String name) {
			return (T) parameters.get(name);
		}

		public void putParameter(@NotNull String name, @NotNull Object v) {
			parameters.put(name, v);
		}

		@Override
		public boolean equals(Object o) {
			if (o == this) {
				return true;
			}
			if (!(o instanceof AcceptedToken)) {
				return false;
			}
			AcceptedToken other = (AcceptedToken) o;
			return this.method == other.method && parameters.equals(other.parameters);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("AcceptedToken{");
			sb.append("method=");
			sb.append(method.name());
			sb.append(',');
			sb.append("parameters=");
			sb.append(parameters.toString());
			sb.append('}');
			return sb.toString();
		}

		@NotNull
		public static AcceptedToken acceptCommand(int id, int preprocessedOffset, int preprocessedLength,
												  int originalOffset, int originalLength) {
			AcceptedToken t = new AcceptedToken(AcceptMethod.AcceptCommand);
			t.putParameter("id", id);
			t.putParameter("preprocessedOffset", preprocessedOffset);
			t.putParameter("preprocessedLength", preprocessedLength);
			t.putParameter("originalOffset", originalOffset);
			t.putParameter("originalLength", originalLength);
			return t;
		}

		@NotNull
		public static AcceptedToken acceptLocalVariable(int id, int preprocessedOffset, int preprocessedLength,
														int originalOffset, int originalLength) {
			AcceptedToken t = new AcceptedToken(AcceptMethod.AcceptLocalVariable);
			t.putParameter("id", id);
			t.putParameter("preprocessedOffset", preprocessedOffset);
			t.putParameter("preprocessedLength", preprocessedLength);
			t.putParameter("originalOffset", originalOffset);
			t.putParameter("originalLength", originalLength);
			return t;
		}

		@NotNull
		public static AcceptedToken acceptGlobalVariable(int id, int preprocessedOffset, int preprocessedLength,
														 int originalOffset, int originalLength) {
			AcceptedToken t = new AcceptedToken(AcceptMethod.AcceptGlobalVariable);
			t.putParameter("id", id);
			t.putParameter("preprocessedOffset", preprocessedOffset);
			t.putParameter("preprocessedLength", preprocessedLength);
			t.putParameter("originalOffset", originalOffset);
			t.putParameter("originalLength", originalLength);
			return t;
		}

		@NotNull
		public static AcceptedToken acceptLiteral(@NotNull OrinocoLexerLiteralType type, @NotNull String token, int preprocessedOffset,
												  int preprocessedLength, int originalOffset, int originalLength) {
			AcceptedToken t = new AcceptedToken(AcceptMethod.AcceptLiteral);
			t.putParameter("type", type);
			t.putParameter("token", token);
			t.putParameter("preprocessedOffset", preprocessedOffset);
			t.putParameter("preprocessedLength", preprocessedLength);
			t.putParameter("originalOffset", originalOffset);
			t.putParameter("originalLength", originalLength);
			return t;
		}

		@NotNull
		public static AcceptedToken preProcessorTokenSkipped(@NotNull String token, int offset) {
			AcceptedToken t = new AcceptedToken(AcceptMethod.PreProcessorTokenSkipped);
			t.putParameter("token", token);
			t.putParameter("offset", offset);
			return t;
		}

		@NotNull
		public static AcceptedToken preProcessorCommandSkipped(@NotNull String command, int offset) {
			AcceptedToken t = new AcceptedToken(AcceptMethod.PreProcessorCommandSkipped);
			t.putParameter("command", command);
			t.putParameter("offset", offset);
			return t;
		}

		@NotNull
		public static AcceptedToken acceptPreProcessorCommand(@NotNull PreProcessorCommand command, @NotNull char[] bufReadOnly, int offset,
															  int bodyLength) {
			AcceptedToken t = new AcceptedToken(AcceptMethod.acceptPreprocessorCommand);
			t.putParameter("command", command);
			t.putParameter("bufReadOnly", bufReadOnly);
			t.putParameter("offset", offset);
			t.putParameter("bodyLength", bodyLength);
			return t;
		}

		@NotNull
		public static AcceptedToken acceptWhitespace(int originalOffset, int originalLength, int preprocessedOffset, int preprocessedLength,
													 @NotNull String originalToken) {
			AcceptedToken t = new AcceptedToken(AcceptMethod.acceptWhitespace);
			t.putParameter("originalOffset", originalOffset);
			t.putParameter("originalLength", originalLength);
			t.putParameter("preprocessedOffset", preprocessedOffset);
			t.putParameter("preprocessedLength", preprocessedLength);
			t.putParameter("originalToken", originalToken);
			return t;
		}

		@NotNull
		public static AcceptedToken acceptComment(int originalOffset, int originalLength, int preprocessedOffset, int preprocessedLength,
												  @NotNull String originalToken) {
			AcceptedToken t = new AcceptedToken(AcceptMethod.acceptComment);
			t.putParameter("originalOffset", originalOffset);
			t.putParameter("originalLength", originalLength);
			t.putParameter("preprocessedOffset", preprocessedOffset);
			t.putParameter("preprocessedLength", preprocessedLength);
			t.putParameter("originalToken", originalToken);
			return t;
		}
	}

	public static class AcceptedTokenFactory implements OrinocoLexerStream {
		private final List<AcceptedToken> q = new ArrayList<>();
		private final MacroSet macroSet = new MacroSet();

		@Override
		public void begin() {

		}

		@Override
		public void acceptCommand(int id, int preprocessedOffset, int preprocessedLength, int originalOffset, int originalLength,
								  @NotNull OrinocoLexerContext ctx) {
			q.add(AcceptedToken.acceptCommand(id, preprocessedOffset, preprocessedLength, originalOffset, originalLength));
		}

		@Override
		public void acceptLocalVariable(int id, int preprocessedOffset, int preprocessedLength, int originalOffset, int originalLength,
										@NotNull OrinocoLexerContext ctx) {
			q.add(AcceptedToken.acceptLocalVariable(id, preprocessedOffset, preprocessedLength, originalOffset, originalLength));
		}

		@Override
		public void acceptGlobalVariable(int id, int preprocessedOffset, int preprocessedLength, int originalOffset, int originalLength,
										 @NotNull OrinocoLexerContext ctx) {
			q.add(AcceptedToken.acceptGlobalVariable(id, preprocessedOffset, preprocessedLength, originalOffset, originalLength));
		}

		@Override
		public void acceptLiteral(@NotNull OrinocoLexerLiteralType type, int preprocessedOffset, int preprocessedLength, int originalOffset,
								  int originalLength, @NotNull OrinocoLexerContext ctx) {
			q.add(AcceptedToken.acceptLiteral(type, ctx.getTextBuffer().getText(originalOffset, originalLength),
					preprocessedOffset, preprocessedLength, originalOffset, originalLength));
		}

		@Override
		public void preProcessorTokenSkipped(int offset, int length, @NotNull OrinocoLexerContext ctx) {
			q.add(AcceptedToken.preProcessorTokenSkipped(ctx.getTextBuffer().getText(offset, length), offset));
		}

		@Override
		public void preProcessorCommandSkipped(int offset, int length, @NotNull OrinocoLexerContext ctx) {
			q.add(AcceptedToken.preProcessorCommandSkipped(ctx.getTextBuffer().getText(offset, length), offset));
		}

		@Override
		public void setLexer(@NotNull OrinocoLexer lexer) {

		}

		@Override
		public boolean skipPreProcessing() {
			return true;
		}

		@Override
		public void acceptPreProcessorCommand(@NotNull PreProcessorCommand command, @NotNull char[] bufReadOnly, int offset,
											  int bodyLength) {
			q.add(AcceptedToken.acceptPreProcessorCommand(command, bufReadOnly, offset, bodyLength));
		}

		@Override
		public void preProcessToken(@NotNull char[] bufReadOnly, int offset, int length) {

		}

		@Override
		public void acceptWhitespace(int originalOffset, int originalLength, int preprocessedOffset, int preprocessedLength,
									 @NotNull OrinocoLexerContext ctx) {
			q.add(AcceptedToken.acceptWhitespace(originalOffset, originalLength, preprocessedOffset, preprocessedLength,
					ctx.getTextBuffer().getText(originalOffset, originalLength)));
		}

		@Override
		public void acceptComment(int originalOffset, int originalLength, int preprocessedOffset, int preprocessedLength,
								  @NotNull OrinocoLexerContext ctx) {
			q.add(AcceptedToken.acceptComment(originalOffset, originalLength, preprocessedOffset, preprocessedLength,
					ctx.getTextBuffer().getText(originalOffset, originalLength)));
		}

		@Override
		public @NotNull MacroSet getMacroSet() {
			return macroSet;
		}

		@Override
		public void end() {

		}

		@NotNull
		public List<AcceptedToken> getTokens() {
			return q;
		}
	}

	public enum AcceptMethod {
		AcceptCommand, AcceptLocalVariable, AcceptGlobalVariable, AcceptLiteral, PreProcessorTokenSkipped, PreProcessorCommandSkipped,
		acceptPreprocessorCommand, acceptWhitespace, acceptComment
	}
}
