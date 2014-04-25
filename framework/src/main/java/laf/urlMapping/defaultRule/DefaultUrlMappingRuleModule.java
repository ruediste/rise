package laf.urlMapping.defaultRule;

import laf.initialization.InitializationModule;
import laf.urlMapping.UrlMappingModule;
import laf.urlMapping.parameterHandler.ParameterHandlerModule;

import org.jabsaw.Module;

@Module(imported = { ParameterHandlerModule.class, UrlMappingModule.class,
		InitializationModule.class })
public class DefaultUrlMappingRuleModule {

}
