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
	private final OrinocoTokenProcessor processor;
	private final MacroSet macroSet = new MacroSet();

	public OrinocoPreProcessor(@NotNull OrinocoTokenProcessor p) {
		this.processor = p;
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
	public void begin() {

	}

	@Override
	public void acceptCommand(int id, int preprocessedOffset, int preprocessedLength, int originalOffset, int originalLength,
							  @NotNull OrinocoLexerContext ctx) {

	}

	@Override
	public void acceptLocalVariable(int id, int preprocessedOffset, int preprocessedLength, int originalOffset, int originalLength,
									@NotNull OrinocoLexerContext ctx) {

	}

	@Override
	public void acceptGlobalVariable(int id, int preprocessedOffset, int preprocessedLength, int originalOffset, int originalLength,
									 @NotNull OrinocoLexerContext ctx) {

	}

	@Override
	public void acceptLiteral(@NotNull OrinocoLexerLiteralType type, int preprocessedOffset, int preprocessedLength, int originalOffset,
							  int originalLength, @NotNull OrinocoLexerContext ctx) {

	}

	@Override
	public void preProcessorTokenSkipped(int offset, int length, @NotNull OrinocoLexerContext ctx) {

	}

	@Override
	public void preProcessorCommandSkipped(int offset, int length, @NotNull OrinocoLexerContext ctx) {

	}

	@Override
	public void end() {

	}

	@Override
	public void acceptWhitespace(int originalOffset, int originalLength, int preprocessedOffset, int preprocessedLength,
								 @NotNull OrinocoLexerContext ctx) {

	}

	@Override
	public void acceptComment(int originalOffset, int originalLength, int preprocessedOffset, int preprocessedLength,
							  @NotNull OrinocoLexerContext ctx) {

	}

	@Override
	public @NotNull MacroSet getMacroSet() {
		return macroSet;
	}


}
