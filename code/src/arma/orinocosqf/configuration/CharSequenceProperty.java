package arma.orinocosqf.configuration;

import org.jetbrains.annotations.NotNull;

import arma.orinocosqf.exceptions.InvalidSyntaxException;

/**
 * A {@link OrinocoConfigurationProperty} holding a CharSequencea as a value
 * 
 * @author Raven
 *
 */
public class CharSequenceProperty extends OrinocoConfigurationProperty {

	protected CharSequence value;

	protected CharSequenceProperty() {
	}
	
	/**
	 * @param value The initial value of this property
	 */
	public CharSequenceProperty(CharSequence value) {
		this.value = value;
	}

	@Override
	protected void load(@NotNull CharSequence input) throws InvalidSyntaxException {
		this.value = input;
	}

	@Override
	public @NotNull CharSequence getSaveFormat() {
		return value;
	}

	/**
	 * @return The value of this property
	 */
	public @NotNull CharSequence getValue() {
		return value;
	}
}
