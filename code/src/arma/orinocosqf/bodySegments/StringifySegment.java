package arma.orinocosqf.bodySegments;

import java.util.List;

import org.jetbrains.annotations.NotNull;

/**
 * A segment which has a # in it ("#define MACRO #segment"). When {@link #applyArguments(List)} is invoked, it returns the segment following
 * the # with quotes wrapped around it
 */
public class StringifySegment extends BodySegment {

	protected final BodySegment segment;

	/**
	 * @param segment the segment following the #
	 */
	public StringifySegment(@NotNull BodySegment segment) {
		this.segment = segment;
	}

	@NotNull
	@Override
	public CharSequence applyArguments(@NotNull List<CharSequence> args) {
		return "\"" + segment.applyArguments(args) + "\"";
	}

	@Override
	@NotNull
	public CharSequence toStringNoPreProcessing() {
		return segment.toStringNoPreProcessing();
	}

	@Override
	public String toString() {
		return "StringifySegment{" + segment + '}';
	}
}
