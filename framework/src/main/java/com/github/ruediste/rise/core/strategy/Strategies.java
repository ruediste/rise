package com.github.ruediste.rise.core.strategy;

import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
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

    private final ListMultimap<Class<?>, Strategy> strategies = MultimapBuilder.hashKeys().arrayListValues().build();

    public void putStrategy(Strategy strategy) {
        getStrategyClasses(strategy.getClass()).forEach(s -> strategies.put(s, strategy));
    }

    public <T extends Strategy> void putStrategy(Class<T> strategy) {
        T instance = injector.getInstance(strategy);
        getStrategyClasses(strategy).forEach(s -> strategies.put(s, instance));
    }

    public void putStrategy(Strategy strategy, AnnotatedElement element) {
        getStrategyClasses(strategy.getClass()).forEach(s -> perElementStrategies.put(Pair.of(s, element), strategy));
    }

    public <T extends Strategy> void putStrategy(Class<T> cls, T strategy) {
        strategies.put(cls, strategy);
    }

    public <T extends Strategy> void putStrategyFirst(Class<T> cls, T strategy) {
        strategies.get(cls).add(0, strategy);
    }

    public void putStrategyFirst(Strategy strategy) {
        getStrategyClasses(strategy.getClass()).forEach(s -> strategies.get(s).add(0, strategy));
    }

    public void putStrategyFirst(Strategy strategy, AnnotatedElement element) {
        getStrategyClasses(strategy.getClass())
                .forEach(s -> perElementStrategies.get(Pair.of(s, element)).add(0, strategy));
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
    public <T extends Strategy> Stream<T> getStrategies(Class<T> strategyClass) {

        return Stream.concat((Stream<T>) strategies.get(strategyClass).stream(),
                Stream.of((Supplier<Optional<T>>) () -> injector.tryGetInstance(strategyClass)).map(x -> x.get())
                        .flatMap(x -> x.isPresent() ? Stream.of(x.get()) : Stream.of()));
    }

    @SuppressWarnings({ "unchecked" })
    public <T extends Strategy> Stream<T> getStrategies(Class<T> strategyClass, AnnotatedElement element) {
        if (element == null)
            return getStrategies(strategyClass);
        // use per element strategies
        Stream<Strategy> perElementStrategyStream = perElementStrategies.get(Pair.of(strategyClass, element)).stream();

        // check annotation
        Stream<Object> annotationStream = Arrays.stream(element.getAnnotationsByType(UseStrategy.class))
                .map(UseStrategy::value).filter(strategyClass::isAssignableFrom).map(injector::getInstance);

        // use default strategies
        return (Stream<T>) Stream.concat(Stream.concat(perElementStrategyStream, annotationStream),
                getStrategies(strategyClass));
    }

    private ConcurrentHashMap<Object, Optional<Strategy>> strategyCache = new ConcurrentHashMap<>();

    public class GetStrategyApi<T extends Strategy> {
        private final Class<T> strategyClass;
        private Optional<AnnotatedElement> element = Optional.empty();
        private boolean isCached;
        private Object cacheKey;

        public GetStrategyApi(Class<T> strategyClass) {
            this.strategyClass = strategyClass;
        }

        public GetStrategyApi<T> element(AnnotatedElement element) {
            this.element = Optional.ofNullable(element);
            return this;
        }

        public GetStrategyApi<T> cached(Object key) {
            isCached = true;
            cacheKey = key;
            return this;
        }

        private <R> Optional<Pair<T, R>> getUncached(Function<T, Optional<R>> filter) {
            return element.map(e -> getStrategies(strategyClass, e)).orElseGet(() -> getStrategies(strategyClass))
                    .flatMap(s -> filter.apply(s).map(r -> Stream.of(Pair.of(s, r))).orElse(Stream.of())).findFirst();
        }

        public <R> Optional<R> get(Function<T, Optional<R>> filter) {
            if (isCached) {
                Object key = Pair.of(
                        element.map(x -> (Object) Pair.of(strategyClass, x)).orElseGet(() -> strategyClass), cacheKey);

                return strategyCache.getOrDefault(key, Optional.empty()).map(s -> filter.apply(strategyClass.cast(s)))
                        .orElseGet(() -> {
                            Optional<Pair<T, R>> pair = getUncached(filter);
                            strategyCache.put(key, pair.map(Pair::getA));
                            return pair.map(Pair::getB);
                        });
            } else {
                return getUncached(filter).map(Pair::getB);
            }
        }
    }

    public <T extends Strategy> GetStrategyApi<T> getStrategy(Class<T> strategyClass) {
        return new GetStrategyApi<>(strategyClass);
    }
}
