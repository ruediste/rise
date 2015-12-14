package com.github.ruediste.rise.api;

import com.github.ruediste.rise.nonReloadable.CoreNonRestartableModule;
import com.github.ruediste.rise.nonReloadable.front.FrontServletBase;
import com.github.ruediste.rise.nonReloadable.front.LoggerModule;
import com.github.ruediste.salta.jsr330.AbstractModule;

public class ApplicationModuleNonRestartable extends AbstractModule {

    private FrontServletBase frontServlet;

    public ApplicationModuleNonRestartable(FrontServletBase frontServlet) {
        this.frontServlet = frontServlet;
    }

    @Override
    protected void configure() throws Exception {

        installCoreModule();
        installLoggerModule();
    }

    protected void installCoreModule() {
        install(new CoreNonRestartableModule(frontServlet));
    }

    protected void installLoggerModule() {
        install(new LoggerModule());
    }

}
