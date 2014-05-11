package laf.requestProcessing.http.defaultProcessor;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import laf.requestProcessing.RequestProcessor;
import laf.requestProcessing.http.HttpRequestProcessor;
import laf.requestProcessing.http.RequestParser;
import laf.requestProcessing.http.ResultRenderer;

public class DefaultHttpRequestProcessor implements HttpRequestProcessor {

	final private RequestParser parser;
	final private ResultRenderer renderer;
	final private RequestProcessor innerProcessor;

	public DefaultHttpRequestProcessor(RequestParser parser,
			ResultRenderer renderer, RequestProcessor innerProcessor) {
		this.parser = parser;
		this.renderer = renderer;
		this.innerProcessor = innerProcessor;
	}

	@Override
	public void process(HttpServletRequest request, HttpServletResponse response) {
		try {
			renderer.renderResult(
					innerProcessor.process(parser.parse(request)), response);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}