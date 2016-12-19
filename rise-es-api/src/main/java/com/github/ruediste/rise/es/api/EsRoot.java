package com.github.ruediste.rise.es.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a type as beeing the root type of a ES document
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface EsRoot {

    /**
     * Defaults to the simple name of the class
     */
    String type() default "";

    String index() default "";

    /**
     * By default, the field names will be prefixed according to their type. Set
     * to false to use the names as is.
     */
    boolean typedFieldNames() default true;

    @SuppressWarnings("rawtypes")
    Class<? extends IndexSuffixExtractor>suffixExtractor() default IndexSuffixExtractor.class;
}
