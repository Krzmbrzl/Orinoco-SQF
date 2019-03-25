package arma.orinocosqf;

import arma.orinocosqf.exceptions.UnknownIdException;
import org.jetbrains.annotations.NotNull;

/**
 * An interface describing an IdTransformer that is responsible for transforming objects to Ids and backwords
 *
 * @param <T> The type of objects this transformer will transform from and to
 * @author Raven
 */
public interface IdTransformer<T> {
	/**
	 * Transforms the given Id into the corresponding representation
	 *
	 * @param id The id to convert
	 * @return The respective representation
	 * @throws UnknownIdException If the provided id is unknown
	 */
	@NotNull
	public T fromId(int id) throws UnknownIdException;

	/**
	 * Transforms the given object into an id
	 *
	 * @param value The object to transform
	 * @return The respective id
	 * @throws UnknownIdException If no id for the given object can be found/created
	 */
	public int toId(@NotNull T value) throws UnknownIdException;
}
