package laf.httpRequestProcessing.defaultProcessor;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import laf.httpRequestProcessing.HttpRequestProcessor;
import laf.httpRequestProcessing.RequestParserConfigurationValue;
import laf.httpRequestProcessing.ResultRendererConfigurationValue;
import laf.requestProcessing.RequestProcessorConfigurationValue;

public class DefaultHttpRequestProcessor implements HttpRequestProcessor {

	@Inject
	RequestParserConfigurationValue parser;

	@Inject
	ResultRendererConfigurationValue renderer;

	@Inject
	RequestProcessorConfigurationValue innerProcessor;

	@Override
	public void process(HttpServletRequest request, HttpServletResponse response) {
		try {
			renderer.get().renderResult(
					innerProcessor.get().process(parser.get().parse(request)),
					response);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}