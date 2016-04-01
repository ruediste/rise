package com.github.ruediste.rise.core.scopes;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Scope;

/**
 * Indicates that a type is to be placed in the session scope
 */
@Target({ TYPE, METHOD })
@Retention(RUNTIME)
@Scope
@Documented
public @interface SessionScoped {

}
