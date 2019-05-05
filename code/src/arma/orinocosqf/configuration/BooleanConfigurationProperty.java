package arma.orinocosqf.configuration;

import org.jetbrains.annotations.NotNull;

import arma.orinocosqf.exceptions.InvalidSyntaxException;

/**
 * A {@link OrinocoConfigurationProperty} holding a boolean value
 * 
 * @author Raven
 *
 */
public class BooleanConfigurationProperty extends OrinocoConfigurationProperty {
	protected boolean value;

	protected BooleanConfigurationProperty() {
	}
	
	/**
	 * @param value The initial value of this property
	 */
	public BooleanConfigurationProperty(boolean value) {
		this.value = value;
	}

	@Override
	protected void load(@NotNull CharSequence input) throws InvalidSyntaxException {
		if (input.length() == 0) {
			throw new InvalidSyntaxException("Empty input");
		}

		if (input.length() == 1) {
			try {
				value = Integer.parseInt(input, 0, input.length(), 2) == 1;
				return;
			} catch (NumberFormatException e) {
			}
		} else {
			if (input.length() == "true".length() || input.length() == "false".length()) {
				String strInput = input.toString().toLowerCase();

				if (strInput.equals("true")) {
					value = true;
					return;
				}
				if (strInput.equals("false")) {
					value = false;
					return;
				}
			}
		}

		throw new InvalidSyntaxException("Expected the input to be either \"true\", \"false\", \"1\" or \"0\"");
	}

	@Override
	public @NotNull CharSequence getSaveFormat() {
		return value ? "1" : "0";
	}

	/**
	 * @return Whether this property is set to true
	 */
	public boolean isTrue() {
		return value;
	}

}
