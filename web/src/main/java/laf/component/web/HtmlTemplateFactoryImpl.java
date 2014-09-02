package laf.component.web;

import java.lang.annotation.Annotation;
import java.util.*;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.TypeLiteral;
import javax.inject.Inject;

import laf.component.core.tree.Component;

import com.google.common.reflect.TypeToken;

public class HtmlTemplateFactoryImpl implements HtmlTemplateFactory {

	@Inject
	Instance<Object> instance;

	@Inject
	BeanManager beanManager;

	private final Map<Class<?>, CWTemplate<?>> templates = new HashMap<>();

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Component> CWTemplate<T> createTemplate(T component) {
		// try the component class or any super class
		Class<?> c = component.getClass();
		CWTemplate<?> result = null;
		while (c != null) {
			result = templates.get(c);
			if (result != null) {
				return (CWTemplate<T>) result;
			}
			c = c.getSuperclass();
		}
		return null;
	}

	public void addTemplates(Iterable<CWTemplate<?>> templates) {
		for (CWTemplate<?> template : templates) {
			Class<?> componentType = TypeToken.of(template.getClass())
					.resolveType(CWTemplate.class.getTypeParameters()[0])
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
		ArrayList<CWTemplate<?>> templates = new ArrayList<>();
		for (Bean<?> bean : beanManager
				.getBeans(new TypeLiteral<CWTemplate<?>>() {
					private static final long serialVersionUID = 1L;
				}.getType())) {
			Package beanPackage = bean.getBeanClass().getPackage();
			if (beanPackage != null && beanPackage.getName().startsWith(pkg)) {
				templates.add((CWTemplate<?>) instance.select(
						bean.getBeanClass()).get());
			}
		}
		addTemplates(templates);
	}
}
