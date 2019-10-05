package arma.orinocosqf.parsing.postfix;

/**
 * @author K
 * @since 7/28/19
 */
public interface OrinocoNode {

	boolean isScopeNode();

	boolean isArrayNode();

	boolean isCollectionNode();

}
