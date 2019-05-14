package arma.orinocosqf;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author K
 * @since 5/12/19
 */
public class CaseInsensitiveHashSet<Cik extends CaseInsentiveKey> implements Iterable<Cik> {
	private Group[] data = new Group[32];
	private int entryCount = 0;
	private int groupCount = 0;
	private double dataCap = computeDataCap();

	private double computeDataCap() {
		return data.length / 0.75;
	}

	private void grow() {
		Group[] copy = new Group[data.length * 2];
		for (Group group : data) {
			if (group == null) {
				continue;
			}
			copy[group.hash % copy.length] = group;
		}
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
	public Cik getKeyForCharSequence(@NotNull CharSequence cs) {
		Group g = getGroup(cs);
		for (Object o : g.list) {
			Cik cik = (Cik) o;
			if (keyIsEqual(cik, cs)) {
				return cik;
			}
		}
		return null;
	}

	private boolean keyIsEqual(@NotNull Cik cik, @NotNull CharSequence cs) {
		return ASCIITextHelper.CHARSEQUENCE_CASE_INSENSITIVE_COMPARATOR.compare(cik.getKey(), cs) == 0;
	}

	public boolean put(@NotNull Cik key) {
		Group g = getGroup(key.getKey());

		for (Object value : g.list) {
			Cik next = (Cik) value;
			if (keyIsEqual(key, next.getKey())) {
				return true;
			}
		}
		g.list.add(key);
		entryCount++;
		maybeGrow();
		return false;
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
		int hash = computeHash(cs);
		final int ind = Math.abs(hash) % data.length;
		Group g = data[ind];
		if (g == null) {
			data[ind] = new Group(hash);
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

	@NotNull
	@Override
	public Iterator<Cik> iterator() {
		return new MyIterator();
	}

	private static class Group<C extends CaseInsentiveKey> {
		public final LinkedList<C> list = new LinkedList<>();
		public final int hash;

		public Group(int hash) {
			this.hash = hash;
		}
	}

	private class MyIterator implements Iterator<Cik> {
		private int dataInd = 0;
		private Iterator<Cik> groupIter;
		private boolean checked = false;
		private boolean toast = false;

		@Override
		public boolean hasNext() {
			if (!toast && !checked) {
				checked = true;
				while (true) {
					if (groupIter != null) {
						if (groupIter.hasNext()) {
							break;
						} else {
							dataInd++;
							groupIter = null;
						}
					}
					if (dataInd >= data.length) {
						toast = true;
						break;
					}
					if (data[dataInd] == null) {
						dataInd++;
						continue;
					}
					groupIter = data[dataInd].list.iterator();
				}
			}
			return !toast && groupIter != null && groupIter.hasNext();
		}

		@Override
		public Cik next() {
			if (!hasNext()) {
				throw new IllegalStateException();
			}
			checked = false;
			return groupIter.next();
		}
	}
}
