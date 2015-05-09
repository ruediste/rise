package com.github.ruediste.laf.component.reload;

import javax.inject.Inject;

import com.github.ruediste.laf.component.PageInfo;
import com.github.ruediste.laf.core.ChainedRequestHandler;
import com.github.ruediste.laf.core.persistence.em.EntityManagerHolder;

public class ReloadPagePersistenceHandler extends ChainedRequestHandler {

	@Inject
	PageInfo pageInfo;

	@Inject
	EntityManagerHolder holder;

	@Override
	public void run(Runnable next) {
		holder.setCurrentEntityManagerSet(pageInfo.getEntityManagerSet());
		try {
			next.run();
		} finally {
			holder.removeCurrentSet();
		}

	}
}
