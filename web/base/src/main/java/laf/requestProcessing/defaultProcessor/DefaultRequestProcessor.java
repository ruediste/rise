package laf.requestProcessing.defaultProcessor;

import javax.inject.Inject;

import laf.actionPath.ActionPath;
import laf.base.ActionResult;
import laf.httpRequestMapping.parameterValueProvider.ParameterValueProvider;
import laf.requestProcessing.ControllerInvokerConfigurationValue;
import laf.requestProcessing.ObjectActionPathProducer;
import laf.requestProcessing.ParameterLoaderConfigurationValue;
import laf.requestProcessing.RequestProcessor;

public class DefaultRequestProcessor implements RequestProcessor {

	@Inject
	ControllerInvokerConfigurationValue invoker;

	@Inject
	ParameterLoaderConfigurationValue loader;

	@Inject
	ObjectActionPathProducer objectActionPathProducer;

	@Override
	public ActionResult process(ActionPath<ParameterValueProvider> path) {
		ActionPath<Object> objectPath = loader.get().load(path);
		objectActionPathProducer.setPath(objectPath);
		return invoker.get().invoke(objectPath);
	}
}