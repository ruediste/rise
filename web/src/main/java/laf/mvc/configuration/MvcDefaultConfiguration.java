package laf.mvc.configuration;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import laf.configuration.ConfigurationDefiner;
import laf.configuration.ExtendConfiguration;
import laf.controllerInfo.ControllerDiscoverers;
import laf.mvc.Controller;
import laf.mvc.MvcControllerDiscoverer;
import laf.mvc.MvcControllerInvoker;
import laf.mvc.MvcPersistenceRequestProcessor;
import laf.requestProcessing.ControllerTypeRequestProcessors;
import laf.requestProcessing.DefaultParameterLoader;
import laf.requestProcessing.MvcControllerInvokerConfigurationParameter;
import laf.requestProcessing.MvcLoadAndInvokeControllerRequestProcessor;
import laf.requestProcessing.MvcParameterLoaderConfigurationParameter;

public class MvcDefaultConfiguration implements ConfigurationDefiner {

	@Inject
	Instance<Object> instance;

	@ExtendConfiguration
	public void produce(ControllerTypeRequestProcessors map) {
		MvcPersistenceRequestProcessor persistenceProcessor = instance.select(
				MvcPersistenceRequestProcessor.class).get();

		persistenceProcessor.initialize(instance.select(
				MvcLoadAndInvokeControllerRequestProcessor.class).get());

		map.get().put(Controller.class, persistenceProcessor);
	}

	public void produce(MvcControllerInvokerConfigurationParameter val) {
		val.set(instance.select(MvcControllerInvoker.class).get());
	}

	public void produce(MvcParameterLoaderConfigurationParameter val) {
		val.set(instance.select(DefaultParameterLoader.class).get());
	}

	@ExtendConfiguration
	public void produce(ControllerDiscoverers val) {
		val.get().add(instance.select(MvcControllerDiscoverer.class).get());
	}
}
