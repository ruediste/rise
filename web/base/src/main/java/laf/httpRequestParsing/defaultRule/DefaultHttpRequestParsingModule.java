package laf.httpRequestParsing.defaultRule;

import javax.inject.Singleton;

import laf.base.BaseModule;
import laf.httpRequestParsing.HttpRequestParsingModule;
import laf.httpRequestParsing.parameterHandler.ParameterHandlerModule;
import laf.httpRequestParsing.twoStageMappingRule.TwoStageMappingRuleModule;

import org.jabsaw.Module;

@Module(imported = { ParameterHandlerModule.class,
		HttpRequestParsingModule.class, TwoStageMappingRuleModule.class,
		BaseModule.class })
@Singleton
public class DefaultHttpRequestParsingModule {

}
