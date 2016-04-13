package com.github.ruediste.rise.crud.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specify the validation group to be used. By default entities are validated
 * upon save against the default validation group. Use this annotation to
 * override. If {@link Void}.class is specified as only group, validation is
 * skipped.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface CrudValidationGroup {

    Class<?>[]value() default {};
}
