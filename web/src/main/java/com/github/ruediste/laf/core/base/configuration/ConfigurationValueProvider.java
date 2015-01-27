package com.github.ruediste.laf.core.base.configuration;

import com.github.ruediste.laf.core.base.Val;
import com.google.common.reflect.TypeToken;

/**
 * Provider of configuration values. Used by the {@link ConfigurationFactory} to
 * load configuration values
 */
interface ConfigurationValueProvider {
	<V, T extends ConfigurationParameter<V>> Val<V> provideValue(
			Class<T> configInterfaceClass, TypeToken<V> configValueType);

	void setSuccessor(ConfigurationValueProvider successor);
}