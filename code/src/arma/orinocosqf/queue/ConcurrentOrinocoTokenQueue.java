package arma.orinocosqf.queue;

import org.jetbrains.annotations.NotNull;

import arma.orinocosqf.OrinocoToken;
import arma.orinocosqf.OrinocoTokenQueue;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.LinkedTransferQueue;

/**
 * Holds a FIFO queue of {@link OrinocoToken} instances that is thread-safe.
 *
 * @author K
 * @since 02/21/2019
 */
public class ConcurrentOrinocoTokenQueue implements OrinocoTokenQueue {

	private final LinkedTransferQueue<OrinocoToken> q = new LinkedTransferQueue<>();

	@Override
	public void add(@NotNull OrinocoToken token) {
		synchronized (this) {
			q.add(token);
		}
	}

	@Override
	public void addAll(@NotNull Collection<OrinocoToken> others) {
		synchronized (this) {
			q.addAll(others);
		}
	}

	/**
	 * A thread blocking operation that will copy all of this queue's tokens to another {@link ConcurrentOrinocoTokenQueue}. All data is
	 * passed by reference to the other queue. This queue is then emptied.
	 */
	@Override
	public void drainTo(@NotNull OrinocoTokenQueue other) {
		synchronized (this) {
			if (other instanceof ConcurrentOrinocoTokenQueue) {
				ConcurrentOrinocoTokenQueue otherq = (ConcurrentOrinocoTokenQueue) other;
				q.drainTo(otherq.q);
			} else {
				other.addAll(q);
				q.clear();
			}
		}
	}

	@NotNull
	@Override
	public Iterator<OrinocoToken> iterator() {
		return q.iterator();
	}

}
