/**
 * @author K
 * @see OrinocoLexerStream
 * @since 02/21/2019
 */
public enum OrinocoTokenType {
	/** Commands, including operators like (){}[],-+; */
	Command(true),
	/** _localVariable */
	LocalVariable(true),
	/** globalVariable */
	GlobalVariable(true),
	/** {@link OrinocoLexerLiteralType} */
	Literal(false),

	UnPreProcessed(false),
	Whitespace(false);

	private final boolean idBased;

	OrinocoTokenType(boolean idBased) {
		this.idBased = idBased;
	}

	/** @return true if this token doesn't have text and is id based, or return false if the token is text based and doesn't have an id */
	public boolean isIdBased() {
		return idBased;
	}
}
