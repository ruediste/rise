package com.github.ruediste.rise.core.strategy;

import com.github.ruediste.salta.jsr330.ImplementedBy;

/**
 * Marker interface for strategies which can be registered globally with
 * {@link Strategies}, or on a per-element basis using {@link UseStrategy}.
 * 
 * <p>
 * In addition, an attempt to obtain an instance of the strategy interface from
 * the dependency injector is made and the resulting instance is used as
 * additional strategy. This feature is typically used by using
 * {@link ImplementedBy} to specify a default strategy implementation.
 * </p>
 */
public interface Strategy {

}