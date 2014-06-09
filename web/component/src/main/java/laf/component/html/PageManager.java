package laf.component.html;

import javax.ejb.*;
import javax.inject.Inject;

import laf.component.core.ComponentView;
import laf.component.core.PagePersistenceManager;

/**
 * A Page Manager manages a single page
 */
@Stateful
@TransactionManagement(TransactionManagementType.CONTAINER)
public class PageManager {

	private ComponentView<?> view;

	@Inject
	PagePersistenceManager persistenceManager;

	public void initialize(ComponentView<?> view) {
		this.view = view;
	}

	public ComponentView<?> getView() {
		return view;
	}

	@Remove
	public void destroy() {

	}
}
