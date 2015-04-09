package com.github.ruediste.laf.core.classReload;

import java.lang.annotation.*;

/**
 * Declares that a class or package belongs to the permanent class space
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.PACKAGE })
@Documented
public @interface PermanentSpace {

}
