package arma.orinocosqf;

import org.jetbrains.annotations.NotNull;

import arma.orinocosqf.preprocessing.MacroSet;
import arma.orinocosqf.preprocessing.PreProcessorCommand;

/**
 * This interface describes a stream-object that will process everything {@link OrinocoLexer} lexes. Additionally it is also responsible for
 * providing preprocessing-support.
 *
 * @author K
 * @since 02/20/2019
 */
public interface OrinocoLexerStream extends OrinocoTokenProcessor {
	/**
	 * Sets the lexer for this lexer stream. When a {@link OrinocoLexer} is constructed, this method will be invoked automatically by the
	 * {@link OrinocoLexer}.
	 */
	void setLexer(@NotNull OrinocoLexer lexer);

	/**
	 * This method is invoked once at the beginning of each lex. If true,
	 * {@link #acceptPreProcessorCommand(PreProcessorCommand, char[], int, int)} will never be invoked. Also, if true,
	 * {@link #preProcessorTokenSkipped(String, int)} will be invoked for each whitespace separated "word"/token that wasn't preprocessed.
	 * Also, each command will be sent to {@link #preProcessorCommandSkipped(String, int)}
	 *
	 * @return true if skip preprocessing, or false to do preprocessing
	 */
	boolean skipPreProcessing();

	/**
	 * Accepts a whole block of text that contains the whole preprocessor command and the body of the command (if applicable). Here is the
	 * format: <b>#commandName body?</b>. The ? means optional.
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
	 * @param originalOffset text offset at which the whitespace begins in the original input
	 * @param originalLength length of the whitespace area in the original input
	 * @param preprocessedOffset text offset at which the whitespace begins in the preprocessed input. <b>This offset should be used when
	 *        emitting (error) messages.</b>
	 * @param preprocessedLength length of the whitespace area in the preprocessed input. If the whitespace was not inserted in the input by
	 *        preprocessing, this length is equal to <code>originalLenght</code>. However if it was introduced by preprocessing, this length
	 *        will be the length of the preprocessor-token that was expanded to insert this whitespace. <b>This length should be used when
	 *        emitting (error) messages.</b>
	 * @param ctx The {@link OrinocoLexerContext} that can be used to process/interpret the lexed result
	 */
	void acceptWhitespace(int originalOffset, int originalLength, int preprocessedOffset, int preprocessedLength,
						  @NotNull OrinocoLexerContext ctx);

	/**
	 * Used to notify the stream that a comment (either single- or multiline) has been lexed
	 *
	 * @param originalOffset text offset of where the comment begins in the original input
	 * @param originalLength length of the comment in the original input
	 * @param preprocessedOffset text offset at which the comment begins in the preprocessed input. <b>This offset should be used when
	 *        emitting (error) messages.</b>
	 * @param preprocessedLength length of the commentin the preprocessed input. If the comment was not inserted in the input by
	 *        preprocessing, this length is equal to <code>originalLenght</code>. However if it was introduced by preprocessing, this length
	 *        will be the length of the preprocessor-token that was expanded to insert this whitespace. <b>This length should be used when
	 *        emitting (error) messages.</b><br>
	 *        Note that a comment can only be inserted by preprocessing if the preprocessor is instructed to keep comments.
	 * @param ctx The {@link OrinocoLexerContext} that can be used to process/interpret the lexed result
	 * @param newlineCount The amount of newlines in this comment
	 */
	void acceptComment(int originalOffset, int originalLength, int preprocessedOffset, int preprocessedLength,
					   @NotNull OrinocoLexerContext ctx, int newlineCount);

	@NotNull
	MacroSet getMacroSet();
}
