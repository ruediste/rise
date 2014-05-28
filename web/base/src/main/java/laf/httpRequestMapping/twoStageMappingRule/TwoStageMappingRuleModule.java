package laf.httpRequestMapping.twoStageMappingRule;

import laf.httpRequestMapping.HttpRequestMappingModule;

import org.jabsaw.Module;

@Module(description = "base implementation of HttpRequestRule, which "
		+ "separates the splitting of the servlet path "
		+ "from the parameter to string mapping", exported = { HttpRequestMappingModule.class })
public class TwoStageMappingRuleModule {

}
