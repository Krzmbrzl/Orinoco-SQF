package arma.orinocosqf;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.junit.BeforeClass;
import org.junit.Test;

import arma.orinocosqf.bodySegments.BodySegment;
import arma.orinocosqf.bodySegments.BodySegmentParser;
import arma.orinocosqf.bodySegments.BodySegmentSequence;
import arma.orinocosqf.bodySegments.GlueSegment;
import arma.orinocosqf.bodySegments.MacroArgumentSegment;
import arma.orinocosqf.bodySegments.ParenSegment;
import arma.orinocosqf.bodySegments.StringifySegment;
import arma.orinocosqf.bodySegments.TextSegment;
import arma.orinocosqf.bodySegments.WordSegment;
import arma.orinocosqf.helpers.DummyFileSystem;
import arma.orinocosqf.problems.ProblemListenerPanicImplementation;

public class BodySegmentParserTest {

	static BodySegmentParser parser;
	static OrinocoPreProcessor p;
	static List<String> DefaultParams;

	@BeforeClass
	public static void setUp() throws Exception {
		parser = new BodySegmentParser(new ProblemListenerPanicImplementation());
		p = new OrinocoPreProcessor(new OrinocoTokenProcessorAdapter() {
		}, new DummyFileSystem());
		DefaultParams = new ArrayList<>();
		DefaultParams.add("MyMacroParam");
		DefaultParams.add("SomeParam");
	}

	void assertSegment(@NotNull String input, @NotNull BodySegment expected) {
		assertSegment(input, expected, DefaultParams);
	}

	void assertSegment(@NotNull String input, @NotNull BodySegment expected, @NotNull List<String> params) {
		BodySegment produced = parser.parseSegments(input.toCharArray(), 0, input.length(), params,
				(c, isFirstLetter) -> p.isMacroNamePart(c, isFirstLetter));

		assertEquals("Incorrect parsing!", expected, produced);
		assertEquals("A call to the segment's toStringNoPreprocessing() didn't result in the original input", input,
				produced.toStringNoPreProcessing().toString());
	}

	void assertSingleWordSegment(@NotNull String input) {
		assertSegment(input, new WordSegment(input));
	}

	void assertSingleMacroArgument(@NotNull String input, int argIndex) {
		assertSegment(input, new MacroArgumentSegment(input, argIndex));
	}

	void assertSingleTextSegment(@NotNull String input) {
		assertSegment(input, new TextSegment(input));
	}

	void assertSingleParenSegment_onlyWords(@NotNull String input, @NotNull String[] words) {
		List<BodySegment> segments = new ArrayList<>(words.length);
		for (String currentWord : words) {
			segments.add(new WordSegment(currentWord));
		}

		assertSegment(input, new ParenSegment(segments));
	}

	void assertSingleParenSegment_onlyText(@NotNull String input, @NotNull String[] words) {
		List<BodySegment> segments = new ArrayList<>(words.length);
		for (String currentWord : words) {
			segments.add(new TextSegment(currentWord));
		}

		assertSegment(input, new ParenSegment(segments));
	}

	void assertSingleStringifySegmentFollowedByWord(@NotNull String input, String word) {
		assertSegment(input, new StringifySegment(new WordSegment(word)));
	}

	void assertSingleStringifySegmentFollowedByText(@NotNull String input, @NotNull String text) {
		List<BodySegment> list = new ArrayList<>(2);
		list.add(new StringifySegment(null));
		list.add(new TextSegment(text));

		assertSegment(input, new BodySegmentSequence(list));
	}

	void assertSingleGlueSegment(@NotNull String input, BodySegment left, BodySegment right) {
		assertSegment(input, new GlueSegment(left, right));
	}

	@Test
	public void emptyInput() {
		assertSingleTextSegment("");
	}

	@Test
	public void singleWordSegment() {
		assertSingleWordSegment("bla");

		assertSingleWordSegment("HelloThere");

		assertSingleWordSegment("l");

		assertSingleWordSegment("I_am_A_Macro_Name");

		assertSingleWordSegment("_test");
	}

	@Test
	public void singleWordSegment_withNumbers() {
		assertSingleWordSegment("bla1");

		assertSingleWordSegment("Hello2There3");

		assertSingleWordSegment("l5");

		assertSingleWordSegment("I_am_A_5Macro_Name");

		assertSingleWordSegment("_2test");
	}

	@Test
	public void singleMacroArgument() {
		assertSingleMacroArgument(DefaultParams.get(0), 0);

		assertSingleMacroArgument(DefaultParams.get(1), 1);
	}

