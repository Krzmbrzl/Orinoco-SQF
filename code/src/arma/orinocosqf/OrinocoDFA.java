package arma.orinocosqf;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author K
 * @since 4/27/19
 */
public class OrinocoDFA {
	public final Node root = new Node();

	static {
		InputCharacter.loadPool();
	}

	public static class Node {
		public final Map<InputCharacter, List<Node>> paths = new HashMap<>();

		@NotNull
		public List<Node> next(char c) {
			for (InputCharacter ic : paths.keySet()) {
				if (ic.matches(c)) {
					return next(ic);
				}
			}
			return new ArrayList<>();
		}

		@NotNull
		public List<Node> next(@NotNull InputCharacter c) {
			return paths.computeIfAbsent(c, cc -> new ArrayList<>());
		}

		protected void appendEnd() {
			append(InputCharacter.MATCH_WHITESPACE, END);
		}

		protected void append(char c, @NotNull Node n) {
			List<Node> nodes = paths.computeIfAbsent(InputCharacter.get(c), ic -> new ArrayList<>());
			nodes.add(n);
		}

		protected void append(@NotNull InputCharacter c, @NotNull Node n) {
			List<Node> nodes = paths.computeIfAbsent(c, ic -> new ArrayList<>());
			nodes.add(n);
		}
	}

	public static final Node END = new Node();

	private static class WordNode extends Node {
		public WordNode(@NotNull String text, @Nullable Node endNode, int ind) {
			List<Node> nodes = paths.computeIfAbsent(InputCharacter.get(text.charAt(ind)), (c) -> new ArrayList<>());
			if (ind + 1 < text.length()) {
				nodes.add(new WordNode(text, endNode, ind + 1));
			} else if (endNode != null) {
				nodes.add(endNode);
			}
		}

		public WordNode(@NotNull String text, @Nullable Node endNode) {
			List<Node> nodes = paths.computeIfAbsent(InputCharacter.get(text.charAt(0)), c -> new ArrayList<>());
			nodes.add(new WordNode(text, endNode, 1));
		}
	}

	public static final class WhitespaceNode extends Node {

		public WhitespaceNode() {
			append(InputCharacter.MATCH_WHITESPACE, this);
			appendEnd();
		}
	}

	public static final class NumberNode extends Node {

		public NumberNode() {
			append(InputCharacter.MATCH_NUMERIC, this);
			appendEnd();
		}
	}

	private static class LetterOrDigit extends Node {
		public static final Node instance = new LetterOrDigit();

		public LetterOrDigit() {
			append(InputCharacter.MATCH_ALPHANUMERIC, this);
			appendEnd();
		}
	}

	public static final class IdentifierNode extends Node {
		public IdentifierNode() {
			append(InputCharacter.MATCH_ALPHABETIC, LetterOrDigit.instance);
			appendEnd();
		}
	}

	private static class PreProcessorCommandNode extends Node {
		public PreProcessorCommandNode() {
			for (PreProcessorCommand ppc : PreProcessorCommand.values()) {
				append(ppc.commandName().charAt(1), new WordNode(ppc.commandName(), null, 2));
			}
		}
	}

	public static class SQF extends OrinocoDFA {
		public static final SQF INSTANCE = new SQF();

		private SQF() {
			root.append(InputCharacter.get('#'), new Hash());
//			root.append(InputCharacter.get('#'), END);
			root.append(InputCharacter.MATCH_WHITESPACE, new WhitespaceNode());
			root.append(InputCharacter.MATCH_NUMERIC, new NumberNode());
			root.append(InputCharacter.MATCH_ALPHABETIC, new IdentifierNode());
		}

		private static class Hash extends Node {

			public Hash() {
				for (PreProcessorCommand ppc : PreProcessorCommand.values()) {
					append(ppc.commandName().charAt(0), new PreProcessorCommandNode());
				}
			}
		}
	}
}
