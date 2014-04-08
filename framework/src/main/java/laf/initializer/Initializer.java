package laf.initializer;

/**
 *
 *
 */
public interface Initializer {
	/**
	 * Returns true if this initializer is to be run before the other
	 * initializer. Also takes into account if the other initializer declares to
	 * be after this.
	 */
	boolean isBefore(Initializer other);

	/**
	 * Return true if this initializer declares to be before the other
	 * initializer. Use {@link #isBefore(Initializer)} to determine if this
	 * initializer is to be run before the other initializer.
	 */
	boolean declaresIsBefore(Initializer other);

	/**
	 * Returns true if this initializer is to be run after the other
	 * initializer. Also takes into account if the other initializer declares to
	 * be before this.
	 */
	boolean isAfter(Initializer other);

	/**
	 * Return true if this initializer declares to be after the other
	 * initializer. Use {@link #isAfter(Initializer)} to determine if this
	 * initializer is to be run before the other initializer.
	 */
	boolean declaresIsAfter(Initializer other);

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
