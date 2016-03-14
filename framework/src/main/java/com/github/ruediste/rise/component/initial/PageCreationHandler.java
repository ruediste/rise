package com.github.ruediste.rise.component.initial;

import javax.inject.Inject;

import com.github.ruediste.rise.component.ComponentPage;
import com.github.ruediste.rise.component.ComponentPageHandleRepository;
import com.github.ruediste.rise.component.ComponentRequestInfo;
import com.github.ruediste.rise.component.IControllerComponent;
import com.github.ruediste.rise.component.PageHandle;
import com.github.ruediste.rise.component.PageScopeManager;
import com.github.ruediste.rise.core.ChainedRequestHandler;
import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.salta.jsr330.Injector;

public class PageCreationHandler extends ChainedRequestHandler {

    @Inject
    PageScopeManager pageScopeHandler;

    @Inject
    ComponentPage pageInfo;

    @Inject
    ComponentPageHandleRepository sessionInfo;

    @Inject
    CoreRequestInfo coreRequestInfo;

    @Inject
    Injector injector;

    @Inject
    ComponentRequestInfo componentRequestInfo;

    @Override
    public void run(Runnable next) {
        PageHandle handle = sessionInfo.createPageHandle();
        synchronized (handle.lock) {
            componentRequestInfo.setPageHandle(handle);
            handle.pageScopeState = pageScopeHandler.createFreshState();
            try {
                pageScopeHandler.setState(handle.pageScopeState);
                ComponentPage pi = pageInfo.self();
                pi.setPageId(handle.id);

                Object controller = injector
                        .getInstance(coreRequestInfo.getStringActionInvocation().methodInvocation.getInstanceClass());
                pi.setController((IControllerComponent) controller);

                next.run();
            } finally {
                pageScopeHandler.setState(null);
            }
        }
    }
}
