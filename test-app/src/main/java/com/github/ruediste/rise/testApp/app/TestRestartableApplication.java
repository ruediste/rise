package com.github.ruediste.rise.testApp.app;

import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.ruediste.rise.component.ComponentTemplateIndex;
import com.github.ruediste.rise.component.components.CPage;
import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.DefaultRequestErrorHandler;
import com.github.ruediste.rise.core.front.RestartableApplicationBase;
import com.github.ruediste.rise.integration.DynamicIntegrationModule;
import com.github.ruediste.rise.nonReloadable.ApplicationStage;
import com.github.ruediste.rise.nonReloadable.persistence.DataBaseLinkRegistry;
import com.github.ruediste.rise.testApp.TestCanvas;
import com.github.ruediste.rise.testApp.component.CPageTemplate;
import com.github.ruediste.rise.util.InitializerUtil;
import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.Injector;
import com.github.ruediste.salta.jsr330.Provides;
import com.github.ruediste.salta.jsr330.Salta;
import com.github.ruediste1.i18n.lString.DefaultPatternStringResolver;
import com.github.ruediste1.i18n.lString.PatternStringResolver;
import com.github.ruediste1.i18n.lString.ResouceBundleTranslatedStringResolver;
import com.github.ruediste1.i18n.lString.TranslatedStringResolver;

public class TestRestartableApplication extends RestartableApplicationBase {

    @Inject
    ComponentTemplateIndex index;

    @Inject
    CoreConfiguration config;

    @Inject
    javax.inject.Provider<TestCanvas> canvasProvider;

    @Inject
    DefaultRequestErrorHandler errorHandler;

    private static class Initializer implements
            com.github.ruediste.rise.util.Initializer {

        @Inject
        DataBaseLinkRegistry registry;

        @Override
        public void initialize() {
            registry.dropAndCreateSchemas();

        }

    }

    @Override
    protected void startImpl(Injector nonRestartableInjector) {
        ApplicationStage stage = nonRestartableInjector
                .getInstance(ApplicationStage.class);
        Salta.createInjector(stage.getSaltaStage(), new AbstractModule() {

            @Override
            protected void configure() throws Exception {
                InitializerUtil.register(config(), Initializer.class);
                bind(PatternStringResolver.class).to(
                        DefaultPatternStringResolver.class);
            }

            @Singleton
            @Provides
            public TranslatedStringResolver resolver(
                    ResouceBundleTranslatedStringResolver resolver) {
                resolver.initialize("translations/translations");
                return resolver;
            }
        }, new DynamicIntegrationModule(nonRestartableInjector)).injectMembers(
                this);
        index.registerTemplate(CPage.class, CPageTemplate.class);
        config.applicationCanvasFactory = Optional.of(canvasProvider::get);
        config.requestErrorHandler = errorHandler;
        errorHandler.initialize(util -> util.go(
                RequestErrorHandlerController.class).index());
    }

}
