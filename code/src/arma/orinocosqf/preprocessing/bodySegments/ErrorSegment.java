package arma.orinocosqf.preprocessing.bodySegments;

import java.util.List;

import org.jetbrains.annotations.NotNull;

public class ErrorSegment extends BodySegment {

	protected CharSequence content;

	public ErrorSegment(@NotNull CharSequence content) {
		this.content = content;
	}

	@Override
	@NotNull
	public CharSequence applyArguments(@NotNull List<CharSequence> args) {
		return "";
	}

	@Override
	@NotNull
	public CharSequence toStringNoPreProcessing() {
		return content;
	}

	@Override
	public String toString() {
		return "ErrorSegment {" + content + "}";
	}

}
