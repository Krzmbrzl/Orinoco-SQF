package arma.orinocosqf.parsing.postfix;

import arma.orinocosqf.Command;
import arma.orinocosqf.OrinocoToken;
import org.jetbrains.annotations.NotNull;

/**
 * @author K
 * @since 10/5/19
 */
public class CommandOrinocoNode extends OrinocoNode {
	private final OrinocoToken token;
	private final Command command;

	public CommandOrinocoNode(@NotNull OrinocoToken token, @NotNull Command command) {
		super(Flag.Command);
		this.token = token;
		this.command = command;
	}

	@NotNull
	public OrinocoToken getToken() {
		return token;
	}

	@NotNull
	public Command getCommand() {
		return command;
	}

}
