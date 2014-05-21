package laf.requestProcessing;

import laf.actionPath.ActionPath;
import laf.httpRequestParsing.parameterValueProvider.ParameterValueProvider;

public interface ParameterLoader {
	ActionPath<Object> load(ActionPath<ParameterValueProvider> path);
}