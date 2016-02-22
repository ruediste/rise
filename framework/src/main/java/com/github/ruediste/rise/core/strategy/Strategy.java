package com.github.ruediste.rise.core.strategy;

/**
 * Interface of strategies which can be registered globally with
 * {@link Strategies}, or on a per-element basis using {@link UseStrategy}.
 */
public interface Strategy<TKey> {
    /**
     * Test is this strategy applies to the given key
     */
    boolean applies(TKey key);
}