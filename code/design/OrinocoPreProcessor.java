import org.jetbrains.annotations.NotNull;

/**
 * A {@link OrinocoLexerStream} implementation that fully preprocesses tokens.
 *
 * @author K
 * @since 02/20/2019
 */
public abstract /*todo: remove abstract*/ class OrinocoPreProcessor implements OrinocoLexerStream {
	private final OrinocoLexer lexer;

	public OrinocoPreProcessor(@NotNull OrinocoLexer lexer) {
		this.lexer = lexer;
	}
}
