package laf.configuration;

public interface ConfigurationParameter<T> {

	T get();

	void set(T value);
}
