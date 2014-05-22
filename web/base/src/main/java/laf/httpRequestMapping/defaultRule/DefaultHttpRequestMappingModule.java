package laf.httpRequestMapping.defaultRule;

import javax.inject.Singleton;

import laf.base.BaseModule;
import laf.httpRequestMapping.HttpRequestMappingModule;
import laf.httpRequestMapping.parameterHandler.ParameterHandlerModule;
import laf.httpRequestMapping.twoStageMappingRule.TwoStageMappingRuleModule;

import org.jabsaw.Module;

@Module(imported = { ParameterHandlerModule.class,
		HttpRequestMappingModule.class, TwoStageMappingRuleModule.class,
		BaseModule.class })
@Singleton
public class DefaultHttpRequestMappingModule {

}
