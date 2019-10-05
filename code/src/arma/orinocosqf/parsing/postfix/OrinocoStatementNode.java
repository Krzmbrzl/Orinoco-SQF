package arma.orinocosqf.parsing.postfix;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author K
 * @since 7/28/19
 */
public class OrinocoStatementNode extends OrinocoCollectionNode {
	private final List<OrinocoNode> nodes = new ArrayList<>();

	@Override
	@NotNull
	public List<OrinocoNode> getNodes() {
		return nodes;
	}

	@Override
	public boolean isScopeNode() {
		return false;
	}

	@Override
	public boolean isArrayNode() {
		return false;
	}
}
