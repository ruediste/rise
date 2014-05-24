package laf.configuration;

public interface ConfigurationValue<T> {

	T get();

	void set(T value);
}
