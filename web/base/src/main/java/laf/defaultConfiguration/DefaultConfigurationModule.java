package laf.defaultConfiguration;

import laf.configuration.ConfigurationModule;
import laf.httpRequestMapping.defaultRule.DefaultHttpRequestMappingModule;
import laf.httpRequestMapping.parameterHandler.ParameterHandlerModule;
import laf.httpRequestProcessing.defaultProcessor.DefaultHttpRequestProcessorModule;
import laf.requestProcessing.defaultProcessor.DefaultRequestProcessingModule;

import org.jabsaw.Module;

@Module(description = "Module containing the default configuration of the framework", imported = {
		DefaultHttpRequestMappingModule.class, ParameterHandlerModule.class,
		DefaultRequestProcessingModule.class,
		DefaultHttpRequestProcessorModule.class, ConfigurationModule.class, })
public class DefaultConfigurationModule {

}
