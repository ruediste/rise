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

public class SampleFrontServlet extends FrontServletBase {
    private boolean testing;

    public SampleFrontServlet() {
        this(false);
    }

    public SampleFrontServlet(boolean testing) {
        super(SampleRestartableApplication.class, !testing);
        this.testing = testing;
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
                        new EclipseLinkPersistenceUnitManager(),
                        new BitronixDataSourceFactory(
                                new H2DatabaseIntegrationInfo()) {

                    @Override
                    protected void initializeProperties(Properties props) {
                        if (testing)
                            props.setProperty("URL",
                                    "jdbc:h2:meme:sampleApp;DB_CLOSE_DELAY=-1;MVCC=false");
                        else
                            props.setProperty("URL",
                                    "jdbc:h2:file:~/sampleApp;DB_CLOSE_DELAY=-1;MVCC=false");
                        props.setProperty("user", "sa");
                        props.setProperty("password", "sa");
                    }
                });
            }

        }, new BitronixModule(), new IntegrationModuleNonRestartable(this));
        config.setBasePackage(SamplePackage.class);
    }

}
