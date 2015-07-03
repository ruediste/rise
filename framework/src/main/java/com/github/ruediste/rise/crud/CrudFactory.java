package com.github.ruediste.rise.crud;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.ruediste.rise.crud.CrudUtil.BrowserFactory;

/**
 * Defines the factory to be used for a certain type
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(CrudFactories.class)
@Inherited
@Documented
public @interface CrudFactory {

    /**
     * The type of the factory to set, for example {@link BrowserFactory}
     */
    Class<?> type();

    /**
     * The class implementing the {@link #type()}
     */
    Class<?> implementation();
}
