package laf.http.requestMapping.defaultRule;

import javax.inject.Singleton;

import laf.base.BaseModule;
import laf.http.requestMapping.parameterHandler.ParameterHandlerModule;
import laf.http.requestMapping.twoStageMappingRule.TwoStageMappingRuleModule;

import org.jabsaw.Module;

@Module(imported = { ParameterHandlerModule.class, BaseModule.class }, exported = { TwoStageMappingRuleModule.class })
@Singleton
public class DefaultHttpRequestMappingModule {

}
