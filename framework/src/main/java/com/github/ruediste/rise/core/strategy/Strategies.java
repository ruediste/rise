package com.github.ruediste.rise.core.strategy;

import java.lang.reflect.AnnotatedElement;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.ruediste.salta.jsr330.Injector;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;

/**
 * Contains {@link Strategy strategies} and allows to retrieve them. Strategies
 * can be added on a per Java-element (method, field, ...) level using
 * {@link UseStrategy}..
 */
@Singleton
public class Strategies {

    @Inject
    Injector injector;

    private final ListMultimap<Class<?>, Strategy<?>> strategies = MultimapBuilder
            .hashKeys().arrayListValues().build();

    public void putStrategy(Strategy<?> strategy) {
        getStrategyClasses(strategy.getClass())
                .forEach(s -> strategies.put(s, strategy));
    }

    public <TKey, T extends Strategy<TKey>> void putStrategyFirst(T strategy) {
        getStrategyClasses(strategy.getClass())
                .forEach(s -> strategies.get(s).add(0, strategy));
    }

    HashSet<Class<?>> getStrategyClasses(Class<?> cls) {
        HashSet<Class<?>> classes = new HashSet<>();
        fillStrategyClasses(cls, classes);
        return classes;
    }

    private void fillStrategyClasses(Class<?> cls, Set<Class<?>> classes) {
        if (cls.getSuperclass() != null
                && Strategy.class.isAssignableFrom(cls.getSuperclass())) {
            if (classes.add(cls.getSuperclass()))
                fillStrategyClasses(cls.getSuperclass(), classes);
        }

        for (Class<?> i : cls.getInterfaces()) {
            if (Strategy.class.equals(i))
                continue;
            if (Strategy.class.isAssignableFrom(i) && classes.add(i))
                fillStrategyClasses(i, classes);
        }
    }

    @SuppressWarnings("unchecked")
    public <TKey, T extends Strategy<TKey>> Optional<T> getStrategy(
            Class<T> strategyClass, TKey key) {
        return strategies.get(strategyClass).stream()
                .filter(x -> ((Strategy<TKey>) x).applies(key))
                .map(strategyClass::cast).findFirst();
    }

    public <TKey, T extends Strategy<TKey>> Optional<T> getStrategy(
            Class<T> strategyClass, TKey key, AnnotatedElement element) {
        for (UseStrategy a : element.getAnnotationsByType(UseStrategy.class)) {
            if (strategyClass.isAssignableFrom(a.value())) {
                T strategy = strategyClass
                        .cast(injector.getInstance(a.value()));
                if (strategy.applies(key))
                    return Optional.of(strategy);
            }
        }
        return getStrategy(strategyClass, key);
    }
}
