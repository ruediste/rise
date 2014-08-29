package laf.component.web.template;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import laf.component.core.tree.Component;
import laf.component.web.api.CWTemplate;
import laf.core.base.attachedProperties.AttachedProperty;
import laf.core.base.configuration.ConfigurationValue;

@ApplicationScoped
public class HtmlTemplateService {

	@Inject
	ConfigurationValue<HtmlTemplateFactories> factories;

	private AttachedProperty<Component, CWTemplate<?>> templateProperty = new AttachedProperty<>();

	@SuppressWarnings("unchecked")
	public <T extends Component> CWTemplate<T> getTemplate(T component) {
		// get associated template if set
		if (templateProperty.isSet(component)) {
			return (CWTemplate<T>) templateProperty.get(component);
		}

		// otherwise create template and associate
		for (HtmlTemplateFactory factory : factories.value().get()) {
			CWTemplate<T> template = factory.createTemplate(component);
			if (template != null) {
				templateProperty.set(component, template);
				return template;
			}
		}

		throw new RuntimeException("No Template found for component "
				+ component);
	}

}
