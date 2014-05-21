package laf.requestProcessing.http.defaultProcessor;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import laf.actionPath.ActionPath;
import laf.httpRequest.DelegatingHttpRequest;
import laf.httpRequestParsing.HttpRequestParsingService;
import laf.httpRequestParsing.parameterValueProvider.ParameterValueProvider;
import laf.requestProcessing.http.RequestParser;

public class DefaultRequestParser implements RequestParser {
	@Inject
	HttpRequestParsingService httpRequestParsingService;

	@Override
	public ActionPath<ParameterValueProvider> parse(HttpServletRequest request) {
		ActionPath<ParameterValueProvider> actionPath = httpRequestParsingService
				.parse(new DelegatingHttpRequest(request));

		if (actionPath == null) {
			throw new RuntimeException("URL could not be parsed "
					+ request.getPathInfo());
		}
		return actionPath;
	}

}