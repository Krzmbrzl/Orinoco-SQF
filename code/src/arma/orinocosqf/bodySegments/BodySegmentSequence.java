package arma.orinocosqf.bodySegments;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import arma.orinocosqf.PreProcessorMacro;

/**
 * A type of {@link BodySegment} in that it contains a sequence/list of segments. When {@link #applyArguments(List)} is invoked, each
 * {@link BodySegment#applyArguments(List)} is invoked and appended one after the other with no delimeter.
 */
public class BodySegmentSequence extends BodySegment {

	protected final List<BodySegment> segments;

	/**
	 * @param segments the list of segments
	 */
	public BodySegmentSequence(@NotNull List<BodySegment> segments) {
		this.segments = segments;
	}

	@Override
	public void setOwner(@NotNull PreProcessorMacro macro) {
		super.setOwner(macro);

		// propagate owner
		for (BodySegment current : segments) {
			current.setOwner(macro);
		}
	}

	@NotNull
	@Override
	public CharSequence applyArguments(@NotNull List<CharSequence> args) {
		StringBuilder sb = new StringBuilder();
		for (BodySegment bs : segments) {
			sb.append(bs.applyArguments(args));
		}
		return sb;
	}

	@Override
	@NotNull
	public CharSequence toStringNoPreProcessing() {
		StringBuilder sb = new StringBuilder();
		for (BodySegment bs : segments) {
			sb.append(bs.toStringNoPreProcessing());
		}
		return sb;
	}

	@Override
	public String toString() {
		return "BodySegmentSequence{" + segments + '}';
	}
}
