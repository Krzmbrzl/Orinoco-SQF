package arma.orinocosqf.parsing.postfix;

import org.jetbrains.annotations.NotNull;

/**
 * @author K
 * @since 7/28/19
 */
public abstract class OrinocoNode {
	public enum Flag {
		Literal, Variable, Array, Command, CodeBlock, InvalidToken
	}

	private final Flag flag;

	public OrinocoNode(@NotNull Flag flag) {
		this.flag = flag;
	}

	@NotNull
	public Flag getFlag() {
		return flag;
	}

}
