package arma.orinocosqf;

import arma.orinocosqf.syntax.BIGame;
import arma.orinocosqf.syntax.CommandSyntax;
import arma.orinocosqf.util.ASCIITextHelper;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * An interface describing a general command-object that will be used to offer the information necessary to parse its potential arguments
 * properly.
 *
 * @author K
 * @since 02/20/2019
 */
public interface Command<Syntax extends CommandSyntax> {
	/** @return the camelCase name of the command */
	@NotNull
	String getName();

	default boolean commandNameEquals(@NotNull Command other) {
		return ASCIITextHelper.equalsIgnoreCase(getName(), other.getName());
	}

	/**
	 * @return true if this command is always nular (always no arguments). Returns false if the command is never nular (always has at least
	 * 1 argument)
	 * @see #getSyntaxList()
	 */
	boolean isStrictlyNular();


	/** @return true if {@link #isStrictlyBinary()} and {@link #isStrictlyNular()} and {@link #isStrictlyUnary()} return false */
	default boolean isNotStrict() {
		return !isStrictlyBinary() && !isStrictlyNular() && !isStrictlyUnary();
	}

	/**
	 * @return true if this command is always binary (always has a left and right argument). Returns false if the command is not always
	 * binary (at least one argument is optional)
	 * @see #canBeBinary()
	 * @see #getSyntaxList()
	 */
	boolean isStrictlyBinary();

	/**
	 * @return true if this command can be binary (can take a left and a right argument)
	 * @see #getSyntaxList()
	 */
	boolean canBeBinary();

	/**
	 * @return true if this command is always unary (always has one right argument). Returns false if this command is not always unary
	 * (right argument is optional or is binary command)
	 * @see #canBeUnary()
	 * @see #getSyntaxList()
	 */
	boolean isStrictlyUnary();

	/**
	 * @return true if this command can be unary (can takes a right argument)
	 * @see #getSyntaxList()
	 */
	boolean canBeUnary();

	/**
	 * Get a list of {@link CommandSyntax} instances. These instances are used to determine what these methods return:
	 * <ul>
	 * <li>{@link #isStrictlyNular()}</li>
	 * <li>{@link #isStrictlyUnary()}</li>
	 * <li>{@link #canBeUnary()}</li>
	 * <li>{@link #isStrictlyBinary()}</li>
	 * <li>{@link #canBeBinary()}</li>
	 * </ul>
	 *
	 * @return a list of all syntaxes for this command
	 */
	@NotNull
	List<Syntax> getSyntaxList();


	/**
	 * @return true if the command is deprecated, false if it isn't
	 */
	boolean isDeprecated();

	/**
	 * @return {@link BIGame} that describes that this command was introduced in
	 */
	@NotNull
	BIGame getGameIntroducedIn();
}
