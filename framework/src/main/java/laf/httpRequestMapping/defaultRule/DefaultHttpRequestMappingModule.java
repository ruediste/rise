package laf.httpRequestMapping.defaultRule;

import javax.inject.Inject;
import javax.inject.Singleton;

import laf.base.BaseModule;
import laf.configuration.ConfigurationModule;
import laf.httpRequestMapping.HttpRequestMappingModule;
import laf.httpRequestMapping.HttpRequestMappingService;
import laf.httpRequestMapping.parameterHandler.ParameterHandlerModule;
import laf.httpRequestMapping.twoStageMappingRule.DefaultActionPathSigner;
import laf.httpRequestMapping.twoStageMappingRule.TwoStageMappingRule;
import laf.httpRequestMapping.twoStageMappingRule.TwoStageMappingRuleModule;
import laf.initialization.LafInitializer;
import laf.initialization.laf.DefaultConfigurationInitializer;
import laf.initialization.laf.LafConfigurationPhase;
import laf.initialization.laf.LafInitializationModule;

import org.jabsaw.Module;

@Module(imported = { ParameterHandlerModule.class,
		HttpRequestMappingModule.class, TwoStageMappingRuleModule.class,
		LafInitializationModule.class, ConfigurationModule.class,
		BaseModule.class })
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

	@LafInitializer(phase = LafConfigurationPhase.class, before = DefaultConfigurationInitializer.class)
	public void setupDefaultMappingRule() {
		TwoStageMappingRule rule = new TwoStageMappingRule(
				requestMapperBuilder
				.create(new DefaultControllerIdentifierStrategy()),
				defaultParameterMapper, defaultActionPathSigner);
		httpRequestMappingService.getMappingRules().add(rule);

	}

}
