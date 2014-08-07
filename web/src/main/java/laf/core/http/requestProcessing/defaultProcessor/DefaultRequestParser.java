package laf.core.http.requestProcessing.defaultProcessor;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import laf.core.actionPath.ActionPath;
import laf.core.http.request.DelegatingHttpRequest;
import laf.core.http.requestMapping.HttpRequestMappingService;
import laf.core.http.requestMapping.parameterValueProvider.ParameterValueProvider;

public class DefaultRequestParser implements RequestParser {
	@Inject
	HttpRequestMappingService httpRequestMappingService;

	@Override
	public ActionPath<ParameterValueProvider> parse(HttpServletRequest request) {
		ActionPath<ParameterValueProvider> actionPath = httpRequestMappingService
				.parse(new DelegatingHttpRequest(request));

		if (actionPath == null) {
			throw new RuntimeException("URL could not be parsed: "
					+ request.getPathInfo());
		}
		return actionPath;
	}

}