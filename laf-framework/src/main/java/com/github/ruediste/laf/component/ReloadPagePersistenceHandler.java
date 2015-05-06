package com.github.ruediste.laf.component;

import javax.inject.Inject;

import com.github.ruediste.laf.core.ChainedRequestHandler;
import com.github.ruediste.laf.core.persistence.em.EntityManagerHolder;

public class ReloadPagePersistenceHandler extends ChainedRequestHandler {

	@Inject
	PageInfo pageInfo;

	@Inject
	EntityManagerHolder holder;

	@Override
	public void run(Runnable next) {

		holder.setNewEntityManagerSet();
		pageInfo.setEntityManagerSet(holder.getCurrentEntityManagerSet());
		try {
			next.run();
		} finally {
			holder.removeCurrentSet();
		}

	}
}
