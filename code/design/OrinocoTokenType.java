/**
 * This interface describes objects representing a token-type inside Orinoco. It
 * forces the respective ojects to implement a way of checking whether the token
 * will be text- or ID-based (that is it does not have a text set directly onto
 * it. The corresponding text can be found when looking up the respective
 * ID).<br>
 * 
 * It is intended to be implemented by <b>singleton-classes</b> (e.g. enums) so
 * that equality can be checked via the <code>==</code> operator.
 * 
 * @author Raven
 *
 */
public interface OrinocoTokenType {
	/**
	 * @return true if this token doesn't have text and is id based, or return false
	 *         if the token is text based and doesn't have an id
	 */
	public boolean isIdBased();
}
