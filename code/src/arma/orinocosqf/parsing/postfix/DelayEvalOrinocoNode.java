package arma.orinocosqf.parsing.postfix;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author K
 * @since 10/5/19
 */
public class DelayEvalOrinocoNode extends OrinocoNode {
	private final List<List<OrinocoNode>> children = new ArrayList<>();
	private final OrinocoNode node;

	public DelayEvalOrinocoNode(@NotNull OrinocoNode node) {
		super(node.getFlag());
		this.node = node;
	}

	@NotNull
	public OrinocoNode getNode() {
		return node;
	}

	@NotNull
	public List<OrinocoNode> getLastItemList() {
		if (children.isEmpty()) {
			children.add(new ArrayList<>());
		}
		return children.get(children.size() - 1);
	}

	@NotNull
	public List<List<OrinocoNode>> getItems() {
		return children;
	}
}
