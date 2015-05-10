package com.github.ruediste.rise.core.front.reload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares that a class or the classes of a package can not be reloaded.
 * 
 * @see Reloadable @Reloadable
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.PACKAGE })
@Documented
public @interface Permanent {

}
