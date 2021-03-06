package com.github.ruediste.rise.util;

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
