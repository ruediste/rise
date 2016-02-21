package com.github.ruediste.rise.core.strategy;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.ruediste.rise.crud.CrudUtil.BrowserFactory;

/**
 * Applied to an entity, overrides a factory. This allows to customize the crud
 * UI.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(UseStrategies.class)
@Inherited
@Documented
public @interface UseStrategy {

    /**
     * The type of the strategy to set, for example {@link BrowserFactory}
     */
    Class<?> type();

    /**
     * The class implementing the {@link #type()}
     */
    Class<?> implementation();
}
