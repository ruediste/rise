package com.github.ruediste.rise.crud.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.ruediste.rise.crud.CrudUtil;
import com.github.ruediste.rise.crud.CrudUtil.BrowserFactory;

/**
 * Applied to an entity, overrides a factory. This allows to customize the crud
 * UI.
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
