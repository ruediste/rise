package laf.httpRequestMapping.twoStageMappingRule;

import laf.httpRequestMapping.HttpRequestMappingModule;
import laf.initialization.InitializationModule;

import org.jabsaw.Module;

@Module(description = "base implementation of HttpRequestRule, which "
		+ "separates the splitting of the servlet path "
		+ "from the parameter to string mapping", imported = {
		HttpRequestMappingModule.class, InitializationModule.class })
public class TwoStageMappingRuleModule {

}
