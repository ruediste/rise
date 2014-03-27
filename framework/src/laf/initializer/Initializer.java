package laf.initializer;

/**
 *
 *
 */
public interface Initializer {
	/**
	 * Returns true if this initializer is to be run before the other
	 * initializer.
	 */
	boolean isBefore(Initializer other);

	/**
	 * Returns true if this initializer is to be run after the other
	 * initializer.
	 */
	boolean isAfter(Initializer other);

	/**
	 * Return the class of the component which defined this initializer.
	 *
	 * @return
	 */
	Class<?> getComponentClass();

	/**
	 * Returns the unique ID within the component class of this initializer.
	 */
	String getId();

	/**
	 * Runs this initializer
	 */
	void run();
}
