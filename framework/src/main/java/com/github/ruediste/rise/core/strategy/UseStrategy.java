package com.github.ruediste.rise.core.strategy;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.github.ruediste.rise.core.strategy.Strategies.Strategy;
import com.github.ruediste.salta.jsr330.Injector;

/**
 * Add a strategy to an element
 */
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(UseStrategies.class)
@Inherited
@Documented
public @interface UseStrategy {

    /**
     * Additonal strategy to add to the annotated element. The strategy is
     * instantiated using the {@link Injector}
     */
    Class<? extends Strategy<?>> value();
}
