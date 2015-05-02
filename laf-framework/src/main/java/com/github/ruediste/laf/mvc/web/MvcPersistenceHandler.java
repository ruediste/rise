package com.github.ruediste.laf.mvc.web;

import java.lang.reflect.Method;

import javax.inject.Inject;
import javax.transaction.TransactionManager;

import org.slf4j.Logger;

import com.github.ruediste.laf.core.persistence.TransactionProperties;
import com.github.ruediste.laf.core.persistence.TransactionTemplate;
import com.github.ruediste.laf.core.persistence.em.EntityManagerHolder;
import com.github.ruediste.laf.mvc.ActionInvocation;
import com.github.ruediste.laf.mvc.Updating;

public class MvcPersistenceHandler extends ChainedRequestHandler {

	@Inject
	Logger log;

	@Inject
	TransactionManager txm;

	@Inject
	EntityManagerHolder holder;

	@Inject
	MvcWebRequestInfo info;

	@Inject
	TransactionProperties transactionProperties;

	@Inject
	TransactionTemplate template;

	@Override
	public void run(Runnable next) {
		// determine transaction isolation
		ActionInvocation<String> invocation = info.getStringActionInvocation();
		Method method = invocation.methodInvocation.getMethod();
		boolean updating = method.isAnnotationPresent(Updating.class);
		log.debug("updating = {} for method {}", updating, method);
		info.setIsUpdating(updating);

		template.builder().updating(updating).execute(trx -> {
			info.setTransactionControl(trx);
			next.run();
		});
	}

}
