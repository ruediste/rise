package com.github.ruediste.rise.crud;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Predicate;

import com.github.ruediste.rise.util.Pair;

public class FactoryCollectionNew<TKey, TFactory> {
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

    public TFactory getFactory(TKey key) {
        return factories.stream().filter(x -> x.getA().test(key)).findFirst()
                .orElseThrow(
                        () -> new RuntimeException(getClass().getSimpleName()
                                + ": No factory found for " + key))
                .getB();
    }
}
