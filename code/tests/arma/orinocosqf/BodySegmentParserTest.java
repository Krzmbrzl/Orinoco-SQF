package arma.orinocosqf;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import arma.orinocosqf.bodySegments.BodySegment;
import arma.orinocosqf.bodySegments.BodySegmentParser;
import arma.orinocosqf.bodySegments.ParenSegment;
import arma.orinocosqf.bodySegments.TextSegment;
import arma.orinocosqf.bodySegments.WordSegment;

public class BodySegmentParserTest {

	BodySegmentParser parser;
	OrinocoPreProcessor p;
	List<String> EMPTY;

	@Before
	public void setUp() throws Exception {
		parser = new BodySegmentParser();
		p = new OrinocoPreProcessor(new OrinocoTokenProcessorAdapter() {
		});
		EMPTY = new ArrayList<>();
	}

	public boolean isMacroNamePart(char c, boolean isFirstLetter) {
		return true;
	}

	void assertSegment(String input, BodySegment expected) {
		assertSegment(input, expected, EMPTY);
	}

	void assertSegment(String input, BodySegment expected, List<String> params) {
		BodySegment produced = parser.parseSegments(input.toCharArray(), 0, input.length(), params,
				(c, isFirstLetter) -> p.isMacroNamePart(c, isFirstLetter));

		assertEquals("Incorrect parsing!", expected, produced);
		assertEquals("A call to the segment's toStringNoPreprocessing() didn't result in the original input", input,
				produced.toStringNoPreProcessing().toString());
	}

	void assertSingleWordSegment(String input) {
		assertSegment(input, new WordSegment(input));
	}

	void assertSingleTextSegment(String input) {
		assertSegment(input, new TextSegment(input));
	}

	void assertSingleParenSegment_onlyWords(String input, String[] words) {
		List<BodySegment> segments = new ArrayList<>(words.length);
		for (String currentWord : words) {
			segments.add(new WordSegment(currentWord));
		}

		assertSegment(input, new ParenSegment(segments));
	}

	void assertSingleParenSegment_onlyText(String input, String[] words) {
		List<BodySegment> segments = new ArrayList<>(words.length);
		for (String currentWord : words) {
			segments.add(new TextSegment(currentWord));
		}

		assertSegment(input, new ParenSegment(segments));
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

		assertSingleParenSegment_onlyText("(())", new String[] { "()" });
		
		assertSingleParenSegment_onlyText("(() -*?)", new String[] { "() -*?" });
		
		assertSingleParenSegment_onlyText("( -*?())", new String[] { " -*?()" });
		
		assertSingleParenSegment_onlyText("(( -*?))", new String[] { "( -*?)" });
		
		assertSingleParenSegment_onlyText("((,,))", new String[] { "(", "", ")" });
		
		assertSingleParenSegment_onlyText("((, ,))", new String[] { "(", " ", ")" });
	}
}
