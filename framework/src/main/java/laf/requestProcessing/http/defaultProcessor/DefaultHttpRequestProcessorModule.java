package laf.requestProcessing.http.defaultProcessor;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import laf.actionPath.ActionPathModule;
import laf.base.BaseModule;
import laf.controllerInfo.ControllerInfoModule;
import laf.initialization.LafInitializer;
import laf.initialization.laf.*;
import laf.requestProcessing.RequestProcessingModule;
import laf.requestProcessing.RequestProcessingService.RequestProcessor;
import laf.requestProcessing.defaultProcessor.DefaultRequestProcessorModule;
import laf.requestProcessing.http.HttpRequestProcessingModule;
import laf.requestProcessing.http.HttpRequestProcessingService.HttpRequestProcessor;
import laf.requestProcessing.http.defaultProcessor.DefaultHttpRequestProcessingService.RequestParserImpl;
import laf.requestProcessing.http.defaultProcessor.DefaultHttpRequestProcessingService.ResultRendererImpl;

import org.jabsaw.Module;

@Singleton
@Module(description = "Default implementation of a HttpRequestProcessor", imported = {
		HttpRequestProcessingModule.class, BaseModule.class,
		ControllerInfoModule.class, LafInitializationModule.class,
		RequestProcessingModule.class, DefaultRequestProcessorModule.class,
		ActionPathModule.class })
public class DefaultHttpRequestProcessorModule {

	@Inject
	DefaultHttpRequestProcessingService service;

	@Inject
	RequestProcessingModule requestProcessingModule;
	@Inject
	HttpRequestProcessingModule httpRequestProcessingModule;

	public class DefaultHttpRequestProcessor implements HttpRequestProcessor {

		private RequestParserImpl parser;
		private ResultRendererImpl renderer;
		private RequestProcessor innerProcessor;

		public DefaultHttpRequestProcessor() {
			parser = service.new RequestParserImpl();
			renderer = service.new ResultRendererImpl();
			innerProcessor = requestProcessingModule.getProcessor();
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

	@LafInitializer(phase = LafConfigurationPhase.class, after = DefaultRequestProcessorModule.class, before = DefaultConfigurationInitializer.class)
	public void initialize() {
		httpRequestProcessingModule
		.setHttpProcessor(new DefaultHttpRequestProcessor());
	}
}