	@Test
	public void singleTextSegment_singleChars() {
		assertSingleTextSegment(" ");

		assertSingleTextSegment("\t");

		assertSingleTextSegment("\n");

		assertSingleTextSegment(",");

		// Although a single closing paren will result in a TextSegment, it will never be part of a TextSegment containing other chars as
		// well
		assertSingleTextSegment(")");

		assertSingleTextSegment("\\");

		assertSingleTextSegment("'");

		assertSingleTextSegment("-");

		assertSingleTextSegment("+");

		assertSingleTextSegment("?");

		assertSingleTextSegment("=");

		assertSingleTextSegment("!");

		assertSingleTextSegment("§");

		assertSingleTextSegment("$");

		assertSingleTextSegment(".");

		assertSingleTextSegment("&");

		assertSingleTextSegment("|");

		assertSingleTextSegment("/");

		assertSingleTextSegment("*");

		assertSingleTextSegment("%");

		assertSingleTextSegment("{");

		assertSingleTextSegment("}");

		assertSingleTextSegment("[");

		assertSingleTextSegment("]");

		assertSingleTextSegment("`");

		assertSingleTextSegment("~");

		assertSingleTextSegment(":");

		assertSingleTextSegment(";");

		assertSingleTextSegment("ß");

		assertSingleTextSegment("°");

		assertSingleTextSegment("@");

		assertSingleTextSegment("€");

		assertSingleTextSegment("Ä");

		assertSingleTextSegment("Œ");

		assertSingleTextSegment("Ƕ");

		assertSingleTextSegment("");

		assertSingleTextSegment("0");

		assertSingleTextSegment("1");

		assertSingleTextSegment("2");

		assertSingleTextSegment("3");

		assertSingleTextSegment("4");

		assertSingleTextSegment("5");

		assertSingleTextSegment("6");

		assertSingleTextSegment("7");

		assertSingleTextSegment("8");

		assertSingleTextSegment("9");
	}

	@Test
	public void singleTextSegment() {
		assertSingleTextSegment(" ,..:");

		assertSingleTextSegment(" ,.3.:");

		assertSingleTextSegment(" ,..]:");

		assertSingleTextSegment(" ,\"..\"]:");

		assertSingleTextSegment("{äöü7/&$,,}");
	}

	@Test
	public void singleParenSegment_empty() {
		assertSingleParenSegment_onlyWords("()", new String[] {});
	}

	@Test
	public void singleParenSegment() {
		assertSingleParenSegment_onlyWords("(a)", new String[] { "a" });

		assertSingleParenSegment_onlyWords("(a,b)", new String[] { "a", "b" });

		assertSingleParenSegment_onlyWords("(a,b,c)", new String[] { "a", "b", "c" });

		assertSingleParenSegment_onlyText("(,)", new String[] { "", "" });

		assertSingleParenSegment_onlyText("(,,)", new String[] { "", "", "" });

		assertSingleParenSegment_onlyText("(,,,)", new String[] { "", "", "", "" });

		assertSingleParenSegment_onlyText("( ,\"I am a test\")", new String[] { " ", "\"I am a test\"" });

		assertSingleParenSegment_onlyText("( ,\"I am a test\" )", new String[] { " ", "\"I am a test\" " });

		assertSingleParenSegment_onlyText("( ,\t\"I am a test\" )", new String[] { " ", "\t\"I am a test\" " });

		assertSingleParenSegment_onlyText("( ,\"I, ( am ,a,, test\")", new String[] { " ", "\"I, ( am ,a,, test\"" });
	}

	@Test
	public void singleStringifySegment() {
		assertSegment("#", new StringifySegment(null));

		assertSingleStringifySegmentFollowedByWord("#bla", "bla");

		assertSingleStringifySegmentFollowedByWord("#_", "_");

		assertSingleStringifySegmentFollowedByWord("#bla123", "bla123");

		assertSingleStringifySegmentFollowedByWord("#bl34a", "bl34a");

		assertSingleStringifySegmentFollowedByWord("#bl_a", "bl_a");

		assertSingleStringifySegmentFollowedByWord("#__bla", "__bla");
	}

	@Test
	public void stringifyOperatorFollowedByText() {
		assertSingleStringifySegmentFollowedByText("#3", "3");

		assertSingleStringifySegmentFollowedByText("#,", ",");

		assertSingleStringifySegmentFollowedByText("#-", "-");

		assertSingleStringifySegmentFollowedByText("#\\", "\\");

		assertSingleStringifySegmentFollowedByText("#)", ")");

		assertSingleStringifySegmentFollowedByText("#{", "{");

		assertSingleStringifySegmentFollowedByText("#335,87!§$", "335,87!§$");
	}

	@Test
	public void singleGlueSegment() {
		assertSingleGlueSegment("##", null, null);

		assertSingleGlueSegment("a##", new WordSegment("a"), null);

		assertSingleGlueSegment("##b", null, new WordSegment("b"));

		assertSingleGlueSegment("a##b", new WordSegment("a"), new WordSegment("b"));

		assertSingleGlueSegment("some##word", new WordSegment("some"), new WordSegment("word"));

		assertSingleGlueSegment("_someTest##word23", new WordSegment("_someTest"), new WordSegment("word23"));
	}

	@Test
	public void singleStringSegment() {
		assertSingleTextSegment("\"I am a test\"");

		assertSingleTextSegment("\"I am 3 tests and ł@æ\"");

		assertSingleTextSegment("\"()\"");

		assertSingleTextSegment("\"I am (a test)\"");

		assertSingleTextSegment("\"(a,b,z d,ref)\"");

		assertSingleTextSegment("\"test(a,b,z d,ref)\"");
	}

