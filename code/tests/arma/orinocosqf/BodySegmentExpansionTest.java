package arma.orinocosqf;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.junit.BeforeClass;
import org.junit.Test;

import arma.orinocosqf.helpers.NoProblemListener;
import arma.orinocosqf.helpers.TestOrinocoPreprocessor;
import arma.orinocosqf.preprocessing.MacroSet;
import arma.orinocosqf.preprocessing.PreProcessorMacro;
import arma.orinocosqf.preprocessing.bodySegments.BodySegment;
import arma.orinocosqf.preprocessing.bodySegments.BodySegmentParser;
import arma.orinocosqf.preprocessing.bodySegments.TextSegment;

public class BodySegmentExpansionTest {

	static BodySegmentParser segmentParser;
	static TestOrinocoPreprocessor p;
	static List<String> macroArguments;
	static PreProcessorMacro pseudoMacro;
	static MacroSet macroSet;
	final static List<CharSequence> NO_ARGS = new ArrayList<>(0);

	@BeforeClass
	public static void setUp() throws Exception {
		segmentParser = new BodySegmentParser(new NoProblemListener());
		p = new TestOrinocoPreprocessor();
		macroArguments = new ArrayList<>();
		macroSet = new MacroSet();
		pseudoMacro = new PreProcessorMacro(macroSet, "NonExistantMacro", new ArrayList<>(), new TextSegment("I don't really exist"));
	}

	void assertSegmentNoMacro(@NotNull String input) {
		assertSegment(input, input, NO_ARGS);
	}

	void assertSegment(@NotNull String originalInput, @NotNull String preprocessedInput, @NotNull List<CharSequence> args) {
		BodySegment segment = segmentParser.parseSegments(originalInput.toCharArray(), 0, originalInput.length(), macroArguments,
				(c, isFirstLetter) -> p.isMacroNamePart(c, isFirstLetter));

		segment.setOwner(pseudoMacro);

		assertEquals("Expected unpreprocessed input to be equal to original input", originalInput, segment.toStringNoPreProcessing().toString());
		assertEquals("Preprocessed to different result", preprocessedInput, segment.applyArguments(args).toString());
	}

	@Test
	public void no_macros() {
		assertSegmentNoMacro("test");
		assertSegmentNoMacro("I can do what (i, want)");
	}

}
