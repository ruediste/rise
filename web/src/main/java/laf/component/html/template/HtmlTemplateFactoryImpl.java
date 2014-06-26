package laf.component.html.template;

import java.lang.annotation.Annotation;
import java.util.*;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.TypeLiteral;
import javax.inject.Inject;

import laf.component.tree.Component;

import com.google.common.reflect.TypeToken;

public class HtmlTemplateFactoryImpl implements HtmlTemplateFactory {

	@Inject
	Instance<Object> instance;

	@Inject
	BeanManager beanManager;

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

	public void addTemplates(Iterable<HtmlTemplate<?>> templates) {
		for (HtmlTemplate<?> template : templates) {
			Class<?> componentType = TypeToken.of(template.getClass())
					.resolveType(HtmlTemplate.class.getTypeParameters()[0])
					.getRawType();
			this.templates.put(componentType, template);
		}
	}

	public void addTemplatesFromPackage(String pkg) {
		Default defaultAnnotation = new Default() {
			@Override
			public Class<? extends Annotation> annotationType() {
				return Default.class;
			}
		};
		ArrayList<HtmlTemplate<?>> templates = new ArrayList<>();
		for (Bean<?> bean : beanManager
				.getBeans(new TypeLiteral<HtmlTemplate<?>>() {
					private static final long serialVersionUID = 1L;
				}.getType())) {
			Package beanPackage = bean.getBeanClass().getPackage();
			if (beanPackage != null && beanPackage.getName().startsWith(pkg)) {
				templates.add((HtmlTemplate<?>) instance.select(
						bean.getBeanClass()).get());
			}
		}
		addTemplates(templates);
	}
}
