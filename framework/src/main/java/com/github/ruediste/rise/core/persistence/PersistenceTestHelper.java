package com.github.ruediste.rise.core.persistence;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.CoreRestartableModule;
import com.github.ruediste.rise.nonReloadable.ApplicationStage;
import com.github.ruediste.rise.nonReloadable.CoreConfigurationNonRestartable;
import com.github.ruediste.rise.nonReloadable.front.ApplicationEventQueue;
import com.github.ruediste.rise.nonReloadable.front.CurrentRestartableApplicationHolder;
import com.github.ruediste.rise.nonReloadable.front.CurrentRestartableApplicationHolder.RestartableApplicationInfo;
import com.github.ruediste.rise.nonReloadable.front.LoggerModule;
import com.github.ruediste.rise.nonReloadable.front.reload.ClassHierarchyIndex;
import com.github.ruediste.rise.nonReloadable.front.reload.ResourceChangeNotifier;
import com.github.ruediste.rise.nonReloadable.persistence.BitronixDataSourceFactory;
import com.github.ruediste.rise.nonReloadable.persistence.BitronixModule;
import com.github.ruediste.rise.nonReloadable.persistence.CompositeSessionCustomizer;
import com.github.ruediste.rise.nonReloadable.persistence.DataBaseLinkRegistry;
import com.github.ruediste.rise.nonReloadable.persistence.EclipseLinkPersistenceUnitManager;
import com.github.ruediste.rise.nonReloadable.persistence.EmbeddedFieldNamesSessionCustomizer;
import com.github.ruediste.rise.nonReloadable.persistence.H2DatabaseIntegrationInfo;
import com.github.ruediste.rise.nonReloadable.persistence.PersistenceModuleUtil;
import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.Injector;
import com.github.ruediste.salta.jsr330.Salta;
import com.github.ruediste.salta.jsr330.SaltaModule;

import bitronix.tm.resource.jdbc.PoolingDataSource;

public class PersistenceTestHelper {
    private static int initializationCount = 0;
    private Object testCase;

    @Inject
    private DataBaseLinkRegistry dbLinkRegistry;

    private SaltaModule additionalNonRestartableModule = new AbstractModule() {

        @Override
        protected void configure() throws Exception {

        }
    };

    private SaltaModule additionalRestartableModule = new AbstractModule() {

        @Override
        protected void configure() throws Exception {

        }
    };

    @SuppressWarnings("unchecked")
    private Class<? extends Annotation>[] qualifiers = new Class[] { null };
    private Class<?> basePackage;

    public PersistenceTestHelper(Object testCase, Class<?> basePackage) {
        this.testCase = testCase;
        this.basePackage = basePackage;

    }

    private static class NonRestartable {
        @Inject
        CurrentRestartableApplicationHolder appHolder;

        @Inject
        CoreConfigurationNonRestartable config;

        @Inject
        ResourceChangeNotifier notifier;

        @Inject
        Injector injector;

        @Inject
        DataBaseLinkRegistry registry;

        @Inject
        ApplicationEventQueue queue;

        @Inject
        ClassHierarchyIndex classHierarchyIndex;
    }

    NonRestartable nonRestartable = new NonRestartable();

    public void before() {
        CompositeSessionCustomizer.customizers = Arrays.asList(new EmbeddedFieldNamesSessionCustomizer());
        initializationCount++;
        Salta.createInjector(additionalNonRestartableModule, new AbstractModule() {

            @Override
            protected void configure() throws Exception {
                bind(ApplicationStage.class).toInstance(ApplicationStage.DEVELOPMENT);
                for (Class<? extends Annotation> qualifier : qualifiers) {
                    PersistenceModuleUtil.bindDataSource(binder(), qualifier, new EclipseLinkPersistenceUnitManager(),
                            new BitronixDataSourceFactory(new H2DatabaseIntegrationInfo()) {

                                @Override
                                protected void initializeProperties(Properties props) {
                                    props.setProperty("URL",
                                            "jdbc:h2:mem:test" + (qualifier == null ? "" : qualifier.getSimpleName())
                                                    + "_" + initializationCount + ";MVCC=false");
                                    props.setProperty("user", "sa");
                                    props.setProperty("password", "sa");
                                }

                                @Override
                                protected void customizeDataSource(PoolingDataSource btmDataSource) {
                                    btmDataSource.setMinPoolSize(1);
                                }
                            });

                }
            }
        }, new BitronixModule(), new LoggerModule()).injectMembers(nonRestartable);

        nonRestartable.appHolder
                .setCurrentApplication(new RestartableApplicationInfo(null, getClass().getClassLoader()));
        nonRestartable.config.addScannedPackage(basePackage);
        nonRestartable.classHierarchyIndex.setup();
        try {
            nonRestartable.queue.submit(() -> nonRestartable.notifier.start()).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        nonRestartable.registry.dropAndCreateSchemas();
        Injector restartableInjector = Salta.createInjector(additionalRestartableModule,
                new CoreRestartableModule(nonRestartable.injector), new LoggerModule(), new AbstractModule() {
                    @Override
                    protected void configure() throws Exception {
                    }

                });
        restartableInjector.injectMembers(testCase);
        restartableInjector.injectMembers(this);
        restartableInjector.getInstance(CoreConfiguration.class).initialize();
    }

    public void after() {
        dbLinkRegistry.close();
    }

    public void setAdditionalRestartableModule(SaltaModule additionalRestartableModule) {
        this.additionalRestartableModule = additionalRestartableModule;
    }

    public void setAdditionalNonRestartableModule(SaltaModule additionalNonRestartableModule) {
        this.additionalNonRestartableModule = additionalNonRestartableModule;
    }

    @SafeVarargs
    final public PersistenceTestHelper setQualifiers(Class<? extends Annotation>... qualifers) {
        this.qualifiers = qualifers;
        return this;
    }
}
