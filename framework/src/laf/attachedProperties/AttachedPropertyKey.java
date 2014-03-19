package laf.attachedProperties;

/**
 * Identifies a property which can be attached to any
 * {@link AttachedPropertyBearer}
 */
public class AttachedPropertyKey<T> {
	/**
	 * Return the value associated with the bearer. If the property is not set,
	 * null is returned. To determine if a property is set, use
	 * {@link #isSet(AttachedPropertyBearer)}
	 */
	public T get(AttachedPropertyBearer bearer) {
		return bearer.getAttachedPropertyMap().getAttachedProperty(this);
	}

	/**
	 * Set the value associated with the bearer. Note that setting a property to
	 * null does not clear the property. Use
	 * {@link #clear(AttachedPropertyBearer)} for that purpose.
	 */
	public void set(AttachedPropertyBearer bearer, T value) {
		bearer.getAttachedPropertyMap().set(this, value);
	}

	/**
	 * Clear the property on the bearer. After calling this method,
	 * {@link #isSet(AttachedPropertyBearer)} will return false.
	 */
	void clear(AttachedPropertyBearer bearer) {
		bearer.getAttachedPropertyMap().clear(this);
	}

	/**
	 * Determine if the property is set on the.
	 */
	boolean isSet(AttachedPropertyBearer bearer) {
		return bearer.getAttachedPropertyMap().isSet(this);
	}

}
