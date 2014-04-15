package laf.initialization;


public interface CreateInitializersEvent {

	/**
	 * Add an initializer. If the initializer is created from an object, do not
	 * forget to use {@link #isChecked(Object)} and
	 * {@link #addCheckedObject(Object)} to avoid creating initializers multiple
	 * times.
	 */
	public void addInitializer(Initializer initializer);

	/**
	 * Search and create initializers from the provided object using
	 * {@link InitializationService#createInitializers(Object)}. Skip objects
	 * which are already marked as checked
	 */
	public void createInitializersFrom(Object object);

	/**
	 * Report that an object is already checked for initializers.
	 */
	public void addCheckedObject(Object o);

	/**
	 * Check if an object has already been checked for initializers
	 */
	public boolean isChecked(Object o);
}
