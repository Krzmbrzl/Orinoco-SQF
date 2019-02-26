import org.jetbrains.annotations.NotNull;

/**
 * Accepts {@link OrinocoTokenQueue} instances, drains the queues to a worker
 * which is assigned 1 or many {@link StatementProcessor} instances. Since
 * {@link OrinocoTokenQueue} is thread-safe, it is possible to make both a
 * synchronous and worker thread pool of {@link StatementProcessingScheduler}
 *
 * @author K
 * @since 02/20/2019
 */
public interface StatementProcessingScheduler {
	/**
	 * Drains the provided queue into a worker queue via
	 * {@link OrinocoTokenQueue#drainTo(OrinocoTokenQueue)}
	 *
	 * @param queue the queue to drain
	 */
	void scheduleQueue(@NotNull OrinocoTokenQueue queue);
}
