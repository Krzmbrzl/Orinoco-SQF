package arma.orinocosqf.preprocessing.bodySegments;

import java.util.List;

import org.jetbrains.annotations.NotNull;

/**
 * A segment used to distinguish when a parameter of a macro is appearing in the body of a macro.
 * 
 * <pre>
 *     #define MACRO(PARAM) PARAM word
 *     //PARAM inside the body of the macro is the {@link MacroArgumentSegment}
 * </pre>
 * 
 * when {@link #applyArguments(List)} is invoked, it simply returns the element at index {@link #getArgumentIndex()}
 */
public class MacroArgumentSegment extends BodySegment {

	protected final String argumentName;
	protected final int argIndex;

	/**
	 * @param argumentName the name of the argument
	 * @param argIndex the index of the argument which appears in the #define (i.e. #define MACRO(PARAM1,PARAM2) => PARAM1 is index 0,
	 *        PARAM2 is index 1)
	 */
	public MacroArgumentSegment(@NotNull String argumentName, int argIndex) {
		this.argumentName = argumentName;
		this.argIndex = argIndex;
	}

	/** @return the index of the argument which appears in the macro #define (see class level doc) */
	public int getArgumentIndex() {
		return argIndex;
	}

	/**
	 * @return the argument name
	 */
	@NotNull
	public String getArgumentName() {
		return argumentName;
	}

	@NotNull
	@Override
	public CharSequence applyArguments(@NotNull List<CharSequence> args) {
		return args.get(argIndex);
	}

	@Override
	@NotNull
	public CharSequence toStringNoPreProcessing() {
		return argumentName;
	}

	@Override
	public String toString() {
		return "MacroArgumentSegment{" + argumentName + ", index=" + argIndex + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (!super.equals(o)) {
			return false;
		}

		MacroArgumentSegment other = (MacroArgumentSegment) o;

		if (other.argumentName == null) {
			if (this.argumentName != null) {
				return false;
			}

			return other.argIndex == this.argIndex;
		}

		return other.argumentName.equals(this.argumentName) && other.argIndex == this.argIndex;
	}
}
