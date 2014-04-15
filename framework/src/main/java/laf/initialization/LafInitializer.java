package laf.initialization;

import java.lang.annotation.*;

import laf.LAF;

/**
 * Mark a method of a component registered in {@link LAF} as defining an
 * {@link Initializer}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LafInitializer {

	/**
	 * A list of representing classes whose initializers should be run after
	 * this initializer.
	 */
	Class<?>[] before() default {};

	/**
	 * A list of representing classes whose initializers should be run before
	 * this initializer.
	 */
	Class<?>[] after() default {};

}
