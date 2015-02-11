package com.github.ruediste.laf.core.base.configuration;

import com.google.common.reflect.TypeToken;

public class TerminalConfigurationValueProvider implements
		ConfigurationValueProvider {

	@Override
	public <V, T extends ConfigurationParameter<V>> V provideValue(
			Class<T> parameterInterfaceClass, TypeToken<V> configValueType) {
		throw new ConfigurationFactory.NoValueFoundException(
				parameterInterfaceClass);
	}

	@Override
	public void setSuccessor(ConfigurationValueProvider successor) {
		throw new UnsupportedOperationException(
				"Cannot set successor of terminal configuration value provider");
	}

}
