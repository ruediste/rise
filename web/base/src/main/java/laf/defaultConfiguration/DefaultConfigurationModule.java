package laf.defaultConfiguration;

import laf.httpRequestMapping.defaultRule.DefaultHttpRequestMappingModule;
import laf.httpRequestMapping.parameterHandler.ParameterHandlerModule;

import org.jabsaw.Module;

@Module(description = "Module containing the default configuration of the framework", imported = {
		DefaultHttpRequestMappingModule.class, ParameterHandlerModule.class })
public class DefaultConfigurationModule {

}
