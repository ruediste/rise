package laf.initializer;

public interface InitializerProvider {

	/**
	 * Returns the initializers for this component.
	 */
	Iterable<Initializer> getInitializers();
}
