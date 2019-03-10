package arma.orinocosqf;

import org.jetbrains.annotations.NotNull;

/**
 * A {@link OrinocoLexerStream} implementation that fully preprocesses tokens.
 *
 * @author K
 * @since 02/20/2019
 */
public class OrinocoPreProcessor implements OrinocoLexerStream {
	private OrinocoLexer lexer;

	public OrinocoPreProcessor() {

	}

	@Override
	public void setLexer(@NotNull OrinocoLexer lexer) {
		this.lexer = lexer;
	}

	@Override
	public boolean skipPreProcessing() {
		return false;
	}

	@Override
	public void acceptPreProcessorCommand(@NotNull PreProcessorCommand command, @NotNull char[] bufReadOnly, int offset, int bodyLength) {

	}

	@Override
	public void preProcessToken(@NotNull char[] bufReadOnly, int offset, int length) {

	}

	@Override
	public void acceptWhitespace(int originalOffset, int originalLength, int preprocessedOffset, int preprocessedLength) {

	}

	@Override
	public void acceptComment(int originalOffset, int originalLength, int preprocessedOffset, int preprocessedLength) {

	}

	@Override
	public void begin() {

	}

	@Override
	public void acceptCommand(int id, int preprocessedOffset, int originalOffset, int originalLength) {

	}

	@Override
	public void acceptLocalVariable(int id, int preprocessedOffset, int originalOffset, int originalLength) {

	}

	@Override
	public void acceptGlobalVariable(int id, int preprocessedOffset, int originalOffset, int originalLength) {

	}

	@Override
	public void acceptLiteral(@NotNull OrinocoLexerLiteralType type, @NotNull String token, int preprocessedOffset, int originalOffset,
							  int originalLength) {

	}

	@Override
	public void preProcessorTokenSkipped(@NotNull String token, int offset) {

	}

	@Override
	public void preProcessorCommandSkipped(@NotNull String command, int offset) {

	}

	@Override
	public void end() {

	}
}
