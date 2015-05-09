package com.github.ruediste.rise.core.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Define a path info which is mapped to this action method. The annotation can
 * be repeated to accept multiple paths. The path marked with {@link #primary()}
 * is used when generating a path info for the method. If no path is marked
 * {@link #primary()}, the default path is used.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
@Repeatable(ActionPaths.class)
public @interface ActionPath {
	String value();

	boolean primary() default false;
}
