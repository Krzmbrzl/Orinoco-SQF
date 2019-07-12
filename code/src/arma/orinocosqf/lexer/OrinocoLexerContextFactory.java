package arma.orinocosqf.lexer;

import org.jetbrains.annotations.NotNull;

/**
 * This interface describes a context object factory that will be used to instantiate context objects with the desired properties
 * 
 * @author Raven
 *
 */
public interface OrinocoLexerContextFactory {

	/**
	 * Produces an {@link OrinocoLexerContext} with the desired property
	 * 
	 * @param lexer The {@link OrinocoLexer} to produce this context for
	 * @param textBufferingEnabled Whether the produced context should have text-buffering enabled or not
	 * @return The produced context object
	 */
	public OrinocoLexerContext produce(@NotNull OrinocoLexer lexer, boolean textBufferingEnabled);
}
