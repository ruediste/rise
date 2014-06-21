package laf.defaultConfiguration;

import laf.configuration.ConfigurationModule;
import laf.http.requestMapping.defaultRule.DefaultHttpRequestMappingModule;
import laf.http.requestMapping.parameterHandler.ParameterHandlerModule;
import laf.http.requestProcessing.defaultProcessor.DefaultHttpRequestProcessorModule;
import laf.requestProcessing.RequestProcessingModule;

import org.jabsaw.Module;

@Module(description = "Module containing the default configuration of the framework", imported = {
		DefaultHttpRequestMappingModule.class, ParameterHandlerModule.class,
		DefaultHttpRequestProcessorModule.class, ConfigurationModule.class,
		RequestProcessingModule.class })
public class DefaultConfigurationModule {

}
