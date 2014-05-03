package laf.requestProcessing.http.defaultProcessor;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import laf.initialization.LafInitializer;
import laf.initialization.laf.DefaultInitializer;
import laf.requestProcessing.defaultProcessor.DefaultRequestProcessorModule;
import laf.requestProcessing.defaultProcessor.DefaultRequestProcessorModule.DefaultRequestProcessor;
import laf.requestProcessing.http.*;
import laf.requestProcessing.http.HttpRequestProcessingService.HttpRequestProcessor;
import laf.requestProcessing.http.HttpRequestProcessingService.RequestParserImpl;
import laf.requestProcessing.http.HttpRequestProcessingService.ResultRendererImpl;

import org.jabsaw.Module;

@Module(description = "Default implementation of a HttpRequestProcessor", imported = { HttpRequestProcessingModule.class })
public class DefaultHttpRequestProcessorModule {
	private DefaultHttpRequestProcessor defaultHttpProcessor;

	public DefaultHttpRequestProcessor getDefaultHttpProcessor() {
		return defaultHttpProcessor;
	}

	@Inject
	HttpRequestProcessingService service;

	@Inject
	DefaultRequestProcessorModule defaultRequestProcessorModule;

	@Inject
	HttpRequestProcessingModule httpRequestProcessingModule;

	public class DefaultHttpRequestProcessor implements HttpRequestProcessor {

		private RequestParserImpl parser;
		private ResultRendererImpl renderer;
		private DefaultRequestProcessor innerProcessor;

		public DefaultHttpRequestProcessor() {
			parser = service.new RequestParserImpl();
			renderer = service.new ResultRendererImpl();
			innerProcessor = defaultRequestProcessorModule
					.getDefaultProcessor();
		}

		@Override
		public void process(HttpServletRequest request,
				HttpServletResponse response) {
			try {
				renderer.renderResult(
						innerProcessor.process(parser.parse(request)), response);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@LafInitializer(after = DefaultRequestProcessorModule.class, before = DefaultInitializer.class)
	void initialize() {
		defaultHttpProcessor = new DefaultHttpRequestProcessor();
		httpRequestProcessingModule.setHttpProcessor(defaultHttpProcessor);
	}
}
