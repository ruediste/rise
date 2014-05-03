package laf.requestProcessing.http;

import laf.requestProcessing.RequestProcessingModule;
import laf.requestProcessing.http.HttpRequestProcessingService.HttpRequestProcessor;

import org.jabsaw.Module;

@Module(description = "processing logic for HTTP requests", imported = { RequestProcessingModule.class })
public class HttpRequestProcessingModule {

	private HttpRequestProcessor httpProcessor;

	public HttpRequestProcessor getHttpProcessor() {
		return httpProcessor;
	}

	public void setHttpProcessor(HttpRequestProcessor httpProcessor) {
		this.httpProcessor = httpProcessor;
	}
}
