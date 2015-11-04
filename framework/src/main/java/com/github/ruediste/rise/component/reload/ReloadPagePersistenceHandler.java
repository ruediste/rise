package com.github.ruediste.rise.component.reload;

import javax.inject.Inject;

import com.github.ruediste.rise.component.ComponentPage;
import com.github.ruediste.rise.core.ChainedRequestHandler;
import com.github.ruediste.rise.core.persistence.em.EntityManagerHolder;

public class ReloadPagePersistenceHandler extends ChainedRequestHandler {

    @Inject
    ComponentPage pageInfo;

    @Inject
    EntityManagerHolder holder;

    @Override
    public void run(Runnable next) {
        holder.withEntityManagerSet(pageInfo.getEntityManagerSet(), next);
    }
}
