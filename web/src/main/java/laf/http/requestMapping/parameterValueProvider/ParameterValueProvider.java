package laf.http.requestMapping.parameterValueProvider;

import javax.persistence.EntityManager;

import com.google.common.base.Supplier;

public interface ParameterValueProvider extends Supplier<Object> {
	/**
	 * Provides the parameter value. Producing the value might involve database
	 * access (via the current {@link EntityManager}), so this method should
	 * only be called before actually invoking an action method.
	 */
	@Override
	Object get();

	/**
	 * Returns true if the provided value is not equal to the other object. If
	 * the provided value might be equal, returns false.
	 */
	boolean providesNonEqualValue(Object other);
}
