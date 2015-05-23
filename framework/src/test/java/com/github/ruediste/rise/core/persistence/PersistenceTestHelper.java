package com.github.ruediste.rise.core.persistence;

import java.util.Properties;

import bitronix.tm.resource.jdbc.PoolingDataSource;

import com.github.ruediste.rise.core.CoreRestartableModule;
import com.github.ruediste.rise.nonReloadable.front.LoggerModule;
import com.github.ruediste.rise.nonReloadable.persistence.BitronixDataSourceFactory;
import com.github.ruediste.rise.nonReloadable.persistence.BitronixModule;
import com.github.ruediste.rise.nonReloadable.persistence.DataBaseLinkRegistry;
import com.github.ruediste.rise.nonReloadable.persistence.EclipseLinkPersistenceUnitManager;
import com.github.ruediste.rise.nonReloadable.persistence.H2DatabaseIntegrationInfo;
import com.github.ruediste.rise.nonReloadable.persistence.PersistenceModuleUtil;
import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.Injector;
import com.github.ruediste.salta.jsr330.Salta;

public class PersistenceTestHelper {
    private static int dbCount = 0;
    private Object testCase;
    private DataBaseLinkRegistry dbLinkRegistry;

    PersistenceTestHelper(Object testCase) {
        this.testCase = testCase;

    }

    public void before() {

        Injector permanentInjector = Salta.createInjector(new AbstractModule() {

            @Override
            protected void configure() throws Exception {
                PersistenceModuleUtil.bindDataSource(binder(), null,
                        new EclipseLinkPersistenceUnitManager("frameworkTest"),
                        new BitronixDataSourceFactory(
                                new H2DatabaseIntegrationInfo()) {

                            @Override
                            protected void initializeProperties(Properties props) {
                                props.setProperty("URL", "jdbc:h2:mem:test"
                                        + (dbCount++) + ";MVCC=false");
                                props.setProperty("user", "sa");
                                props.setProperty("password", "sa");
                            }

                            @Override
                            protected void customizeDataSource(
                                    PoolingDataSource btmDataSource) {
                                btmDataSource.setMinPoolSize(1);
                            }
                        });
            }
        }, new BitronixModule(), new LoggerModule());

        dbLinkRegistry = permanentInjector
                .getInstance(DataBaseLinkRegistry.class);
        Salta.createInjector(new CoreRestartableModule(permanentInjector),
                new LoggerModule()).injectMembers(testCase);
    }

    public void after() {
        dbLinkRegistry.close();
    }
}
