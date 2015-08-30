package com.github.ruediste.rise.core.security.authorization.right;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that an annotation is used to mark methods as to require a certain
 * right to execute.
 *
 * <p>
 * The annotation needs to have a {@code value} element of an enum type. The
 * enum indicates the right required
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface MetaRequiresRight {

}
