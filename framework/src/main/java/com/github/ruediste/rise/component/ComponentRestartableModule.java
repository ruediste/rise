package com.github.ruediste.rise.component;

import javax.inject.Singleton;

import com.github.ruediste.rise.util.InitializerUtil;
import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.Injector;
import com.github.ruediste.salta.jsr330.Provides;
import com.github.ruediste.salta.standard.ScopeImpl;

public class ComponentRestartableModule extends AbstractModule {

    @SuppressWarnings("unused")
    private Injector permanentInjector;

    public ComponentRestartableModule(Injector permanentInjector) {
        this.permanentInjector = permanentInjector;
    }

    @Override
    protected void configure() throws Exception {
        PageScopeManager scopeHandler = new PageScopeManager();

        bindScope(PageScoped.class, new ScopeImpl(scopeHandler));
        bind(PageScopeManager.class).toInstance(scopeHandler);

        InitializerUtil.register(config(), ComponentRestartableInitializer.class);
    }

    @Provides
    @Singleton
    protected ViewFactory viewFactory(ComponentViewRepository repo) {
        return repo;
    }
}
