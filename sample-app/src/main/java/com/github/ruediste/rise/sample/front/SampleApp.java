package com.github.ruediste.rise.sample.front;

import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.github.ruediste.rise.api.RestartableApplicationModule;
import com.github.ruediste.rise.component.ComponentTemplateIndex;
import com.github.ruediste.rise.component.components.CPage;
import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.DefaultRequestErrorHandler;
import com.github.ruediste.rise.core.front.RestartableApplicationBase;
import com.github.ruediste.rise.nonReloadable.ApplicationStage;
import com.github.ruediste.rise.sample.SampleCanvas;
import com.github.ruediste.rise.sample.SamplePackage;
import com.github.ruediste.rise.sample.component.CPageHtmlTemplate;
import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.Injector;
import com.github.ruediste.salta.jsr330.Provides;
import com.github.ruediste.salta.jsr330.Salta;
import com.github.ruediste1.i18n.lString.DefaultPatternStringResolver;
import com.github.ruediste1.i18n.lString.PatternStringResolver;
import com.github.ruediste1.i18n.lString.ResouceBundleTranslatedStringResolver;
import com.github.ruediste1.i18n.lString.TranslatedStringResolver;

public class SampleApp extends RestartableApplicationBase {

    @Inject
    CoreConfiguration config;

    @Inject
    ComponentTemplateIndex componentTemplateIndex;

    @Inject
    DefaultRequestErrorHandler errorHandler;

    @Inject
    DevelopmentFixture fixture;

    @Inject
    Provider<SampleCanvas> canvasProvider;

    @Override
    protected void startImpl(Injector permanentInjector) {
        Salta.createInjector(
                permanentInjector.getInstance(ApplicationStage.class)
                        .getSaltaStage(), new AbstractModule() {

                    @Override
                    protected void configure() throws Exception {
                        bind(PatternStringResolver.class).to(
                                DefaultPatternStringResolver.class).in(
                                Singleton.class);

                    }

                    @Provides
                    @Singleton
                    TranslatedStringResolver tStringResolver(
                            ResouceBundleTranslatedStringResolver resolver) {
                        resolver.initialize("translations/translations");
                        return resolver;
                    }

                }, new RestartableApplicationModule(permanentInjector))
                .injectMembers(this);

        errorHandler.initialize(util -> util.go(ReqestErrorController.class)
                .index());
        config.requestErrorHandler = errorHandler;

        config.setBasePackage(SamplePackage.class);
        config.applicationCanvasFactory = Optional.of(canvasProvider::get);

        config.developmentFixtureLoader = Optional.of(fixture);

        componentTemplateIndex.registerTemplate(CPage.class,
                CPageHtmlTemplate.class);
    }
}
