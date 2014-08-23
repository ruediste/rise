package laf.mvc.configuration;

import java.util.ArrayDeque;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import laf.base.configuration.ConfigurationDefiner;
import laf.base.configuration.ExtendConfiguration;
import laf.core.controllerInfo.ControllerDiscoverersCP;
import laf.core.requestProcessing.*;
import laf.mvc.*;
import laf.mvc.html.RendersnakeViewRenderer;

public class MvcDefaultConfiguration implements ConfigurationDefiner {

	@Inject
	Instance<Object> instance;

	@ExtendConfiguration
	public void produce(ControllerTypeRequestProcessorsCP map) {
		MvcPersistenceRequestHandlerImpl persistenceProcessor = instance.select(
				MvcPersistenceRequestHandlerImpl.class).get();

		persistenceProcessor.initialize(instance.select(
				MvcLoadAndInvokeControllerRequestProcessor.class).get());

		map.get().put(MvcControllerType.class, persistenceProcessor);
	}

	public void produce(MvcControllerInvokerConfigurationParameter val) {
		val.set(instance.select(ControllerInvokerImpl.class).get());
	}

	public void produce(MvcParameterLoaderConfigurationParameter val) {
		val.set(instance.select(DefaultParameterLoader.class).get());
	}

	@ExtendConfiguration
	public void produce(ControllerDiscoverersCP val) {
		val.get().add(instance.select(MvcControllerDiscoverer.class).get());
	}

	public void produce(ViewRenderers val) {
		ArrayDeque<ViewRenderer> renderers = new ArrayDeque<>();
		renderers.add(instance.select(RendersnakeViewRenderer.class).get());
		val.set(renderers);
	}
}
