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
public class LexerBinarySearchTokenSet {
	private final TokenCluster[] lookup;

	public LexerBinarySearchTokenSet(@NotNull LexerBinarySearchToken[] fixed) {
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
		for (LexerBinarySearchToken token : fixed) {
			if (token.getName().length() == 1) {
				lookup[26].add(indices[26]++, token);
			} else {
				char firstChar = token.getName().charAt(0);
				if (Character.isAlphabetic(firstChar)) {
					char c = Character.toLowerCase(firstChar);
					int index = c - 'a';
					lookup[index].add(indices[index]++, token);
				} else {
					lookup[27].add(indices[27]++, token);
				}
			}
		}

		for (int i = 0; i < lookup.length; i++) {
			lookup[i].shrink();
		}
	}

	public void resetSearch() {

	}

	/**
	 * Advances the binary search for matching a token.
	 *
	 * @return true if a token has been matched, false otherwise
	 */
	public boolean advance(char c) {
		return false;
	}

	@Nullable
	public LexerBinarySearchToken getMatched() {
		return null;
	}

	@NotNull
	protected List<List<String>> copyToStringLists() {
		List<List<String>> list = new ArrayList<>(lookup.length);
		for (TokenCluster cluster : lookup) {
			List<String> items = new ArrayList<>(cluster.tokens.length);
			list.add(items);
			for (BSTokenHelper helper : cluster.tokens) {
				items.add(helper.getName());
			}
		}
		return list;
	}

	private static class TokenCluster {
		private BSTokenHelper[] tokens;

		public TokenCluster(int max) {
			this.tokens = new BSTokenHelper[max];
		}

		public void add(int index, @NotNull LexerBinarySearchToken token) {
			tokens[index] = new BSTokenHelper(token);
		}

		/**
		 * This method is meant to shrink the array such that there are no ending nulls.
		 */
		public void shrink() {
			int end = 0;
			for (int i = 0; i < tokens.length; i++) {
				if (tokens[i] == null) {
					end = i;
					break;
				}
			}
			BSTokenHelper[] copy = new BSTokenHelper[end];
			System.arraycopy(tokens, 0, copy, 0, copy.length);
			this.tokens = copy;
		}

		@Override
		public String toString() {
			return "TokenCluster{" +
					"tokens=" + Arrays.toString(tokens) +
					'}';
		}
	}

	private static class BSTokenHelper implements LexerBinarySearchToken {

		private final LexerBinarySearchToken token;

		public BSTokenHelper(@NotNull LexerBinarySearchToken token) {
			this.token = token;
		}

		@Override
		public boolean isWordDelimiter() {
			return token.isWordDelimiter();
		}

		@Override
		@NotNull
		public String getName() {
			return token.getName();
		}

		@Override
		public String toString() {
			return token.getName();
		}
	}
}
