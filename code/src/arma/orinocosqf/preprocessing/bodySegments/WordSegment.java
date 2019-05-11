package arma.orinocosqf.preprocessing.bodySegments;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import arma.orinocosqf.preprocessing.PreProcessorMacro;

/**
 * A non {@link MacroArgumentSegment} which is simply a word which can either be a {@link PreProcessorMacro} itself or just plain text. When
 * {@link #applyArguments(List)} is invoked, it returns either the word, or if the word is a macro, returns the macro's
 * {@link PreProcessorMacro#getBody()} result
 * 
 * <pre>
 *     #define MACRO hello //hello is just a word
 *     #define MAC2 MACRO //MACRO is a reference to a macro
 *     MAC2 //results in "hello"
 *     #undef MACRO
 *     MAC2 //results in "MACRO" as MACRO is no longer a defined macro
 * </pre>
 */
public class WordSegment extends BodySegment {

	protected final String word;

	/**
	 * @param word the word (see class level doc)
	 */
	public WordSegment(@NotNull String word) {
		this.word = word;
	}

	@NotNull
	@Override
	public CharSequence applyArguments(@NotNull List<CharSequence> args) {
		PreProcessorMacro macro = ownerMacro.getMacroSet().get(word);
		if (macro != null) {
			return macro.getBody().applyArguments(args);
		}
		return word;
	}

	@Override
	@NotNull
	public CharSequence toStringNoPreProcessing() {
		return word;
	}

	@Override
	public String toString() {
		return "WordSegment{" + word + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (!super.equals(o)) {
			return false;
		}

		WordSegment other = (WordSegment) o;

		if (other.word == null) {
			return this.word == null;
		}

		return other.word.equals(this.word);
	}
}
