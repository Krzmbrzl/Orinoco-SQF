package arma.orinocosqf;


import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author K
 * @since 4/27/19
 */
public abstract class InputCharacter {
	public abstract boolean matches(char c);

	public static final Map<Character, InputCharacter> POOL = new HashMap<>();

	public static final InputCharacter MATCH_az = new CharRangeInputCharacter('a', 'z');
	public static final InputCharacter MATCH_AZ = new CharRangeInputCharacter('A', 'Z');
	public static final InputCharacter MATCH_WHITESPACE = new MatchWhitespaceInputCharacter();
	public static final InputCharacter MATCH_ALPHABETIC = new MatchAlphabeticInputCharacter();
	public static final InputCharacter MATCH_NUMERIC = new MatchNumericInputCharacter();
	public static final InputCharacter MATCH_ALPHANUMERIC = new MatchLetterOrDigitInputCharacter();

	public static final InputCharacter MATCH_ANY = new MatchAnyInputCharacter();

	@Nullable
	public static InputCharacter get(char c) {
		return POOL.get(c);
	}

	public static void loadPool() {
		for (char c = 'a'; c <= 'z'; c++) {
			POOL.put(c, new CharInputCharacter(c));

		}

		for (char c = 'A'; c <= 'Z'; c++) {
			POOL.put(c, new CharInputCharacter(c));
		}

		final char[] specialChars = {
				'+', '-', '/', '*', '!', '(', ')', '{', '}', '[', ']', '?', '<', '>', '#',
				'=', '&', '|', '_', '\\', '\'', '"', ' ', '\t', '\r', '\n', '^'
		};
		for (char c : specialChars) {
			POOL.put(c, new CharInputCharacter(c));
		}
	}


	private static class CharRangeInputCharacter extends InputCharacter {

		private final char start;
		private final char end;

		public CharRangeInputCharacter(char start, char end) {
			this.start = start;
			this.end = end;
		}

		@Override
		public boolean matches(char c) {
			return c >= start && c <= end;
		}
	}

	private static class CharInputCharacter extends InputCharacter {

		private char c;

		public CharInputCharacter(char c) {
			this.c = c;
		}

		@Override
		public boolean matches(char c) {
			return this.c == c;
		}

	}


	private static class MatchAnyInputCharacter extends InputCharacter {
		@Override
		public boolean matches(char c) {
			return true;
		}
	}

	private static class MatchWhitespaceInputCharacter extends InputCharacter {
		@Override
		public boolean matches(char c) {
			return Character.isWhitespace(c);
		}
	}

	private static class MatchAlphabeticInputCharacter extends InputCharacter {
		@Override
		public boolean matches(char c) {
			return Character.isAlphabetic(c);
		}
	}

	private static class MatchNumericInputCharacter extends InputCharacter {
		@Override
		public boolean matches(char c) {
			return Character.isDigit(c);
		}
	}

	private static class MatchLetterOrDigitInputCharacter extends InputCharacter {
		@Override
		public boolean matches(char c) {
			return Character.isLetterOrDigit(c);
		}
	}
}
