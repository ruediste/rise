package laf.httpRequestMapping.defaultRule;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import laf.configuration.ConfigurationModule;
import laf.configuration.LoadDefaultConfigurationEvent;
import laf.httpRequestMapping.HttpRequestMappingModule;
import laf.httpRequestMapping.parameterHandler.ParameterHandlerModule;
import laf.initialization.InitializationModule;

import org.jabsaw.Module;

@Module(imported = { ParameterHandlerModule.class, HttpRequestMappingModule.class,
		InitializationModule.class, ConfigurationModule.class })
@Singleton
public class DefaultHttpRequestMappingModule {

	@Inject
	HttpRequestMappingModule httpRequestMappingModule;

	@Inject
	DefaultHttpRequestMappingRule defaultHttpRequestMappingRule;

	public void loadDefaultConfiguration(
			@Observes LoadDefaultConfigurationEvent e) {
		httpRequestMappingModule.httpRequestMappingRules.getValue().add(defaultHttpRequestMappingRule);
	}
}
