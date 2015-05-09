package com.github.ruediste.rise.core;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When present on an injection point, or a type beeing injected indicates that
 * a dependency should be satisfied from the permanent injector when beeing
 * injected using the dynamic injector
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD,
		ElementType.TYPE })
public @interface Permanent {

}
