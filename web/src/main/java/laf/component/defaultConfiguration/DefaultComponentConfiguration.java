package laf.component.defaultConfiguration;

import java.util.*;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import laf.base.ViewTechnology;
import laf.base.configuration.ConfigurationDefiner;
import laf.base.configuration.ExtendConfiguration;
import laf.component.ComponentControllerDiscoverer;
import laf.component.basic.htmlTemplate.BasicComponentsHtmlTemplateModule;
import laf.component.core.ComponentController;
import laf.component.html.impl.HtmlInvokeInitialRequestProcessor;
import laf.component.html.impl.HtmlInvokeReloadReqestProcessor;
import laf.component.html.template.*;
import laf.component.reqestProcessing.*;
import laf.controllerInfo.ControllerDiscoverersCP;
import laf.html.HtmlViewTechnology;
import laf.http.requestProcessing.DefaultControllerInvoker;
import laf.requestProcessing.*;

public class DefaultComponentConfiguration implements ConfigurationDefiner {

	@Inject
	Instance<Object> instance;

	@SafeVarargs
	final private <T> Iterable<T> getInstances(Class<? extends T>... classes) {
		ArrayList<T> result = new ArrayList<>();
		for (Class<? extends T> c : classes) {
			result.add(instance.select(c).get());
		}
		return result;
	}

	private <T> T getInstance(Class<T> cls) {
		return instance.select(cls).get();
	}

	@ExtendConfiguration
	public void produce(ControllerDiscoverersCP discoverers) {
		discoverers.get().add(
				instance.select(ComponentControllerDiscoverer.class).get());
	}

	@ExtendConfiguration
	public void produce(ControllerTypeRequestProcessors map) {
		map.get().put(ComponentController.class,
				getInstance(SwitchComponentRequestProcessor.class));
	}

	public void produce(InitialRequestProcessorCP val) {
		PersistenceInitialRequestProcessor persistence = getInstance(PersistenceInitialRequestProcessor.class);
		persistence.initialize(getInstance(InvokeInitialReqestProcessor.class));
		val.set(persistence);
	}

	public void produce(ReloadRequestProcessorCP val) {
		PersistenceInPageRequestProcessor persistence = getInstance(PersistenceInPageRequestProcessor.class);
		persistence.initialize(getInstance(InvokeReloadReqestProcessor.class));
		val.set(persistence);
	}

	public void produce(ComponentActionRequestProcessorCP val) {
		val.set(null);
	}

	public void produce(HtmlTemplateFactories factories) {
		ArrayDeque<HtmlTemplateFactory> value = new ArrayDeque<>();
		HtmlTemplateFactoryImpl factory = getInstance(HtmlTemplateFactoryImpl.class);
		factory.addTemplatesFromPackage(BasicComponentsHtmlTemplateModule.class
				.getPackage().getName());
		value.add(factory);
		factories.set(value);
	}

	public void produce(InvokeReloadReqestProcessorsCP val) {
		Map<Class<? extends ViewTechnology>, RequestProcessor> map = new HashMap<>();
		map.put(HtmlViewTechnology.class,
				instance.select(HtmlInvokeReloadReqestProcessor.class).get());
		val.set(map);
	}

	public void produce(InvokeInitialReqestProcessorsCP val) {
		Map<Class<? extends ViewTechnology>, RequestProcessor> map = new HashMap<>();
		map.put(HtmlViewTechnology.class,
				instance.select(HtmlInvokeInitialRequestProcessor.class).get());
		val.set(map);
	}

	public void produce(InitialParameterLoaderCP val) {
		val.set(getInstance(DefaultParameterLoader.class));
	}

	public void produce(InitialControllerInvokerCP val) {
		val.set(getInstance(DefaultControllerInvoker.class));
	}
}
