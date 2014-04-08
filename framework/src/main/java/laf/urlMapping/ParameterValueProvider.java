package laf.urlMapping;

import javax.persistence.EntityManager;

public interface ParameterValueProvider {
	/**
	 * Provides the parameter value. Producing the value might involve database
	 * access (via the current {@link EntityManager}), so this method should
	 * only be called before actually invoking an action method.
	 */
	Object provideValue();

	/**
	 * Returns true if the provided value is not equal to the other object. If
	 * the provided value might be equal, returns false.
	 */
	boolean providesNonEqualValue(Object other);
}
