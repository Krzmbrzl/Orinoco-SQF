package arma.orinocosqf.bodySegments;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import arma.orinocosqf.PreProcessorMacro;

/**
 * A {@link BodySegment} is a portion of a {@link PreProcessorMacro} body. #define MACRO body. When a macro is invoked, use
 * {@link #applyArguments(List)} on the body to get the preprocessed result. If a macro has parameters (i.e. #define MACRO(PARAM) PARAM),
 * the the arguments passed from the macro invocation are listed in the order that they appear in text.
 * 
 * <pre>
 *     #define MACRO(PARAM1,PARAM2) PARAM1=PARAM2
 *     MACRO(hello,2)
 *     // "hello" is first argument
 *     // "2" is second argument
 * </pre>
 */
public abstract class BodySegment {
	protected PreProcessorMacro ownerMacro;

	public BodySegment() {
	}

	/**
	 * Sets the owner macro of this segment. Usually this method will be called as soon as this segment gets assigned to a macro. If this
	 * segment contains any sub-segment this method is also responsible for propagating the set owner downwards.
	 * 
	 * @param macro The respective owner macro
	 */
	public void setOwner(@NotNull PreProcessorMacro macro) {
		this.ownerMacro = macro;
	}

	/**
	 * Applies the given arguments to this segment and expands it with that
	 * 
	 * @param args the arguments of the macro's invocation, or empty list if no arguments were passed (see class level doc)
	 * @return the preprocessed result
	 */
	@NotNull
	public abstract CharSequence applyArguments(@NotNull List<CharSequence> args);

	/**
	 * @return the unpreprocessed segment as it would appear in text/the #define
	 */
	@NotNull
	public abstract CharSequence toStringNoPreProcessing();

	@Override
	public boolean equals(Object o) {
		if (o == null || !o.getClass().equals(this.getClass())) {
			return false;
		}

		return ((BodySegment) o).ownerMacro == this.ownerMacro;
	}
}
