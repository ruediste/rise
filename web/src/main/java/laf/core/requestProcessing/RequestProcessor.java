package laf.core.requestProcessing;

import laf.base.ActionResult;
import laf.core.actionPath.ActionPath;
import laf.core.http.requestMapping.parameterValueProvider.ParameterValueProvider;

public interface RequestProcessor {
	ActionResult process(ActionPath<ParameterValueProvider> path);
}