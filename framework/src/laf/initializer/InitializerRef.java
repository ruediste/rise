package laf.initializer;

/**
 * Used by the {@link LafInitializer} to specify dependencies on other
 * initializers.
 */
public @interface InitializerRef {

	/**
	 * The component class of the dependent {@link Initializer}
	 */
	Class<?> componentClass();

	/**
	 * The id within the component class of the dependent {@link Initializer}.
	 * If left empty, all initializers of the component class match.
	 */
	String id() default "";
}
