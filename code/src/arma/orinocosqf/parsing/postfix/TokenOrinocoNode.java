package arma.orinocosqf.parsing.postfix;

import arma.orinocosqf.OrinocoToken;
import org.jetbrains.annotations.NotNull;

/**
 * @author K
 * @since 10/5/19
 */
public class TokenOrinocoNode extends OrinocoNode {
	private final OrinocoToken token;

	public TokenOrinocoNode(@NotNull Flag flag, @NotNull OrinocoToken token) {
		super(flag);
		this.token = token;
	}

	@NotNull
	public OrinocoToken getToken() {
		return token;
	}
}
