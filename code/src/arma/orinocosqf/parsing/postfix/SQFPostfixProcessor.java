package arma.orinocosqf.parsing.postfix;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author K
 * @since 10/5/19
 */
public class SQFPostfixProcessor {
	public void acceptStatement(@NotNull SQFInfixToPostfixPrecedenceProcessor processor, @NotNull List<OrinocoNode> nodes) {
		for (OrinocoNode node : nodes) {
			if (node instanceof DelayEvalOrinocoNode) {//[]
				processor.acceptDelayEvalNode((DelayEvalOrinocoNode) node);
			}
		}
		
	}

	public void beginCodeBlock() {

	}

	public void endCodeBlock() {

	}

	public void beginArray() {

	}

	public void endArray() {

	}

	public void acceptArrayItem(@NotNull SQFInfixToPostfixPrecedenceProcessor processor, @NotNull List<OrinocoNode> nodes) {

	}
}
