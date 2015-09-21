package com.github.ruediste.rise.core.persistence;

import java.util.Properties;

import bitronix.tm.resource.jdbc.PoolingDataSource;

import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.CoreRestartableModule;
import com.github.ruediste.rise.nonReloadable.ApplicationStage;
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
    private static int initializationCount = 0;
    private Object testCase;
    private DataBaseLinkRegistry dbLinkRegistry;

    public PersistenceTestHelper(Object testCase) {
        this.testCase = testCase;

    }

    public void before() {
        initializationCount++;
        Injector nonRestartableInjector = Salta
                .createInjector(new AbstractModule() {

                    @Override
                    protected void configure() throws Exception {
                        bind(ApplicationStage.class)
                                .toInstance(ApplicationStage.DEVELOPMENT);
                        PersistenceModuleUtil.bindDataSource(binder(), null,
                                new EclipseLinkPersistenceUnitManager(
                                        "frameworkTest"),
                                new BitronixDataSourceFactory(
                                        new H2DatabaseIntegrationInfo()) {

                            @Override
                            protected void initializeProperties(
                                    Properties props) {
                                props.setProperty("URL", "jdbc:h2:mem:test"
                                        + initializationCount + ";MVCC=false");
                                props.setProperty("user", "sa");
                                props.setProperty("password", "sa");
                            }

                            @Override
                            protected void customizeDataSource(
                                    PoolingDataSource btmDataSource) {
                                btmDataSource.setMinPoolSize(1);
                            }
                        });
                        PersistenceModuleUtil.bindDataSource(binder(),
                                Unit1.class,
                                new EclipseLinkPersistenceUnitManager(
                                        "frameworkTest1"),
                                new BitronixDataSourceFactory(
                                        new H2DatabaseIntegrationInfo()) {

                            @Override
                            protected void initializeProperties(
                                    Properties props) {
                                props.setProperty("URL", "jdbc:h2:mem:test1"
                                        + initializationCount + ";MVCC=false");
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

        dbLinkRegistry = nonRestartableInjector
                .getInstance(DataBaseLinkRegistry.class);
        dbLinkRegistry.dropAndCreateSchemas();
        Injector restartableInjector = Salta.createInjector(
                new CoreRestartableModule(nonRestartableInjector),
                new LoggerModule(), new AbstractModule() {
                    @Override
                    protected void configure() throws Exception {
                    }
                });
        restartableInjector.injectMembers(testCase);
        restartableInjector.getInstance(CoreConfiguration.class).initialize();
    }

    public void after() {
        dbLinkRegistry.close();
    }
}
