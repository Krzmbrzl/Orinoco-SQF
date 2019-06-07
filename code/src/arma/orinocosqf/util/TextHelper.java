package arma.orinocosqf.util;

import org.jetbrains.annotations.NotNull;

/**
 * @author K
 * @since 6/6/19
 */
public class TextHelper {

	public static char[] asChars(@NotNull CharSequence cs) {
		char[] chars = new char[cs.length()];
		for (int i = 0; i < cs.length(); i++) {
			chars[i] = cs.charAt(i);
		}
		return chars;
	}

	@NotNull
	public static String asString(@NotNull CharSequence cn) {
		return new String(asChars(cn));
	}
}
