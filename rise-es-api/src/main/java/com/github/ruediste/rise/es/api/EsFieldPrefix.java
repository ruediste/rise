package com.github.ruediste.rise.es.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When placed on a field in an {@link EsRoot}, sets the prefix used to store
 * the field in Elastic Search. Can also be used as Meta-Annotation
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Documented
public @interface EsFieldPrefix {
    String value();
}
