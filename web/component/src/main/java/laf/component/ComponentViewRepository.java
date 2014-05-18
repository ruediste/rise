package laf.component;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import com.google.common.reflect.TypeToken;

@ApplicationScoped
public class ComponentViewRepository {

	@Inject
	Instance<ComponentView<?>> componentViewInstance;

	@Inject
	BeanManager beanManager;

	Map<Class<?>, List<ViewEntry>> viewMap = new HashMap<>();

	private static class ViewEntry {
		public Class<? extends IViewQualifier> qualifier;
		public Class<? extends ComponentView<?>> viewClass;
	}

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void initialize() {

		for (Bean<?> bean : beanManager.getBeans(
				new TypeToken<ComponentView<?>>() {
					private static final long serialVersionUID = 1L;
				}.getType(), new Any() {

					@Override
					public Class<? extends Annotation> annotationType() {
						return Any.class;
					}
				})) {
			Class<?> controllerClass = TypeToken.of(bean.getBeanClass())
					.resolveType(ComponentView.class.getTypeParameters()[0])
					.getRawType();
			List<ViewEntry> list = viewMap.get(controllerClass);
			if (list == null) {
				list = new ArrayList<>();
				viewMap.put(controllerClass, list);
			}

			ViewEntry entry = new ViewEntry();
			entry.viewClass = (Class<? extends ComponentView<?>>) bean
					.getBeanClass();
			ViewQualifier viewQualifier = bean.getBeanClass().getAnnotation(
					ViewQualifier.class);
			if (viewQualifier != null) {
				entry.qualifier = viewQualifier.value();
			}
			list.add(entry);
		}
	}

	public <T> ComponentView<T> createView(Class<T> controllerClass) {
		return createView(controllerClass, null);
	}

	@SuppressWarnings("unchecked")
	public <T> ComponentView<T> createView(Class<T> controllerClass,
			Class<? extends IViewQualifier> qualifier) {
		// get the list of possible views
		List<ViewEntry> list = viewMap.get(controllerClass);
		if (list == null) {
			throw new RuntimeException(
					"There are no views for controller class "
							+ controllerClass.getName());
		}

		// choose view by qualifiers
		ViewEntry matchingEntry = null;
		entryLoop: for (ViewEntry entry : list) {
			if (qualifier != null) {
				if (!Objects.equals(entry.qualifier, qualifier)) {
					continue entryLoop;
				}
			}
			if (matchingEntry != null) {
				throw new RuntimeException(
						"Multiple views found for controller classs "
								+ controllerClass.getName() + " and qualifier "
								+ qualifier);
			}
			matchingEntry = entry;
		}

		// create view instance
		return (ComponentView<T>) componentViewInstance.select(
				matchingEntry.viewClass).get();
	}
}
