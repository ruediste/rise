package laf.attachedProperties;

/**
 * Identifies a property which can be attached to any
 * {@link AttachedPropertyBearer}
 */
public class AttachedProperty<Bearer extends AttachedPropertyBearer, T> {
	/**
	 * Return the value associated with the bearer. If the property is not set,
	 * null is returned. To determine if a property is set, use
	 * {@link #isSet(Bearer)}
	 */
	public T get(Bearer bearer) {
		return bearer.getAttachedPropertyMap().get(this);
	}

	/**
	 * Set the value associated with the bearer. Note that setting a property to
	 * null does not clear the property. Use {@link #clear(Bearer)} for that
	 * purpose.
	 */
	public void set(Bearer bearer, T value) {
		bearer.getAttachedPropertyMap().set(this, value);
	}

	/**
	 * Clear the property on the bearer. After calling this method,
	 * {@link #isSet(Bearer)} will return false.
	 */
	void clear(Bearer bearer) {
		bearer.getAttachedPropertyMap().clear(this);
	}

	/**
	 * Determine if the property is set on the.
	 */
	boolean isSet(Bearer bearer) {
		return bearer.getAttachedPropertyMap().isSet(this);
	}

}
