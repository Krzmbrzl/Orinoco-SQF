package arma.orinocosqf;

/**
 * @author K
 * @since 4/10/19
 */
public class ASCIITextHelper {
	/**
	 * An optimized toLowerCase method. This method assumes no non-ASCII characters are to be submitted to this method
	 */
	public static char toLowerCase(char c) {
		if (c >= 'A' && c <= 'Z') {
			int i = c - 'A';
			c = (char) ('a' + i);
		}
		return c;
	}

	/** An optimized isAlphabetic method. This method assumes no non-ASCII characters are to be submitted to this method */
	public static boolean isAlphabetic(char c) {
		if (c >= 'A' && c <= 'Z') {
			return true;
		}
		return c >= 'a' && c <= 'z';
	}
}
