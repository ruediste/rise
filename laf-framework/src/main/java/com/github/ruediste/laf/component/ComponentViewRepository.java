package com.github.ruediste.laf.component;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;

import com.github.ruediste.laf.api.CView;
import com.github.ruediste.laf.core.CoreConfiguration;
import com.github.ruediste.laf.core.front.reload.ClassHierarchyCache;
import com.github.ruediste.laf.util.AsmUtil;
import com.github.ruediste.laf.util.Pair;
import com.github.ruediste.salta.jsr330.Injector;

/**
 * Repository keeping track of all {@link CView}s
 */
@Singleton
public class ComponentViewRepository {

	@Inject
	Logger log;

	@Inject
	Injector injector;

	@Inject
	CoreConfiguration config;

	@Inject
	ClassHierarchyCache index;

	Map<Pair<String, String>, ViewEntry> viewMap = new HashMap<>();

	private static class ViewEntry {
		public String viewClassInternalName;
	}

	@PostConstruct
	public void initialize() {
		// iterate over all views
		for (ClassNode view : index.getAllChildren(Type
				.getInternalName(CView.class))) {
			// check if it is a concrete class
			if ((view.access & Opcodes.ACC_ABSTRACT) != 0) {
				continue;
			}

			// find the controller class of the view
			String controllerClass = index.resolve(view, CView.class,
					"TController");

			// create an entry for the view
			ViewEntry entry = new ViewEntry();
			entry.viewClassInternalName = view.name;

			AnnotationNode viewQualifierAnnotation = AsmUtil
					.getAnnotationByType(view.visibleAnnotations,
							ViewQualifier.class);
			String qualifier = null;
			if (viewQualifierAnnotation != null) {
				qualifier = AsmUtil.getType(viewQualifierAnnotation, "value")
						.getInternalName();
			}

			ViewEntry existing = viewMap.put(
					Pair.of(controllerClass, qualifier), entry);

			if (existing != null) {
				throw new RuntimeException(
						"Two views found for controller " + controllerClass
								+ " and qualifier " + qualifier == null ? "null"
								: qualifier + ": "
										+ entry.viewClassInternalName + ", "
										+ existing.viewClassInternalName);
			}
			log.info("found view " + view.name + " with qualifier " + qualifier
					+ " for controller " + controllerClass);
		}
	}

	/**
	 * Create a view for the given controller
	 */
	public <T> CView<T> createView(T controller) {
		return createView(controller, null);
	}

	/**
	 * Create a view for the given controller and qualifier.
	 * 
	 * @param qualifier
	 *            qualifier class, or null if no qualifier is set
	 */
	@SuppressWarnings("unchecked")
	public <T> CView<T> createView(T controller,
			Class<? extends IViewQualifier> qualifier) {
		Class<? extends Object> controllerClass = controller.getClass();
		// get the list of possible views
		ViewEntry entry = viewMap.get(Pair.create(Type
				.getInternalName(controllerClass), qualifier == null ? null
				: Type.getInternalName(qualifier)));
		if (entry == null) {
			throw new RuntimeException("There is no view for controller class "
					+ controllerClass.getName() + " and qualifier "
					+ (qualifier == null ? "null" : qualifier.getName()));
		}

		// create view instance
		Class<?> viewClass = AsmUtil.loadClass(
				Type.getObjectType(entry.viewClassInternalName),
				config.dynamicClassLoader);
		CView<T> result = (CView<T>) injector.getInstance(viewClass);
		result.initialize(controller);
		return result;
	}
}
