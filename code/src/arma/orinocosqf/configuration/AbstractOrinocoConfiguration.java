package arma.orinocosqf.configuration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.PrimitiveIterator.OfInt;
import java.util.Set;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import arma.orinocosqf.exceptions.InvalidSyntaxException;

/**
 * A basic implementation of {@link OrinocoConfiguration}
 * 
 * @author Raven
 *
 */
public abstract class AbstractOrinocoConfiguration implements OrinocoConfiguration {
	/**
	 * The map keeping track of all stored configurations
	 */
	protected HashMap<CharSequence, OrinocoConfigurationProperty> settings;
	/**
	 * The key that has been requested most recently
	 */
	protected CharSequence lastRequestedKey;
	/**
	 * The object associated with the {@link #lastRequestedKey}
	 */
	protected OrinocoConfigurationProperty lastObject;


	public AbstractOrinocoConfiguration() {
		settings = new HashMap<>();

		loadDefaults();
	}

	/**
	 * Loads this configuration's default values into place
	 */
	public abstract void loadDefaults();

	@Override
	public void load(@NotNull CharSequence input) throws InvalidSyntaxException {
		boolean discardLine = false;
		boolean inKey = true;
		boolean startOfLine = true;

		int keyStart = -1;
		int keyLength = 0;
		int valueStart = -1;
		int valueLength = 0;

		int offset = 0;

		IntStream inStream = input.chars();

		OfInt it = inStream.iterator();

		while (it.hasNext()) {
			char c = (char) it.nextInt();

			if (startOfLine && c == '#') {
				// This line is a comment -> skip
				discardLine = true;
			}

			if (c == '\n') {
				if (discardLine) {
					discardLine = false;
				} else {
					if (inKey) {
						// invalid
						throw new InvalidSyntaxException("Encountered newline while processing key!");
					} else {
						inKey = true;

						// set the respective key-value-pair
						CharSequence key = input.subSequence(keyStart, valueStart + keyLength);

						if (key.length() == 0) {
							throw new InvalidSyntaxException("Encountered empty key!");
						}

						if (!containsKey(key)) {
							set(key, createConfigurationPropertyFrom(key, input.subSequence(valueStart, valueStart + valueLength)));
						} else {
							// reuse the already existing property object by calling its load function again
							get(key).load(input.subSequence(valueStart, valueStart + valueLength));
						}

						// reset vars
						keyStart = -1;
						keyLength = 0;
						valueStart = -1;
						valueLength = 0;
					}
				}

				startOfLine = true;
			} else {
				if (!discardLine) {
					if (inKey) {
						if (startOfLine) {
							keyStart = offset;
						}

						if (c == '=') {
							inKey = false;
						} else {
							keyLength++;
						}
					} else {
						// in value
						if (startOfLine) {
							valueStart = offset;
						}
						valueLength++;
					}
				}

				startOfLine = false;
			}

			offset++;
		}

		inStream.close();
	}

	/**
	 * @param key The key corresponding to the configuration object that should be created
	 * @param str The text representation of the configuration object that should be created
	 * @return The configuration object for the given text representation
	 * @throws InvalidSyntaxException If the syntax of the input is wrong
	 */
	protected abstract @NotNull OrinocoConfigurationProperty createConfigurationPropertyFrom(@NotNull CharSequence key,
			@NotNull CharSequence str) throws InvalidSyntaxException;

	@Override
	public @NotNull CharSequence getSaveFormat() {
		Set<CharSequence> keySet = settings.keySet();

		// assume an average of 10 characters per setting
		StringBuilder saveFormat = new StringBuilder(keySet.size() * 10);

		for (CharSequence currentKey : keySet) {
			saveFormat.append(currentKey);
			saveFormat.append('=');
			saveFormat.append(settings.get(currentKey).getSaveFormat());
			saveFormat.append('\n');
		}

		return saveFormat;
	}

	@Override
	public @Nullable OrinocoConfigurationProperty get(@NotNull CharSequence key) {
		if (key.equals(lastRequestedKey)) {
			return lastObject;
		} else {
			OrinocoConfigurationProperty o = settings.get(key);

			if (o != null) {
				// cache the last existing key for cases in which the user has used containsKey directly before accessing its value
				// -> prevents double lookup
				lastRequestedKey = key;
				lastObject = o;
			}

			return o;
		}
	}

	@Override
	public void set(@NotNull CharSequence key, @NotNull OrinocoConfigurationProperty property) {
		settings.put(key, (OrinocoConfigurationProperty) property);
	}

	@Override
	public void unset(@NotNull CharSequence key) {
		// clear cached key in case this action influence it in a way so that the cached object isn't valid anymore
		lastRequestedKey = null;
		settings.remove(key);
	}

	@Override
	public boolean containsKey(@NotNull CharSequence key) {
		return get(key) != null;
	}

	@Override
	public @NotNull Set<CharSequence> keys() {
		// return copies so the user can't mess this configuration up by performing changes on the returned set
		return new HashSet<>(settings.keySet());
	}
}
