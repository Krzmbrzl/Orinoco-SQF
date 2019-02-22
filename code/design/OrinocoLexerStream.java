import org.jetbrains.annotations.NotNull;

/**
 * @author K
 * @since 02/20/2019
 */
public interface OrinocoLexerStream extends OrinocoTokenProcessor {
	/**
	 * This method is invoked once at the beginning of each lex.
	 * If true, {@link #acceptPreProcessorCommand(PreProcessorCommand, char[], int, int)} will never be invoked.
	 * Also, if true, {@link #preProcessorTokenSkipped(String, int)} will be invoked
	 * for each whitespace separated "word"/token that wasn't preprocessed.
	 * Also, each command will be sent to {@link #preProcessorCommandSkipped(String, int)}
	 *
	 * @return true if skip preprocessing, or false to do preprocessing
	 */
	boolean skipPreProcessing();

	/**
	 * Accepts a whole block of text that contains the whole preprocessor command and the body of the command (if applicable).
	 * Here is the format: <b>#commandName body?</b>. The ? means optional.
	 *
	 * @param command the command matched
	 * @param bufReadOnly the read-only char[] buffer that contains the whole command and body
	 * @param offset the offset in the buffer where the command begins, starting with #
	 * @param bodyLength length of the command and body inside the char[] buffer
	 */
	void acceptPreProcessorCommand(@NotNull PreProcessorCommand command, @NotNull char[] bufReadOnly, int offset, int bodyLength);

	/**
	 * A single token ready to be preprocessed (no whitespace will be submitted)
	 *
	 * @param bufReadOnly the read only char[] buffer that contains the token
	 * @param offset the offset where the token begins in the char[] buffer
	 * @param length length of the token in the buffer
	 */
	void preProcessToken(@NotNull char[] bufReadOnly, int offset, int length);

	/**
	 * Used to notify the stream that whitespace was lexed
	 *
	 * @param offset text offset of where whitespace began
	 * @param length length of the whitespace
	 */
	void acceptWhitespace(int offset, int length);
}
