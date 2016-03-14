package com.github.ruediste.rise.component;

import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;

import com.github.ruediste.rise.component.components.DefaultTemplate;
import com.github.ruediste.rise.component.components.IComponentTemplate;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.salta.jsr330.Injector;

/**
 * Index storing the {@link IComponentTemplate}s associated with a
 * {@link Component}. The index typically looks up templates using the
 * {@link DefaultTemplate} annotation.
 */
@Singleton
public class ComponentTemplateIndex {

    @Inject
    Logger log;

    @Inject
    Injector injector;

    private ConcurrentHashMap<Class<?>, IComponentTemplate<?>> templates = new ConcurrentHashMap<>();

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T extends Component> IComponentTemplate<T> getTemplate(T component) {
        return getTemplate((Class) component.getClass());
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> IComponentTemplate<T> getTemplate(Class<T> component) {
        return (IComponentTemplate<T>) templates.computeIfAbsent(component, cls -> {
            DefaultTemplate defaultTemplate = component.getAnnotation(DefaultTemplate.class);
            if (defaultTemplate == null) {
                throw new RuntimeException("No template has been registered explicitely for " + component.getName()
                        + " and no @DefaultTemplate annotation is present");
            }
            return injector.getInstance(defaultTemplate.value());
        });
    }

    public <T extends Component> void registerTemplate(Class<T> component, IComponentTemplate<T> template) {
        templates.put(component, template);
    }

    public <T extends Component> void registerTemplate(Class<T> component,
            Class<? extends IComponentTemplate<T>> template) {
        templates.put(component, injector.getInstance(template));
    }
}
