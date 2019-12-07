package arma.orinocosqf;

import arma.orinocosqf.lexer.OrinocoLexer;
import arma.orinocosqf.lexer.OrinocoLexerContext;
import org.jetbrains.annotations.NotNull;

/**
 * @author K
 * @since 07/28/2019
 */
public interface OrinocoTokenInstanceProcessor {
	default void acceptToken(@NotNull OrinocoToken token, @NotNull OrinocoLexerContext ctx) {
		switch ((OrinocoSQFTokenType) token.getTokenType()) {
			case Command: {
				acceptCommand(token, ctx);
				return;
			}
			case LiteralNumber: // fall
			case LiteralString: {
				acceptLiteral(token, ctx);
				return;
			}
			case LocalVariable: {
				acceptLocalVariable(token, ctx);
				return;
			}
			case GlobalVariable: {
				acceptGlobalVariable(token, ctx);
				return;
			}
			case Comment: //fall
			case Whitespace: {
				return;
			}
			case UnPreProcessed: {
				preProcessorTokenSkipped(token.getOriginalOffset(), token.getOriginalLength(), ctx);
				return;
			}
		}
	}

	/**
	 * Invoked once when the {@link OrinocoLexer} has begun lexing
	 *
	 * @param ctx The {@link OrinocoLexerContext} that can be used to process/interpret the lexed result
	 */
	void begin(@NotNull OrinocoLexerContext ctx);

	void acceptCommand(@NotNull OrinocoToken token, @NotNull OrinocoLexerContext ctx);

	/**
	 * Accept a _localVariable token
	 */
	void acceptLocalVariable(@NotNull OrinocoToken token, @NotNull OrinocoLexerContext ctx);

	/**
	 * Accept a global variable token
	 */
	void acceptGlobalVariable(@NotNull OrinocoToken token, @NotNull OrinocoLexerContext ctx);

	/**
	 * Accepts literals
	 */
	void acceptLiteral(@NotNull OrinocoToken token, @NotNull OrinocoLexerContext ctx);

	/**
	 * Invoked when a macro-token was encountered in the input. This method is only invoked if preprocessing is disabled<br>
	 *
	 * @see OrinocoTokenProcessor#preProcessorTokenSkipped(int, int, OrinocoLexerContext)
	 */
	void preProcessorTokenSkipped(int offset, int length, @NotNull OrinocoLexerContext ctx);

	/**
	 * Invoked when a whole preprocessor-command is skipped. This method is only invoked if preprocessing is disabled.<br>
	 *
	 * @see OrinocoTokenProcessor#preProcessorCommandSkipped(int, int, OrinocoLexerContext)
	 */
	void preProcessorCommandSkipped(int offset, int length, @NotNull OrinocoLexerContext ctx);

	/**
	 * Invoked once when the {@link OrinocoLexer} has finished lexing
	 *
	 * @param ctx The {@link OrinocoLexerContext} that can be used to process/interpret the lexed result
	 */
	void end(@NotNull OrinocoLexerContext ctx);

	void reset();

	class ToInstanceTranslator implements OrinocoTokenProcessor {

		private final OrinocoTokenInstanceProcessor p;
		private boolean captureLiteralValues;


		public ToInstanceTranslator(@NotNull OrinocoTokenInstanceProcessor p) {
			this.p = p;
		}

		public void setCaptureLiteralValues(boolean captureLiteralValues) {
			this.captureLiteralValues = captureLiteralValues;
		}

		@Override
		public void begin(@NotNull OrinocoLexerContext ctx) {
			p.begin(ctx);
		}

		@Override
		public void acceptCommand(int id, int preprocessedOffset, int preprocessedLength, int originalOffset, int originalLength, @NotNull OrinocoLexerContext ctx) {
			p.acceptCommand(new OrinocoToken(id, OrinocoSQFTokenType.Command, preprocessedOffset, preprocessedLength, originalOffset, originalLength), ctx);
		}

		@Override
		public void acceptLocalVariable(int id, int preprocessedOffset, int preprocessedLength, int originalOffset, int originalLength, @NotNull OrinocoLexerContext ctx) {
			p.acceptLocalVariable(new OrinocoToken(id, OrinocoSQFTokenType.LocalVariable, preprocessedLength, preprocessedLength, originalOffset, originalLength), ctx);
		}

		@Override
		public void acceptGlobalVariable(int id, int preprocessedOffset, int preprocessedLength, int originalOffset, int originalLength, @NotNull OrinocoLexerContext ctx) {
			p.acceptGlobalVariable(new OrinocoToken(id, OrinocoSQFTokenType.GlobalVariable, preprocessedOffset, preprocessedLength, originalOffset, originalLength), ctx);
		}

		@Override
		public void acceptLiteral(@NotNull OrinocoLiteralType type, int preprocessedOffset, int preprocessedLength, int originalOffset, int originalLength, @NotNull OrinocoLexerContext ctx) {
			String val = "";
			if (captureLiteralValues) {
				if (ctx.getTextBufferPreprocessed() != null) {
					val = ctx.getTextBufferPreprocessed().getText(preprocessedOffset, preprocessedLength);
				} else {
					throw new IllegalStateException("Trying to capture literal value, but text buffer is null");
				}
			}
			OrinocoTokenType ott = OrinocoSQFTokenType.LiteralNumber;
			if (type == OrinocoLiteralType.String) {
				ott = OrinocoSQFTokenType.LiteralString;
			}
			p.acceptLiteral(new OrinocoToken(val, ott, preprocessedOffset, preprocessedLength, originalOffset, originalLength), ctx);
		}

		@Override
		public void preProcessorTokenSkipped(int offset, int length, @NotNull OrinocoLexerContext ctx) {
			p.preProcessorTokenSkipped(offset, length, ctx);
		}

		@Override
		public void preProcessorCommandSkipped(int offset, int length, @NotNull OrinocoLexerContext ctx) {
			p.preProcessorCommandSkipped(offset, length, ctx);
		}

		@Override
		public void end(@NotNull OrinocoLexerContext ctx) {
			p.end(ctx);
		}

		@Override
		public void reset() {
			p.reset();
		}
	}
}
