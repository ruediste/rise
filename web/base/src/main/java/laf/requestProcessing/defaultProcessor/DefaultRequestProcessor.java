package laf.requestProcessing.defaultProcessor;

import javax.inject.Inject;

import laf.actionPath.ActionPath;
import laf.base.ActionResult;
import laf.configuration.ConfigurationValue;
import laf.httpRequestMapping.parameterValueProvider.ParameterValueProvider;
import laf.requestProcessing.*;

public class DefaultRequestProcessor implements RequestProcessor {

	@Inject
	ConfigurationValue<ControllerInvokerConfigurationParameter> invoker;

	@Inject
	ConfigurationValue<ParameterLoaderConfigurationParameter> loader;

	@Inject
	ObjectActionPathProducer objectActionPathProducer;

	@Override
	public ActionResult process(ActionPath<ParameterValueProvider> path) {
		ActionPath<Object> objectPath = loader.value().get().load(path);
		objectActionPathProducer.setPath(objectPath);
		return invoker.value().get().invoke(objectPath);
	}
}