package arma.orinocosqf;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;

/**
 * @author K
 * @since 5/12/19
 */
public class CaseInsensitiveHashMap<Cik extends CaseInsentiveKey> {
	private Group[] data = new Group[32];
	private int entryCount = 0;
	private int groupCount = 0;
	private double dataCap = computeDataCap();

	private double computeDataCap() {
		return data.length / 0.75;
	}

	private void grow() {
		Group[] copy = new Group[data.length * 2];
		System.arraycopy(data, 0, copy, 0, copy.length);
		data = copy;
		dataCap = computeDataCap();
	}

	public int size() {
		int size = 0;
		for (Group g : data) {
			if (g != null) {
				size += g.list.size();
			}
		}
		return size;
	}

	public boolean isEmpty() {
		for (Group g : data) {
			if (g != null) {
				boolean empty = g.list.isEmpty();
				if (!empty) {
					return false;
				}
			}
		}
		return true;
	}

	@Nullable
	public Cik getKey(@NotNull CharSequence cs) {
		Group g = getGroup(cs);
		for (Object o : g.list) {
			Cik cik = (Cik) o;
			if (ASCIITextHelper.CHARSEQUENCE_CASE_INSENSITIVE_COMPARATOR.compare(cik.getKey(), cs) == 0) {
				return cik;
			}
		}
		return null;
	}

	public boolean put(@NotNull Cik key) {
		Group g = getGroup(key.getKey());

		boolean removed = g.list.remove(key);
		g.list.add(key);
		entryCount++;
		maybeGrow();
		return removed;
	}

	public void remove(@NotNull Cik key) {
		Group g = getGroup(key.getKey());
		boolean remove = g.list.remove(key);
		if (remove) {
			entryCount--;
		}
		maybeGrow();
	}

	public void clear() {
		for (Group g : data) {
			if (g != null) {
				g.list.clear();
			}
		}
	}

	private void maybeGrow() {
		if (groupCount >= dataCap) {
			grow();
		}
	}

	@NotNull
	private Group getGroup(@NotNull CharSequence cs) {
		final int ind = computeHash(cs) % data.length;
		Group g = data[ind];
		if (g == null) {
			data[ind] = new Group();
			g = data[ind];
			groupCount++;
		}
		return g;
	}

	private int computeHash(@NotNull CharSequence cs) {
		int hash = 0, multiplier = 1;
		for (int i = cs.length() - 1; i >= 0; i--) {
			hash += ASCIITextHelper.toLowerCase(cs.charAt(i)) * multiplier;
			int shifted = multiplier << 5;
			multiplier = shifted - multiplier;
		}
		return hash;
	}

	private static class Group<C extends CaseInsentiveKey> {
		public final LinkedList<C> list = new LinkedList<>();
	}
}
