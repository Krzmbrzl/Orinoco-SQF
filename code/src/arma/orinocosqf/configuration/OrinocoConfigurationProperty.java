package arma.orinocosqf.configuration;

import org.jetbrains.annotations.NotNull;

import arma.orinocosqf.exceptions.InvalidSyntaxException;

/**
 * An object representing some sort of configuration property. It is used in {@link AbstractOrinocoConfiguration}
 * 
 * @author Raven
 *
 */
public abstract class OrinocoConfigurationProperty {
	/**
	 * Initializes this property from the given input string. This function may also be used to change the value of this property after it
	 * has been created.
	 * 
	 * @param input The input to initialize this property from
	 * @throws InvalidSyntaxException If the input has improper syntax
	 */
	protected abstract void load(@NotNull CharSequence input) throws InvalidSyntaxException;

	/**
	 * @return The text representation of this property that can be used to recreate it by feeding it into {@link #load(CharSequence)}
	 */
	public abstract @NotNull CharSequence getSaveFormat();

	@Override
	public boolean equals(Object other) {
		return other != null && this.getClass().equals(other.getClass());
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ": " + getSaveFormat();
	}
}
