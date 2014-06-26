package laf.http.requestProcessing.defaultProcessor;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import laf.base.ActionResult;
import laf.base.ViewTechnologyManager;
import laf.configuration.ConfigurationValue;
import laf.html.HtmlViewTechnology;
import laf.http.requestProcessing.HttpRequestProcessor;
import laf.requestProcessing.RequestProcessorConfigurationParameter;

public class DefaultHttpRequestProcessor implements HttpRequestProcessor {

	@Inject
	ConfigurationValue<RequestParserConfigurationParameter> parser;

	@Inject
	ConfigurationValue<ResultRenderers> renderers;

	@Inject
	ConfigurationValue<RequestProcessorConfigurationParameter> innerProcessor;

	@Inject
	ViewTechnologyManager viewTechnologyManager;

	@Override
	public void process(HttpServletRequest request, HttpServletResponse response) {
		viewTechnologyManager.setViewTechnology(HtmlViewTechnology.class);

		try {
			ActionResult actionResult = innerProcessor.value().get()
					.process(parser.value().get().parse(request));

			// Only render the result if it is not equal null.
			// If the result is null, the controller indicates that the request
			// has been handled already
			if (actionResult != null) {
				boolean rendered = false;
				for (ResultRenderer renderer : renderers.value().get()) {
					if (renderer.renderResult(actionResult, response)) {
						rendered = true;
						break;
					}
				}

				if (!rendered) {
					throw new RuntimeException(
							"No renderer found for actionResult "
									+ actionResult);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}