package laf.core.http.requestMapping.defaultRule;

import javax.inject.Singleton;

import laf.base.BaseModule;
import laf.core.http.requestMapping.parameterHandler.ParameterHandlerModule;
import laf.core.http.requestMapping.twoStageMappingRule.TwoStageMappingRuleModule;

import org.jabsaw.Module;

@Module(imported = { ParameterHandlerModule.class, BaseModule.class }, exported = { TwoStageMappingRuleModule.class })
@Singleton
public class DefaultHttpRequestMappingModule {

}
