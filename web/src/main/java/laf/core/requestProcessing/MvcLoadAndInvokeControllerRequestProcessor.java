package laf.core.requestProcessing;

import javax.inject.Inject;

import laf.base.configuration.ConfigurationValue;

public class MvcLoadAndInvokeControllerRequestProcessor extends LoadAndInvokeRequestProcessor {

	@Inject
	ConfigurationValue<MvcParameterLoaderConfigurationParameter> loader;

	@Inject
	ConfigurationValue<MvcControllerInvokerConfigurationParameter> invoker;

	@Override
	public ParameterLoader getLoader() {
		return loader.value().get();
	}

	@Override
	public ControllerInvoker getInvoker() {
		return invoker.value().get();
	}

}
