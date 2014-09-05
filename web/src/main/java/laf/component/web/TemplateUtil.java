package laf.component.web;

import javax.enterprise.context.RequestScoped;

import laf.component.core.tree.Component;
import laf.core.base.attachedProperties.AttachedProperty;

@RequestScoped
public class TemplateUtil {
	private Iterable<HtmlTemplateFactory> factories;
	private static AttachedProperty<Component, CWTemplate<?>> templateProperty = new AttachedProperty<>();

	public void initialize(Iterable<HtmlTemplateFactory> factories) {
		this.factories = factories;
	}

	@SuppressWarnings("unchecked")
	public <T extends Component> CWTemplate<T> getTemplate(T component) {
		// get associated template if set
		if (templateProperty.isSet(component)) {
			return (CWTemplate<T>) templateProperty.get(component);
		}

		// otherwise create template and associate
		for (HtmlTemplateFactory factory : factories) {
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
