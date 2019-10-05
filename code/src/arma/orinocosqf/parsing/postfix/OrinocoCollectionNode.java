package arma.orinocosqf.parsing.postfix;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author K
 * @since 7/28/19
 */
public abstract class OrinocoCollectionNode implements OrinocoNode {
	@NotNull
	public abstract List<OrinocoNode> getNodes();

	@Override
	public boolean isCollectionNode() {
		return true;
	}
}
