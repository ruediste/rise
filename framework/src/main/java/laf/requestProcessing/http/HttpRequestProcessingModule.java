package laf.requestProcessing.http;

import laf.base.BaseModule;
import laf.controllerInfo.ControllerInfoModule;
import laf.httpRequest.HttpRequestModule;
import laf.httpRequestMapping.HttpRequestMappingModule;
import laf.requestProcessing.RequestProcessingModule;
import laf.requestProcessing.http.HttpRequestProcessingService.HttpRequestProcessor;

import org.jabsaw.Module;

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
}
