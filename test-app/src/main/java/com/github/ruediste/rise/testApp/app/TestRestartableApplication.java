package com.github.ruediste.rise.testApp.app;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.ruediste.rise.component.ComponentConfiguration;
import com.github.ruediste.rise.component.ComponentTemplateIndex;
import com.github.ruediste.rise.component.components.CPage;
import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.DefaultRequestErrorHandler;
import com.github.ruediste.rise.core.front.RestartableApplicationBase;
import com.github.ruediste.rise.core.security.Principal;
import com.github.ruediste.rise.core.security.authentication.AuthenticationSuccess;
import com.github.ruediste.rise.core.security.authentication.DefaultAuthenticationManager;
import com.github.ruediste.rise.core.security.authentication.InMemoryAuthenticationProvider;
import com.github.ruediste.rise.core.security.authorization.AuthorizationFailure;
import com.github.ruediste.rise.core.security.authorization.AuthorizationManager;
import com.github.ruediste.rise.core.security.authorization.AuthorizationResult;
import com.github.ruediste.rise.core.security.authorization.RequiresRightAnnotationRight;
import com.github.ruediste.rise.core.security.authorization.Right;
import com.github.ruediste.rise.core.security.web.rememberMe.InMemoryRememberMeTokenDao;
import com.github.ruediste.rise.core.security.web.rememberMe.RememberMeAuthenticationProvider;
import com.github.ruediste.rise.integration.DynamicIntegrationModule;
import com.github.ruediste.rise.nonReloadable.ApplicationStage;
import com.github.ruediste.rise.nonReloadable.persistence.DataBaseLinkRegistry;
import com.github.ruediste.rise.testApp.Rights;
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

    @Inject
    DefaultAuthenticationManager defaultAuthenticationManager;

    @Inject
    AuthorizationManager authorizationManager;

    @Inject
    RememberMeAuthenticationProvider rememberMeAuthenticationProvider;

    @Inject
    InMemoryRememberMeTokenDao rememberMeTokenDao;

    @Inject
    ComponentConfiguration componentConfiguration;

    private static class Initializer
            implements com.github.ruediste.rise.util.Initializer {

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
                bind(PatternStringResolver.class)
                        .to(DefaultPatternStringResolver.class);
            }

            @Singleton
            @Provides
            public TranslatedStringResolver resolver(
                    ResouceBundleTranslatedStringResolver resolver) {
                resolver.initialize("translations/translations");
                return resolver;
            }
        }, new DynamicIntegrationModule(nonRestartableInjector))
                .injectMembers(this);
        index.registerTemplate(CPage.class, CPageTemplate.class);
        config.applicationCanvasFactory = Optional.of(canvasProvider::get);
        config.requestErrorHandler = errorHandler;
        errorHandler.initialize(
                util -> util.go(RequestErrorHandlerController.class).index());
        componentConfiguration.heartbeatInterval = Duration.ofSeconds(1);
        componentConfiguration.heartbeatInterval = Duration.ofSeconds(3);

        // security
        rememberMeAuthenticationProvider.setDao(rememberMeTokenDao);
        defaultAuthenticationManager
                .addProvider(rememberMeAuthenticationProvider);

        defaultAuthenticationManager
                .addProvider(new InMemoryAuthenticationProvider<Principal>()
                        .with("foo", "foo", null));

        authorizationManager
                .setAuthorizationPerformer((Set<? extends Right> rights,
                        Optional<AuthenticationSuccess> authentication) -> {
                    for (Right right : rights) {
                        if (right instanceof RequiresRightAnnotationRight) {
                            Object value = ((RequiresRightAnnotationRight) right)
                                    .getValue();
                            if (Objects.equals(Rights.ALLOWED, value))
                                continue;
                        }
                        return AuthorizationResult
                                .failure(new AuthorizationFailure(
                                        "right was not ALLOWED"));
                    }
                    return AuthorizationResult.authorized();
                });

    }

}
