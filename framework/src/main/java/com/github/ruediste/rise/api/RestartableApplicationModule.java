package com.github.ruediste.rise.api;

import com.github.ruediste.rise.component.ComponentRestartableModule;
import com.github.ruediste.rise.core.CoreRestartableModule;
import com.github.ruediste.rise.mvc.MvcRestartableModule;
import com.github.ruediste.rise.nonReloadable.front.LoggerModule;
import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.Injector;

public class RestartableApplicationModule extends AbstractModule {

    private Injector permanentInjector;

    public RestartableApplicationModule(Injector permanentInjector) {
        this.permanentInjector = permanentInjector;

    }

    @Override
    protected void configure() throws Exception {
        installMvcWebModule();
        installComponentModule();
        installCoreModule();
        installLoggerModule();

    }

    protected void installComponentModule() {
        install(new ComponentRestartableModule(permanentInjector));
    }

    protected void installMvcWebModule() {
        install(new MvcRestartableModule(permanentInjector));
    }

    protected void installCoreModule() {
        install(new CoreRestartableModule(permanentInjector));
    }

    protected void installLoggerModule() {
        install(new LoggerModule());
    }

}
