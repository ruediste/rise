package laf.requestProcessing.http.defaultProcessor;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import laf.actionPath.ActionPath;
import laf.httpRequest.DelegatingHttpRequest;
import laf.httpRequestMapping.HttpRequestMappingService;
import laf.httpRequestMapping.parameterValueProvider.ParameterValueProvider;
import laf.requestProcessing.http.RequestParser;

public class DefaultRequestParser implements RequestParser {
	@Inject
	HttpRequestMappingService httpRequestMappingService;

	@Override
	public ActionPath<ParameterValueProvider> parse(
			HttpServletRequest request) {
		ActionPath<ParameterValueProvider> actionPath = httpRequestMappingService
				.parse(new DelegatingHttpRequest(request));

		if (actionPath == null) {
			throw new RuntimeException("URL could not be handled "
					+ request.getPathInfo());
		}
		return actionPath;
	}

}