package arma.orinocosqf;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author K
 * @since 4/1/19
 */
public class LexerBinarySearchTokenSet<T extends LexerBinarySearchToken> {
	private final TokenCluster[] lookup;
	private boolean clusterSelected;
	private int selectedClusterInd;
	private char lastChar;
	private int lookupInd;
	private int advanceCount;
	private T matchedToken;
	private boolean impossibleMatch;

	private static final int SINGLES_INDEX = 26;
	private static final int NON_ALPHABETIC_INDEX = 27;

	public LexerBinarySearchTokenSet(@NotNull T[] fixed) {
		Arrays.sort(fixed);

		/*
		 * The following code structures the lookup array such that:
		 * 1. indices 0-25 are for tokens starting with a-z.
		 * 2. index 26 is for tokens of length 1 ("?","!", etc)
		 * 3. index 27 is for non alphabetic tokens that aren't of length 1 (">=", "<=", etc)
		 */

		lookup = new TokenCluster[28];
		for (int i = 0; i < lookup.length; i++) {
			lookup[i] = new TokenCluster(fixed.length);
		}
		int[] indices = new int[lookup.length];
		for (T token : fixed) {
			if (token.getName().length() == 1) {
				lookup[SINGLES_INDEX].set(indices[SINGLES_INDEX]++, token);
			} else {
				char firstChar = token.getName().charAt(0);
				if (isAlphabetic(firstChar)) {
					char c = toLowerCase(firstChar);
					int index = c - 'a';
					lookup[index].set(indices[index]++, token);
				} else {
					lookup[NON_ALPHABETIC_INDEX].set(indices[NON_ALPHABETIC_INDEX]++, token);
				}
			}
		}

		for (TokenCluster cluster : lookup) {
			cluster.shrink();
		}

		resetSearch();
	}

	public void resetSearch() {
		clusterSelected = false;
		selectedClusterInd = 0;
		lastChar = '\0';
		lookupInd = 0;
		advanceCount = 0;
		matchedToken = null;
		impossibleMatch = false;
	}

	/**
	 * Advances the binary search for matching a token.
	 *
	 * @return true if a token has been matched, false otherwise
	 */
	public boolean advance(char c) {
		if (impossibleMatch) {
			return false;
		}
		char lower = toLowerCase(c);
		if (!clusterSelected) {
			if (isAlphabetic(c)) {
				lookupInd = lower - 'a';
			} else {
				ArrayList<LexerBinarySearchToken> singles = lookup[SINGLES_INDEX].tokens;
				for (LexerBinarySearchToken token : singles) {
					if (toLowerCase(token.getName().charAt(0)) == lower) {
						clusterSelected = true;
						lookupInd = SINGLES_INDEX;
						matchedToken = (T) token;
						lastChar = c;
						advanceCount++;
						return true;
					}
				}
				//if at this point, not a single
				lookupInd = NON_ALPHABETIC_INDEX;
			}
			clusterSelected = true;
		}
		if (lookupInd == SINGLES_INDEX) {
			if (isAlphabetic(lastChar)) {
				lookupInd = toLowerCase(lastChar) - 'a';
			} else {
				lookupInd = NON_ALPHABETIC_INDEX;
			}
		}
		ArrayList<LexerBinarySearchToken> tokens = lookup[lookupInd].tokens;
		boolean possibleMatch = false;
		boolean clusterIndUpdated = false;
		int i = selectedClusterInd;
		for (; i < tokens.size(); i++) {
			LexerBinarySearchToken token = tokens.get(i);
			String name = token.getName();
			if (advanceCount >= name.length()) {
				continue;
			}
			final char nameCharLower = toLowerCase(name.charAt(advanceCount));
			if (nameCharLower == lower) {
				possibleMatch = true;
				if (!clusterIndUpdated) {
					clusterIndUpdated = true;
					selectedClusterInd = i;
				}
			} else {
				if (lower > nameCharLower) {
					// the inputted char comes after the name char which means there is no point in continuing binary search
					// because we have already scanned all items that come before name alphabetically
					break;
				}
				// update i to the index where we can start matching
				while (i < tokens.size()) {
					token = tokens.get(i);
					name = token.getName();
					if (toLowerCase(name.charAt(advanceCount)) == lower) {
						break;
					}
					i++;
				}
				// subtract one from i as the for loop will add 1 after continue
				i--;
				continue;
			}

			if (advanceCount == name.length() - 1) {
				matchedToken = (T) token;
			}
		}
		if (!possibleMatch) {
			impossibleMatch = true;
		}
		lastChar = c;
		advanceCount++;
		return matchedToken != null;
	}

	@Nullable
	public T getMatched() {
		return matchedToken;
	}

	@NotNull
	protected List<List<String>> copyToStringLists() {
		List<List<String>> list = new ArrayList<>(lookup.length);
		for (TokenCluster cluster : lookup) {
			List<String> items = new ArrayList<>(cluster.tokens.size());
			list.add(items);
			for (LexerBinarySearchToken helper : cluster.tokens) {
				items.add(helper.getName());
			}
		}
		return list;
	}

	/**
	 * An optimized toLowerCase method. This method assumes no non-ASCII characters are to be submitted to this method
	 */
	private static char toLowerCase(char c) {
		if (c >= 'A' && c <= 'Z') {
			int i = c - 'A';
			c = (char) ('a' + i);
		}
		return c;
	}

	/** An optimized isAlphabetic method. This method assumes no non-ASCII characters are to be submitted to this method */
	private static boolean isAlphabetic(char c) {
		if (c >= 'A' && c <= 'Z') {
			return true;
		}
		return c >= 'a' && c <= 'z';
	}

	private static class TokenCluster {
		private final ArrayList<LexerBinarySearchToken> tokens;

		public TokenCluster(int max) {
			this.tokens = new ArrayList<>(max);
		}

		public void set(int index, @NotNull LexerBinarySearchToken token) {
			if (index == tokens.size()) {
				tokens.add(token);
			} else {
				tokens.set(index, token);
			}
		}

		/**
		 * This method is meant to shrink the underlying data array such that there are no ending nulls.
		 */
		public void shrink() {
			tokens.trimToSize();
		}

		@Override
		public String toString() {
			return "TokenCluster{" +
					"tokens=" + tokens +
					'}';
		}
	}

}
