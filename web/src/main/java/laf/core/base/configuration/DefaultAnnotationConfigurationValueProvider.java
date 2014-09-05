package laf.core.base.configuration;

import javax.inject.Inject;

import laf.core.base.Val;

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
	public <V, T extends ConfigurationParameter<V>> Val<V> provideValue(
			Class<T> configInterfaceClass, TypeToken<V> configValueType) {
		ConfigurationDefault annotation = configInterfaceClass
				.getAnnotation(ConfigurationDefault.class);
		if (annotation != null) {
			return Val.of(parsingService.parse(configValueType,
					annotation.value()));

		} else if (getSuccessor() != null) {
			return getSuccessor().provideValue(configInterfaceClass,
					configValueType);
		} else {
			return null;
		}
	}

}
