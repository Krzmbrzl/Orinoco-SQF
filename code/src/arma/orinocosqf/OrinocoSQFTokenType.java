package arma.orinocosqf;

/**
 * @author K
 * @see OrinocoLexerStream
 * @since 02/21/2019
 */
public enum OrinocoSQFTokenType implements OrinocoTokenType {
	/**
	 * This type includes all SQF-commands as well as all (structural) operators
	 * that exist in the SQF-language: <code>(){}89,-*+/;=</code>
	 */
	Command(true),
	/**
	 * This type represents local variables
	 */
	LocalVariable(true),
	/**
	 * This type represents global variables
	 */
	GlobalVariable(true),
	/**
	 * This type represents all available literal-types available in SQF
	 *
	 * @see OrinocoLexerLiteralType
	 * @see OrinocoLexerSQFLiteralType
	 */
	Literal(false),
	/**
	 * This type represents input that needs preprocessing but hasn't been
	 * preprocessed yet
	 */
	UnPreProcessed(false),
	/**
	 * This type represents Whitespace
	 */
	Whitespace(false),
	/**
	 * This type represents comments (either single- or multiline)
	 */
	Comment(false);

	private final boolean idBased;

	OrinocoSQFTokenType(boolean idBased) {
		this.idBased = idBased;
	}

	@Override
	public boolean isIdBased() {
		return idBased;
	}
}
