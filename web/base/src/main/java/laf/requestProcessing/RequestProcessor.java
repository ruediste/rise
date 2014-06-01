package laf.requestProcessing;

import laf.actionPath.ActionPath;
import laf.base.ActionResult;
import laf.http.requestMapping.parameterValueProvider.ParameterValueProvider;

public interface RequestProcessor {
	ActionResult process(ActionPath<ParameterValueProvider> path);
}