import org.jetbrains.annotations.NotNull;

/**
 * Holds a FIFO queue (a {@link OrinocoToken} array) that is thread-safe.
 *
 * @author K
 * @since 02/21/2019
 */
public abstract class OrinocoTokenQueue implements OrinocoTokenProcessor {
	/**
	 * A thread blocking operation that will copy all of this queue's tokens to another {@link OrinocoTokenQueue}.
	 * All data is passed by reference to the other queue. This queue is then emptied.
	 */
	public abstract void drainTo(@NotNull OrinocoTokenQueue other);
}
