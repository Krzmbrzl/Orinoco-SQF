package arma.orinocosqf.parsing.postfix;

import arma.orinocosqf.Command;
import arma.orinocosqf.OrinocoToken;
import arma.orinocosqf.OrinocoTokenInstanceProcessor;
import arma.orinocosqf.lexer.OrinocoLexerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author K
 * @since 12/3/19
 */
public class InfixPatternMatcher implements OrinocoTokenInstanceProcessor {
	private final InfixPattern pattern;
	private int matchIndex;
	private final Map<String, Match> captures = new HashMap<>();
	private boolean matches;
	private boolean matchComplete;

	public InfixPatternMatcher(@NotNull InfixPattern pattern) {
		this.pattern = pattern;
		this.reset();
	}

	public boolean matchComplete() {
		return matchComplete;
	}

	public boolean matches() {
		return matches;
	}

	@Override
	public void begin(@NotNull OrinocoLexerContext ctx) {
		this.matchIndex = 0;
	}

	@Override
	public void acceptCommand(@NotNull OrinocoToken token, @NotNull OrinocoLexerContext ctx) {
		// todo first figure out non-nested pattern matching, then figure out nested pattern matching
		if (!matches) {
			return;
		}
		if (this.matchIndex > pattern.root.getChildren().size()) {
			this.matches = false;
			return;
		}
		InfixPattern.Node node = pattern.root.getChildren().get(this.matchIndex);
		switch (node.nodeType) {
			case Command: {
				InfixPattern.CommandNode cmdNode = (InfixPattern.CommandNode) node;
				Command cmdToken = ctx.getCommandInstance(token.getId());
				if (!cmdToken.commandNameEquals(cmdNode.command)) {
					this.matches = false;
					return;
				}
			}
			case Literal: {
				break; // todo matches=false
			}
			case Operand: {
				break;
			}
			case Pattern: {
				break;
			}
		}
		this.matchIndex++;
		if (node.getCaptureName() != null) {
			this.captures.put(node.getCaptureName(), new SingleMatch(token));
		}
	}

	@Override
	public void acceptLocalVariable(@NotNull OrinocoToken token, @NotNull OrinocoLexerContext ctx) {
		if (!matches) {
			return;
		}
	}

	@Override
	public void acceptGlobalVariable(@NotNull OrinocoToken token, @NotNull OrinocoLexerContext ctx) {
		if (!matches) {
			return;
		}
	}

	@Override
	public void acceptLiteral(@NotNull OrinocoToken token, @NotNull OrinocoLexerContext ctx) {
		if (!matches) {
			return;
		}
	}

	@Override
	public void preProcessorTokenSkipped(int offset, int length, @NotNull OrinocoLexerContext ctx) {
	}

	@Override
	public void preProcessorCommandSkipped(int offset, int length, @NotNull OrinocoLexerContext ctx) {
	}

	@Override
	public void end(@NotNull OrinocoLexerContext ctx) {

	}

	@Override
	public void reset() {
		this.matches = true;
		this.matchComplete = false;
		this.matchIndex = 0;
	}

	public interface Match extends Iterable<OrinocoToken> {
		int length();

		@Nullable OrinocoToken first();
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

		@NotNull
		@Override
		public Iterator<OrinocoToken> iterator() {
			return tokens.iterator();
		}
	}
}
