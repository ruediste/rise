package laf.base.configuration;

public interface ConfigurationParameter<T> {

	T get();

	void set(T value);
}
