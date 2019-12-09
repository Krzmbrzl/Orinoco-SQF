package arma.orinocosqf.parsing.postfix;

import arma.orinocosqf.OrinocoToken;
import arma.orinocosqf.lexer.OrinocoLexerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author K
 * @since 12/3/19
 */
public class InfixPatternMatcher {
	private final InfixPattern rootPattern;
	private final Map<String, Match> captures = new HashMap<>();
	private boolean matches;
	private final Stack<InfixPattern.Node> nodes = new Stack<>();
	private final Stack<Integer> matchIndices = new Stack<>();
	private final Stack<ArrayList<OrinocoToken>> tokenListStack = new Stack<>();

	public InfixPatternMatcher(@NotNull InfixPattern pattern) {
		this.rootPattern = pattern;
		this.reset();
	}

	public boolean matchComplete() {
		System.out.println(matchIndices);
		return this.matchIndices.isEmpty();
	}

	public boolean matches() {
		return matches;
	}

	@Nullable
	public Match getMatch(@NotNull String captureName) {
		return captures.get(captureName);
	}

	public void acceptToken(@NotNull OrinocoToken token, @NotNull OrinocoLexerContext ctx) {
		if (!matches) {
			return;
		}
		int matchIndex = matchIndex();
		InfixPattern.Node parentNode = node();
		if (matchIndex >= parentNode.getChildren().size()) {
			this.matches = false;
			return;
		}

		InfixPattern.Node node = parentNode.getChildren().get(matchIndex);
		if (node.isPattern()) {
			matchIndices.push(0);
			nodes.push(node);
			tokenListStack.push(new ArrayList<>());
			acceptToken(token, ctx);
			return;
		}

		this.matches = node.matches(token, ctx);
		if (!this.matches) {
			return;
		}

		if (parentNode.isPattern() && parentNode != this.rootPattern.root) {
			ArrayList<OrinocoToken> tokenList = tokenList();
			tokenList.add(token);
		}
		if (node.getCaptureName() != null) {
			captures.put(node.getCaptureName(), new SingleMatch(token));
		}

		matchIndices.pop();
		if (matchIndex < parentNode.getChildren().size() - 1) {
			matchIndices.push(matchIndex + 1);
		} else if (parentNode.isPattern() && parentNode != this.rootPattern.root) {
			ArrayList<OrinocoToken> tokenList = tokenListStack.pop();
			tokenList.trimToSize();
			captures.put(parentNode.getCaptureName(), new GroupMatch(tokenList));
			nodes.pop();
			matchIndices.push(matchIndices.pop() + 1);
		}
	}


	public void end(@NotNull OrinocoLexerContext ctx) {

	}

	private int matchIndex() {
		return this.matchIndices.peek();
	}

	private ArrayList<OrinocoToken> tokenList() {
		return this.tokenListStack.peek();
	}

	private InfixPattern.Node node() {
		return this.nodes.peek();
	}

	public void reset() {
		this.matches = true;
		this.matchIndices.clear();
		this.matchIndices.push(0);
		this.nodes.clear();
		this.nodes.push(this.rootPattern.root);
	}

	public interface Match extends Iterable<OrinocoToken> {
		int length();

		@Nullable OrinocoToken first();

		@Nullable OrinocoToken token(int i);
	}

	private static class SingleMatch implements Match {
		private final OrinocoToken token;

		public SingleMatch(@NotNull OrinocoToken token) {
			this.token = token;
		}

		@Override
		public int length() {
			return 1;
		}

		@Override
		@Nullable
		public OrinocoToken first() {
			return token;
		}

		@Override
		public @Nullable OrinocoToken token(int i) {
			if (i == 0) {
				return token;
			}
			throw new IndexOutOfBoundsException(i);
		}

		@NotNull
		@Override
		public Iterator<OrinocoToken> iterator() {
			return new Iterator<OrinocoToken>() {
				boolean iterated = false;

				@Override
				public boolean hasNext() {
					return !iterated;
				}

				@Override
				public OrinocoToken next() {
					if (iterated) {
						throw new IllegalStateException();
					}
					iterated = true;
					return token;
				}
			};
		}
	}

	private static class GroupMatch implements Match {
		private final List<OrinocoToken> tokens;

		public GroupMatch(@NotNull List<OrinocoToken> tokens) {
			this.tokens = tokens;
		}

		@Override
		public int length() {
			return tokens.size();
		}

		@Override
		@Nullable
		public OrinocoToken first() {
			return tokens.size() > 0 ? tokens.get(0) : null;
		}

		@Override
		@Nullable
		public OrinocoToken token(int i) {
			return tokens.get(i);
		}

		@NotNull
		@Override
		public Iterator<OrinocoToken> iterator() {
			return tokens.iterator();
		}
	}
}
