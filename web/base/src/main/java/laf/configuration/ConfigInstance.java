package laf.configuration;

/**
 * Wrapper for configuration values. The {@link ConfigValue @ConfigValue}
 * annotation is not required.
 */
public interface ConfigInstance<T> {
	/**
	 * Return the associated value
	 */
	T get();
}