	@SuppressWarnings("serial")
	@Test
	public void segmentCombinations() {
		List<BodySegment> segments = new ArrayList<>();
		List<BodySegment> parenSegments = new ArrayList<>();

		String input = "Hello there";
		segments.add(new WordSegment("Hello"));
		segments.add(new TextSegment(" "));
		segments.add(new WordSegment("there"));
		assertSegment(input, new BodySegmentSequence(segments));
		segments.clear();

		input = "Hello .,- there";
		segments.add(new WordSegment("Hello"));
		segments.add(new TextSegment(" .,- "));
		segments.add(new WordSegment("there"));
		assertSegment(input, new BodySegmentSequence(segments));
		segments.clear();

		input = "Hello #there";
		segments.add(new WordSegment("Hello"));
		segments.add(new TextSegment(" "));
		segments.add(new StringifySegment(new WordSegment("there")));
		assertSegment(input, new BodySegmentSequence(segments));
		segments.clear();

		input = "Hello (there)";
		segments.add(new WordSegment("Hello"));
		segments.add(new TextSegment(" "));
		parenSegments.add(new WordSegment("there"));
		segments.add(new ParenSegment(parenSegments));
		assertSegment(input, new BodySegmentSequence(segments));
		segments.clear();
		parenSegments.clear();

		input = "Hello (there,and,there)";
		segments.add(new WordSegment("Hello"));
		segments.add(new TextSegment(" "));
		parenSegments.add(new WordSegment("there"));
		parenSegments.add(new WordSegment("and"));
		parenSegments.add(new WordSegment("there"));
		segments.add(new ParenSegment(parenSegments));
		assertSegment(input, new BodySegmentSequence(segments));
		segments.clear();
		parenSegments.clear();

		input = "Hello (there,#())";
		segments.add(new WordSegment("Hello"));
		segments.add(new TextSegment(" "));
		parenSegments.add(new WordSegment("there"));
		parenSegments.add(new BodySegmentSequence(new ArrayList<BodySegment>() {
			{
				add(new StringifySegment(null));
				add(new ParenSegment(new ArrayList<>()));
			}
		}));
		segments.add(new ParenSegment(parenSegments));
		assertSegment(input, new BodySegmentSequence(segments));
		segments.clear();
		parenSegments.clear();

		input = "Hello (there, and,there)";
		segments.add(new WordSegment("Hello"));
		segments.add(new TextSegment(" "));
		parenSegments.add(new WordSegment("there"));
		parenSegments.add(new BodySegmentSequence(new ArrayList<BodySegment>() {
			{
				add(new TextSegment(" "));
				add(new WordSegment("and"));
			}
		}));
		parenSegments.add(new WordSegment("there"));
		segments.add(new ParenSegment(parenSegments));
		assertSegment(input, new BodySegmentSequence(segments));
		segments.clear();
		parenSegments.clear();

		input = "Hello ((there, and,there))";
		segments.add(new WordSegment("Hello"));
		segments.add(new TextSegment(" "));
		parenSegments.add(new ParenSegment(new ArrayList<BodySegment>() {
			{
				add(new WordSegment("there"));
				add(new BodySegmentSequence(new ArrayList<BodySegment>() {
					{
						add(new TextSegment(" "));
						add(new WordSegment("and"));
					}
				}));
				add(new WordSegment("there"));
			}
		}));
		segments.add(new ParenSegment(parenSegments));
		assertSegment(input, new BodySegmentSequence(segments));
		segments.clear();
		parenSegments.clear();

		input = "Hello (test,(there, and,there))";
		segments.add(new WordSegment("Hello"));
		segments.add(new TextSegment(" "));
		parenSegments.add(new WordSegment("test"));
		parenSegments.add(new ParenSegment(new ArrayList<BodySegment>() {
			{
				add(new WordSegment("there"));
				add(new BodySegmentSequence(new ArrayList<BodySegment>() {
					{
						add(new TextSegment(" "));
						add(new WordSegment("and"));
					}
				}));
				add(new WordSegment("there"));
			}
		}));
		segments.add(new ParenSegment(parenSegments));
		assertSegment(input, new BodySegmentSequence(segments));
		segments.clear();
		parenSegments.clear();

		input = "Hello (test,\"(there, and,there)\")";
		segments.add(new WordSegment("Hello"));
		segments.add(new TextSegment(" "));
		parenSegments.add(new WordSegment("test"));
		parenSegments.add(new TextSegment("\"(there, and,there)\""));
		segments.add(new ParenSegment(parenSegments));
		assertSegment(input, new BodySegmentSequence(segments));
		segments.clear();
		parenSegments.clear();

		input = "Hello (" + DefaultParams.get(0) + ",\"(there, and,there)\")";
		segments.add(new WordSegment("Hello"));
		segments.add(new TextSegment(" "));
		parenSegments.add(new MacroArgumentSegment(DefaultParams.get(0), 0));
		parenSegments.add(new TextSegment("\"(there, and,there)\""));
		segments.add(new ParenSegment(parenSegments));
		assertSegment(input, new BodySegmentSequence(segments));
		segments.clear();
		parenSegments.clear();
	}
}
