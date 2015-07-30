package com.github.ruediste.rise.crud;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class FactoryCollection<TKey, TFactory> {
    private final ArrayList<Function<TKey, TFactory>> factories = new ArrayList<>();

    public List<Function<TKey, TFactory>> getFactories() {
        return factories;
    }

    public TFactory getFactory(TKey key) {
        return factories
                .stream()
                .map(x -> x.apply(key))
                .filter(x -> x != null)
                .findFirst()
                .orElseThrow(
                        () -> new RuntimeException("No factory found for "
                                + key));
    }
}
