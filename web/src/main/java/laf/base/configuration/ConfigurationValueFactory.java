package laf.base.configuration;

/**
 * Interface for factories of configuration values. These factory classes can be
 * specified in .properties files.
 */
public interface ConfigurationValueFactory<T> {

	T getValue();
}
