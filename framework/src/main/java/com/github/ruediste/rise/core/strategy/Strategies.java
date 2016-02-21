package com.github.ruediste.rise.core.strategy;

import java.lang.reflect.AnnotatedElement;
import java.util.Optional;
import java.util.function.Predicate;

import javax.inject.Singleton;

import com.github.ruediste.rise.util.Pair;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;

@Singleton
public class Strategies {

    interface Strategy<TKey> {

    }

    private final ListMultimap<Class<?>, Pair<Predicate<?>, Object>> strategies = MultimapBuilder
            .hashKeys().arrayListValues().build();

    public <TKey, T extends Strategy<TKey>> void putStrategy(
            Class<T> strategyClass, Predicate<TKey> predicate, T strategy) {
        strategies.get(strategyClass).add(Pair.of(predicate, strategy));
    }

    public <TKey, T extends Strategy<TKey>> void putStrategyFirst(
            Class<T> strategyClass, Predicate<TKey> predicate, T strategy) {
        strategies.get(strategyClass).add(0, Pair.of(predicate, strategy));
    }

    @SuppressWarnings("unchecked")
    public <TKey, T extends Strategy<TKey>> Optional<T> getStrategy(
            Class<T> strategyClass, TKey key) {
        return strategies.get(strategyClass).stream()
                .filter(x -> ((Predicate<TKey>) x.getA()).test(key))
                .findFirst();
    }

    public <TKey, T extends Strategy<TKey>> Optional<T> getStrategy(
            Class<T> strategyClass, TKey key, AnnotatedElement element) {
        return null;
    }
}
