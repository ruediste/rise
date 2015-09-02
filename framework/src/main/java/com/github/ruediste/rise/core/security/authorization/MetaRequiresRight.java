package com.github.ruediste.rise.core.security.authorization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that an annotation is used to mark methods as to require a certain
 * right to execute.
 *
 * <p>
 * The annotation needs to have a {@code value} element, indicating the required
 * right. If the element is of array type, each element of the array is a
 * required right.
 * <p>
 * The annotation can be repeated. In this case, the wrapper annotation has to
 * be annotated with this meta annotation as well.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface MetaRequiresRight {

}
