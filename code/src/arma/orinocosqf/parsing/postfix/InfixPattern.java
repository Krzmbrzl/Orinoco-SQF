package arma.orinocosqf.parsing.postfix;

import arma.orinocosqf.Command;
import arma.orinocosqf.lexer.OrinocoLexerLiteralType;
import arma.orinocosqf.lexer.OrinocoLexerSQFLiteralType;
import arma.orinocosqf.sqf.SQFCommands;
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

	private InfixPattern(@NotNull Node root) {
		this.root = root;
		SQFCommands.Operators ops = SQFCommands.ops();
		InfixPattern.start(ops.L_SQ_BRACKET).pattern(null,
				InfixPattern.start(SQFCommands.command("getPos")).operand("OPERAND").toPattern()
		).command(null, ops.R_SQ_BRACKET).toPattern();
		// [getPos ANY]
		// getCaptured("OPERAND")

		// createVehicle [String, getPos thing]
		// ^ getCaptured("STRING")
		// ^ getCaptured("PATTERN")
		InfixPattern.start(SQFCommands.command("createVehicle")).command(ops.L_SQ_BRACKET).pattern("STRING",
				InfixPattern.start(OrinocoLexerSQFLiteralType.String).command(ops.COMMA).pattern("PATTERN", InfixPattern.start(SQFCommands.command("getPos")).operand().toPattern()).toPattern()
		).toPattern();

	}

	@NotNull
	public static Node start(@NotNull OrinocoLexerLiteralType literal) {
		return new LiteralNode("", literal);
	}

	@NotNull
	public static Node start(@NotNull Command command) {
		return new CommandNode("", command);
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
		public Node literal(@NotNull OrinocoLexerLiteralType literal) {
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
		public Node literal(@Nullable String captureName, @NotNull OrinocoLexerLiteralType literal) {
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
			this.getChildren().add(new PatternNode(captureName, pattern));
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
	}

	public static class LiteralNode extends Node {
		protected final OrinocoLexerLiteralType literal;

		public LiteralNode(@Nullable String captureName, @NotNull OrinocoLexerLiteralType literal) {
			super(captureName, NodeType.Literal);
			this.literal = literal;
			this.canHaveChildren = false;
		}

		@NotNull
		public OrinocoLexerLiteralType getLiteral() {
			return literal;
		}
	}

	public static class OperandNode extends Node {

		public OperandNode(@Nullable String captureName) {
			super(captureName, NodeType.Operand);
			this.canHaveChildren = false;
		}
	}


	public static class PatternNode extends Node {
		protected final InfixPattern pattern;

		public PatternNode(@Nullable String captureName, @NotNull InfixPattern pattern) {
			super(captureName, NodeType.Pattern);
			this.pattern = pattern;
			this.canHaveChildren = false;
		}
	}

}
