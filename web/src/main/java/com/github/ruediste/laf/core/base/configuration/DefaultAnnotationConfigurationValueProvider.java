package com.github.ruediste.laf.core.base.configuration;

import javax.inject.Inject;

import com.google.common.reflect.TypeToken;

/**
 * {@link ConfigurationValueProvider} providing values by parsing
 * {@link ConfigurationDefault} annotaionts
 *
 */
public class DefaultAnnotationConfigurationValueProvider extends
		ConfigurationValueProviderBase {

	@Inject
	ConfigurationValueParsingService parsingService;

	@Override
	public <V, T extends ConfigurationParameter<V>> V provideValue(
			Class<T> parameterInterfaceClass, TypeToken<V> configValueType) {
		ConfigurationDefault annotation = parameterInterfaceClass
				.getAnnotation(ConfigurationDefault.class);
		if (annotation != null) {
			return parsingService.parse(configValueType, annotation.value());
		} else {
			return getSuccessor().provideValue(parameterInterfaceClass,
					configValueType);
		}
	}

}
