package arma.orinocosqf.bodySegments;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import arma.orinocosqf.PreProcessorMacro;

/**
 * A segment used for ## inside the macro body (#define MACRO leftSegment##rightSegment). When {@link #applyArguments(List)} is invoked, it
 * concatenates the left and right {@link BodySegment}s and returns the result
 */
public class GlueSegment extends BodySegment {

	protected final BodySegment left;
	protected final BodySegment right;

	/**
	 * @param left The segment to the left of this GlueSegment or <code>null</code> if there's none
	 * @param right The segment to the right of this GlueSegment or <code>null</code> if there's none
	 */
	public GlueSegment(@Nullable BodySegment left, @Nullable BodySegment right) {
		this.left = left;
		this.right = right;
	}

	@Override
	public void setOwner(@NotNull PreProcessorMacro macro) {
		super.setOwner(macro);

		if (left != null) {
			left.setOwner(macro);
		}
		if (right != null) {
			right.setOwner(macro);
		}
	}

	@NotNull
	@Override
	public CharSequence applyArguments(@NotNull List<CharSequence> args) {
		StringBuilder sb = new StringBuilder();
		if (left != null) {
			sb.append(left.applyArguments(args));
		}
		if (right != null) {
			sb.append(right.applyArguments(args));
		}
		return sb;
	}

	@Override
	@NotNull
	public CharSequence toStringNoPreProcessing() {
		StringBuilder sb = new StringBuilder();
		if (left != null) {
			sb.append(left.toStringNoPreProcessing());
		}
		sb.append("##");
		if (right != null) {
			sb.append(right.toStringNoPreProcessing());
		}
		return sb;
	}

	@Override
	public String toString() {
		return "GlueSegment{left=" + (left != null ? left : "Null") + ",right=" + (right != null ? right : "Null") + '}';
	}
}
