package arma.orinocosqf.preprocessing.bodySegments;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import arma.orinocosqf.exceptions.NoMacroArgumentsGivenException;
import arma.orinocosqf.exceptions.OrinocoPreprocessorException;
import arma.orinocosqf.preprocessing.PreProcessorMacro;

/**
 * A type of {@link BodySegment} in that it contains a sequence/list of segments. When {@link #applyArguments(List)} is invoked, each
 * {@link BodySegment#applyArguments(List)} is invoked and appended one after the other with no delimeter.
 */
public class BodySegmentSequence extends BodySegment implements Iterable<BodySegment> {

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
	public CharSequence applyArguments(@NotNull List<CharSequence> args) throws OrinocoPreprocessorException {
		if (!isValid()) {
			// invalid macros return empty texts
			return "";
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < segments.size(); i++) {
			BodySegment currentSegment = segments.get(i);

			if (currentSegment instanceof WordSegment) {
				if (((WordSegment) currentSegment).takesArguments()) {
					if (i + 1 < segments.size() && segments.get(i + 1) instanceof ParenSegment) {
						i++;
						ParenSegment argSegment = (ParenSegment) segments.get(i);

						// Set flag that this segment was used as macro argument
						argSegment.useAsMacroArgument();

						// Preprocess all arguments in order to obtain the actual arguments for the macro
						List<CharSequence> argList = new ArrayList<>(argSegment.segments.size());
						for (int k = 0; k < argSegment.segments.size(); k++) {
							argList.add(argSegment.segments.get(k).applyArguments(args));
						}

						sb.append(currentSegment.applyArguments(argList));
					} else {
						throw new NoMacroArgumentsGivenException(currentSegment.toStringNoPreProcessing().toString(), currentSegment);
					}
				} else {
					sb.append(currentSegment.applyArguments(args));
				}
			} else {
				sb.append(currentSegment.applyArguments(args));
			}
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

	@Override
	public boolean equals(Object o) {
		if (!super.equals(o)) {
			return false;
		}

		BodySegmentSequence other = (BodySegmentSequence) o;

		if (other.segments == null) {
			return this.segments == null;
		}

		return other.segments.equals(this.segments);
	}

	/**
	 * @return Whether this segment is in a valid state (doesn't contain any segment of type {@link ErrorSegment}).
	 */
	public boolean isValid() {
		for (BodySegment currentSegment : segments) {
			if (currentSegment instanceof BodySegmentSequence) {
				if (!((BodySegmentSequence) currentSegment).isValid()) {
					return false;
				}
			} else {
				if (currentSegment instanceof ErrorSegment) {
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public Iterator<BodySegment> iterator() {
		return segments.iterator();
	}
}
