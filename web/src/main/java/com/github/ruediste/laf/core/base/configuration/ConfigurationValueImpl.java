package com.github.ruediste.laf.core.base.configuration;

import com.github.ruediste.laf.core.base.configuration.ConfigurationParameter;
import com.github.ruediste.laf.core.base.configuration.ConfigurationValue;

public class ConfigurationValueImpl<T extends ConfigurationParameter<?>>
		implements ConfigurationValue<T> {

	final private T value;

	public ConfigurationValueImpl(T value) {
		this.value = value;
	}

	@Override
	public T value() {
		return value;
	}

}
