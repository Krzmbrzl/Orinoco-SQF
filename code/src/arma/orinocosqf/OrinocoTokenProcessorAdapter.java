package arma.orinocosqf;

import org.jetbrains.annotations.NotNull;

/**
 * An adapter implementation of a {@link OrinocoTokenProcessor} that does nothing by default. This class is intended to be extended if only
 * specific methods of a TokenProcessor are needed
 * 
 * @author Raven
 *
 */
public abstract class OrinocoTokenProcessorAdapter implements OrinocoTokenProcessor {

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

}
