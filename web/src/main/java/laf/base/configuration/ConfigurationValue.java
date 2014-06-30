package laf.base.configuration;

public interface ConfigurationValue<T extends ConfigurationParameter<?>> {
	T value();
}
