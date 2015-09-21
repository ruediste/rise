package com.github.ruediste.rise.test;

import org.junit.Before;

import com.github.ruediste.rise.api.RestartableApplicationModule;
import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.mvc.MvcPermanentModule;
import com.github.ruediste.rise.nonReloadable.CoreNonRestartableModule;
import com.github.ruediste.rise.nonReloadable.front.ApplicationEventQueue;
import com.github.ruediste.rise.nonReloadable.front.LoggerModule;
import com.github.ruediste.rise.util.InitializerUtil;
import com.github.ruediste.salta.jsr330.Injector;
import com.github.ruediste.salta.jsr330.Salta;

public abstract class SaltaTestBase {

    private Injector permanentInjector;

    @Before
    public void beforeSaltaTest() throws Exception {
        permanentInjector = Salta.createInjector(new MvcPermanentModule(),
                new CoreNonRestartableModule(null), new LoggerModule());
        permanentInjector.getInstance(ApplicationEventQueue.class)
                .submit(this::startInAET).get();
    }

    protected void initialize() {
    }

    private void startInAET() {
        initialize();
        InitializerUtil.runInitializers(permanentInjector);

        Injector instanceInjector = Salta.createInjector(
                new RestartableApplicationModule(permanentInjector));

        instanceInjector.getInstance(
                CoreConfiguration.class).dynamicClassLoader = Thread
                        .currentThread().getContextClassLoader();

        InitializerUtil.runInitializers(instanceInjector);
        instanceInjector.injectMembers(this);
    }
}
