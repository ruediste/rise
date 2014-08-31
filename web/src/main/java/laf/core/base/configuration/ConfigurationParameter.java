package laf.core.base.configuration;

import java.util.function.Supplier;

public interface ConfigurationParameter<T> {

	T get();

	void set(T value);

	void set(Supplier<T> value);
}
