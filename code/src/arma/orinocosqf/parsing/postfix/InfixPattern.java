package arma.orinocosqf.parsing.postfix;

import arma.orinocosqf.Command;
import arma.orinocosqf.OrinocoLiteralType;
import arma.orinocosqf.OrinocoSQFTokenType;
import arma.orinocosqf.OrinocoToken;
import arma.orinocosqf.lexer.OrinocoLexerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author K
 * @since 10/5/19
 */
public class InfixPattern {
	protected final Node root;

	public InfixPattern(@NotNull Node root) {
		this.root = root;
	}

	@NotNull
	public static Node start(@NotNull OrinocoLiteralType literal) {
		return start(null, literal);
	}

	@NotNull
	public static Node start(@NotNull Command command) {
		return start(null, command);
	}

	@NotNull
	public static Node start(@Nullable String captureName, @NotNull OrinocoLiteralType literal) {
		return new PatternNode(".root", new LiteralNode(captureName, literal));
	}

	@NotNull
	public static Node start(@Nullable String captureName, @NotNull Command command) {
		return new PatternNode(".root", new CommandNode(captureName, command));
	}

	protected enum NodeType {
		Command, Operand, Literal, Pattern
	}

	public static abstract class Node {

		protected boolean canHaveChildren = false;
		protected final NodeType nodeType;
		private List<Node> children;
		private final String captureName;

		public Node(@Nullable String captureName, @NotNull NodeType nodeType) {
			this.captureName = captureName;
			this.nodeType = nodeType;
		}

		public abstract boolean matches(@NotNull OrinocoToken token, @NotNull OrinocoLexerContext ctx);

		public boolean isPattern() {
			return this.nodeType == NodeType.Pattern;
		}

		@NotNull
		public List<Node> getChildren() {
			if (!this.canHaveChildren) {
				throw new IllegalStateException("This node can't have children");
			}
			if (this.children == null) {
				this.children = new ArrayList<>();
			}
			return this.children;
		}

		@Nullable
		public String getCaptureName() {
			return captureName;
		}

		@NotNull
		public Node command(@NotNull Command command) {
			return command(null, command);
		}

		@NotNull
		public Node literal(@NotNull OrinocoLiteralType literal) {
			return literal(null, literal);
		}

		@NotNull
		public Node operand() {
			return operand(null);
		}

		@NotNull
		public Node pattern(@NotNull InfixPattern pattern) {
			return pattern(null, pattern);
		}

		@NotNull
		public Node command(@Nullable String captureName, @NotNull Command command) {
			this.getChildren().add(new CommandNode(captureName, command));
			return this;
		}

		@NotNull
		public Node literal(@Nullable String captureName, @NotNull OrinocoLiteralType literal) {
			this.getChildren().add(new LiteralNode(captureName, literal));
			return this;
		}

		@NotNull
		public Node operand(@Nullable String captureName) {
			this.getChildren().add(new OperandNode(captureName));
			return this;
		}

		@NotNull
		public Node pattern(@Nullable String captureName, @NotNull InfixPattern pattern) {
			PatternNode node = new PatternNode(captureName, null);
			node.getChildren().addAll(pattern.root.getChildren());
			this.getChildren().add(node);
			return this;
		}

		@NotNull
		public InfixPattern toPattern() {
			return new InfixPattern(this);
		}
	}

	public static class CommandNode extends Node {
		protected final Command command;

		public CommandNode(@Nullable String captureName, @NotNull Command command) {
			super(captureName, NodeType.Command);
			this.command = command;
			this.canHaveChildren = true;
		}

		@Override
		public boolean matches(@NotNull OrinocoToken token, @NotNull OrinocoLexerContext ctx) {
			return token.getTokenType() == OrinocoSQFTokenType.Command
					&& token.getId() == this.command.getUUID();
		}
	}

	public static class LiteralNode extends Node {
		protected final OrinocoLiteralType literal;

		public LiteralNode(@Nullable String captureName, @NotNull OrinocoLiteralType literal) {
			super(captureName, NodeType.Literal);
			this.literal = literal;
			this.canHaveChildren = false;
		}

		@NotNull
		public OrinocoLiteralType getLiteral() {
			return literal;
		}

		@Override
		public boolean matches(@NotNull OrinocoToken token, @NotNull OrinocoLexerContext ctx) {
			final boolean isLiteral = token.getTokenType() == OrinocoSQFTokenType.LiteralNumber
					|| token.getTokenType() == OrinocoSQFTokenType.LiteralString;
			if (!isLiteral) {
				return false;
			}
			if (token.getTokenType() == OrinocoSQFTokenType.LiteralNumber) {
				return this.literal == OrinocoLiteralType.Number;
			}
			if (token.getTokenType() == OrinocoSQFTokenType.LiteralString) {
				return this.literal == OrinocoLiteralType.String;
			}
			return false;
		}
	}

	public static class OperandNode extends Node {

		public OperandNode(@Nullable String captureName) {
			super(captureName, NodeType.Operand);
			this.canHaveChildren = false;
		}

		@Override
		public boolean matches(@NotNull OrinocoToken token, @NotNull OrinocoLexerContext ctx) {
			if (token.getTokenType() == OrinocoSQFTokenType.Command) {
				return ctx.getCommandInstance(token.getId()).isStrictlyNular();
			}
			OrinocoSQFTokenType ott = (OrinocoSQFTokenType) token.getTokenType();
			switch (ott) {
				case LiteralNumber: //fall
				case LiteralString: //fall
				case GlobalVariable: //fall
				case LocalVariable: {
					return true;
				}
			}
			return false;
		}
	}

	public static class PatternNode extends Node {

		public PatternNode(@Nullable String captureName, @Nullable Node firstChild) {
			super(captureName, NodeType.Pattern);
			this.canHaveChildren = true;
			if (firstChild != null) {
				this.getChildren().add(firstChild);
			}
		}

		@Override
		public boolean matches(@NotNull OrinocoToken token, @NotNull OrinocoLexerContext ctx) {
			// Patterns will not match with an individual token
			throw new IllegalStateException();
		}
	}

}
