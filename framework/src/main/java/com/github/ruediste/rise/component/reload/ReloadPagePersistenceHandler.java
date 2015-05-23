package com.github.ruediste.rise.component.reload;

import javax.inject.Inject;

import com.github.ruediste.rise.component.PageInfo;
import com.github.ruediste.rise.core.ChainedRequestHandler;
import com.github.ruediste.rise.core.persistence.em.EntityManagerHolder;

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
