package com.github.ruediste.rise.integration;

import com.github.ruediste.rise.api.ApplicationModuleNonRestartable;
import com.github.ruediste.rise.nonReloadable.ApplicationStage;
import com.github.ruediste.rise.nonReloadable.front.FrontServletBase;
import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.Provides;

/**
 * Module configuring the permanent injector for LAF
 */
public class IntegrationModuleNonRestartable extends AbstractModule {

    private FrontServletBase frontServlet;

    public IntegrationModuleNonRestartable(FrontServletBase frontServlet) {
        this.frontServlet = frontServlet;
    }

    @Override
    protected void configure() {
        install(new ApplicationModuleNonRestartable(frontServlet));
    }

    @Provides
    ApplicationStage stage() {
        // get the stage from the servlet
        return frontServlet.getStage();
    }
}