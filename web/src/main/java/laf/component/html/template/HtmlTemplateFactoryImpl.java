package laf.component.html.template;

import java.util.HashMap;
import java.util.Map;

import laf.component.tree.Component;

import com.google.common.reflect.TypeToken;

public class HtmlTemplateFactoryImpl implements HtmlTemplateFactory {

	private final Map<Class<?>, HtmlTemplate<?>> templates = new HashMap<>();

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Component> HtmlTemplate<T> createTemplate(T component) {
		// try the component class or any super class
		Class<?> c = component.getClass();
		HtmlTemplate<?> result = null;
		while (c != null) {
			result = templates.get(c);
			if (result != null) {
				return (HtmlTemplate<T>) result;
			}
			c = c.getSuperclass();
		}
		return null;
	}

	public void setTemplates(Iterable<HtmlTemplate<?>> templates) {
		for (HtmlTemplate<?> template : templates) {
			Class<?> componentType = TypeToken.of(template.getClass())
					.resolveType(HtmlTemplate.class.getTypeParameters()[0])
					.getRawType();
			this.templates.put(componentType, template);
		}
	}
}
