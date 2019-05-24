package arma.orinocosqf;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Holds a FIFO queue of {@link OrinocoToken} instances.
 *
 * @author K
 * @since 02/21/2019
 */
public interface OrinocoTokenQueue extends Iterable<OrinocoToken> {

	/**
	 * Adds a single {@link OrinocoToken}
	 *
	 * @param token token to add
	 */
	void add(@NotNull OrinocoToken token);

	/**
	 * Adds all of the following collection to the queue
	 *
	 * @param others tokens to add
	 */
	void addAll(@NotNull Collection<OrinocoToken> others);


	/**
	 * Copy all of this queue's tokens to another {@link OrinocoTokenQueue}. All data is passed by reference to the other queue. This queue
	 * is then emptied.
	 */
	void drainTo(@NotNull OrinocoTokenQueue other);
}
