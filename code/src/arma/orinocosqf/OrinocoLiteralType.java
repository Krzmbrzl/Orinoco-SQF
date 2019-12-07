package arma.orinocosqf;

import arma.orinocosqf.lexer.OrinocoLexer;

/**
 * This interface describes objects representing literal types inside the
 * {@link OrinocoLexer}.<br>
 *
 * @author Raven
 *
 */
public enum OrinocoLiteralType {
	/**
	 * The type identifying a number-literal
	 */
	Number,
	/**
	 * The type identifying a String-literal
	 */
	String;
}
