package laf.component.defaultConfiguration;

import java.util.ArrayDeque;
import java.util.ArrayList;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import laf.component.ComponentController;
import laf.component.ComponentControllerDiscoverer;
import laf.component.basic.html.*;
import laf.component.html.HtmlPageResultRenderer;
import laf.component.html.template.*;
import laf.configuration.ConfigurationDefiner;
import laf.configuration.ExtendConfiguration;
import laf.controllerInfo.ControllerDiscoverers;
import laf.http.requestProcessing.defaultProcessor.ResultRenderers;
import laf.requestProcessing.ControllerTypeRequestProcessors;

public class DefaultComponentConfiguration implements ConfigurationDefiner {

	@Inject
	Instance<Object> instance;

	@ExtendConfiguration
	public void produce(ResultRenderers renderers) {
		renderers.get()
		.add(instance.select(HtmlPageResultRenderer.class).get());
	}

	@ExtendConfiguration
	public void produce(ControllerDiscoverers discoverers) {
		discoverers.get().add(
				instance.select(ComponentControllerDiscoverer.class).get());
	}

	@ExtendConfiguration
	public void produce(ControllerTypeRequestProcessors map) {
		map.get().put(ComponentController.class, null);
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
}
