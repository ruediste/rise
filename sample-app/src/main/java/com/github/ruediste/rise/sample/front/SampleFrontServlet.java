package com.github.ruediste.rise.sample.front;

import java.util.Properties;

import com.github.ruediste.rise.integration.PermanentIntegrationModule;
import com.github.ruediste.rise.nonReloadable.ApplicationStage;
import com.github.ruediste.rise.nonReloadable.front.FrontServletBase;
import com.github.ruediste.rise.nonReloadable.persistence.BitronixDataSourceFactory;
import com.github.ruediste.rise.nonReloadable.persistence.BitronixModule;
import com.github.ruediste.rise.nonReloadable.persistence.EclipseLinkPersistenceUnitManager;
import com.github.ruediste.rise.nonReloadable.persistence.H2DatabaseIntegrationInfo;
import com.github.ruediste.rise.nonReloadable.persistence.PersistenceModuleUtil;
import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.Provides;
import com.github.ruediste.salta.jsr330.Salta;

public class SampleFrontServlet extends FrontServletBase {
    public SampleFrontServlet() {
        super(SampleApp.class);
    }

    private static final long serialVersionUID = 1L;

    @Override
    protected void initImpl() throws Exception {
        // set stage in the super class
        setStage(ApplicationStage.PRODUCTION);
        Salta.createInjector(
                new AbstractModule() {

                    @Override
                    protected void configure() throws Exception {
                        PersistenceModuleUtil.bindDataSource(binder(), null,
                                new EclipseLinkPersistenceUnitManager(
                                        "sampleApp"),
                                new BitronixDataSourceFactory(
                                        new H2DatabaseIntegrationInfo()) {

                                    @Override
                                    protected void initializeProperties(
                                            Properties props) {
                                        props.setProperty("URL",
                                                "jdbc:h2:file:~/sampleApp;DB_CLOSE_DELAY=-1;MVCC=false");
                                        props.setProperty("user", "sa");
                                        props.setProperty("password", "sa");
                                    }
                                });
                    }

                    @Provides
                    ApplicationStage stage() {
                        // get the stage from the super class
                        return getStage();
                    }
                }, new BitronixModule(),
                new PermanentIntegrationModule(getServletConfig()))
                .injectMembers(this);
    }
}
