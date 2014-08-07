package laf.core.requestProcessing;

import laf.core.actionPath.ActionPath;
import laf.core.http.requestMapping.parameterValueProvider.ParameterValueProvider;

public interface ParameterLoader {
	ActionPath<Object> load(ActionPath<ParameterValueProvider> path);
}