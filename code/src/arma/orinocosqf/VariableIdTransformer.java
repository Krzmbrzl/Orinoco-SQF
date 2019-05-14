package arma.orinocosqf;

import arma.orinocosqf.exceptions.UnknownIdException;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * @author K
 * @since 5/12/19
 */
public class VariableIdTransformer implements IdTransformer<String> {
	private final CaseInsensitiveHashSet<SQFVariable> localVars;
	private final CaseInsensitiveHashSet<SQFVariable> globalVars;
	private final Function<String, Integer> nextIdFunc;

	public VariableIdTransformer(@NotNull CaseInsensitiveHashSet<SQFVariable> localVars,
								 @NotNull CaseInsensitiveHashSet<SQFVariable> globalVars,
								 @NotNull Function<String, Integer> nextIdFunc) {
		this.localVars = localVars;
		this.globalVars = globalVars;
		this.nextIdFunc = nextIdFunc;
	}

	@NotNull
	@Override
	public String fromId(int id) throws UnknownIdException {
		for (SQFVariable var : new DoubleIterable<>(localVars, globalVars)) {
			if (var.getId() == id) {
				return var.getName();
			}
		}
		throw new UnknownIdException(id + "");
	}

	@Override
	public int toId(@NotNull String value) throws UnknownIdException {
		final boolean local = value.charAt(0) == '_';
		CaseInsensitiveHashSet<SQFVariable> set = local ? localVars : globalVars;
		SQFVariable var = set.getKeyForCharSequence(value);
		if (var != null) {
			return var.getId();
		}
		var = new SQFVariable(value, nextIdFunc.apply(value));
		if (local) {
			localVars.put(var);
		} else {
			globalVars.put(var);
		}
		return var.getId();
	}
}
