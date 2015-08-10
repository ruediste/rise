package com.github.ruediste.rise.integration;

import javax.servlet.ServletConfig;

import com.github.ruediste.rise.api.ApplicationModuleNonRestartable;
import com.github.ruediste.salta.jsr330.AbstractModule;

/**
 * Module configuring the permanent injector for LAF
 */
public class IntegrationModuleNonRestartable extends AbstractModule {

    private ServletConfig servletConfig;

    public IntegrationModuleNonRestartable(ServletConfig servletConfig) {
        this.servletConfig = servletConfig;
    }

    @Override
    protected void configure() {
        install(new ApplicationModuleNonRestartable(servletConfig));
    }
}