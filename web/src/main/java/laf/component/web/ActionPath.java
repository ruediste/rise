package laf.component.web;

import java.lang.annotation.*;

/**
 * Define a servlet path an action method is mapped to. The anntation can be
 * repeated to accept multipe paths. The path marked with {@link #mainPath()} is
 * used when generating a path to the method
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
@Repeatable(ActionPaths.class)
public @interface ActionPath {
	String value();

	boolean mainPath() default false;
}
