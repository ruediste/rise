package com.github.ruediste.rise.crud;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

import com.github.ruediste.rise.crud.annotations.CrudFactory;
import com.github.ruediste.rise.util.Pair;
import com.github.ruediste.salta.jsr330.ImplementedBy;
import com.github.ruediste.salta.jsr330.Injector;

@Singleton
public class CrudUtil {

    @Inject
    Injector injector;

    private Map<Pair<Class<?>, Class<?>>, Object> explicitFactoryMap = new HashMap<>();

    public <T> void setExplicitFactory(Class<T> factoryClass,
            Class<?> entityClass, T factory) {
        explicitFactoryMap.put(Pair.of(factoryClass, entityClass), factory);
    }

    @SuppressWarnings("unchecked")
    public <T> T getFactory(Class<T> factoryClass, Class<?> entityClass) {
        // first check explicitly registered factories
        Object result = explicitFactoryMap.get(Pair.of(factoryClass,
                entityClass));

        // check annotations
        if (result == null) {
            for (CrudFactory f : entityClass
                    .getAnnotationsByType(CrudFactory.class)) {
                if (factoryClass.equals(f.type())) {
                    return (T) injector.getInstance(f.implementation());
                }
            }
        }

        // use default factory
        if (result == null) {
            result = injector.getInstance(factoryClass);
        }

        return (T) result;
    }

    public interface Filter<T> {
        void applyFilter(Root<T> root, CriteriaBuilder cb);
    }

    public static class BrowserSettings<T> {
        final public List<Consumer<T>> additionalOperations = new ArrayList<>();
        final public List<Filter<T>> fixedFilters = new ArrayList<>();
        public Class<? extends Annotation> emQualifier;
    }

    /**
     * A browser displays all instances of a certain type and allows the user to
     * search/filter the list. For each instance, certain operations can be
     * performed.
     */
    @ImplementedBy(DefaultBrowserFactory.class)
    public interface BrowserFactory {
        <T> Object createBrowser(Class<T> entityClass,
                BrowserSettings<T> settings);
    }

    @SuppressWarnings("rawtypes")
    private static class DefaultBrowserFactory implements BrowserFactory {

        @Inject
        Provider<DefaultCrudBrowserController> browserControllerProvider;

        @SuppressWarnings("unchecked")
        @Override
        public <T> Object createBrowser(Class<T> entityClass,
                BrowserSettings<T> settings) {

            return browserControllerProvider.get().initialize(entityClass,
                    settings);
        }

    }

    /*
     */
}
