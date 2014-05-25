package laf.configuration;

import laf.base.Val;

import com.google.common.reflect.TypeToken;

/**
 * Provider of configuration values. Used by the
 * {@link ConfigurationFactoryBase} to load configuration values
 */
interface ConfigurationValueProvider {
	<V, T extends ConfigurationValue<V>> Val<V> provideValue(
			Class<T> configInterfaceClass, TypeToken<V> configValueType);
}