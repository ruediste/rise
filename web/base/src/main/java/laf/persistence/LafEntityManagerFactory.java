package laf.persistence;

import javax.persistence.EntityManager;

/**
 *
 */
public interface LafEntityManagerFactory {

	EntityManager createEntityManager();
}
