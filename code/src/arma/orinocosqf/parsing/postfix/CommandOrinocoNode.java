package arma.orinocosqf.parsing.postfix;

import arma.orinocosqf.Command;
import org.jetbrains.annotations.NotNull;

/**
 * @author K
 * @since 10/5/19
 */
public class CommandOrinocoNode extends OrinocoNode {
	private final Command command;

	public CommandOrinocoNode(@NotNull Command command) {
		super(Flag.Command);
		this.command = command;
	}

	@NotNull
	public Command getCommand() {
		return command;
	}
}
