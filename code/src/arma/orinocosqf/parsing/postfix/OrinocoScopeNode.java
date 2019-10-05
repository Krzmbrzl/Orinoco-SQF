package arma.orinocosqf.parsing.postfix;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author K
 * @since 7/28/19
 */
public class OrinocoScopeNode implements OrinocoNode {
	private final List<OrinocoStatementNode> nodes = new ArrayList<>();

	public OrinocoScopeNode() {
		nodes.add(new OrinocoStatementNode());
	}

	public boolean isEmptyScope() {
		return nodes.size() == 1 && nodes.get(0).getNodes().isEmpty();
	}

	@NotNull
	public List<OrinocoStatementNode> getStatements() {
		return nodes;
	}

	@Override
	public boolean isScopeNode() {
		return true;
	}

	@Override
	public boolean isArrayNode() {
		return false;
	}

	@Override
	public boolean isCollectionNode() {
		return false;
	}

	@NotNull
	public OrinocoStatementNode getLastStatement() {
		return nodes.get(nodes.size() - 1);
	}
}
