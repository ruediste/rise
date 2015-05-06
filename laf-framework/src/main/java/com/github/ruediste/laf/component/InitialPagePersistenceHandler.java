package com.github.ruediste.laf.component;

import javax.inject.Inject;
import javax.transaction.TransactionManager;

import com.github.ruediste.laf.core.ChainedRequestHandler;
import com.github.ruediste.laf.core.persistence.em.EntityManagerHolder;

public class InitialPagePersistenceHandler extends ChainedRequestHandler {

	@Inject
	PageInfo pageInfo;

	@Inject
	EntityManagerHolder holder;

	@Inject
	TransactionManager txm;

	@Override
	public void run(Runnable next) {
		try {
			try {
				txm.begin();

				holder.setNewEntityManagerSet();
				pageInfo.setEntityManagerSet(holder
						.getCurrentEntityManagerSet());
				try {
					next.run();
				} finally {
					holder.removeCurrentSet();
				}

			} finally {
				txm.rollback();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
}
