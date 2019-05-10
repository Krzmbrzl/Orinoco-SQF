package arma.orinocosqf.configuration;

import org.jetbrains.annotations.NotNull;

import arma.orinocosqf.exceptions.InvalidSyntaxException;

/**
 * A {@link OrinocoConfigurationProperty} holding a number value
 * 
 * @author Raven
 *
 */
public class IntegerConfigurationProperty extends OrinocoConfigurationProperty {

	protected int value;
	
	protected IntegerConfigurationProperty() {
	}

	/**
	 * @param value The initial value of this property
	 */
	public IntegerConfigurationProperty(int value) {
		this.value = value;
	}

	@Override
	protected void load(@NotNull CharSequence input) throws InvalidSyntaxException {
		try {
			value = Integer.parseInt(input, 0, input.length(), 10);
		} catch (NumberFormatException e) {
			throw new InvalidSyntaxException(e);
		}
	}

	@Override
	public @NotNull CharSequence getSaveFormat() {
		return String.valueOf(value);
	}

	/**
	 * @return The current value of this property
	 */
	public int getInt() {
		return value;
	}

}
