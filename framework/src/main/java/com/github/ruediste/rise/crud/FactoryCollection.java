package com.github.ruediste.rise.crud;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class FactoryCollection<IN, OUT> {
    private final ArrayList<Function<IN, OUT>> factories = new ArrayList<>();

    public List<Function<IN, OUT>> getFactories() {
        return factories;
    }

    public OUT create(IN input) {
        return factories
                .stream()
                .map(x -> x.apply(input))
                .filter(x -> x != null)
                .findFirst()
                .orElseThrow(
                        () -> new RuntimeException("No factory found for "
                                + input));
    }
}
