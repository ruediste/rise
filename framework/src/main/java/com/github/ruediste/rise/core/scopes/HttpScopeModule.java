package com.github.ruediste.rise.core.scopes;

import com.github.ruediste.rise.core.scopes.HttpScopeManager.SessionScopeManager;
import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.Salta;
import com.github.ruediste.salta.standard.ScopeImpl;

/**
 * {@link Salta} module binding {@link RequestScoped} and {@link SessionScoped}
 * as well as the {@link SessionScopeManager}.
 */
public class HttpScopeModule extends AbstractModule {

    @Override
    protected void configure() {

        HttpScopeManager scopeManager = new HttpScopeManager();

        bind(HttpScopeManager.class).toInstance(scopeManager);

        // bind scopes
        bindScope(SessionScoped.class, new ScopeImpl(scopeManager.sessionScopeManager));
        bindScope(RequestScoped.class, new ScopeImpl(scopeManager.requestScopeHandler));
    }

}