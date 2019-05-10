package arma.orinocosqf.configuration;

import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import arma.orinocosqf.exceptions.InvalidSyntaxException;

/**
 * This interface describes a configuration object that is used to determine/influence certain actions taken by Orinoco (objects)
 * 
 * @author Raven
 *
 */
public interface OrinocoConfiguration {

	/**
	 * Loads the values for this configuration from the given input (text-form). Each configuration has to be on its own line and consists
	 * of a key-value-pair. The exact shape and form of the value depends on the key. A line is considered a comment if it starts with a
	 * hashtag (unix-like comments). Empty lines are permitted.
	 * 
	 * @param input The input to process
	 * @throws InvalidSyntaxException If the input isnt't of the proper syntax
	 */
	public void load(@NotNull CharSequence input) throws InvalidSyntaxException;

	/**
	 * Converts this configuration in its text representation that can later be loaded via {@link #load(CharSequence)}. This text
	 * representation contains all information/settings this configuration object currently holds
	 * 
	 * @return The respective text-representation of this configuration object.
	 */
	@NotNull
	public CharSequence getSaveFormat();

	/**
	 * Gets the value stored for the given key
	 * 
	 * @param key The key identifying the configuration property that should be obtained
	 * @return An {@link OrinocoConfigurationProperty} representing the requested property or <code>null</code> if no mapping for this key could be found.
	 */
	@Nullable
	public OrinocoConfigurationProperty get(@NotNull CharSequence key);

	/**
	 * Associates the given property with the given key
	 * 
	 * @param key The key to map to
	 * @param property The property to map
	 */
	public void set(@NotNull CharSequence key, @NotNull OrinocoConfigurationProperty property);

	/**
	 * Removes the given key from this configuration discarding its associated property
	 * 
	 * @param key The key to remove
	 */
	public void unset(@NotNull CharSequence key);

	/**
	 * @param key The key to check for
	 * @return Whether this configuration contains a mapping for the provided key
	 */
	public boolean containsKey(@NotNull CharSequence key);

	/**
	 * @return The set of all keys currently held by this configuration
	 */
	@NotNull
	public Set<CharSequence> keys();
}
