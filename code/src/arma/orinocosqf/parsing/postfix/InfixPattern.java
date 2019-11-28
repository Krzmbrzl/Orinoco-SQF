package arma.orinocosqf.parsing.postfix;

import arma.orinocosqf.Command;
import arma.orinocosqf.OrinocoToken;
import arma.orinocosqf.lexer.OrinocoLexerLiteralType;
import arma.orinocosqf.lexer.OrinocoLexerSQFLiteralType;
import arma.orinocosqf.sqf.SQFCommands;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author K
 * @since 10/5/19
 */
public class InfixPattern {
	private final Node root;

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

	public List<OrinocoToken> getCapturedPattern(@NotNull String captureName) {
		Map<String, List<OrinocoToken>> captured = new HashMap<>();
		// todo implementation
		return captured.get(captureName);
	}

	public OrinocoToken getCaptured(@NotNull String captureName) {
		Map<String, OrinocoToken> captured = new HashMap<>();
		// todo implementation
		return captured.get(captureName);
	}

	@NotNull
	public static Node start(@NotNull OrinocoLexerLiteralType literal) {
		return new LiteralNode("", literal);
	}

	@NotNull
	public static Node start(@NotNull Command command) {
		return new CommandNode("", command);
	}

	public static abstract class Node {
		private List<Node> children;
		protected boolean canHaveChildren = false;
		private final String captureName;

		public Node(@Nullable String captureName) {
			this.captureName = captureName;
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
			return pattern(null);
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
		private final Command command;

		public CommandNode(@Nullable String captureName, @NotNull Command command) {
			super(captureName);
			this.command = command;
			this.canHaveChildren = true;
		}
	}

	public static class LiteralNode extends Node {
		private final OrinocoLexerLiteralType literal;

		public LiteralNode(@Nullable String captureName, @NotNull OrinocoLexerLiteralType literal) {
			super(captureName);
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
			super(captureName);
			this.canHaveChildren = false;
		}
	}


	public static class PatternNode extends Node {
		private final InfixPattern pattern;

		public PatternNode(@Nullable String captureName, @NotNull InfixPattern pattern) {
			super(captureName);
			this.pattern = pattern;
			this.canHaveChildren = false;
		}
	}

}
