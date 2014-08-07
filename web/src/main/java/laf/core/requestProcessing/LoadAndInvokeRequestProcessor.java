package laf.core.requestProcessing;

import javax.inject.Inject;

import laf.base.ActionResult;
import laf.core.actionPath.ActionPath;
import laf.core.http.requestMapping.parameterValueProvider.ParameterValueProvider;

public abstract class LoadAndInvokeRequestProcessor implements RequestProcessor {

	@Inject
	ObjectActionPathProducer objectActionPathProducer;

	@Override
	public ActionResult process(ActionPath<ParameterValueProvider> path) {
		ActionPath<Object> objectPath = getLoader().load(path);
		objectActionPathProducer.setPath(objectPath);
		return getInvoker().invoke(objectPath);
	}

	public abstract ParameterLoader getLoader();

	abstract public ControllerInvoker getInvoker();

}