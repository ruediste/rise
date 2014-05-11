package laf.requestProcessing;

import laf.actionPath.ActionPath;
import laf.base.ActionResult;
import laf.httpRequestMapping.parameterValueProvider.ParameterValueProvider;

public interface RequestProcessor {
	ActionResult process(ActionPath<ParameterValueProvider> path);
}