package com.github.ruediste.rise.component;

import com.github.ruediste.salta.standard.util.SimpleProxyScopeManager;

/**
 * Scope handler for the the page scope. Do not enter this scope without also
 * locking {@link PageHandle#lock}.
 * 
 * @see ComponentUtil#runInPageScope(Runnable)
 * @see PageScopeManager
 */
public class PageScopeManager extends SimpleProxyScopeManager {

    public PageScopeManager() {
        super("pageScope");
    }

}
