package laf.httpRequestMapping.defaultRule;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import laf.base.BaseModule;
import laf.configuration.ConfigurationModule;
import laf.configuration.LoadDefaultConfigurationEvent;
import laf.httpRequestMapping.HttpRequestMappingModule;
import laf.httpRequestMapping.HttpRequestMappingService;
import laf.httpRequestMapping.parameterHandler.ParameterHandlerModule;
import laf.httpRequestMapping.twoStageMappingRule.*;
import laf.initialization.InitializationModule;
import laf.initialization.LafInitializer;

import org.jabsaw.Module;

@Module(imported = { ParameterHandlerModule.class,
		HttpRequestMappingModule.class, TwoStageMappingRuleModule.class,
		InitializationModule.class, ConfigurationModule.class, BaseModule.class })
@Singleton
public class DefaultHttpRequestMappingModule {

	@Inject
	HttpRequestMappingModule httpRequestMappingModule;

	@Inject
	HttpRequestMappingService httpRequestMappingService;

	@Inject
	DefaultHttpRequestMapper.Builder requestMapperBuilder;

	@Inject
	DefaultActionPathSigner defaultActionPathSigner;

	@Inject
	DefaultParameterMapper defaultParameterMapper;

	public void loadDefaultConfiguration(
			@Observes LoadDefaultConfigurationEvent e) {
		httpRequestMappingModule.mappingRuleInitializers.getValue().add(this);
	}

	@LafInitializer
	public void setupDefaultMappingRule() {
		TwoStageMappingRule rule = new TwoStageMappingRule(
				requestMapperBuilder
						.create(new DefaultControllerIdentifierStrategy()),
				defaultParameterMapper, defaultActionPathSigner);
		httpRequestMappingService.getMappingRules().add(rule);

	}
}
