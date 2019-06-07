package arma.orinocosqf.syntax;

import arma.orinocosqf.type.ValueType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kayler
 * @since 02/24/2017
 */
public class ArrayReturnValueHolder extends ReturnValueHolder implements ArrayValueHolder {
	private List<ReturnValueHolder> values;
	private boolean unbounded;

	public ArrayReturnValueHolder(@NotNull String description, @NotNull List<ReturnValueHolder> values, boolean unbounded) {
		super(ValueType.BaseType.ARRAY, description);
		this.values = values;
		this.unbounded = unbounded;
	}

	public boolean hasUnboundedParams() {
		return unbounded;
	}

	@NotNull
	public List<ReturnValueHolder> getValueHolders() {
		return values;
	}

	@Override
	public void memCompact() {
		super.memCompact();
		if (values instanceof ArrayList) {
			((ArrayList<ReturnValueHolder>) values).trimToSize();
		}
	}

	@NotNull
	@Override
	public ValueType getType() {
		//cannot inherit from ArrayValueHolder implementation because we are extending ReturnValueHolder
		return ArrayValueHolder.createType(this);
	}

}
