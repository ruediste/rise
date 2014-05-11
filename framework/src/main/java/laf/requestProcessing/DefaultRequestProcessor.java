package laf.requestProcessing;

import javax.inject.Inject;

import laf.actionPath.ActionPath;
import laf.base.ActionResult;
import laf.httpRequestMapping.parameterValueProvider.ParameterValueProvider;

public class DefaultRequestProcessor implements RequestProcessor {

	@Inject
	DefaultControllerInvoker invoker;

	@Inject
	DefaultParameterLoader loader;

	@Override
	public ActionResult process(ActionPath<ParameterValueProvider> path) {
		return invoker.invoke(loader.load(path));
	}
}