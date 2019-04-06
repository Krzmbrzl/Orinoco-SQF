package arma.orinocosqf.bodySegments;

import java.util.List;

import org.jetbrains.annotations.NotNull;

/**
 * The simplest type of {@link BodySegment}. When {@link #applyArguments(List)} is invoked, it returns a hardcoded String
 */
public class TextSegment extends BodySegment {

	protected final String text;

	/**
	 * @param text the text to return in {@link #applyArguments(List)}
	 */
	public TextSegment(@NotNull String text) {
		this.text = text;
	}

	@NotNull
	@Override
	public CharSequence applyArguments(@NotNull List<CharSequence> args) {
		return text;
	}

	@Override
	@NotNull
	public CharSequence toStringNoPreProcessing() {
		return text;
	}

	@Override
	public String toString() {
		return "TextSegment{" + text + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (!super.equals(o)) {
			return false;
		}

		TextSegment other = (TextSegment) o;

		if (other.text == null) {
			return this.text == null;
		}

		return other.text.equals(this.text);
	}
}
