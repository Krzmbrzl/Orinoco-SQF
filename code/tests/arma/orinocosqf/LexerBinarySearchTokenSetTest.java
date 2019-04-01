package arma.orinocosqf;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class LexerBinarySearchTokenSetTest {
	@Test
	public void constructionTest() {
		//this test is for making sure that the commands are loaded internally correctly

		SimpleBinarySearchToken[] tokens = {
				token("command", false),
				token("?", true),
				token("COMAND", false),
				token("alpha", false),
				token("zzz", false),
				token("<=", false),
				token(">=", true),
				token("!", true),
		};

		List<List<String>> expected = new ArrayList<>();

		for (int i = 0; i < 28; i++) {
			expected.add(new ArrayList<>());
		}

		{
			List<String> cList = expected.get('c' - 'a');
			cList.add("COMAND");
			cList.add("command");
		}

		{
			List<String> zList = expected.get('z' - 'a');
			zList.add("zzz");
		}

		{
			List<String> aList = expected.get(0);
			aList.add("alpha");
		}

		{
			List<String> singles = expected.get(26);
			singles.add("?");
			singles.add("!");
			singles.sort(String::compareTo);
		}

		{
			List<String> nonAlphabetic = expected.get(27);
			nonAlphabetic.add("<=");
			nonAlphabetic.add(">=");
			nonAlphabetic.sort(String::compareTo);
		}

		LexerBinarySearchTokenSet set = new LexerBinarySearchTokenSet(tokens);
		List<List<String>> lists = set.copyToStringLists();
		assertEquals(28, lists.size());
		int i = 0;
		for (List<String> list : lists) {
			assertEquals(list, expected.get(i++));
		}

	}


	private static SimpleBinarySearchToken token(@NotNull String name, boolean isWordDelim) {
		return new SimpleBinarySearchToken(name, isWordDelim);
	}

	private static class SimpleBinarySearchToken implements LexerBinarySearchToken {

		private final String name;
		private final boolean isWordDelimeter;

		public SimpleBinarySearchToken(@NotNull String name, boolean isWordDelimeter) {
			this.name = name;
			this.isWordDelimeter = isWordDelimeter;
		}

		@Override
		public boolean isWordDelimeter() {
			return isWordDelimeter;
		}

		@Override
		@NotNull
		public String getName() {
			return name;
		}
	}

}