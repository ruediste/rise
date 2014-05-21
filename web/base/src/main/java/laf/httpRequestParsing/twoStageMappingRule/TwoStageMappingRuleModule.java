package laf.httpRequestParsing.twoStageMappingRule;

import laf.httpRequestParsing.HttpRequestParsingModule;

import org.jabsaw.Module;

@Module(description = "base implementation of HttpRequestRule, which "
		+ "separates the splitting of the servlet path "
		+ "from the parameter to string mapping", imported = { HttpRequestParsingModule.class })
public class TwoStageMappingRuleModule {

}
