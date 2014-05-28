package laf.httpRequestProcessing.defaultProcessor;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import laf.configuration.ConfigurationValue;
import laf.httpRequestProcessing.*;
import laf.requestProcessing.RequestProcessorConfigurationParameter;

public class DefaultHttpRequestProcessor implements HttpRequestProcessor {

	@Inject
	ConfigurationValue<RequestParserConfigurationParameter> parser;

	@Inject
	ConfigurationValue<ResultRendererConfigurationParameter> renderer;

	@Inject
	ConfigurationValue<RequestProcessorConfigurationParameter> innerProcessor;

	@Override
	public void process(HttpServletRequest request, HttpServletResponse response) {
		try {
			renderer.value()
					.get()
					.renderResult(
							innerProcessor
									.value()
									.get()
									.process(
											parser.value().get().parse(request)),
							response);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}