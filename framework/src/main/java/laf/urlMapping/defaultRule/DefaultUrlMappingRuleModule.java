package laf.urlMapping.defaultRule;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import laf.configuration.ConfigurationModule;
import laf.configuration.LoadDefaultConfigurationEvent;
import laf.initialization.InitializationModule;
import laf.urlMapping.UrlMappingModule;
import laf.urlMapping.parameterHandler.ParameterHandlerModule;

import org.jabsaw.Module;

@Module(imported = { ParameterHandlerModule.class, UrlMappingModule.class,
		InitializationModule.class, ConfigurationModule.class })
@Singleton
public class DefaultUrlMappingRuleModule {

	@Inject
	UrlMappingModule urlMappingModule;

	@Inject
	DefaultUrlMappingRule defaultUrlMappingRule;

	public void loadDefaultConfiguration(
			@Observes LoadDefaultConfigurationEvent e) {
		urlMappingModule.urlMappingRules.getValue().add(defaultUrlMappingRule);
	}
}
