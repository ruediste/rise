package com.github.ruediste.rise.component.reload;

import javax.inject.Inject;
import javax.inject.Named;

import com.github.ruediste.rise.component.ComponentRequestInfo;
import com.github.ruediste.rise.component.ComponentSessionInfo;
import com.github.ruediste.rise.component.PageHandle;
import com.github.ruediste.rise.core.ChainedRequestHandler;
import com.github.ruediste.salta.standard.util.SimpleProxyScopeHandler;

public class ReloadPageScopeHandler extends ChainedRequestHandler {
    @Inject
    @Named("pageScoped")
    SimpleProxyScopeHandler pageScopeHandler;

    @Inject
    ComponentSessionInfo sessionInfo;

    @Inject
    PageReloadRequest request;

    @Inject
    ComponentRequestInfo info;

    @Override
    public void run(Runnable next) {
        PageHandle pageHandle = sessionInfo.getPageHandle(request.getPageNr());
        info.setPageHandle(pageHandle);
        synchronized (pageHandle.lock) {
            pageScopeHandler.enter(pageHandle.instances);
            try {
                next.run();
            } finally {
                pageScopeHandler.exit();
            }
        }
    }

}
