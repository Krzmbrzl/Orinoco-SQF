/**
 * A type of {@link OrinocoTokenProcessor} that collects whole statements
 * (commands and variables) up to a semicolon and stores in a
 * {@link OrinocoTokenQueue}.
 *
 * @author K
 * @since 02/20/2019
 */
public interface StatementProcessor extends OrinocoTokenProcessor {
}
