package laf.component.html.template;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import laf.attachedProperties.AttachedProperty;
import laf.component.core.Component;
import laf.configuration.ConfigurationValue;

@ApplicationScoped
public class HtmlTemplateService {

	@Inject
	ConfigurationValue<HtmlTemplateFactories> factories;

	private AttachedProperty<Component, HtmlTemplate<?>> templateProperty = new AttachedProperty<>();

	@SuppressWarnings("unchecked")
	public <T extends Component> HtmlTemplate<T> getTemplate(T component) {
		// get associated template if set
		if (templateProperty.isSet(component)) {
			return (HtmlTemplate<T>) templateProperty.get(component);
		}

		// otherwise create template and associate
		for (HtmlTemplateFactory factory : factories.value().get()) {
			HtmlTemplate<T> template = factory.createTemplate(component);
			if (template != null) {
				templateProperty.set(component, template);
				return template;
			}
		}

		throw new RuntimeException("No Template found for component "
				+ component);
	}

}
