package com.github.ruediste.rise.core.strategy;

import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.ruediste.rise.util.Pair;
import com.github.ruediste.salta.jsr330.Injector;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;

/**
 * Contains {@link Strategy strategies} and allows to retrieve them. Strategies
 * can be added on a per Java-element (method, field, ...) level using
 * {@link UseStrategy}. If no strategy is registered explicitely, an instance of
 * the strategy interface requested is used (if possible to instantiate)
 */
@Singleton
public class Strategies {

    @Inject
    Injector injector;

    private final ListMultimap<Pair<Class<?>, AnnotatedElement>, Strategy> perElementStrategies = MultimapBuilder
            .hashKeys().arrayListValues().build();

    private final ListMultimap<Class<?>, Strategy> strategies = MultimapBuilder
            .hashKeys().arrayListValues().build();

    public void putStrategy(Strategy strategy) {
        getStrategyClasses(strategy.getClass())
                .forEach(s -> strategies.put(s, strategy));
    }

    public void putStrategy(Strategy strategy, AnnotatedElement element) {
        getStrategyClasses(strategy.getClass()).forEach(
                s -> perElementStrategies.put(Pair.of(s, element), strategy));
    }

    public <T extends Strategy> void putStrategy(Class<T> cls, T strategy) {
        strategies.put(cls, strategy);
    }

    public <T extends Strategy> void putStrategyFirst(Class<T> cls,
            T strategy) {
        strategies.get(cls).add(0, strategy);
    }

    public void putStrategyFirst(Strategy strategy) {
        getStrategyClasses(strategy.getClass())
                .forEach(s -> strategies.get(s).add(0, strategy));
    }

    public void putStrategyFirst(Strategy strategy, AnnotatedElement element) {
        getStrategyClasses(strategy.getClass())
                .forEach(s -> perElementStrategies.get(Pair.of(s, element))
                        .add(0, strategy));
    }

    HashSet<Class<?>> getStrategyClasses(Class<?> strategyClass) {
        HashSet<Class<?>> classes = new HashSet<>();
        fillStrategyClasses(strategyClass.getSuperclass(), classes);
        for (Class<?> i : strategyClass.getInterfaces()) {
            fillStrategyClasses(i, classes);
        }
        return classes;
    }

    private void fillStrategyClasses(Class<?> cls, Set<Class<?>> classes) {
        if (cls == null || Strategy.class.equals(cls))
            return;

        if (Strategy.class.isAssignableFrom(cls) && classes.add(cls)) {
            fillStrategyClasses(cls.getSuperclass(), classes);
            for (Class<?> i : cls.getInterfaces()) {
                fillStrategyClasses(i, classes);
            }
        }

    }

    @SuppressWarnings({ "unchecked" })
    public <T extends Strategy> Stream<T> getStrategies(
            Class<T> strategyClass) {

        return Stream.concat((Stream<T>) strategies.get(strategyClass).stream(),
                Stream.of((Supplier<Optional<T>>) () -> injector
                        .tryGetInstance(strategyClass)).map(x -> x.get())
                        .flatMap(x -> x.isPresent() ? Stream.of(x.get())
                                : Stream.of()));
    }

    public <T extends Strategy, R> Optional<R> getStrategy(
            Class<T> strategyClass, Function<T, Optional<R>> filter) {

        return getStrategies(strategyClass).map(filter)
                .filter(x -> x.isPresent()).findFirst().flatMap(x -> x);
    }

    public <T extends Strategy, R> Optional<R> getStrategy(
            Class<T> strategyClass, AnnotatedElement element,
            Function<T, Optional<R>> filter) {
        return getStrategies(strategyClass, element).map(filter)
                .filter(x -> x.isPresent()).findFirst().flatMap(x -> x);
    }

    @SuppressWarnings({ "unchecked" })
    public <T extends Strategy> Stream<T> getStrategies(Class<T> strategyClass,
            AnnotatedElement element) {
        if (element == null)
            return getStrategies(strategyClass);
        // use per element strategies
        Stream<Strategy> perElementStrategyStream = perElementStrategies
                .get(Pair.of(strategyClass, element)).stream();

        // check annotation
        Stream<Object> annotationStream = Arrays
                .stream(element.getAnnotationsByType(UseStrategy.class))
                .map(UseStrategy::value).filter(strategyClass::isAssignableFrom)
                .map(injector::getInstance);

        // use default strategies
        return (Stream<T>) Stream.concat(
                Stream.concat(perElementStrategyStream, annotationStream),
                getStrategies(strategyClass));
    }
}
