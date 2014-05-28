package laf.httpRequestProcessing.defaultProcessor;

import javax.servlet.http.HttpServletRequest;

import laf.actionPath.ActionPath;
import laf.httpRequestMapping.parameterValueProvider.ParameterValueProvider;

public interface RequestParser {
	ActionPath<ParameterValueProvider> parse(HttpServletRequest request);
}