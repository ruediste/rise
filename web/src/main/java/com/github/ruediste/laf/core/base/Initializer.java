package com.github.ruediste.laf.core.base;

/**
 * Interface for initializers. Initializers are registered in the Salta modules.
 *
 * @see InitializerUtil
 */
public interface Initializer {

	/**
	 * Perform the initializations
	 */
	void initialize();
}
