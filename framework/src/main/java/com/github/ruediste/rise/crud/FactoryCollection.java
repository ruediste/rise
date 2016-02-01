package com.github.ruediste.rise.crud;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;
import java.util.function.Predicate;

import com.github.ruediste.rise.util.Pair;

public class FactoryCollection<TKey, TFactory> {
    private final Deque<Pair<Predicate<TKey>, TFactory>> factories = new ArrayDeque<>();

    public Deque<Pair<Predicate<TKey>, TFactory>> getFactories() {
        return factories;
    }

    public void addFactoryFirst(Predicate<TKey> filter, TFactory factory) {
        factories.addFirst(Pair.of(filter, factory));
    }

    public void addFactory(Predicate<TKey> filter, TFactory factory) {
        factories.addLast(Pair.of(filter, factory));
    }

    public Optional<TFactory> tryGetFactory(TKey key) {
        return factories.stream().filter(x -> x.getA().test(key)).findFirst()
                .map(p -> p.getB());
    }

    public TFactory getFactory(TKey key) {
        return tryGetFactory(key).orElseThrow(() -> new RuntimeException(
                getClass().getSimpleName() + ": No factory found for " + key));
    }
}
