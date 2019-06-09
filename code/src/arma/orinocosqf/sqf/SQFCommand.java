package arma.orinocosqf.sqf;

import arma.orinocosqf.Command;
import arma.orinocosqf.syntax.CommandSyntax;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author K
 * @since 5/8/19
 */
public class SQFCommand implements Command {
	private final String name;

	public SQFCommand(@NotNull String name) {
		this.name = name;
	}

	@Override
	@NotNull
	public String getName() {
		return name;
	}

	@Override
	public boolean isStrictlyNular() {
		return false;
	}

	@Override
	public boolean canBeNular() {
		return false;
	}

	@Override
	public boolean isStrictlyBinary() {
		return false;
	}

	@Override
	public boolean canBeBinary() {
		return false;
	}

	@Override
	public boolean isStrictlyUnary() {
		return false;
	}

	@Override
	public boolean canBeUnary() {
		return false;
	}

	@Override
	public @NotNull List<CommandSyntax> getSyntaxList() {
		return null;
	}

	@Override
	public int getPrecedence() {
		return 0;
	}
}
