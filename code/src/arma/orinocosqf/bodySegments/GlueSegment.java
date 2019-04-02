package arma.orinocosqf.bodySegments;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import arma.orinocosqf.PreProcessorMacro;

/**
 * A segment used for ## inside the macro body (#define MACRO leftSegment##rightSegment). When {@link #applyArguments(List)} is invoked, it
 * concatenates the left and right {@link BodySegment}s and returns the result
 */
public class GlueSegment extends BodySegment {

	protected final BodySegment left;
	protected final BodySegment right;

	public GlueSegment(@NotNull BodySegment left, @NotNull BodySegment right) {
		this.left = left;
		this.right = right;
	}

	@Override
	public void setOwner(@NotNull PreProcessorMacro macro) {
		super.setOwner(macro);

		left.setOwner(macro);
		right.setOwner(macro);
	}

	@NotNull
	@Override
	public CharSequence applyArguments(@NotNull List<CharSequence> args) {
		StringBuilder sb = new StringBuilder();
		sb.append(left.applyArguments(args));
		sb.append(right.applyArguments(args));
		return sb;
	}

	@Override
	@NotNull
	public CharSequence toStringNoPreProcessing() {
		StringBuilder sb = new StringBuilder();
		sb.append(left.toStringNoPreProcessing());
		sb.append("##");
		sb.append(right.toStringNoPreProcessing());
		return sb;
	}

	@Override
	public String toString() {
		return "GlueSegment{left=" + left + ",right=" + right + '}';
	}
}
