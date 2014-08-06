package laf.mvc.configuration;

import java.util.ArrayDeque;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import laf.base.configuration.ConfigurationDefiner;
import laf.base.configuration.ExtendConfiguration;
import laf.controllerInfo.ControllerDiscoverersCP;
import laf.http.requestProcessing.DefaultControllerInvoker;
import laf.mvc.*;
import laf.mvc.html.RendersnakeViewRenderer;
import laf.requestProcessing.*;

public class MvcDefaultConfiguration implements ConfigurationDefiner {

	@Inject
	Instance<Object> instance;

	@ExtendConfiguration
	public void produce(ControllerTypeRequestProcessorsCP map) {
		MvcPersistenceRequestProcessor persistenceProcessor = instance.select(
				MvcPersistenceRequestProcessor.class).get();

		persistenceProcessor.initialize(instance.select(
				MvcLoadAndInvokeControllerRequestProcessor.class).get());

		map.get().put(MvcControllerType.class, persistenceProcessor);
	}

	public void produce(MvcControllerInvokerConfigurationParameter val) {
		val.set(instance.select(DefaultControllerInvoker.class).get());
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
