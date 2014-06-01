package laf.defaultConfiguration;

import laf.configuration.ConfigurationModule;
import laf.http.requestMapping.defaultRule.DefaultHttpRequestMappingModule;
import laf.http.requestMapping.parameterHandler.ParameterHandlerModule;
import laf.http.requestProcessing.defaultProcessor.DefaultHttpRequestProcessorModule;
import laf.requestProcessing.defaultProcessor.DefaultRequestProcessingModule;

import org.jabsaw.Module;

@Module(description = "Module containing the default configuration of the framework", imported = {
		DefaultHttpRequestMappingModule.class, ParameterHandlerModule.class,
		DefaultRequestProcessingModule.class,
		DefaultHttpRequestProcessorModule.class, ConfigurationModule.class, })
public class DefaultConfigurationModule {

}
