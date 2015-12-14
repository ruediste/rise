package com.github.ruediste.rise.sample.front;

import java.util.Properties;

import javax.inject.Inject;

import com.github.ruediste.rise.integration.IntegrationModuleNonRestartable;
import com.github.ruediste.rise.nonReloadable.ApplicationStage;
import com.github.ruediste.rise.nonReloadable.CoreConfigurationNonRestartable;
import com.github.ruediste.rise.nonReloadable.front.FrontServletBase;
import com.github.ruediste.rise.nonReloadable.persistence.BitronixDataSourceFactory;
import com.github.ruediste.rise.nonReloadable.persistence.BitronixModule;
import com.github.ruediste.rise.nonReloadable.persistence.EclipseLinkPersistenceUnitManager;
import com.github.ruediste.rise.nonReloadable.persistence.H2DatabaseIntegrationInfo;
import com.github.ruediste.rise.nonReloadable.persistence.PersistenceModuleUtil;
import com.github.ruediste.rise.sample.SamplePackage;
import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.Provides;

public class SampleFrontServlet extends FrontServletBase {
    public SampleFrontServlet() {
        super(SampleRestartableApp.class);
    }

    @Inject
    CoreConfigurationNonRestartable config;

    private static final long serialVersionUID = 1L;

    @Override
    protected void initImpl() throws Exception {
        createInjector(ApplicationStage.DEVELOPMENT, new AbstractModule() {

            @Override
            protected void configure() throws Exception {
                PersistenceModuleUtil.bindDataSource(binder(), null,
                        new EclipseLinkPersistenceUnitManager("sampleApp"),
                        new BitronixDataSourceFactory(
                                new H2DatabaseIntegrationInfo()) {

                    @Override
                    protected void initializeProperties(Properties props) {
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
        }, new BitronixModule(), new IntegrationModuleNonRestartable(this));
        config.setBasePackage(SamplePackage.class);
    }

}
