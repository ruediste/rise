package com.github.ruediste.rise.core.front.reload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares that a class or the classes of a package can be reloaded.
 * 
 * <p>
 * When determining if a class is reloadable, the following locations are
 * checked in order for the presence of {@link Reloadable @Reloadable} or
 * {@link Permanent @Permanent} annotations:
 * <ol>
 * <li>the class itself</li>
 * <li>the outer class (recursively)</li>
 * <li>the containing package (recursively)</li>
 * </ol>
 * The first annotation found determines if the class is considered reloadable.
 * The default is non-reloadable (Permanent)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.PACKAGE })
@Documented
public @interface Reloadable {

}
