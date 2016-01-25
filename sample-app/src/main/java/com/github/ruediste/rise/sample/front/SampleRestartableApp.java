package com.github.ruediste.rise.sample.front;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.github.ruediste.rise.api.RestartableApplicationModule;
import com.github.ruediste.rise.component.ComponentTemplateIndex;
import com.github.ruediste.rise.component.components.CPage;
import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.DefaultRequestErrorHandler;
import com.github.ruediste.rise.core.front.RestartableApplicationBase;
import com.github.ruediste.rise.core.security.Principal;
import com.github.ruediste.rise.core.security.authentication.InMemoryAuthenticationProvider;
import com.github.ruediste.rise.core.security.authentication.core.AuthenticationSuccess;
import com.github.ruediste.rise.core.security.authentication.core.DefaultAuthenticationManager;
import com.github.ruediste.rise.core.security.authorization.AuthorizationFailure;
import com.github.ruediste.rise.core.security.authorization.AuthorizationDecisionManager;
import com.github.ruediste.rise.core.security.authorization.AuthorizationResult;
import com.github.ruediste.rise.core.security.authorization.MethodAuthorizationManager;
import com.github.ruediste.rise.core.security.authorization.Right;
import com.github.ruediste.rise.core.security.web.rememberMe.InMemoryRememberMeTokenDao;
import com.github.ruediste.rise.core.security.web.rememberMe.RememberMeAuthenticationProvider;
import com.github.ruediste.rise.core.web.assetPipeline.AssetPipelineConfiguration;
import com.github.ruediste.rise.nonReloadable.ApplicationStage;
import com.github.ruediste.rise.sample.SampleCanvas;
import com.github.ruediste.rise.sample.component.CPageHtmlTemplate;
import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.Injector;
import com.github.ruediste.salta.jsr330.Provides;
import com.github.ruediste.salta.jsr330.Salta;
import com.github.ruediste1.i18n.lString.DefaultPatternStringResolver;
import com.github.ruediste1.i18n.lString.PatternStringResolver;
import com.github.ruediste1.i18n.lString.ResouceBundleTranslatedStringResolver;
import com.github.ruediste1.i18n.lString.TranslatedStringResolver;

public class SampleRestartableApp extends RestartableApplicationBase {

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

    @Inject
    AssetPipelineConfiguration assetPipelineConfiguration;

    @Inject
    DefaultAuthenticationManager defaultAuthenticationManager;

    @Inject
    AuthorizationDecisionManager authorizationDecisionManager;

    @Inject
    RememberMeAuthenticationProvider rememberMeAuthenticationProvider;

    @Inject
    InMemoryRememberMeTokenDao rememberMeTokenDao;

    @Override
    protected void startImpl(Injector permanentInjector) {
        Salta.createInjector(permanentInjector
                .getInstance(ApplicationStage.class).getSaltaStage(),
                new AbstractModule() {

                    @Override
                    protected void configure() throws Exception {
                        bind(PatternStringResolver.class)
                                .to(DefaultPatternStringResolver.class)
                                .in(Singleton.class);
                        MethodAuthorizationManager.get(binder()).addRule(
                                RequiresRight.class,
                                a -> Collections.singleton(a.value()));

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

        errorHandler.initialize(
                util -> util.go(ReqestErrorController.class).index());
        config.requestErrorHandler = errorHandler;

        config.applicationCanvasFactory = Optional.of(canvasProvider::get);

        config.developmentFixtureLoader = Optional.of(fixture);

        componentTemplateIndex.registerTemplate(CPage.class,
                CPageHtmlTemplate.class);

        // assetPipelineConfiguration.assetMode = AssetMode.PRODUCTION;

        // security
        rememberMeAuthenticationProvider.setDao(rememberMeTokenDao);
        defaultAuthenticationManager
                .addProvider(rememberMeAuthenticationProvider);

        defaultAuthenticationManager
                .addProvider(new InMemoryAuthenticationProvider<Principal>()
                        .with("admin", "admin",
                                new ExplicitRightsPrincipal(
                                        SampleRight.VIEW_USER_PAGE,
                                        SampleRight.VIEW_ADMIN_PAGE))
                        .with("user", "user", new ExplicitRightsPrincipal(
                                SampleRight.VIEW_USER_PAGE)));

        authorizationDecisionManager
                .setPerformer((Set<? extends Right> rights,
                        Optional<AuthenticationSuccess> authentication) -> {
                    if (!authentication.isPresent()) {
                        if (rights.isEmpty())
                            return AuthorizationResult.authorized();
                        else
                            return AuthorizationResult
                                    .failure(new AuthorizationFailure(
                                            "No user logged in"));
                    }
                    Principal principal = authentication.get().getPrincipal();
                    if (principal instanceof ExplicitRightsPrincipal) {
                        for (Right right : rights) {
                            if (((ExplicitRightsPrincipal) principal).grantedRights
                                    .contains(right))
                                continue;
                            return AuthorizationResult.failure(
                                    new AuthorizationFailure("right " + right
                                            + " is not granted for principal "
                                            + principal));
                        }
                    } else
                        return AuthorizationResult
                                .failure(new AuthorizationFailure(
                                        "Unable to handle principal "
                                                + principal));

                    return AuthorizationResult.authorized();
                });

    }
}
