package com.github.ruediste.rise.mvc;

import java.lang.reflect.Method;

import javax.inject.Inject;
import javax.transaction.TransactionManager;

import org.slf4j.Logger;

import com.github.ruediste.rise.core.ChainedRequestHandler;
import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocation;
import com.github.ruediste.rise.core.persistence.TransactionTemplate;
import com.github.ruediste.rise.core.persistence.em.EntityManagerHolder;
import com.github.ruediste.rise.nonReloadable.persistence.TransactionProperties;

public class MvcPersistenceHandler extends ChainedRequestHandler {

    @Inject
    Logger log;

    @Inject
    TransactionManager txm;

    @Inject
    EntityManagerHolder holder;

    @Inject
    MvcRequestInfo info;

    @Inject
    CoreRequestInfo coreRequestInfo;

    @Inject
    TransactionProperties transactionProperties;

    @Inject
    TransactionTemplate template;

    @Override
    public void run(Runnable next) {
        // determine transaction isolation
        ActionInvocation<String> invocation = coreRequestInfo
                .getStringActionInvocation();
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
