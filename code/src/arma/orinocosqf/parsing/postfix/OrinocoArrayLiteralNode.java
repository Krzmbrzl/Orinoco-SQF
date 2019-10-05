package arma.orinocosqf.parsing.postfix;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author K
 * @since 7/28/19
 */
public class OrinocoArrayLiteralNode implements OrinocoNode {
	private final List<OrinocoArrayItemNode> nodes = new ArrayList<>();

	public OrinocoArrayLiteralNode() {
		nodes.add(new OrinocoArrayItemNode());
	}

	public boolean isEmptyArray() {
		return nodes.size() == 1 && nodes.get(0).getNodes().size() == 0;
	}

	@NotNull
	public List<OrinocoArrayItemNode> getItems() {
		return nodes;
	}

	@Override
	public boolean isScopeNode() {
		return false;
	}

	@Override
	public boolean isArrayNode() {
		return true;
	}

	@Override
	public boolean isCollectionNode() {
		return false;
	}

	@NotNull
	public OrinocoArrayItemNode getLastItem() {
		return nodes.get(nodes.size() - 1);
	}
}
