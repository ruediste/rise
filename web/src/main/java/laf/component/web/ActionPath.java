package laf.component.web;

import java.lang.annotation.*;

/**
 * Define a path info which is mapped to this action method. The annotation can
 * be repeated to accept multiple paths. The path marked with {@link #primary()}
 * is used when generating a path info for the method.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
@Repeatable(ActionPaths.class)
public @interface ActionPath {
	String value();

	boolean primary() default false;
}
