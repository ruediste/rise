package com.github.ruediste.rise.mvc;

import java.lang.reflect.Method;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.github.ruediste.rise.core.ChainedRequestHandler;
import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocation;
import com.github.ruediste.rise.core.persistence.NoTransaction;
import com.github.ruediste.rise.core.persistence.TransactionTemplate;
import com.github.ruediste.rise.core.persistence.Updating;

public class MvcPersistenceHandler extends ChainedRequestHandler {

    @Inject
    Logger log;

    @Inject
    MvcRequestInfo info;

    @Inject
    CoreRequestInfo coreRequestInfo;

    @Inject
    TransactionTemplate template;

    @Override
    public void run(Runnable next) {
        // determine transaction isolation
        ActionInvocation<String> invocation = coreRequestInfo
                .getStringActionInvocation();
        Method method = invocation.methodInvocation.getMethod();

        if (method.isAnnotationPresent(NoTransaction.class)) {
            next.run();
        } else {
            boolean updating = method.isAnnotationPresent(Updating.class);
            log.debug("updating = {} for method {}", updating, method);
            info.setIsUpdating(updating);

            template.updating(updating).execute(next::run);
        }

    }

}
