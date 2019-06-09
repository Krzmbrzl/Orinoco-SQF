package arma.orinocosqf.syntax;

import arma.orinocosqf.util.MemCompact;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Kayler
 * @since 06/11/2016
 */
public class SQFCommandSyntax implements MemCompact, CommandSyntax {
	private final ReturnValueHolder returnValue;
	private final Param leftParam;
	private final Param rightParam;

	public SQFCommandSyntax(@Nullable Param leftParam, @Nullable Param rightParam, @NotNull ReturnValueHolder returnValue) {
		this.leftParam = leftParam;
		this.rightParam = rightParam;
		this.returnValue = returnValue;
	}

	@NotNull
	public static List<Param> params(@NotNull Param... allParams) {
		List<Param> params = new ArrayList<>(allParams.length);
		Collections.addAll(params, allParams);
		return params;
	}

	@Override
	@NotNull
	public ReturnValueHolder getReturnValue() {
		return returnValue;
	}

	@Override
	@Nullable
	public Param getLeftParam() {
		return leftParam;
	}

	@Override
	@Nullable
	public Param getRightParam() {
		return rightParam;
	}

	/**
	 * Will traverse all parameters (both left and right). Note that this will also fully traverse the elements in arrays that are
	 * parameters.
	 *
	 * @return an iterable that will iterate the parameters in order.
	 */
	@Override
	@NotNull
	public Iterable<Param> getAllParams() {
		List<Param> list = new LinkedList<>();
		if (leftParam != null) {
			addAllParamsFor(list, leftParam);
		}
		if (rightParam != null) {
			addAllParamsFor(list, rightParam);
		}

		return list;
	}

	private void addAllParamsFor(@NotNull List<Param> params, @NotNull Param param) {
		if (param instanceof ArrayParam) {
			ArrayParam arrayParam = (ArrayParam) param;
			for (Param subParam : arrayParam.getParams()) {
				if (subParam instanceof ArrayParam) {
					addAllParamsFor(params, subParam);
				} else {
					params.add(subParam);
				}
			}
		} else {
			params.add(param);
		}

	}

	@NotNull
	public Iterable<ArrayParam> getAllArrayParams() {
		List<ArrayParam> list = new LinkedList<>();
		if (leftParam != null && leftParam instanceof ArrayParam) {
			addAllArrayParamsFor(list, (ArrayParam) leftParam);
		}
		if (rightParam != null && rightParam instanceof ArrayParam) {
			addAllArrayParamsFor(list, (ArrayParam) rightParam);
		}
		return list;
	}

	private void addAllArrayParamsFor(@NotNull List<ArrayParam> params, @NotNull ArrayParam param) {
		for (Param subParam : param.getParams()) {
			if (subParam instanceof ArrayParam) {
				ArrayParam subArrayParam = (ArrayParam) subParam;
				params.add(subArrayParam);
				addAllArrayParamsFor(params, subArrayParam);
			}
		}
	}

	@Override
	public void memCompact() {
		if (leftParam != null) {
			leftParam.memCompact();
		}
		if (rightParam != null) {
			rightParam.memCompact();
		}
		returnValue.memCompact();
	}
}
