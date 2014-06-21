package laf.component.defaultConfiguration;

import java.util.ArrayDeque;
import java.util.ArrayList;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import laf.component.ComponentControllerDiscoverer;
import laf.component.basic.htmlTemplate.CButtonHtmlTemplate;
import laf.component.basic.htmlTemplate.CPageHtmlTemplate;
import laf.component.basic.htmlTemplate.CReloadHtmlTemplate;
import laf.component.basic.htmlTemplate.CRenderHtmlTemplate;
import laf.component.basic.htmlTemplate.CTextFieldHtmlTemplate;
import laf.component.basic.htmlTemplate.CTextHtmlTemplate;
import laf.component.core.ComponentController;
import laf.component.html.template.HtmlTemplate;
import laf.component.html.template.HtmlTemplateFactories;
import laf.component.html.template.HtmlTemplateFactory;
import laf.component.html.template.HtmlTemplateFactoryImpl;
import laf.component.reqestProcessing.ComponentActionRequestProcessorCP;
import laf.component.reqestProcessing.InitialRequestProcessorCP;
import laf.component.reqestProcessing.ReloadRequestProcessorCP;
import laf.component.reqestProcessing.SwitchComponentRequestProcessor;
import laf.configuration.ConfigurationDefiner;
import laf.configuration.ExtendConfiguration;
import laf.controllerInfo.ControllerDiscoverers;
import laf.requestProcessing.ControllerTypeRequestProcessors;

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
}
