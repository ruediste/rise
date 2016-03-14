package com.github.ruediste.rise.testApp.app;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import com.github.ruediste.rise.component.ComponentConfiguration;
import com.github.ruediste.rise.component.ComponentTemplateIndex;
import com.github.ruediste.rise.component.components.CPage;
import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.DefaultRequestErrorHandler;
import com.github.ruediste.rise.core.front.RestartableApplicationBase;
import com.github.ruediste.rise.core.security.Principal;
import com.github.ruediste.rise.core.security.authentication.InMemoryAuthenticationProvider;
import com.github.ruediste.rise.core.security.authentication.core.AuthenticationSuccess;
import com.github.ruediste.rise.core.security.authentication.core.DefaultAuthenticationManager;
import com.github.ruediste.rise.core.security.authorization.AuthorizationDecisionManager;
import com.github.ruediste.rise.core.security.authorization.AuthorizationFailure;
import com.github.ruediste.rise.core.security.authorization.AuthorizationResult;
import com.github.ruediste.rise.core.security.authorization.MethodAuthorizationManager;
import com.github.ruediste.rise.core.security.authorization.Right;
import com.github.ruediste.rise.core.security.web.rememberMe.InMemoryRememberMeTokenDao;
import com.github.ruediste.rise.core.security.web.rememberMe.RememberMeAuthenticationProvider;
import com.github.ruediste.rise.integration.DynamicIntegrationModule;
import com.github.ruediste.rise.nonReloadable.ApplicationStage;
import com.github.ruediste.rise.nonReloadable.persistence.DataBaseLinkRegistry;
import com.github.ruediste.rise.testApp.RequiresRight;
import com.github.ruediste.rise.testApp.Rights;
import com.github.ruediste.rise.testApp.TestCanvas;
import com.github.ruediste.rise.testApp.component.CPageTemplate;
import com.github.ruediste.rise.util.InitializerUtil;
import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.Injector;
import com.github.ruediste.salta.jsr330.Salta;

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
    AuthorizationDecisionManager authorizationManager;

    @Inject
    RememberMeAuthenticationProvider rememberMeAuthenticationProvider;

    @Inject
    InMemoryRememberMeTokenDao rememberMeTokenDao;

    @Inject
    ComponentConfiguration componentConfiguration;

    private static class Initializer implements com.github.ruediste.rise.util.Initializer {

        @Inject
        DataBaseLinkRegistry registry;

        @Override
        public void initialize() {
            registry.dropAndCreateSchemas();

        }

    }

    @Override
    protected void startImpl(Injector nonRestartableInjector) {
        ApplicationStage stage = nonRestartableInjector.getInstance(ApplicationStage.class);
        Salta.createInjector(stage.getSaltaStage(), new AbstractModule() {

            @Override
            protected void configure() throws Exception {
                InitializerUtil.register(config(), Initializer.class);
                MethodAuthorizationManager.get(binder()).addRule(RequiresRight.class,
                        a -> new HashSet<>(Arrays.asList(a.value())));
            }

        }, new DynamicIntegrationModule(nonRestartableInjector)).injectMembers(this);
        index.registerTemplate(CPage.class, CPageTemplate.class);
        config.applicationCanvasFactory = Optional.of(canvasProvider::get);
        config.requestErrorHandler = errorHandler;
        errorHandler.initialize(util -> util.go(RequestErrorHandlerController.class).index());
        componentConfiguration.heartbeatInterval = Duration.ofSeconds(1);
        componentConfiguration.heartbeatInterval = Duration.ofSeconds(3);

        // security
        rememberMeAuthenticationProvider.setDao(rememberMeTokenDao);
        defaultAuthenticationManager.addProvider(rememberMeAuthenticationProvider);

        defaultAuthenticationManager
                .addProvider(new InMemoryAuthenticationProvider<Principal>().with("foo", "foo", null));

        authorizationManager
                .setPerformer((Set<? extends Right> rights, Optional<AuthenticationSuccess> authentication) -> {
                    for (Right right : rights) {

                        if (Objects.equals(Rights.ALLOWED, right))
                            continue;
                        return AuthorizationResult.failure(new AuthorizationFailure("right was not ALLOWED"));
                    }
                    return AuthorizationResult.authorized();
                });

    }

}
