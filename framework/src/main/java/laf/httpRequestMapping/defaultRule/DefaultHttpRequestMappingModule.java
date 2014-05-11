package laf.httpRequestMapping.defaultRule;

import javax.inject.Singleton;

import laf.base.BaseModule;
import laf.configuration.ConfigurationModule;
import laf.httpRequestMapping.HttpRequestMappingModule;
import laf.httpRequestMapping.parameterHandler.ParameterHandlerModule;
import laf.httpRequestMapping.twoStageMappingRule.TwoStageMappingRuleModule;

import org.jabsaw.Module;

@Module(imported = { ParameterHandlerModule.class,
		HttpRequestMappingModule.class, TwoStageMappingRuleModule.class,
		ConfigurationModule.class, BaseModule.class })
@Singleton
public class DefaultHttpRequestMappingModule {

}
