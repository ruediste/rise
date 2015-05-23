package com.github.ruediste.rise.testApp.app;

import java.util.Properties;

import com.github.ruediste.rise.integration.PermanentIntegrationModule;
import com.github.ruediste.rise.nonReloadable.front.FrontServletBase;
import com.github.ruediste.rise.nonReloadable.persistence.BitronixDataSourceFactory;
import com.github.ruediste.rise.nonReloadable.persistence.BitronixModule;
import com.github.ruediste.rise.nonReloadable.persistence.EclipseLinkPersistenceUnitManager;
import com.github.ruediste.rise.nonReloadable.persistence.H2DatabaseIntegrationInfo;
import com.github.ruediste.rise.nonReloadable.persistence.PersistenceModuleUtil;
import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.Salta;

public class TestAppFrontServlet extends FrontServletBase {
    private static final long serialVersionUID = 1L;

    public TestAppFrontServlet(
            TestRestartableApplication fixedApplicationInstance) {
        super(fixedApplicationInstance);
    }

    public TestAppFrontServlet() {
        super(TestRestartableApplication.class);
    }

    @Override
    protected void initImpl() throws Exception {
        Salta.createInjector(
                new AbstractModule() {

                    @Override
                    protected void configure() throws Exception {
                        PersistenceModuleUtil.bindDataSource(
                                binder(),
                                null,
                                new EclipseLinkPersistenceUnitManager("testApp"),
                                new BitronixDataSourceFactory(
                                        new H2DatabaseIntegrationInfo()) {

                                    @Override
                                    protected void initializeProperties(
                                            Properties props) {
                                        props.setProperty("URL",
                                                "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MVCC=false");
                                        props.setProperty("user", "sa");
                                        props.setProperty("password", "sa");
                                    }
                                });
                    }
                }, new BitronixModule(),
                new PermanentIntegrationModule(getServletConfig()))
                .injectMembers(this);
    }

}