package com.github.ruediste.rise.component.reload;

import javax.inject.Inject;

import com.github.ruediste.rise.component.ComponentPageHandleRepository;
import com.github.ruediste.rise.component.ComponentRequestInfo;
import com.github.ruediste.rise.component.PageHandle;
import com.github.ruediste.rise.component.PageScopeManager;
import com.github.ruediste.rise.core.ChainedRequestHandler;

public class ReloadPageScopeHandler extends ChainedRequestHandler {
    @Inject
    PageScopeManager pageScopeHandler;

    @Inject
    ComponentPageHandleRepository sessionInfo;

    @Inject
    PageReloadRequest request;

    @Inject
    ComponentRequestInfo info;

    @Override
    public void run(Runnable next) {
        PageHandle pageHandle = sessionInfo.getPageHandle(request.getPageNr());
        info.setPageHandle(pageHandle);
        synchronized (pageHandle.lock) {
            pageScopeHandler.setState(pageHandle.pageScopeState);
            try {
                next.run();
            } finally {
                pageScopeHandler.setState(null);
            }
        }
    }

}
