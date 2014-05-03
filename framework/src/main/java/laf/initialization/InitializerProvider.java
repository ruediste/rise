package laf.initialization;

public interface InitializerProvider {

	/**
	 * Returns a list of {@link Initializer}s and initializer bearer. The
	 * initializers will be used as-is, the bearer will be scanned with
	 * {@link InitializationService#createInitializers(Object)}.
	 * 
	 * @param phase
	 *            The initialization phase to get the initializers for
	 */
	Iterable<Object> getInitializers(Class<? extends Phase> phase);
}
