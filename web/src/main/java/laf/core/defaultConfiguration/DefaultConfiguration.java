package laf.core.defaultConfiguration;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import laf.core.base.DefaultClassNameMapping;
import laf.core.base.configuration.ConfigurationDefiner;

/**
 * Defines the default configuration of the framework.
 */
public class DefaultConfiguration implements ConfigurationDefiner {

	@Inject
	Instance<Object> instance;

	public void produce(ControllerNameMappingCP val, BasePackageCP basePackage) {
		DefaultClassNameMapping mapping = instance.select(
				DefaultClassNameMapping.class).get();
		mapping.initialize(basePackage.get(), "Controller");
		val.set(mapping);
	}
}
