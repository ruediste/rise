package laf.configuration;

public interface ConfigurationValue<T extends ConfigurationParameter<?>> {
	T value();
}
