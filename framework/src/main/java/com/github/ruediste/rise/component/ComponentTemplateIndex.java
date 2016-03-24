package com.github.ruediste.rise.component;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;

import com.github.ruediste.rise.component.components.DefaultTemplate;
import com.github.ruediste.rise.component.components.IComponentTemplate;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.util.Try;
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

    private ConcurrentHashMap<Class<?>, Optional<IComponentTemplate<?>>> templates = new ConcurrentHashMap<>();

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T extends Component> Try<IComponentTemplate<T>> getTemplate(T component) {
        return getTemplate((Class) component.getClass());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T extends Component> Try<IComponentTemplate<T>> getTemplate(Class<T> component) {
        return (Try) Try.of(
                templates.computeIfAbsent(component,
                        cls -> Optional.ofNullable(component.getAnnotation(DefaultTemplate.class))
                                .map(x -> injector.getInstance(x.value()))),
                () -> new RuntimeException("No template has been registered explicitely for " + component.getName()
                        + " and no @DefaultTemplate annotation is present"));
    }

    public <T extends Component> void registerTemplate(Class<T> component, IComponentTemplate<T> template) {
        templates.put(component, Optional.of(template));
    }

    public <T extends Component> void registerTemplate(Class<T> component,
            Class<? extends IComponentTemplate<T>> template) {
        templates.put(component, Optional.of(injector.getInstance(template)));
    }
}
