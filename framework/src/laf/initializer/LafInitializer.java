package laf.initializer;

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
	 * The if of this initializer. If left empty, defaults to the method name
	 */
	String id() default "";

	/**
	 * A list of component classes whose initializers should be run after this
	 * initializer. If differentiation between multiple initializers defined by
	 * single component class is required, use {@link #beforeRef()}
	 */
	Class<?>[] before() default {};

	/**
	 * A list of initializers which should be run after this initializer. If no
	 * differentiation between multiple initializers defined by single component
	 * class is required, use {@link #before()}.
	 */
	InitializerRef[] beforeRef() default {};

	/**
	 * A list of component classes whose initializers should be run before this
	 * initializer. If differentiation between multiple initializers defined by
	 * single component class is required, use {@link #afterRef()}
	 */
	Class<?>[] after() default {};

	/**
	 * A list of initializers which should be run before this initializer. If no
	 * differentiation between multiple initializers defined by single component
	 * class is required, use {@link #after()}.
	 */
	InitializerRef[] afterRef() default {};
}
