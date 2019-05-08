package arma.orinocosqf;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * An interface describing a general command-object that will be used to offer
 * the information necessary to parse its potential arguments properly.
 *
 * @author K
 * @since 02/20/2019
 */
public interface Command {
	/** @return the camelCase name of the command */
	@NotNull
	String getName();

	/**
	 * @return true if this command is always nular (always no arguments). Returns
	 *         false if the command is nular sometimes (arguments are optional) or
	 *         is never nular (always has at least 1 argument)
	 *
	 * @see #canBeNular()
	 * @see #getSyntaxList()
	 */
	boolean isStrictlyNular();

	/**
	 * @return true if this command can be nular (can be used without arguments)
	 *
	 * @see #getSyntaxList()
	 */
	boolean canBeNular();

	/**
	 * @return true if this command is always binary (always has a left and right
	 *         argument). Returns false if the command is not always binary (at
	 *         least one argument is optional)
	 *
	 * @see #canBeBinary()
	 * @see #getSyntaxList()
	 */
	boolean isStrictlyBinary();

	/**
	 * @return true if this command can be binary (can take a left and a right
	 *         argument)
	 *
	 * @see #getSyntaxList()
	 */
	boolean canBeBinary();

	/**
	 * @return true if this command is always unary (always has one right argument).
	 *         Returns false if this command is not always unary (right argument is
	 *         optional or is binary command)
	 *
	 * @see #canBeUnary()
	 * @see #getSyntaxList()
	 */
	boolean isStrictlyUnary();

	/**
	 * @return true if this command can be unary (can takes a right argument)
	 *
	 * @see #getSyntaxList()
	 */
	boolean canBeUnary();

	/**
	 * Get a list of {@link CommandSyntax} instances. These instances are used to
	 * determine what these methods return:
	 * <ul>
	 * <li>{@link #isStrictlyNular()}</li>
	 * <li>{@link #canBeNular()}</li>
	 * <li>{@link #isStrictlyUnary()}</li>
	 * <li>{@link #canBeUnary()}</li>
	 * <li>{@link #isStrictlyBinary()}</li>
	 * <li>{@link #canBeBinary()}</li>
	 * </ul>
	 *
	 * @return a list of all syntaxes for this command
	 */
	@NotNull
	List<CommandSyntax> getSyntaxList();

	/**
	 * @return The precedence of this command. (Higher means higher precedence)
	 */
	int getPrecedence();
}
