package arma.orinocosqf.syntax;

import arma.orinocosqf.sqf.SQFCommandSyntax;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author K
 * @see SQFCommandSyntax
 * @since 02/21/2019
 */
public interface CommandSyntax {
	/** @return the return value of the syntax */
	@NotNull
	ReturnValueHolder getReturnValue();

	/** @return the LEFT_PARAM of the syntax (LEFT_PARAM commandName RIGHT_PARAM) or null if there isn't one */
	@Nullable
	Param getLeftParam();

	/** @return the RIGHT_PARAM of the syntax (LEFT_PARAM commandName RIGHT_PARAM) or null if there isn't one */
	@Nullable
	Param getRightParam();

	/**
	 * Will traverse all parameters (both left and right). Note that this will also fully traverse the elements in arrays that are
	 * parameters.
	 *
	 * @return an iterable that will iterate the parameters in order.
	 */
	@NotNull
	Iterable<Param> getAllParams();

}
