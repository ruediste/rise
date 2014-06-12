package laf.persistence;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jabsaw.Module;

/**
 * This module provides an alternative way of managing {@link EntityManager}s.
 * <p>
 * The managers are injected using the {@link Inject} annotation (instead of the
 * {@link PersistenceContext} annotation). The entity managers are produced by
 * an application provided producer. The use of multiple EnitityManagers in the
 * same application is supported. They are differentiated using qualifiers.
 * </p>
 *
 * <p>
 * With each thread a {@link LafPersistenceHolder} can be associated. The
 * context manages the {@link EntityManager}s used while the context is active.
 * If a context is destroyed, the containing {@link EntityManager}s are closed.
 * </p>
 */
@Module(description = "Manage EntityManagers")
public class PersistenceModule {

}
