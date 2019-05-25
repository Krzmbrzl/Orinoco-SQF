package arma.orinocosqf;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import arma.orinocosqf.exceptions.MissingMacroArgumentException;
import arma.orinocosqf.exceptions.NoMacroArgumentsGivenException;
import arma.orinocosqf.exceptions.OrinocoPreprocessorException;
import arma.orinocosqf.helpers.NoProblemListener;
import arma.orinocosqf.helpers.TestOrinocoPreprocessor;
import arma.orinocosqf.preprocessing.PreProcessorCommand;
import arma.orinocosqf.preprocessing.PreProcessorMacro;
import arma.orinocosqf.preprocessing.bodySegments.BodySegment;
import arma.orinocosqf.preprocessing.bodySegments.BodySegmentParser;
import arma.orinocosqf.preprocessing.bodySegments.BodySegmentSequence;
import arma.orinocosqf.preprocessing.bodySegments.ParenSegment;
import arma.orinocosqf.preprocessing.bodySegments.TextSegment;
import arma.orinocosqf.preprocessing.bodySegments.WordSegment;

public class BodySegmentExpansionTest {

	static BodySegmentParser segmentParser;
	static TestOrinocoPreprocessor p;
	static List<String> macroArguments;
	static PreProcessorMacro pseudoMacro;
	final static List<CharSequence> NO_ARGS = new ArrayList<>(0);

	@BeforeClass
	public static void setUp() throws Exception {
		segmentParser = new BodySegmentParser(new NoProblemListener());
		p = new TestOrinocoPreprocessor();
		macroArguments = new ArrayList<>();
		pseudoMacro = new PreProcessorMacro(p.getMacroSet(), "NonExistantMacro", new ArrayList<>(),
				new TextSegment("I don't really exist"));
	}
	
	@Before
	public void clearMacros() {
		p.getMacroSet().clear();
	}

	void assertSegmentNoMacro(@NotNull String input) throws OrinocoPreprocessorException {
		assertSegment(input, input, NO_ARGS);
	}

	void assertSegment(@NotNull String originalInput, @NotNull String preprocessedInput) throws OrinocoPreprocessorException {
		assertSegment(originalInput, preprocessedInput, NO_ARGS);
	}

	void assertSegment(@NotNull String originalInput, @NotNull String preprocessedInput, @NotNull List<CharSequence> args)
			throws OrinocoPreprocessorException {
		BodySegment segment = segmentParser.parseSegments(originalInput.toCharArray(), 0, originalInput.length(), macroArguments,
				(c, isFirstLetter) -> p.isMacroNamePart(c, isFirstLetter));

		segment.setOwner(pseudoMacro);

		assertEquals("Expected unpreprocessed input to be equal to original input", originalInput,
				segment.toStringNoPreProcessing().toString());
		assertEquals("Preprocessed to different result", preprocessedInput, segment.applyArguments(args).toString());
	}

	void defineMacro(@NotNull String definition) {
		if (!definition.startsWith("#define ")) {
			definition = "#define " + definition;
		}

		p.acceptPreProcessorCommand(PreProcessorCommand.Define, definition.toCharArray(), 0, definition.length());
	}

	@Test
	public void no_macros() throws OrinocoPreprocessorException {
		assertSegmentNoMacro("test");
		assertSegmentNoMacro("I can do what (i, want)");
	}

	@Test
	public void simpleExpansion() throws OrinocoPreprocessorException {
		defineMacro("#define MACRO Test");

		assertSegment("Hello MACRO", "Hello Test");

		assertSegment("Hello (MACRO)", "Hello (Test)");
	}

	@Test
	public void expansion_with_arguments() throws OrinocoPreprocessorException {
		defineMacro("#define MACRO(a) Hello a");
		defineMacro("#define MACRO2(a,b) Hello a and b");
		defineMacro("#define MACRO3(a,b,c) |a|b|c|");

		assertSegment("MACRO(world)", "Hello world");
		assertSegment("MACRO( world)", "Hello  world");
		assertSegment("MACRO( world )", "Hello  world ");
		
		assertSegment("MACRO2(world,)", "Hello world and ");
		assertSegment("MACRO2(,more)", "Hello  and more");
		assertSegment("MACRO2(,)", "Hello  and ");
		assertSegment("MACRO2(world,more)", "Hello world and more");
		assertSegment("MACRO2(world, more)", "Hello world and  more");
		assertSegment("MACRO2(world, more )", "Hello world and  more ");
		
		assertSegment("MACRO3(a,test,here)", "|a|test|here|");
	}
	
