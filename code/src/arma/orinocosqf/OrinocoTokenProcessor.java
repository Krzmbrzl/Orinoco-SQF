package arma.orinocosqf;

import arma.orinocosqf.lexer.OrinocoLexer;
import arma.orinocosqf.lexer.OrinocoLexerContext;
import arma.orinocosqf.lexer.OrinocoLexerLiteralType;
import arma.orinocosqf.lexer.OrinocoTokenDelegator;
import arma.orinocosqf.preprocessing.PreProcessorCommand;
import org.jetbrains.annotations.NotNull;

/**
 * A token processor that is designed to not need {@link OrinocoToken} instances.
 *
 * @author K
 * @since 02/20/2019
 */
public interface OrinocoTokenProcessor {
	/**
	 * Invoked once when the {@link OrinocoLexer} has begun lexing
	 * 
	 * @param ctx The {@link OrinocoLexerContext} that can be used to process/interpret the lexed result
	 */
	void begin(@NotNull OrinocoLexerContext ctx);

	/**
	 * Accept a command token
	 *
	 * @param id globally unique id of the command
	 * @param preprocessedOffset text offset of where the command was located in the preprocessed input
	 * @param preprocessedLength length of the command in the preprocessed input
	 * @param originalOffset text offset of where the command was located in the original, unpreprocessed input. <b>This offset should be
	 *        used when emitting (error) messages</b>
	 * @param originalLength the length of the token in the original input that this command is part of. If the command was not inserted in
	 *        the input by preprocessing, this length is equal to the command-string's length. However if it was introduced by
	 *        preprocessing, this length will be the length of the preprocessor-token that was expanded to insert this command. <b>This
	 *        length should be used when emitting (error) messages.</b>
	 * @param ctx The {@link OrinocoLexerContext} that can be used to process/interpret the lexed result
	 */
	void acceptCommand(int id, int preprocessedOffset, int preprocessedLength, int originalOffset, int originalLength,
			@NotNull OrinocoLexerContext ctx);

	/**
	 * Accept a _localVariable token
	 *
	 * @param id globally unique id of the local variable
	 * @param preprocessedOffset text offset of where the local variable was located in the preprocessed input
	 * @param preprocessedLength length of the variable in the preprocessed input
	 * @param originalOffset text offset of where the local variable was located in the original, unpreprocessed input. <b>This offset
	 *        should be used when emitting (error) messages</b>
	 * @param originalLength the length of the token in the original input that this local variable is part of. If the variable was not
	 *        inserted in the input by preprocessing, this length is equal to the variable-string's length. However if it was introduced by
	 *        preprocessing, this length will be the length of the preprocessor-token that was expanded to insert this local variable.
	 *        <b>This length should be used when emitting (error) messages.</b>
	 * @param ctx The {@link OrinocoLexerContext} that can be used to process/interpret the lexed result
	 */
	void acceptLocalVariable(int id, int preprocessedOffset, int preprocessedLength, int originalOffset, int originalLength,
			@NotNull OrinocoLexerContext ctx);

	/**
	 * Accept a global variable token
	 *
	 * @param id globally unique id of the global variable
	 * @param preprocessedOffset text offset of where the global variable was located in the preprocessed input
	 * @param preprocessedLength length of the variable in the preprocessed input
	 * @param originalOffset text offset of where the global variable was located in the original, unpreprocessed input. <b>This offset
	 *        should be used when emitting (error) messages</b>
	 * @param originalLength the length of the token in the original input that this global variable is part of. If the variable was not
	 *        inserted in the input by preprocessing, this length is equal to the variable-string's length. However if it was introduced by
	 *        preprocessing, this length will be the length of the preprocessor-token that was expanded to insert this command. <b>This
	 *        length should be used when emitting (error) messages.</b>
	 * @param ctx The {@link OrinocoLexerContext} that can be used to process/interpret the lexed result
	 */
	void acceptGlobalVariable(int id, int preprocessedOffset, int preprocessedLength, int originalOffset, int originalLength,
			@NotNull OrinocoLexerContext ctx);

	/**
	 * Accepts literals
	 *
	 * @param type the type of the literal
	 * @param preprocessedOffset the offset of the literal in the preprocessed input
	 * @param preprocessedLength length of the literal in the preprocessed input
	 * @param originalOffset text offset of where the literal was located in the original, unpreprocessed input. <b>This offset should be
	 *        used when emitting (error) messages</b>
	 * @param originalLength the length of the token in the original input that this literal is part of. If the literal was not inserted in
	 *        the input by preprocessing, this length is equal to <code>token.length()</code>. However if it was introduced by
	 *        preprocessing, this length will be the length of the preprocessor-token that was expanded to insert this literal. <b>This
	 *        length should be used when emitting (error) messages.</b>
	 * @param ctx The {@link OrinocoLexerContext} that can be used to process/interpret the lexed result
	 */
	void acceptLiteral(@NotNull OrinocoLexerLiteralType type, int preprocessedOffset, int preprocessedLength, int originalOffset,
			int originalLength, @NotNull OrinocoLexerContext ctx);

	/**
	 * Invoked when a macro-token was encountered in the input. This method is only invoked if preprocessing is disabled<br>
	 * <br>
	 * For creating (error) messages <code>offset</code> and <code>token.length()</code> can be used as those correspond to the ones in the
	 * original input (preprocessing is disabled if this method is being called).
	 *
	 * @param offset the offset in the lexer of where the token was found.
	 * @param length The length of the macro-token (including arguments if present)
	 * @param ctx The {@link OrinocoLexerContext} that can be used to process/interpret the lexed result
	 */
	void preProcessorTokenSkipped(int offset, int length, @NotNull OrinocoLexerContext ctx);

	/**
	 * Invoked when a whole preprocessor-command is skipped. This method is only invoked if preprocessing is disabled.<br>
	 * <br>
	 * For creating (error) messages <code>offset</code> and <code>command.length()</code> can be used as those correspond to the ones in
	 * the original input (preprocessing is disabled if this method is being called).
	 *
	 * @param offset text offset in the preprocessed input
	 * @param length The length of the preprocessor command token
	 * @param ctx The {@link OrinocoLexerContext} that can be used to process/interpret the lexed result
	 * @see PreProcessorCommand
	 * @see OrinocoTokenDelegator#skipPreProcessing()
	 * @see OrinocoTokenDelegator#acceptPreProcessorCommand(PreProcessorCommand, char[], int, int)
	 */
	void preProcessorCommandSkipped(int offset, int length, @NotNull OrinocoLexerContext ctx);

	/**
	 * Invoked once when the {@link OrinocoLexer} has finished lexing
	 * 
	 * @param ctx The {@link OrinocoLexerContext} that can be used to process/interpret the lexed result
	 */
	void end(@NotNull OrinocoLexerContext ctx);
}
