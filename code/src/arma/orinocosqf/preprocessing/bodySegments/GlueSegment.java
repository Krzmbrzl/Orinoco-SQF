package arma.orinocosqf.preprocessing.bodySegments;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import arma.orinocosqf.preprocessing.PreProcessorMacro;

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

	@Override
	public boolean equals(Object o) {
		if (!super.equals(o)) {
			return false;
		}

		GlueSegment other = (GlueSegment) o;

		return leftEquals(other) && rightEquals(other);
	}

	/**
	 * Compares the {@link #left} field of the given GlueSegment with this one's
	 * 
	 * @param other The segment to compare to
	 * @return Whether the left-elements of the segments are equal
	 */
	protected boolean leftEquals(GlueSegment other) {
		if (other.left == null) {
			return this.left == null;
		} else {
			return other.left.equals(this.left);
		}
	}

	/**
	 * Compares the {@link #right} field of the given GlueSegment with this one's
	 * 
	 * @param other The segment to compare to
	 * @return Whether the right-elements of the segments are equal
	 */
	protected boolean rightEquals(GlueSegment other) {
		if (other.right == null) {
			return this.right == null;
		} else {
			return other.right.equals(this.right);
		}
	}
}
