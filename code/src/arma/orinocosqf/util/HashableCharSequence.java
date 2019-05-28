package arma.orinocosqf.util;

import org.jetbrains.annotations.NotNull;

/**
 * @author K
 * @since 5/12/19
 */
public abstract class HashableCharSequence implements CharSequence {

	public static int computeHash(char[] chars) {
		int hash = 0, multiplier = 1;
		for (int i = chars.length - 1; i >= 0; i--) {
			hash += chars[i] * multiplier;
			int shifted = multiplier << 5;
			multiplier = shifted - multiplier;
		}
		return hash;
	}

	public static int computeHash(@NotNull CharSequence cs) {
		int hash = 0, multiplier = 1;
		for (int i = cs.length() - 1; i >= 0; i--) {
			hash += cs.charAt(i) * multiplier;
			int shifted = multiplier << 5;
			multiplier = shifted - multiplier;
		}
		return hash;
	}

	public static char[] asChars(@NotNull CharSequence cs) {
		char[] chars = new char[cs.length()];
		for (int i = 0; i < cs.length(); i++) {
			chars[i] = cs.charAt(i);
		}
		return chars;
	}

	protected final int hashcode;

	public HashableCharSequence(int hashcode) {
		this.hashcode = hashcode;
	}

	/**
	 * Gets a deep copied instance. This method is handy for implementations of this class that may have underyling data that changes via
	 * set methods, but you want to return a new instance in which the underlying data is immutable for caching purposes
	 *
	 * @return a deep copied instance.
	 */
	@NotNull
	public abstract HashableCharSequence deepCopy();

	@Override
	public int hashCode() {
		return hashcode;
	}

	@NotNull
	public static HashableCharSequence fromCharSequence(@NotNull CharSequence cs) {
		return new CharSequenceImpl(cs);
	}

	@NotNull
	public static HashableCharSequence fromCharArray(@NotNull char[] chars) {
		return new CharArrayImpl(chars);
	}

	@NotNull
	public static HashableCharSequence fromString(@NotNull String s) {
		return new StringImpl(s);
	}

	@NotNull
	public abstract String asString();

	@Override
	public String toString() {
		return "{s=" + asString() + ", hash=" + hashcode + "}";
	}

	protected boolean charSequenceEquals(@NotNull CharSequence other) {
		if (this.length() != other.length()) {
			return false;
		}
		for (int i = 0; i < length(); i++) {
			if (other.charAt(i) != charAt(i)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof HashableCharSequence)) {
			return false;
		}
		HashableCharSequence other = (HashableCharSequence) obj;
		return charSequenceEquals(other);
	}

	private static class CharSequenceImpl extends HashableCharSequence {

		private final CharSequence cs;

		protected CharSequenceImpl(@NotNull CharSequence cs) {
			super(computeHash(cs));
			this.cs = cs;
		}

		@Override
		@NotNull
		public HashableCharSequence deepCopy() {
			return fromCharArray(asChars(this));
		}

		@Override
		@NotNull
		public String asString() {
			char[] chars = asChars(this);
			return new String(chars);
		}

		@Override
		public int length() {
			return cs.length();
		}

		@Override
		public char charAt(int i) {
			return cs.charAt(i);
		}

		@Override
		public CharSequence subSequence(int i, int i1) {
			return cs.subSequence(i, i1);
		}
	}

	private static class CharArrayImpl extends HashableCharSequence {

		private final char[] chars;

		public CharArrayImpl(@NotNull char[] chars) {
			super(computeHash(chars));
			this.chars = chars;
		}

		@Override
		public int length() {
			return chars.length;
		}

		@Override
		public char charAt(int i) {
			return chars[i];
		}

		@Override
		public CharSequence subSequence(int i, int i1) {
			char[] copy = new char[i1 - i];
			System.arraycopy(chars, i, copy, 0, copy.length);
			return new CharArrayImpl(copy);
		}

		@Override
		@NotNull
		public HashableCharSequence deepCopy() {
			// chars isn't accessible directly, so it's immutable.
			// Since chars is immutable, no need to duplicate chars
			return this;
		}

		@Override
		@NotNull
		public String asString() {
			return new String(chars);
		}
	}

	private static class StringImpl extends HashableCharSequence {

		private final String s;

		public StringImpl(@NotNull String s) {
			// Don't use s.hashCode().
			// We want the hashcode to be consistent for all CharSequence's.
			// Using the hashcode function for String may not equal a CharSequence's hashcode even though the underlying data is the same.
			super(computeHash(s));
			this.s = s;
		}

		@Override
		public int length() {
			return s.length();
		}

		@Override
		public char charAt(int i) {
			return s.charAt(i);
		}

		@Override
		public CharSequence subSequence(int i, int i1) {
			return s.subSequence(i, i1);
		}

		@Override
		@NotNull
		public HashableCharSequence deepCopy() {
			return this; // this object is immutable, so no need to actually deep copy anything
		}

		@Override
		@NotNull
		public String asString() {
			return s;
		}

	}
}
