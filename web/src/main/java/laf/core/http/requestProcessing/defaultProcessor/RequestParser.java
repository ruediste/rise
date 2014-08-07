package laf.core.http.requestProcessing.defaultProcessor;

import javax.servlet.http.HttpServletRequest;

import laf.core.actionPath.ActionPath;
import laf.core.http.requestMapping.parameterValueProvider.ParameterValueProvider;

public interface RequestParser {
	ActionPath<ParameterValueProvider> parse(HttpServletRequest request);
}