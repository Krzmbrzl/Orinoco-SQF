package arma.orinocosqf.preprocessing.bodySegments;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import arma.orinocosqf.exceptions.OrinocoPreprocessorException;

/**
 * This segment represents a special type of {@link BodySegmentSequence} representing an expression in parenthesis like
 * <code>(ONE,TWO,Three and five)</code>. All elements in this segment are expected to have been separated by a comma and surrounded by
 * parenthesis. The commas and parenthesis themselves are not part of this segment thoug as they'll get added automatically anyways.<br>
 * <br>
 * This segment can also be used as the arguments to some macro. In that case this segment has to be informed about it via
 * {@link #useAsMacroArgument()} in order for it to prevent expansion on its own when {@link #applyArguments(List)} is being called. A call
 * to this method resets the internal state though so the next call to it will yield a normal expansion if {@link #useAsMacroArgument()}
 * wasn't called before again.<br>
 * {@link #toStringNoPreProcessing()} won't be affected by this whatsoever though.
 * 
 * @author Raven
 *
 */
public class ParenSegment extends BodySegmentSequence {

	protected boolean wasUsedAsMacroArgument;

	public ParenSegment(@NotNull List<BodySegment> segments) {
		super(segments);

		this.wasUsedAsMacroArgument = false;
	}

	/**
	 * @return The amount of elements this segment encapsulate. That's how many comma-separated elements there have been in the
	 *         paren-expression this segment was created from.
	 */
	public int getElementCount() {
		return segments.size();
	}

	/**
	 * Signal this segment that it is being used as a macro argument
	 */
	public void useAsMacroArgument() {
		this.wasUsedAsMacroArgument = true;
	}

	/**
	 * @param index The index of the element to retrieve
	 * @return The {@link BodySegment} at the specified index
	 */
	public BodySegment get(int index) {
		return segments.get(index);
	}

	@NotNull
	@Override
	public CharSequence applyArguments(@NotNull List<CharSequence> args) throws OrinocoPreprocessorException {
		if (wasUsedAsMacroArgument) {
			// if this paren-expression has been used as the argument to some macro, it has been expanded there. Therefore it must not be
			// expanded on its own
			wasUsedAsMacroArgument = false; // reset the flag for further uses
			return "";
		}

		StringBuilder sb = new StringBuilder();
		sb.append("(");

		for (int i = 0; i < segments.size(); i++) {
			sb.append(segments.get(i).applyArguments(args));
			if (i + 1 < segments.size()) {
				sb.append(",");
			}
		}

		sb.append(")");

		return sb;
	}

	@Override
	@NotNull
	public CharSequence toStringNoPreProcessing() {
		StringBuilder sb = new StringBuilder();
		sb.append("(");

		for (int i = 0; i < segments.size(); i++) {
			sb.append(segments.get(i).toStringNoPreProcessing());
			if (i + 1 < segments.size()) {
				sb.append(",");
			}
		}

		sb.append(")");

		return sb;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("ParenSequence{(");

		for (int i = 0; i < segments.size(); i++) {
			b.append(segments.get(i));
			if (i + 1 < segments.size()) {
				b.append(",");
			}
		}

		b.append(")}");
		return b.toString();
	}

	@Override
	public boolean equals(Object o) {
		return super.equals(o) && ((ParenSegment) o).wasUsedAsMacroArgument == this.wasUsedAsMacroArgument;
	}

}