	@Test
	public void stringification() throws OrinocoPreprocessorException {
		defineMacro("QUOTE(a) #a");
		
		assertSegment("#test", "\"test\"");
		assertSegment(" #test", " \"test\"");
		assertSegment("#test ", "\"test\" ");
		assertSegment(" #test ", " \"test\" ");
		
		assertSegment("QUOTE(test)", "\"test\"");
		assertSegment("QUOTE(test )", "\"test \"");
		assertSegment("QUOTE( test)", "\" test\"");
		assertSegment("QUOTE( test )", "\" test \"");
	}
	
	@Test
	public void glueing() throws OrinocoPreprocessorException {
		defineMacro("GLUE(a,b) a##b");
		
		assertSegment("A##Test", "ATest");
		assertSegment("A3##Test", "A3Test");
		assertSegment("##Test", "Test");
		assertSegment("A##", "A");
		assertSegment("A## ", "A ");
		assertSegment("A ##", "A ");
		assertSegment(" ##B", " B");
		
		assertSegment("GLUE(Some,Test)", "SomeTest");
	}
	
	@Test
	public void mixed() throws OrinocoPreprocessorException {
		defineMacro("QUOTE(a) #a");
		defineMacro("DOUBLE(a,b) a##b");
		defineMacro("MODULE MyModule");
		defineMacro("FUNC(a) MyMod_##MODULE##_fnc_##a");
		defineMacro("MACRO MyMacro");
		defineMacro("NESTED1(a,b,c) DOUBLE(QUOTE(a),MACRO)");
		defineMacro("NESTED2(a,b,c) DOUBLE(QUOTE(a),MACRO)##b");
		defineMacro("NESTED3(a,b,c) DOUBLE(QUOTE(a),MACRO)##b#c");
		defineMacro("NESTED4(a,b,c) DOUBLE(QUOTE(DOUBLE(a##c,b)),MACRO)");
		defineMacro("TEST(MACRO) MACRO");
		
		assertSegment("DOUBLE(one,two)", "onetwo");
		assertSegment("DOUBLE(one,MACRO)", "oneMyMacro");
		assertSegment("DOUBLE(QUOTE(one),QUOTE(MACRO))", "\"one\"\"MyMacro\"");
		assertSegment("NESTED1(one,two,three)", "\"one\"MyMacro");
		assertSegment("NESTED2(one,two,three)", "\"one\"MyMacrotwo");
		assertSegment("NESTED3(one,two,three)", "\"one\"MyMacrotwo\"three\"");
		assertSegment("FUNC(test)", "MyMod_MyModule_fnc_test");
		assertSegment("NESTED4(one,two,three)", "\"onethreetwo\"MyMacro");
		assertSegment("TEST(Test here)", "Test here");
	}

	@Test
	public void expectedExceptions() throws OrinocoPreprocessorException {
		defineMacro("#define MACRO(a) Hello a");
		boolean caughtException = false;

		// expanding the WordSegment directly should yield a MissingMacroArgumentException
		// because MACRO needs an argument a to be given
		try {
			WordSegment seg = new WordSegment("MACRO");
			seg.setOwner(pseudoMacro);

			seg.applyArguments(NO_ARGS);
		} catch (MissingMacroArgumentException e) {
			caughtException = true;
		}

		assertTrue("Expected an instance of MissingMacroArgumentException to be thrown", caughtException);

		caughtException = false;


		// expanding the WordSegment in a BodySegmentSequence should yield a NoMacroArgumentsGivenException
		// because MACRO needs arguments
		try {
			List<BodySegment> segments = new ArrayList<>(2);
			WordSegment seg = new WordSegment("MACRO");
			seg.setOwner(pseudoMacro);
			segments.add(seg);

			BodySegmentSequence sequence = new BodySegmentSequence(segments);

			sequence.applyArguments(NO_ARGS);
		} catch (NoMacroArgumentsGivenException e) {
			caughtException = true;
		}

		assertTrue("Expected an instance of NoMacroArgumentsGivenException to be thrown", caughtException);

		caughtException = false;
		
		// expanding the WordSegment in a BodySegmentSequence should yield a MissingMacroArgumentException
		// because MACRO needs one argument but is only presented with an empty argument block
		try {
			List<BodySegment> segments = new ArrayList<>(2);
			WordSegment seg = new WordSegment("MACRO");
			seg.setOwner(pseudoMacro);
			segments.add(seg);
			segments.add(new ParenSegment(new ArrayList<>()));

			BodySegmentSequence sequence = new BodySegmentSequence(segments);

			sequence.applyArguments(NO_ARGS);
		} catch (MissingMacroArgumentException e) {
			caughtException = true;
		}

		assertTrue("Expected an instance of MissingMacroArgumentException to be thrown", caughtException);

		caughtException = false;
	}

}
