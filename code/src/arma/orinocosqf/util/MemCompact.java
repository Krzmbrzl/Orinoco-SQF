package arma.orinocosqf.util;

import java.util.ArrayList;

/**
 * @author K
 * @since 6/6/19
 */
public interface MemCompact {
	/**
	 * This method can be invoked when the instance is created as a way of trimming memory utilization (i.e. invoking {@link
	 * ArrayList#trimToSize()} or removing nulls from an internal array).
	 */
	void memCompact();
}
