package laf.requestProcessing;

import laf.actionPath.ActionPath;
import laf.httpRequestMapping.parameterValueProvider.ParameterValueProvider;

public interface ParameterLoader {
	ActionPath<Object> load(ActionPath<ParameterValueProvider> path);
}