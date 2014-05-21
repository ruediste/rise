package laf.requestProcessing.http;

import javax.servlet.http.HttpServletRequest;

import laf.actionPath.ActionPath;
import laf.httpRequestParsing.parameterValueProvider.ParameterValueProvider;

public interface RequestParser {
	ActionPath<ParameterValueProvider> parse(HttpServletRequest request);
}