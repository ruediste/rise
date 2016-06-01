package com.github.ruediste.rise.component;

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

import com.github.ruediste.rise.api.SubControllerComponent;
import com.github.ruediste.rise.api.ViewComponentBase;
import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.nonReloadable.front.reload.ClassHierarchyIndex;
import com.github.ruediste.rise.util.AsmUtil;
import com.github.ruediste.rise.util.Pair;
import com.github.ruediste.salta.jsr330.Injector;

/**
 * Repository keeping track of all {@link ViewComponentBase}s
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
    ClassHierarchyIndex index;

    @Inject
    ClassLoader classLoader;

    @Inject
    ComponentPage componentPage;

    Map<Pair<String, String>, ViewEntry> viewMap = new HashMap<>();

    private static class ViewEntry {
        public String viewClassInternalName;
    }

    public ComponentViewRepository() {
    }

    @PostConstruct
    public void initialize() {
        // iterate over all views
        for (ClassNode view : index.getAllChildren(Type.getInternalName(ViewComponentBase.class))) {
            // check if it is a concrete class
            if ((view.access & Opcodes.ACC_ABSTRACT) != 0) {
                continue;
            }

            // find the controller class of the view
            String controllerClass = index.resolve(view, ViewComponentBase.class, "TController");

            // create an entry for the view
            ViewEntry entry = new ViewEntry();
            entry.viewClassInternalName = view.name;

            AnnotationNode viewQualifierAnnotation = AsmUtil.getAnnotationByType(view.visibleAnnotations,
                    ViewQualifier.class);
            String qualifier = null;
            if (viewQualifierAnnotation != null) {
                qualifier = AsmUtil.getType(viewQualifierAnnotation, "value").getInternalName();
            }

            ViewEntry existing = viewMap.put(Pair.of(controllerClass, qualifier), entry);

            if (existing != null) {
                throw new RuntimeException("Two views found for controller " + controllerClass + " and qualifier "
                        + (qualifier == null ? "null" : qualifier) + ": " + entry.viewClassInternalName + ", "
                        + existing.viewClassInternalName);
            }
            log.debug(
                    "found view " + view.name + " with qualifier " + qualifier + " for controller " + controllerClass);
        }
    }

    /**
     * Create a view for the given controller and qualifier.
     * 
     * @param qualifier
     *            qualifier class, or null if no qualifier is set
     */
    @SuppressWarnings("unchecked")
    public <T extends SubControllerComponent> ViewComponentBase<T> createView(T controller, boolean setAsRootView,
            Class<? extends IViewQualifier> qualifier) {
        // get view, trying super classes
        ViewEntry entry = null;
        {
            Class<? extends Object> controllerClass = controller.getClass();
            String qualifierName = qualifier == null ? null : Type.getInternalName(qualifier);
            while (controllerClass != null && entry == null) {
                entry = viewMap.get(Pair.create(Type.getInternalName(controllerClass), qualifierName));
                if (entry == null)
                    controllerClass = controllerClass.getSuperclass();
            }
        }

        if (entry == null) {
            throw new RuntimeException("There is no view for controller class " + controller.getClass().getName()
                    + " and qualifier " + (qualifier == null ? "null" : qualifier.getName()));

        }

        // create view instance
        Class<?> viewClass = AsmUtil.loadClass(Type.getObjectType(entry.viewClassInternalName), classLoader);
        ViewComponentBase<T> result = (ViewComponentBase<T>) injector.getInstance(viewClass);
        if (setAsRootView)
            componentPage.setView(result);
        result.initialize(controller);
        return result;
    }
}
