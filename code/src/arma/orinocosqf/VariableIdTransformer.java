package arma.orinocosqf;

import arma.orinocosqf.exceptions.UnknownIdException;
import org.jetbrains.annotations.NotNull;


/**
 * @author K
 * @since 5/12/19
 */
public abstract class VariableIdTransformer implements IdTransformer<String> {
	private final CaseInsensitiveHashSet<SQFVariable> localVars;
	private final CaseInsensitiveHashSet<SQFVariable> globalVars;

	public VariableIdTransformer(@NotNull CaseInsensitiveHashSet<SQFVariable> localVars,
								 @NotNull CaseInsensitiveHashSet<SQFVariable> globalVars) {
		this.localVars = localVars;
		this.globalVars = globalVars;
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
		var = new SQFVariable(value, getNextId(value));
		if (local) {
			localVars.put(var);
		} else {
			globalVars.put(var);
		}
		return var.getId();
	}

	protected abstract int getNextId(@NotNull String varName);
}
