package laf.component.core;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import laf.component.core.api.CView;
import laf.core.base.Pair;

import com.google.common.reflect.TypeToken;

@ApplicationScoped
public class ComponentViewRepository {

	@Inject
	Instance<CView<?>> componentViewInstance;

	@Inject
	BeanManager beanManager;

	Map<Pair<Class<?>, Class<? extends IViewQualifier>>, ViewEntry> viewMap = new HashMap<>();

	private static class ViewEntry {
		public Class<? extends CView<?>> viewClass;
	}

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void initialize() {
		// iterate over all views
		for (Bean<?> bean : beanManager.getBeans(
				new TypeToken<CView<?>>() {
					private static final long serialVersionUID = 1L;
				}.getType(), new Any() {

					@Override
					public Class<? extends Annotation> annotationType() {
						return Any.class;
					}
				})) {
			// find the controller class of the view
			Class<?> controllerClass = TypeToken.of(bean.getBeanClass())
					.resolveType(CView.class.getTypeParameters()[0])
					.getRawType();

			// create an entry for the view
			ViewEntry entry = new ViewEntry();

			entry.viewClass = (Class<? extends CView<?>>) bean
					.getBeanClass();
			ViewQualifier viewQualifierAnnotation = bean.getBeanClass()
					.getAnnotation(ViewQualifier.class);
			Class<? extends IViewQualifier> qualifier = null;
			if (viewQualifierAnnotation != null) {
				qualifier = viewQualifierAnnotation.value();
			}

			ViewEntry existing = viewMap.put(
					new Pair<Class<?>, Class<? extends IViewQualifier>>(
							controllerClass, qualifier), entry);

			if (existing != null) {
				throw new RuntimeException("Two views found for controller "
						+ controllerClass.getName() + " and qualifier "
						+ qualifier == null ? "null" : qualifier.getName()
						+ ": " + entry.viewClass.getName() + ", "
						+ existing.viewClass.getName());
			}
		}
	}

	/**
	 * Create a view for the given controller
	 */
	public <T> CView<T> createView(T controller) {
		return createView(controller, null);
	}

	/**
	 * Create a view for the given controller and qualifier
	 */
	@SuppressWarnings("unchecked")
	public <T> CView<T> createView(T controller,
			Class<? extends IViewQualifier> qualifier) {
		Class<? extends Object> controllerClass = controller.getClass();
		// get the list of possible views
		ViewEntry entry = viewMap.get(Pair.create(controllerClass, qualifier));
		if (entry == null) {
			throw new RuntimeException(
					"There is no view for controller class "
							+ controllerClass.getName() + " and qualifier "
							+ qualifier == null ? "null" : qualifier.getName());
		}

		// create view instance
		CView<T> result = (CView<T>) componentViewInstance
				.select(entry.viewClass).get();
		result.initialize(controller);
		return result;
	}
}
