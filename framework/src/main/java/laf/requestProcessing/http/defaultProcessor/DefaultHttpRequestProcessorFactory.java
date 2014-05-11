package laf.requestProcessing.http.defaultProcessor;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import laf.requestProcessing.RequestProcessingService;
import laf.requestProcessing.http.HttpRequestProcessor;
import laf.requestProcessing.http.HttpRequestProcessorFactory;

public class DefaultHttpRequestProcessorFactory implements
		HttpRequestProcessorFactory {

	@Inject
	Instance<DefaultRequestParser> parser;

	@Inject
	Instance<DefaultResultRenderer> renderer;

	@Inject
	RequestProcessingService requestProcessingService;

	@Override
	public HttpRequestProcessor createProcessor() {
		return new DefaultHttpRequestProcessor(parser.get(), renderer.get(),
				requestProcessingService.getProcessor());
	}
}
