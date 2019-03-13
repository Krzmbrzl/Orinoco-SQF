package arma.orinocosqf.helpers;

import arma.orinocosqf.OrinocoLexerLiteralType;
import arma.orinocosqf.OrinocoTokenProcessor;
import arma.orinocosqf.OrinocoTokenProcessorWrapper;
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
public class TokenExpector extends OrinocoTokenProcessorWrapper {
	private final List<AcceptedToken> actualTokens;
	private final List<AcceptedToken> expectedTokens;

	public TokenExpector() {
		this(new ArrayList<>());
	}

	public TokenExpector(@NotNull List<AcceptedToken> expectedTokens) {
		super(new AcceptedTokenFactory());
		this.expectedTokens = expectedTokens;
		this.actualTokens = ((AcceptedTokenFactory) this.wrappedProcessor).getTokens();
	}

	public void addExpectedToken(@NotNull AcceptedToken t) {
		expectedTokens.add(t);
	}

	public void assertTokensMatch() {
		Iterator<AcceptedToken> actualIter = actualTokens.iterator();
		for (AcceptedToken expectedToken : expectedTokens) {
			if (!actualIter.hasNext()) {
				fail("Actual ran out of tokens");
			}
			AcceptedToken actualNext = actualIter.next();
			assertEquals(expectedToken.method, actualNext.method);
			Map<String, Object> actualParams = actualNext.parameters;
			for (Map.Entry<String, Object> entry : expectedToken.parameters.entrySet()) {
				Object o = actualParams.get(entry.getKey());
				if (o == null) {
					String msg = String.format(
							"Missing parameter %s in actual token: %s",
							entry.getKey(),
							actualNext.toString()
					);
					fail(msg);
				}
				String msg = String.format(
						"Expected %s for parameter %s, got %s",
						entry.getValue().toString(),
						entry.getKey(),
						o.toString()
				);
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
	}

	public void addExpectedTokens(@NotNull List<AcceptedToken> tokens) {
		expectedTokens.addAll(tokens);
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
			return this.method == other.method
					&& parameters.equals(other.parameters);
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
		public static AcceptedToken acceptCommand(int id, int preprocessedOffset, int originalOffset, int originalLength) {
			AcceptedToken t = new AcceptedToken(AcceptMethod.AcceptCommand);
			t.putParameter("id", id);
			t.putParameter("preprocessedOffset", preprocessedOffset);
			t.putParameter("originalOffset", originalOffset);
			t.putParameter("originalLength", originalLength);
			return t;
		}

		@NotNull
		public static AcceptedToken acceptLocalVariable(int id, int preprocessedOffset, int originalOffset, int originalLength) {
			AcceptedToken t = new AcceptedToken(AcceptMethod.AcceptLocalVariable);
			t.putParameter("id", id);
			t.putParameter("preprocessedOffset", preprocessedOffset);
			t.putParameter("originalOffset", originalOffset);
			t.putParameter("originalLength", originalLength);
			return t;
		}

		@NotNull
		public static AcceptedToken acceptGlobalVariable(int id, int preprocessedOffset, int originalOffset, int originalLength) {
			AcceptedToken t = new AcceptedToken(AcceptMethod.AcceptGlobalVariable);
			t.putParameter("id", id);
			t.putParameter("preprocessedOffset", preprocessedOffset);
			t.putParameter("originalOffset", originalOffset);
			t.putParameter("originalLength", originalLength);
			return t;
		}

		@NotNull
		public static AcceptedToken acceptLiteral(@NotNull OrinocoLexerLiteralType type, @NotNull String token, int preprocessedOffset,
												  int originalOffset, int originalLength) {
			AcceptedToken t = new AcceptedToken(AcceptMethod.AcceptLiteral);
			t.putParameter("type", type);
			t.putParameter("token", token);
			t.putParameter("preprocessedOffset", preprocessedOffset);
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
	}

	public static class AcceptedTokenFactory implements OrinocoTokenProcessor {
		private final List<AcceptedToken> q = new ArrayList<>();

		@Override
		public void begin() {

		}

		@Override
		public void acceptCommand(int id, int preprocessedOffset, int originalOffset, int originalLength) {
			q.add(AcceptedToken.acceptCommand(id, preprocessedOffset, originalOffset, originalLength));
		}

		@Override
		public void acceptLocalVariable(int id, int preprocessedOffset, int originalOffset, int originalLength) {
			q.add(AcceptedToken.acceptLocalVariable(id, preprocessedOffset, originalOffset, originalLength));
		}

		@Override
		public void acceptGlobalVariable(int id, int preprocessedOffset, int originalOffset, int originalLength) {
			q.add(AcceptedToken.acceptGlobalVariable(id, preprocessedOffset, originalOffset, originalLength));
		}

		@Override
		public void acceptLiteral(@NotNull OrinocoLexerLiteralType type, @NotNull String token, int preprocessedOffset,
								  int originalOffset, int originalLength) {
			q.add(AcceptedToken.acceptLiteral(type, token, preprocessedOffset, originalOffset, originalLength));
		}

		@Override
		public void preProcessorTokenSkipped(@NotNull String token, int offset) {
			q.add(AcceptedToken.preProcessorTokenSkipped(token, offset));
		}

		@Override
		public void preProcessorCommandSkipped(@NotNull String command, int offset) {
			q.add(AcceptedToken.preProcessorCommandSkipped(command, offset));
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
		AcceptCommand,
		AcceptLocalVariable,
		AcceptGlobalVariable,
		AcceptLiteral,
		PreProcessorTokenSkipped,
		PreProcessorCommandSkipped
	}
}
