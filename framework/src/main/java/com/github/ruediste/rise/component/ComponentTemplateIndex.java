package com.github.ruediste.rise.component;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
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
    public Try<IComponentTemplate> getTemplateRaw(Component<?> component) {
        return (Try) getTemplate(component);
    }

    public Try<IComponentTemplate<?>> getTemplate(Component<?> component) {
        return getTemplate(component.getClass());
    }

    public Try<IComponentTemplate<?>> getTemplate(Class<?> component) {
        return Try.of(
                templates.computeIfAbsent(component,
                        cls -> extractTemplate(component).map(c -> injector.getInstance(c))),
                () -> new RuntimeException("No template has been registered explicitely for " + component.getName()
                        + ", no @DefaultTemplate annotation is present and no single matching inner template class has been found"));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    Optional<Class<? extends IComponentTemplate>> extractTemplate(Class<?> component) {
        DefaultTemplate annotation = component.getAnnotation(DefaultTemplate.class);
        if (annotation != null)
            return Optional.of(annotation.value());

        Class<?> cls = component;
        do {
            ArrayList<Class<?>> candidates = new ArrayList<>();
            for (Class<?> template : cls.getDeclaredClasses()) {
                if (template.isSynthetic())
                    continue;
                if (!Modifier.isStatic(template.getModifiers()))
                    continue;
                if (!(IComponentTemplate.class.isAssignableFrom(template)))
                    continue;
                candidates.add(template);
            }
            if (candidates.size() == 1)
                return (Optional) Optional.of(candidates.get(0));
            cls = cls.getSuperclass();
        } while (cls != null);
        return Optional.empty();
    }

    public <T extends Component<T>> void registerTemplate(Class<T> component, IComponentTemplate<T> template) {
        templates.put(component, Optional.of(template));
    }

    public <T extends Component<T>> void registerTemplate(Class<T> component,
            Class<? extends IComponentTemplate<T>> template) {
        templates.put(component, Optional.of(injector.getInstance(template)));
    }
}
