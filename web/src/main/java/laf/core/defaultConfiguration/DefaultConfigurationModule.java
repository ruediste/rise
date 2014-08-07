package laf.core.defaultConfiguration;

import laf.base.configuration.ConfigurationModule;
import laf.core.http.requestMapping.defaultRule.DefaultHttpRequestMappingModule;
import laf.core.http.requestMapping.parameterHandler.ParameterHandlerModule;
import laf.core.http.requestProcessing.defaultProcessor.DefaultHttpRequestProcessorModule;
import laf.core.requestProcessing.RequestProcessingModule;

import org.jabsaw.Module;

@Module(description = "Module containing the default configuration of the framework", imported = {
		DefaultHttpRequestMappingModule.class, ParameterHandlerModule.class,
		DefaultHttpRequestProcessorModule.class, ConfigurationModule.class,
		RequestProcessingModule.class }, hideFromDependencyGraphOutput = true)
public class DefaultConfigurationModule {

}
