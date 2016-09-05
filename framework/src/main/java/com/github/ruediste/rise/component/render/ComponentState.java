package com.github.ruediste.rise.component.render;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Optional;

import com.github.ruediste.rise.component.tree.Component;

/**
 * Fields of {@link Component}s are copied over from the component of the
 * previous page load if this annotation is present.
 * 
 * <p>
 * {@link Optional} fields are copied only if the target field contains
 * {@link Optional#empty()}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface ComponentState {

    /**
     * if set to true, optionals are always overwritten
     */
    boolean alwaysOverwrite() default false;
}
