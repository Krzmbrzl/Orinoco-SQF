package arma.orinocosqf;

import org.jetbrains.annotations.NotNull;

/**
 * @author K
 * @since 3/10/19
 */
public class OrinocoTokenProcessorWrapper implements OrinocoTokenProcessor {
	protected final OrinocoTokenProcessor wrappedProcessor;

	public OrinocoTokenProcessorWrapper(@NotNull OrinocoTokenProcessor processor) {
		this.wrappedProcessor = processor;
	}

	@Override
	public void begin() {
		wrappedProcessor.begin();
	}

	@Override
	public void acceptCommand(int id, int preprocessedOffset, int originalOffset, int originalLength) {
		wrappedProcessor.acceptCommand(id, preprocessedOffset, originalOffset, originalLength);
	}

	@Override
	public void acceptLocalVariable(int id, int preprocessedOffset, int originalOffset, int originalLength) {
		wrappedProcessor.acceptLocalVariable(id, preprocessedOffset, originalOffset, originalLength);
	}

	@Override
	public void acceptGlobalVariable(int id, int preprocessedOffset, int originalOffset, int originalLength) {
		wrappedProcessor.acceptGlobalVariable(id, preprocessedOffset, originalOffset, originalLength);
	}

	@Override
	public void acceptLiteral(@NotNull OrinocoLexerLiteralType type, @NotNull String token, int preprocessedOffset, int originalOffset,
							  int originalLength) {
		wrappedProcessor.acceptLiteral(type, token, preprocessedOffset, originalOffset, originalLength);
	}

	@Override
	public void preProcessorTokenSkipped(@NotNull String token, int offset) {
		wrappedProcessor.preProcessorTokenSkipped(token, offset);
	}

	@Override
	public void preProcessorCommandSkipped(@NotNull String command, int offset) {
		wrappedProcessor.preProcessorCommandSkipped(command, offset);
	}

	@Override
	public void end() {
		wrappedProcessor.end();
	}
}
