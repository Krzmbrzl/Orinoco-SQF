import org.jetbrains.annotations.NotNull;

/**
 * A token processor that is designed to not need {@link OrinocoToken} instances.
 *
 * @author K
 * @since 02/20/2019
 */
public interface OrinocoTokenProcessor {
	/** Invoked once when the {@link OrinocoLexer} has begin lexing */
	void begin();

	/**
	 * Accept a command token
	 *
	 * @param id globally unique id of the command
	 * @param offset text offset of where the command was located
	 */
	void acceptCommand(int id, int offset);

	/**
	 * Accept a _localVariable token
	 *
	 * @param id globally unique id of the local variable
	 * @param offset text offset of where the local variable was located
	 */
	void acceptLocalVariable(int id, int offset);

	/**
	 * Accept a global variable token
	 *
	 * @param offset text offset of where the global variable was located
	 */
	void acceptGlobalVariable(int offset);

	/**
	 * Accepts literals
	 *
	 * @param type the type of the literal
	 * @param token the token of the literal
	 * @param offset the offset of the literal
	 */
	void acceptLiteral(@NotNull OrinocoLexerLiteralType type, @NotNull String token, int offset);

	/**
	 * Invoked when the lexer didn't lex a token and contains a macro usage
	 *
	 * @param token the text of the unprocessed token
	 * @param offset the offset in the lexer of where the token was found
	 */
	void preProcessorTokenSkipped(@NotNull String token, int offset);

	/**
	 * Invoked when a whole command is skipped. This method is only invoked if preprocessing is disabled.
	 *
	 * @param command the command and whole body (#command body?)
	 * @param offset text offset
	 * @see PreProcessorCommand
	 * @see OrinocoLexerStream#skipPreProcessing()
	 * @see OrinocoLexerStream#acceptPreProcessorCommand(PreProcessorCommand, char[], int, int)
	 */
	void preProcessorCommandSkipped(@NotNull String command, int offset);

	/** Invoked once when the {@link OrinocoLexer} has finished lexing */
	void end();
}
