package arma.orinocosqf.lexer;

import arma.orinocosqf.IdTransformer;
import arma.orinocosqf.Resettable;
import arma.orinocosqf.exceptions.UnknownIdException;
import arma.orinocosqf.sqf.SQFVariable;
import arma.orinocosqf.util.CaseInsensitiveHashSet;

import org.jetbrains.annotations.NotNull;


/**
 * @author K
 * @since 5/12/19
 */
public abstract class VariableIdTransformer implements IdTransformer<String>, Resettable {
	private CaseInsensitiveHashSet<SQFVariable> localVars;
	private CaseInsensitiveHashSet<SQFVariable> globalVars;

	public VariableIdTransformer(@NotNull CaseInsensitiveHashSet<SQFVariable> localVars,
			@NotNull CaseInsensitiveHashSet<SQFVariable> globalVars) {
		this.localVars = localVars;
		this.globalVars = globalVars;
	}

	@NotNull
	@Override
	public String fromId(int id) throws UnknownIdException {
		for (SQFVariable var : localVars) {
			if (var.getId() == id) {
				return var.getName();
			}
		}
		for (SQFVariable var : globalVars) {
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

	@Override
	public void reset() {
		// Clear local variables but keep globals
		localVars.clear();
	}

	/**
	 * Sets the local variable set for this transformer
	 * 
	 * @param vars The new set of local variables to use
	 */
	protected void setLocalVars(@NotNull CaseInsensitiveHashSet<SQFVariable> vars) {
		this.localVars = vars;
	}

	/**
	 * Sets the global variable set for this transformer
	 * 
	 * @param vars The new set of global variables to use
	 */
	protected void setGlobalVars(@NotNull CaseInsensitiveHashSet<SQFVariable> vars) {
		this.globalVars = vars;
	}

	protected abstract int getNextId(@NotNull String varName);
}
