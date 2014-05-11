package laf.requestProcessing.http;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import laf.configuration.ConfigInstance;
import laf.configuration.ConfigValue;

@ApplicationScoped
public class HttpRequestProcessingService {

	HttpRequestProcessor processor;

	@Inject
	@ConfigValue("laf.requestProcessing.http.defaultProcessor.DefaultHttpRequestProcessorFactory")
	ConfigInstance<HttpRequestProcessorFactory> processorFactory;

	public void process(HttpServletRequest request, HttpServletResponse response) {
		processor.process(request, response);
	}

	@PostConstruct
	void initialize() {
		processor = processorFactory.get().createProcessor();
	}
}