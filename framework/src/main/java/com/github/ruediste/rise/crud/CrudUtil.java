package com.github.ruediste.rise.crud;

import static java.util.stream.Collectors.joining;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

import com.github.ruediste.rise.core.persistence.PersistentType;
import com.github.ruediste.rise.crud.annotations.CrudStrategy;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;
import com.github.ruediste.rise.util.GenericEvent;
import com.github.ruediste.rise.util.Pair;
import com.github.ruediste.salta.jsr330.ImplementedBy;
import com.github.ruediste.salta.jsr330.Injector;
import com.google.common.base.Preconditions;

@Singleton
public class CrudUtil {

    @Inject
    Injector injector;

    private Map<Pair<Class<?>, Class<?>>, Object> explicitStrategyMap = new HashMap<>();

    public <T> void setExplicitStragegy(Class<T> strategyClas,
            Class<?> entityClass, T strategy) {
        explicitStrategyMap.put(Pair.of(strategyClas, entityClass), strategy);
    }

    @SuppressWarnings("unchecked")
    public <T> T getStrategy(Class<T> strategy, Class<?> entityClass) {
        // first check explicitly registered strategies
        Object result = explicitStrategyMap.get(Pair.of(strategy, entityClass));

        // check annotations
        if (result == null) {
            for (CrudStrategy f : entityClass
                    .getAnnotationsByType(CrudStrategy.class)) {
                if (strategy.equals(f.type())) {
                    return (T) injector.getInstance(f.implementation());
                }
            }
        }

        // use default factory
        if (result == null) {
            result = injector.getInstance(strategy);
        }

        return (T) result;
    }

    public interface Filter<T> {
        void applyFilter(Root<T> root, CriteriaBuilder cb);
    }

    /**
     * A browser displays all instances of a certain type and allows the user to
     * search/filter the list. For each instance, certain operations can be
     * performed.
     */
    @ImplementedBy(DefaultBrowserFactory.class)
    public interface BrowserFactory {
        Object createBrowser(Class<?> entityClass,
                Class<? extends Annotation> emQualifier);
    }

    private static class DefaultBrowserFactory implements BrowserFactory {

        @Inject
        Provider<DefaultCrudBrowserController> provider;

        @Override
        public Object createBrowser(Class<?> entityClass,
                Class<? extends Annotation> emQualifier) {
            return provider.get().initialize(entityClass, emQualifier);
        }

    }

    @ImplementedBy(DefaultDisplayFactory.class)
    public interface DisplayFactory {

        Object createDisplay(Object entity);
    }

    private static class DefaultDisplayFactory implements DisplayFactory {

        @Inject
        Provider<DefaultCrudDisplayController> provider;

        @Override
        public Object createDisplay(Object entity) {
            return provider.get().initialize(entity);
        }

    }

    @ImplementedBy(DefaultEditFactory.class)
    public interface EditFactory {

        Object createEdit(Object entity);
    }

    private static class DefaultEditFactory implements EditFactory {

        @Inject
        Provider<DefaultCrudEditController> provider;

        @Override
        public Object createEdit(Object entity) {
            return provider.get().initialize(entity);
        }

    }

    @ImplementedBy(DefaultCreateFactory.class)
    public interface CreateFactory {

        Object createCreate(Class<?> entityClass,
                Class<? extends Annotation> emQualifier);
    }

    private static class DefaultCreateFactory implements CreateFactory {

        @Inject
        Provider<DefaultCrudCreateController> provider;

        @Override
        public Object createCreate(Class<?> entityClass,
                Class<? extends Annotation> emQualifier) {
            return provider.get().initialize(entityClass, emQualifier);
        }

    }

    @ImplementedBy(DefaultDeleteFactory.class)
    public interface DeleteFactory {

        Object createDelete(Object entity);
    }

    private static class DefaultDeleteFactory implements DeleteFactory {

        @Inject
        Provider<DefaultCrudDeleteController> provider;

        @Override
        public Object createDelete(Object entity) {
            return provider.get().initialize(entity);
        }

    }

    @ImplementedBy(DefaultIdentificationRenderer.class)
    public interface IdentificationRenderer {
        void renderIdenification(BootstrapRiseCanvas<?> html, Object entity);
    }

    private static class DefaultIdentificationRenderer implements
            IdentificationRenderer {

        @Inject
        CrudReflectionUtil util;

        @Override
        public void renderIdenification(BootstrapRiseCanvas<?> html,
                Object entity) {
            if (entity == null)
                html.write("<null>");
            else {
                PersistentType type = util.getPersistentType(entity);
                html.write(util
                        .getIdentificationProperties(type)
                        .stream()
                        .map(p -> p.getProperty().getName()
                                + ":"
                                + String.valueOf(p.getProperty().getValue(
                                        entity))).collect(joining(" ")));
            }
        }
    }

    /**
     * A sub controller which allows to pick an instance of an entity
     */
    public interface CrudPicker {

        /**
         * fired when the picker is closed. The argument is the picked entity,
         * or null if picking has been canceled
         */
        GenericEvent<Object> pickerClosed();
    }

    @ImplementedBy(DefaultCrudPickerFactory.class)
    public interface CrudPickerFactory {
        CrudPicker createPicker(Class<? extends Annotation> emQualifier,
                Class<?> entityClass);
    }

    public static class DefaultCrudPickerFactory implements CrudPickerFactory {

        @Inject
        Provider<DefaultCrudPickerController> provider;

        @Override
        public CrudPicker createPicker(Class<? extends Annotation> emQualifier,
                Class<?> entityClass) {
            Preconditions.checkNotNull(entityClass, "entityClass is null");
            return provider.get().initialize(entityClass, emQualifier);
        }

    }
}
