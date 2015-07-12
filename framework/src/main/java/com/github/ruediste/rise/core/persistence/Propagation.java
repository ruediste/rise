package com.github.ruediste.rise.core.persistence;

import org.springframework.transaction.annotation.Transactional;

/**
 * Enumeration that represents transaction propagation behaviors for use with
 * the {@link Transactional} annotation.
 */
public enum Propagation {

    /**
     * Support a current transaction, create a new one if none exists. Analogous
     * to EJB transaction attribute of the same name.
     * <p>
     * This is the default setting of a transaction annotation.
     */
    REQUIRED(),

    /**
     * Support a current transaction, throw an exception if none exists.
     * Analogous to EJB transaction attribute of the same name.
     */
    MANDATORY(),

    /**
     * Execute non-transactionally, throw an exception if a transaction exists.
     * Analogous to EJB transaction attribute of the same name.
     */
    NEVER(),

}