package com.github.ruediste.laf.core.persistence;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jabsaw.Module;

/**
 * This module is used to manage {@link EntityManager}s.
 * <p>
 * The managers are injected using the {@link Inject} annotation (instead of the
 * {@link PersistenceContext} annotation). The entity managers are produced by
 * an application provided producer. The use of multiple EnitityManagers in the
 * same application is supported. They are differentiated using qualifiers.
 * </p>
 *
 * <p>
 * With each thread a {@link LafPersistenceHolder} can be associated. The holder
 * manages the {@link EntityManager}s used while the holder is active. If a
 * holder is destroyed, the containing {@link EntityManager}s are closed.
 * </p>
 */
@Module(description = "Manage EntityManagers", imported = { com.github.ruediste.laf.core.base.BaseModuleImpl.class })
public class PersistenceModule {

}
