package laf.component.html;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import laf.component.core.ComponentView;

/**
 * A Page Manager manages a single page
 */
@Stateful
@TransactionManagement(TransactionManagementType.CONTAINER)
public class PageManager {

	private ComponentView<?> view;

	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	EntityManager manager;

	public void initialize(ComponentView<?> view) {
		this.view = view;

	}

	public ComponentView<?> getView() {
		return view;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void runInTransaction(Runnable runnable) {
		// access the entity manager to make sure it gets created
		manager.getFlushMode();
		runnable.run();
	}

	@Remove
	public void destroy() {

	}
}
