package com.github.ruediste.rise.testApp.app;

import java.util.Properties;

import com.github.ruediste.rise.integration.IntegrationModuleNonRestartable;
import com.github.ruediste.rise.nonReloadable.ApplicationStage;
import com.github.ruediste.rise.nonReloadable.front.FrontServletBase;
import com.github.ruediste.rise.nonReloadable.front.RestartableApplication;
import com.github.ruediste.rise.nonReloadable.persistence.BitronixDataSourceFactory;
import com.github.ruediste.rise.nonReloadable.persistence.BitronixModule;
import com.github.ruediste.rise.nonReloadable.persistence.EclipseLinkPersistenceUnitManager;
import com.github.ruediste.rise.nonReloadable.persistence.H2DatabaseIntegrationInfo;
import com.github.ruediste.rise.nonReloadable.persistence.PersistenceModuleUtil;
import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste1.i18n.label.LabelUtil;

public class TestAppFrontServlet extends FrontServletBase {
    private static final long serialVersionUID = 1L;

    public TestAppFrontServlet() {
        this(TestRestartableApplication.class);
    }

    public TestAppFrontServlet(Class<? extends RestartableApplication> restartableApplicationClass) {
        super(restartableApplicationClass, true);
    }

    @Override
    protected void initImpl() throws Exception {
        createInjector(ApplicationStage.DEVELOPMENT, new AbstractModule() {

            @Override
            protected void configure() throws Exception {
                bind(LabelUtil.class).toInstance(new LabelUtil((str, locale) -> str.getFallback()));
                PersistenceModuleUtil.bindDataSource(binder(), null, new EclipseLinkPersistenceUnitManager(),
                        new BitronixDataSourceFactory(new H2DatabaseIntegrationInfo()) {

                            @Override
                            protected void initializeProperties(Properties props) {
                                props.setProperty("URL", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MVCC=false");
                                props.setProperty("user", "sa");
                                props.setProperty("password", "sa");
                            }
                        });
            }

        }, new BitronixModule(), new IntegrationModuleNonRestartable(this));
    }

}