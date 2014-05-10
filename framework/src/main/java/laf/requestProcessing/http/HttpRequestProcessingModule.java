package laf.requestProcessing.http;

import javax.enterprise.event.Observes;
import javax.inject.Singleton;

import laf.base.BaseModule;
import laf.controllerInfo.ControllerInfoModule;
import laf.httpRequest.HttpRequestModule;
import laf.httpRequestMapping.HttpRequestMappingModule;
import laf.initialization.CreateInitializersEvent;
import laf.initialization.laf.FrameworkRootInitializer;
import laf.requestProcessing.RequestProcessingModule;
import laf.requestProcessing.http.HttpRequestProcessingService.HttpRequestProcessor;

import org.jabsaw.Module;

@Singleton
@Module(description = "processing logic for HTTP requests", imported = {
		RequestProcessingModule.class, BaseModule.class,
		ControllerInfoModule.class, HttpRequestMappingModule.class }, exported = { HttpRequestModule.class })
public class HttpRequestProcessingModule {

	private HttpRequestProcessor httpProcessor;

	public HttpRequestProcessor getHttpProcessor() {
		return httpProcessor;
	}

	public void setHttpProcessor(HttpRequestProcessor httpProcessor) {
		this.httpProcessor = httpProcessor;
	}

	void createInitializers(@Observes CreateInitializersEvent e) {
		e.createInitializers().before(FrameworkRootInitializer.class)
		.from(httpProcessor);
	}
}
