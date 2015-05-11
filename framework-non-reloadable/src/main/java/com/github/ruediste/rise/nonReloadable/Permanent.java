package com.github.ruediste.rise.nonReloadable;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.ruediste.rise.nonReloadable.front.reload.NonReloadable;

/**
 * When present on an injection point, or a type beeing injected indicates that
 * a dependency should be satisfied from the permanent injector when beeing
 * injected using the dynamic injector
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD,
		ElementType.TYPE })
@NonReloadable
public @interface Permanent {

}
