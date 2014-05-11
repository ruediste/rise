package laf.requestProcessing.http;

import javax.inject.Singleton;

import laf.base.BaseModule;
import laf.configuration.ConfigurationModule;
import laf.controllerInfo.ControllerInfoModule;
import laf.httpRequest.HttpRequestModule;
import laf.httpRequestMapping.HttpRequestMappingModule;
import laf.requestProcessing.RequestProcessingModule;

import org.jabsaw.Module;

@Singleton
@Module(description = "processing logic for HTTP requests", imported = {
		RequestProcessingModule.class, BaseModule.class,
		ControllerInfoModule.class, HttpRequestMappingModule.class,
		ConfigurationModule.class }, exported = { HttpRequestModule.class })
public class HttpRequestProcessingModule {

}
