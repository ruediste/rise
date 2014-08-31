package laf.core.defaultConfiguration;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import laf.core.DefaultClassNameMapping;
import laf.core.base.configuration.ConfigurationDefiner;

/**
 * Defines the default configuration of the framework.
 */
public class DefaultConfiguration implements ConfigurationDefiner {

	@Inject
	Instance<Object> instance;

	public void produce(ContentTypeCP type) {
		// type.set("application/xhtml+xml");
		type.set("text/html; charset=utf-8");
	}

	public void produce(ControllerNameMappingCP val, BasePackageCP basePackage) {
		DefaultClassNameMapping mapping = instance.select(
				DefaultClassNameMapping.class).get();
		mapping.initialize(basePackage.get(), "Controller");
		val.set(mapping);
	}
}
