package laf.base.attachedProperties;

import com.google.common.base.Supplier;

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
	public void clear(Bearer bearer) {
		bearer.getAttachedPropertyMap().clear(this);
	}

	/**
	 * Determine if the property is set on the.
	 */
	public boolean isSet(Bearer bearer) {
		return bearer.getAttachedPropertyMap().isSet(this);
	}

	/**
	 * Set a property to the specified value if it is not set.
	 *
	 * @return the current value of the property
	 */
	public T setIfAbsent(Bearer bearer, T value) {
		synchronized (bearer.getAttachedPropertyMap()) {
			if (!isSet(bearer)) {
				set(bearer, value);
				return value;
			} else {
				return get(bearer);
			}
		}
	}

	/**
	 * Set a property to the specified value if it is not set.
	 *
	 * @return the current value of the property
	 */
	public T setIfAbsent(Bearer bearer, Supplier<T> valueSupplier) {
		synchronized (bearer.getAttachedPropertyMap()) {
			if (!isSet(bearer)) {
				T value = valueSupplier.get();
				set(bearer, value);
				return value;
			} else {
				return get(bearer);
			}
		}
	}
}
