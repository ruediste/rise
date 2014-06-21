package laf.http.requestProcessing.defaultProcessor;

import javax.servlet.http.HttpServletRequest;

import laf.actionPath.ActionPath;
import laf.http.requestMapping.parameterValueProvider.ParameterValueProvider;

public interface RequestParser {
	ActionPath<ParameterValueProvider> parse(HttpServletRequest request);
}