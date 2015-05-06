package com.github.ruediste.laf.component;

import javax.inject.Inject;
import javax.transaction.TransactionManager;

import com.github.ruediste.laf.core.ChainedRequestHandler;
import com.github.ruediste.laf.core.persistence.TransactionTemplate;
import com.github.ruediste.laf.core.persistence.em.EntityManagerHolder;

public class InitialPagePersistenceHandler extends ChainedRequestHandler {

	@Inject
	PageInfo pageInfo;

	@Inject
	EntityManagerHolder holder;

	@Inject
	TransactionManager txm;

	@Inject
	TransactionTemplate template;

	@Override
	public void run(Runnable next) {
		template.builder().execute(trx -> {
			pageInfo.setEntityManagerSet(holder.getCurrentEntityManagerSet());
			next.run();
		});
	}
}
