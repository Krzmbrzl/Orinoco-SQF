package arma.orinocosqf;

import arma.orinocosqf.helpers.TokenExpector;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;

public class OrinocoPreProcessorTest {
	private final TokenExpector expector = new TokenExpector();
	private final OrinocoPreProcessor preProcessor = new OrinocoPreProcessor(expector);
	private OrinocoLexer lexer;

	private void lexerFromText(@NotNull String text) {
		lexer = new OrinocoLexer(OrinocoReader.fromCharSequence(text), preProcessor);
	}

	private void lexerFromFile(@NotNull File f) throws FileNotFoundException {
		lexer = new OrinocoLexer(OrinocoReader.fromStream(new FileInputStream(f), StandardCharsets.UTF_8), preProcessor);
	}

	@Test
	public void noPreProcessing() {
		lexerFromText("text");
		lexer.start();

	}
}