package com.github.ruediste.rise.core.front.reload;

import java.lang.annotation.*;

/**
 * Declares that a class or package belongs to the dynamic class space
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.PACKAGE })
@Documented
public @interface DynamicSpace {

}
