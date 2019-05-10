package arma.orinocosqf.configuration;

import arma.orinocosqf.OrinocoPreProcessor;
import arma.orinocosqf.exceptions.InvalidSyntaxException;
import org.jetbrains.annotations.NotNull;

/**
 * The configuration for the {@link OrinocoPreProcessor}
 * 
 * @author Raven
 *
 */
public class OrinocoPreprocessorConfiguration extends AbstractOrinocoConfiguration {

	public static final String KEEP_COMMENTS_KEY = "arma.orinoco.preprocessor.keepComments";
	public static final String PRESERVE_ALL_NEWLINES_KEY = "arma.orinoco.preprocessor.preserveAllNewlines";
	public static final String MAXIMUM_CONSECUTIVE_NL_KEY = "arma.orinoco.preprocessor.maximumConsecutiveNewlines";

	@Override
	public void loadDefaults() {
		set(KEEP_COMMENTS_KEY, new BooleanConfigurationProperty(false));
		set(PRESERVE_ALL_NEWLINES_KEY, new BooleanConfigurationProperty(true));
		set(MAXIMUM_CONSECUTIVE_NL_KEY, new IntegerConfigurationProperty(Integer.MAX_VALUE));
	}

	@Override
	protected @NotNull OrinocoConfigurationProperty createConfigurationPropertyFrom(@NotNull CharSequence key, @NotNull CharSequence str)
			throws InvalidSyntaxException {
		OrinocoConfigurationProperty property = null;

		switch (key.toString().toLowerCase()) {
			case "keppcomments":
			case "preserveallnewlines":
				property = new BooleanConfigurationProperty();
				break;

			case "maximumconsecutivenewlines":
				property = new IntegerConfigurationProperty();
				break;

			default:
				throw new InvalidSyntaxException("Unknown key " + key + " for OrinocoPreprocessorConfiguration");
		}

		property.load(str);

		return property;
	}

	@Override
	public void unset(@NotNull CharSequence key) {
		// Don't allow unsetting defaults
		switch (key.toString()) {
			case KEEP_COMMENTS_KEY:
			case PRESERVE_ALL_NEWLINES_KEY:
			case MAXIMUM_CONSECUTIVE_NL_KEY:
				break;
			default:
				super.unset(key);
		}
	}

	/**
	 * @return Whether comments should be kept instead if being removed
	 */
	public boolean keepComments() {
		return ((BooleanConfigurationProperty) get(KEEP_COMMENTS_KEY)).isTrue();
	}

	/**
	 * @return Whether newlines inside comments/includes/macro bodies/etc. should be preserved in the preprocessed output
	 */
	public boolean preserveNewlines() {
		return ((BooleanConfigurationProperty) get(PRESERVE_ALL_NEWLINES_KEY)).isTrue();
	}

	/**
	 * @return The maximum amount of consecutive newlines to keep
	 */
	public int maxConsecutiveNLs() {
		return ((IntegerConfigurationProperty) get(KEEP_COMMENTS_KEY)).getInt();
	}
}
