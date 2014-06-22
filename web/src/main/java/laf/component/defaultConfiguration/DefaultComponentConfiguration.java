package laf.component.defaultConfiguration;

import java.util.*;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import laf.base.ViewTechnology;
import laf.component.ComponentControllerDiscoverer;
import laf.component.basic.htmlTemplate.*;
import laf.component.core.ComponentController;
import laf.component.html.impl.HtmlInvokeReloadReqestProcessor;
import laf.component.html.template.*;
import laf.component.reqestProcessing.*;
import laf.configuration.ConfigurationDefiner;
import laf.configuration.ExtendConfiguration;
import laf.controllerInfo.ControllerDiscoverers;
import laf.html.HtmlViewTechnology;
import laf.requestProcessing.ControllerTypeRequestProcessors;
import laf.requestProcessing.RequestProcessor;

public class DefaultComponentConfiguration implements ConfigurationDefiner {

	@Inject
	Instance<Object> instance;

	@ExtendConfiguration
	public void produce(ControllerDiscoverers discoverers) {
		discoverers.get().add(
				instance.select(ComponentControllerDiscoverer.class).get());
	}

	@ExtendConfiguration
	public void produce(ControllerTypeRequestProcessors map) {
		map.get().put(ComponentController.class,
				instance.select(SwitchComponentRequestProcessor.class).get());
	}

	public void produce(InitialRequestProcessorCP val) {

	}

	public void produce(ReloadRequestProcessorCP val) {

	}

	public void produce(ComponentActionRequestProcessorCP val) {

	}

	@SafeVarargs
	final private <T> Iterable<T> getInstances(Class<? extends T>... classes) {
		ArrayList<T> result = new ArrayList<>();
		for (Class<? extends T> c : classes) {
			result.add(instance.select(c).get());
		}
		return result;
	}

	public void produce(HtmlTemplateFactories factories) {
		ArrayDeque<HtmlTemplateFactory> value = new ArrayDeque<>();
		HtmlTemplateFactoryImpl factory = instance.select(
				HtmlTemplateFactoryImpl.class).get();
		factory.setTemplates(this.<HtmlTemplate<?>> getInstances(
				CButtonHtmlTemplate.class, CPageHtmlTemplate.class,
				CRenderHtmlTemplate.class, CTextFieldHtmlTemplate.class,
				CReloadHtmlTemplate.class, CTextHtmlTemplate.class));
		value.add(factory);
		factories.set(value);
	}

	public void produce(InvokeReloadReqestProcessorsCP val) {
		Map<Class<? extends ViewTechnology>, RequestProcessor> map = new HashMap<>();
		map.put(HtmlViewTechnology.class,
				instance.select(HtmlInvokeReloadReqestProcessor.class).get());
		val.set(map);
	}

}
