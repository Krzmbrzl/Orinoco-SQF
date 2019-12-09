package arma.orinocosqf;


import arma.orinocosqf.lexer.OrinocoLexer;
import arma.orinocosqf.parsing.postfix.InfixPattern;
import arma.orinocosqf.parsing.postfix.InfixPatternMatcher;
import arma.orinocosqf.parsing.postfix.SQFInfixToPostfixProcessor;
import arma.orinocosqf.preprocessing.ArmaFilesystem;
import arma.orinocosqf.preprocessing.OrinocoPreProcessor;
import arma.orinocosqf.sqf.SQFCommands;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class InfixPatternMatcherTest {
	@Test
	public void test() {

		ArmaFilesystem fs = new ArmaFilesystem(new File(System.getProperty("user.home")).toPath(), new ArrayList<>());
		SQFInfixToPostfixProcessor infixProcessor = new SQFInfixToPostfixProcessor();

		OrinocoLexer lexer = new OrinocoLexer(
				new OrinocoPreProcessor(
						new OrinocoTokenInstanceProcessor.ToInstanceTranslator(
								infixProcessor
						), fs
				)
		);

		SQFCommands.Operators ops = SQFCommands.ops();
		InfixPattern pattern = InfixPattern.start(ops.L_SQ_BRACKET)
				.command(SQFCommands.command("getPos")).operand("OPERAND")
				.command(ops.R_SQ_BRACKET).toPattern();
		// [getPos ANY]
		// getCaptured("OPERAND")

		InfixPattern patternNested = InfixPattern.start(ops.L_SQ_BRACKET)
				.pattern("PATTERN",
						InfixPattern.start(SQFCommands.command("getPos")).operand("OPERAND2").toPattern()
				)
				.command(ops.R_SQ_BRACKET).toPattern();

		InfixPatternMatcher matcher = new InfixPatternMatcher(pattern);
		InfixPatternMatcher matcher2 = new InfixPatternMatcher(patternNested);

		infixProcessor.getMatchers().add(matcher);
		infixProcessor.getMatchers().add(matcher2);

		lexer.start(OrinocoReader.fromCharSequence("[getPos 1]"));

		assertNotNull(matcher.getMatch("OPERAND"));
		assertTrue(matcher.matches());
		assertTrue(matcher.matchComplete());

		InfixPatternMatcher.Match patternMatch = matcher2.getMatch("PATTERN");
		assertNotNull(patternMatch);
		assertEquals(2, patternMatch.length());
		assertEquals(OrinocoSQFTokenType.Command, patternMatch.token(0).getTokenType());
		assertEquals(OrinocoSQFTokenType.LiteralNumber, patternMatch.token(1).getTokenType());

		assertNotNull(matcher2.getMatch("OPERAND2"));
		assertNull(matcher2.getMatch("OPERAND"));
		assertTrue(matcher2.matches());
		assertTrue(matcher2.matchComplete());
	}
}