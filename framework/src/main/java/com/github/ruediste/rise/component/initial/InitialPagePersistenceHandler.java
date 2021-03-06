package com.github.ruediste.rise.component.initial;

import javax.inject.Inject;
import javax.transaction.TransactionManager;

import com.github.ruediste.rise.component.ComponentPage;
import com.github.ruediste.rise.core.ChainedRequestHandler;
import com.github.ruediste.rise.core.persistence.TransactionControl;
import com.github.ruediste.rise.core.persistence.em.EntityManagerHolder;
import com.github.ruediste.rise.nonReloadable.persistence.IsolationLevel;

public class InitialPagePersistenceHandler extends ChainedRequestHandler {

    @Inject
    ComponentPage pageInfo;

    @Inject
    EntityManagerHolder holder;

    @Inject
    TransactionManager txm;

    @Inject
    TransactionControl template;

    @Override
    public void run(Runnable next) {
        template.updating().isolation(IsolationLevel.REPEATABLE_READ).execute(() -> {
            pageInfo.setEntityManagerSet(holder.getCurrentEntityManagerSet());
            next.run();
        });
    }
}
