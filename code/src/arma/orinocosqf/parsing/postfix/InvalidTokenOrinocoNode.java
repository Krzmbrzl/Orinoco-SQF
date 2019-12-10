package arma.orinocosqf.parsing.postfix;

import org.jetbrains.annotations.NotNull;

/**
 * @author K
 * @since 10/5/19
 */
public class InvalidTokenOrinocoNode extends DelayEvalOrinocoNode {
	private final OrinocoNode node;

	public InvalidTokenOrinocoNode(@NotNull OrinocoNode node) {
		super(Flag.CodeBlock, node);
		this.node = node;
	}

	@Override
	public @NotNull Flag getFlag() {
		return Flag.InvalidToken;
	}

	@NotNull
	public OrinocoNode getNode() {
		return node;
	}
}
