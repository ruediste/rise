package laf.core.persistence;

import javax.persistence.EntityManager;

/**
 *
 */
public interface LafEntityManagerFactory {

	EntityManager createEntityManager();
}
