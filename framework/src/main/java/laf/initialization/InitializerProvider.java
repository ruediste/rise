package laf.initialization;

public interface InitializerProvider {

	/**
	 * Returns a list of {@link Initializer}s and initializer bearer. The
	 * initializers will be used as-is, the bearer will be scanned with
	 * {@link InitializationService#createInitializers(Object)}.
	 */
	Iterable<Object> getInitializers();
}
