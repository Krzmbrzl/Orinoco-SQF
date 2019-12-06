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

import static org.junit.Assert.assertTrue;

public class InfixPatternMatcherTest {
	@Test
	public void test() {
		SQFCommands.Operators ops = SQFCommands.ops();
		InfixPattern pattern = InfixPattern.start(ops.L_SQ_BRACKET)
				.command(SQFCommands.command("getPos")).operand("OPERAND")
				.command(ops.R_SQ_BRACKET).toPattern();
		// [getPos ANY]
		// getCaptured("OPERAND")


		ArmaFilesystem fs = new ArmaFilesystem(new File(System.getProperty("user.home")).toPath(), new ArrayList<>());
		SQFInfixToPostfixProcessor infixProcessor = new SQFInfixToPostfixProcessor();
		InfixPatternMatcher matcher = new InfixPatternMatcher(pattern);
		infixProcessor.getMatchers().add(matcher);
		OrinocoLexer lexer = new OrinocoLexer(
				new OrinocoPreProcessor(
						new OrinocoTokenInstanceProcessor.ToInstanceTranslator(
								infixProcessor
						), fs
				)
		);
		lexer.start(OrinocoReader.fromCharSequence("[getPos 1]"));
		assertTrue(matcher.matches());
	}
}