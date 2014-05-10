package laf.initialization;

import java.util.Collection;

/**
 * There are two ways of defining initializers. Either the initializer stands
 * for itself, or it is created from an object using
 * {@link InitializationService#createInitializers(Object)}. For initializers
 * standing for themselves, use {@link #addInitializer(Initializer)} When
 * initializers are constructed from an object, use
 * {@link #createInitializersFrom(Object)}. Using this method, the same object
 * is only processed once.
 */
public interface CreateInitializersEvent {

	/**
	 * Get the current initialization phase.
	 */
	Class<? extends InitializationPhase> getPhase();

	/**
	 * Add an initializer. The initializer itself is used as underlying object.
	 * If the initializer has been added before with itself as underlying object
	 * it will not be added again.
	 */
	void addInitializer(Initializer initializer);

	/**
	 * Search and create initializers from the provided object using
	 * {@link InitializationService#createInitializers(Object)}. Skip objects
	 * which were used before. If the object implements {@link Iterable}, the
	 * objects in the iterable are processed. (not transistive)
	 *
	 * @return the initializers derived from the single object, or all
	 *         initializers created from the elements of the provided
	 *         {@link Iterable}. If the method is called multiple times with the
	 *         same object, the initializers are added once but returned on
	 *         every call.
	 */
	Collection<Initializer> createInitializersFrom(Object object);

	InitializerBuilder createInitializers();
}
