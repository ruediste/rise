package laf.httpRequestMapping.defaultRule;

import javax.inject.Singleton;

import laf.base.BaseModule;
import laf.httpRequestMapping.parameterHandler.ParameterHandlerModule;
import laf.httpRequestMapping.twoStageMappingRule.TwoStageMappingRuleModule;

import org.jabsaw.Module;

@Module(imported = { ParameterHandlerModule.class, BaseModule.class }, exported = { TwoStageMappingRuleModule.class })
@Singleton
public class DefaultHttpRequestMappingModule {

}
