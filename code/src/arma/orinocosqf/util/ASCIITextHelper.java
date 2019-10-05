package arma.orinocosqf.util;

import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

/**
 * @author K
 * @since 4/10/19
 */
public class ASCIITextHelper {

	public static final Comparator<CharSequence> CHARSEQUENCE_CASE_INSENSITIVE_COMPARATOR = ASCIITextHelper::compareIgnoreCase;

	public static int compareIgnoreCase(@NotNull CharSequence left, @NotNull CharSequence right) {
		int len1 = left.length();
		int len2 = right.length();
		int lim = Math.min(left.length(), right.length());
		for (int k = 0; k < lim; ++k) {
			final char lc = toLowerCase(left.charAt(k));
			final char rc = toLowerCase(right.charAt(k));
			if (lc != rc) {
				return lc - rc;
			}
		}
		return len1 - len2;
	}

	public static boolean containsIgnoreCase(@NotNull CharSequence base, @NotNull CharSequence search) {
		if (search.length() > base.length()) {
			return false;
		}
		int sind = 0;
		for (int b = 0; b < base.length(); ++b) {
			final char blc = toLowerCase(base.charAt(b));
			final char slc = toLowerCase(search.charAt(sind));
			if (blc != slc) {
				sind = 0;
				continue;
			}
			sind++;
			if (sind == search.length()) {
				return true;
			}
		}
		return false;
	}

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

	public static boolean equalsIgnoreCase(@NotNull CharSequence left, @NotNull CharSequence right) {
		return compareIgnoreCase(left, right) == 0;
	}
}
