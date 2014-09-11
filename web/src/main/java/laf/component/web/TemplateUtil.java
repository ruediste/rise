package laf.component.web;

import javax.inject.Inject;

import laf.component.core.tree.Component;
import laf.core.base.attachedProperties.AttachedProperty;

public class TemplateUtil {
	@Inject
	WebRequestInfo webRequestInfo;
	private static AttachedProperty<Component, CWTemplate<?>> templateProperty = new AttachedProperty<>();

	@SuppressWarnings("unchecked")
	public <T extends Component> CWTemplate<T> getTemplate(T component) {
		// get associated template if set
		if (templateProperty.isSet(component)) {
			return (CWTemplate<T>) templateProperty.get(component);
		}

		// otherwise create template and associate
		for (HtmlTemplateFactory factory : webRequestInfo
				.getTemplateFactories()) {
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
