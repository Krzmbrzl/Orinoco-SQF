package arma.orinocosqf;

import org.jetbrains.annotations.NotNull;

/**
 * @author K
 * @since 02/20/2019
 */
public interface LexerBinarySearchToken extends Comparable<LexerBinarySearchToken> {
	boolean isWordDelimeter();

	@NotNull String getName();

	@Override
	default int compareTo(@NotNull LexerBinarySearchToken lexerBinarySearchToken) {
		return getName().compareTo(lexerBinarySearchToken.getName());
	}
}
