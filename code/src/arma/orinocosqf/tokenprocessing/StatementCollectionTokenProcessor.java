package arma.orinocosqf.tokenprocessing;

import arma.orinocosqf.OrinocoTokenProcessor;
import arma.orinocosqf.OrinocoTokenQueue;
import arma.orinocosqf.StatementProcessingScheduler;

/**
 * A type of {@link OrinocoTokenProcessor} that collects whole statements and
 * then submits a filled {@link OrinocoTokenQueue} to a
 * {@link StatementProcessingScheduler}.
 *
 * @author K
 * @since 02/21/2019
 */
public abstract class StatementCollectionTokenProcessor implements OrinocoTokenProcessor {
}
