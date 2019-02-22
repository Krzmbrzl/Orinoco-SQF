import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author K
 * @since 02/20/2019
 */
public interface Command {
	/** @return the camelCase name of the command */
	@NotNull String getName();

	/** @return globally unique static id for all {@link Command} instances that is assigned at runtime */
	int id();

	/**
	 * @return true if this command is always nular (always no arguments). Returns false if the command is
	 * nular sometimes (arguments are optional) or is never nular (always has at least 1 argument)
	 */
	boolean isStrictlyNular();

	/**
	 * @return true if this command is always binary (always has a left and right argument).
	 * Returns false if the command is not always binary (at least one argument is optional)
	 */
	boolean isStrictlyBinary();

	/**
	 * @return true if this command is always unary (always has one right argument).
	 * Returns false if this command is not always unary (right argument is optional or is binary command)
	 */
	boolean isStrictlyUnary();

	/**
	 * Get a list of {@link CommandSyntax} instances. These instances are used to determine what these methods return:
	 * <ul>
	 *     <li>{@link #isStrictlyNular()}</li>
	 *     <li>{@link #isStrictlyUnary()}</li>
	 *     <li>{@link #isStrictlyBinary()}</li>
	 * </ul>
	 *
	 * @return a list of all syntaxes for this command
	 */
	@NotNull List<CommandSyntax> getSyntaxList();
}
