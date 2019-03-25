package arma.orinocosqf.helpers;

import arma.orinocosqf.*;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * @author K
 * @since 3/25/19
 */
public class TestOrinocoPreProcessor extends OrinocoPreProcessor {
	private final Function<String, OrinocoReader> includeHandler;
	private OrinocoLexer lexer;

	public TestOrinocoPreProcessor(@NotNull OrinocoTokenProcessor p, @NotNull Function<String, OrinocoReader> includeHandler) {
		super(p);
		this.includeHandler = includeHandler;
	}

	@Override
	public void setLexer(@NotNull OrinocoLexer lexer) {
		this.lexer = lexer;
		super.setLexer(lexer);
	}

	@Override
	public void acceptPreProcessorCommand(@NotNull PreProcessorCommand command, @NotNull char[] bufReadOnly, int offset, int bodyLength) {
		if (command == PreProcessorCommand.Include) {
			lexer.acceptIncludedReader(includeHandler.apply(new String(bufReadOnly, offset, bodyLength)));
			return;
		}
		super.acceptPreProcessorCommand(command, bufReadOnly, offset, bodyLength);
	}
}
