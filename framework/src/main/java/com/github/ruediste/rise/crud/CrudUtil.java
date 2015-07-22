package com.github.ruediste.rise.crud;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

import com.github.ruediste.rise.crud.annotations.CrudStrategy;
import com.github.ruediste.rise.util.Pair;
import com.github.ruediste.salta.jsr330.ImplementedBy;
import com.github.ruediste.salta.jsr330.Injector;

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

    @SuppressWarnings("rawtypes")
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

}
