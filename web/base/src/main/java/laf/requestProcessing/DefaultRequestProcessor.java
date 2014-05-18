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

	@Inject
	ObjectActionPathProducer objectActionPathProducer;

	@Override
	public ActionResult process(ActionPath<ParameterValueProvider> path) {
		ActionPath<Object> objectPath = loader.load(path);
		objectActionPathProducer.setPath(objectPath);
		return invoker.invoke(objectPath);
	}
}