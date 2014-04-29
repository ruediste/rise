package laf.httpRequestMapping.defaultRule;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import laf.configuration.ConfigurationModule;
import laf.configuration.LoadDefaultConfigurationEvent;
import laf.httpRequestMapping.HttpRequestMappingModule;
import laf.httpRequestMapping.parameterHandler.ParameterHandlerModule;
import laf.httpRequestMapping.twoStageMappingRule.*;
import laf.initialization.InitializationModule;

import org.jabsaw.Module;

@Module(imported = { ParameterHandlerModule.class,
		HttpRequestMappingModule.class, TwoStageMappingRuleModule.class,
		InitializationModule.class, ConfigurationModule.class })
@Singleton
public class DefaultHttpRequestMappingModule {

	@Inject
	HttpRequestMappingModule httpRequestMappingModule;

	@Inject
	DefaultHttpRequestMapper.Builder requestMapperBuilder;

	@Inject
	DefaultActionPathSigner defaultActionPathSigner;

	@Inject
	DefaultParameterMapper defaultParameterMapper;

	public void loadDefaultConfiguration(
			@Observes LoadDefaultConfigurationEvent e) {
		TwoStageMappingRule rule = new TwoStageMappingRule(
				requestMapperBuilder
						.create(new DefaultControllerIdentifierStrategy()),
				defaultParameterMapper, defaultActionPathSigner);
		httpRequestMappingModule.httpRequestMappingRules.getValue().add(rule);
	}
}
