package laf.core.base;

import java.io.Serializable;

import org.slf4j.Logger;

/**
 * Serializable implementation of the {@link Logger} interface, usable
 * in passivation capable scopes.
 */
public interface LafLogger extends Logger, Serializable {

}
