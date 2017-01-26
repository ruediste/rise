package com.github.ruediste.rise.sample.front;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;

import com.github.ruediste.rise.api.RestartableApplicationModule;
import com.github.ruediste.rise.component.ComponentTemplateIndex;
import com.github.ruediste.rise.component.components.CPage;
import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.DefaultRequestErrorHandler;
import com.github.ruediste.rise.core.front.RestartableApplicationBase;
import com.github.ruediste.rise.core.security.Principal;
import com.github.ruediste.rise.core.security.authentication.PasswordMismatchAuthenticationFailure;
import com.github.ruediste.rise.core.security.authentication.UserNameNotFoundAuthenticationFailure;
import com.github.ruediste.rise.core.security.authentication.AuthenticationRequestUsernamePassword;
import com.github.ruediste.rise.core.security.authentication.core.AuthenticationProvider;
import com.github.ruediste.rise.core.security.authentication.core.AuthenticationResult;
import com.github.ruediste.rise.core.security.authentication.core.AuthenticationSuccess;
import com.github.ruediste.rise.core.security.authentication.core.DefaultAuthenticationManager;
import com.github.ruediste.rise.core.security.authorization.AuthorizationDecisionManager;
import com.github.ruediste.rise.core.security.authorization.AuthorizationFailure;
import com.github.ruediste.rise.core.security.authorization.AuthorizationResult;
import com.github.ruediste.rise.core.security.authorization.MethodAuthorizationManager;
import com.github.ruediste.rise.core.security.authorization.Right;
import com.github.ruediste.rise.core.security.login.PasswordHashingService;
import com.github.ruediste.rise.core.security.web.rememberMe.InMemoryRememberMeTokenDao;
import com.github.ruediste.rise.core.security.web.rememberMe.RememberMeAuthenticationProvider;
import com.github.ruediste.rise.nonReloadable.ApplicationStage;
import com.github.ruediste.rise.sample.SampleCanvas;
import com.github.ruediste.rise.sample.User;
import com.github.ruediste.rise.sample.UserRepository;
import com.github.ruediste.rise.sample.component.CPageHtmlTemplate;
import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.Injector;
import com.github.ruediste.salta.jsr330.Salta;

public class SampleRestartableApplication extends RestartableApplicationBase {

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
    DefaultAuthenticationManager defaultAuthenticationManager;

    @Inject
    AuthorizationDecisionManager authorizationDecisionManager;

    @Inject
    RememberMeAuthenticationProvider rememberMeAuthenticationProvider;

    @Inject
    InMemoryRememberMeTokenDao rememberMeTokenDao;

    @Inject
    UserRepository userRepository;

    @Inject
    PasswordHashingService passwordHashingService;

    private static class UserPrincipal implements Principal, Serializable {
        private static final long serialVersionUID = 1L;
        private final long userId;

        public UserPrincipal(long userId) {
            this.userId = userId;
        }

        public long getUserId() {
            return userId;
        }

    }

    @Override
    protected void startImpl(Injector permanentInjector) {
        Salta.createInjector(permanentInjector.getInstance(ApplicationStage.class).getSaltaStage(),
                new AbstractModule() {

                    @Override
                    protected void configure() throws Exception {

                        MethodAuthorizationManager.get(binder()).addRule(RequiresRight.class,
                                a -> Arrays.asList(a.value()));

                    }

                }, new RestartableApplicationModule(permanentInjector)).injectMembers(this);

        errorHandler.initialize(util -> util.go(ReqestErrorController.class).index());
        config.requestErrorHandler = errorHandler;

        config.applicationCanvasFactory = Optional.of(canvasProvider::get);

        config.developmentFixtureLoader = Optional.of(fixture);

        componentTemplateIndex.registerTemplate(CPage.class, CPageHtmlTemplate.class);

        // assetPipelineConfiguration.assetMode = AssetMode.PRODUCTION;

        // security
        rememberMeAuthenticationProvider.setDao(rememberMeTokenDao);
        defaultAuthenticationManager.addProvider(rememberMeAuthenticationProvider);

        // defaultAuthenticationManager
        // .addProvider(new InMemoryAuthenticationProvider<Principal>()
        // .with("admin", "admin",
        // new ExplicitRightsPrincipal(
        // SampleRight.VIEW_USER_PAGE,
        // SampleRight.VIEW_ADMIN_PAGE))
        // .with("user", "user", new ExplicitRightsPrincipal(
        // SampleRight.VIEW_USER_PAGE)));

        defaultAuthenticationManager.addProvider(new AuthenticationProvider<AuthenticationRequestUsernamePassword>() {

            @Override
            public AuthenticationResult authenticate(AuthenticationRequestUsernamePassword request) {
                return userRepository.getUser(request.getUserName()).map(user -> {
                    if (passwordHashingService.validatePassword(request.getPassword(), user.getHash()))
                        return AuthenticationResult.success(new UserPrincipal(user.getId()));
                    else
                        return AuthenticationResult.failure(new PasswordMismatchAuthenticationFailure());
                }).orElseGet(() -> AuthenticationResult
                        .failure(new UserNameNotFoundAuthenticationFailure(request.getUserName())));
            }
        });

        authorizationDecisionManager
                .setPerformer((Set<? extends Right> rights, Optional<AuthenticationSuccess> authentication) -> {
                    if (!authentication.isPresent()) {
                        if (rights.isEmpty())
                            return AuthorizationResult.authorized();
                        else
                            return AuthorizationResult.failure(new AuthorizationFailure("No user logged in"));
                    }
                    Principal principal = authentication.get().getPrincipal();
                    if (principal instanceof ExplicitRightsPrincipal) {
                        for (Right right : rights) {
                            if (((ExplicitRightsPrincipal) principal).grantedRights.contains(right))
                                continue;
                            return AuthorizationResult.failure(new AuthorizationFailure(
                                    "right " + right + " is not granted for principal " + principal));
                        }
                    } else if (principal instanceof UserPrincipal) {
                        UserPrincipal userPrincipal = (UserPrincipal) principal;
                        Optional<User> user = userRepository.getUser(userPrincipal.getUserId());
                        if (!user.isPresent()) {
                            return AuthorizationResult.failure(new AuthorizationFailure(
                                    "User with id " + userPrincipal.getUserId() + " not found"));
                        }
                        for (Right right : rights) {
                            if (user.get().getGrantedRights().contains(right))
                                continue;
                            return AuthorizationResult.failure(new AuthorizationFailure(
                                    "right " + right + " is not granted for user " + user.get().getName()));
                        }
                    } else
                        return AuthorizationResult
                                .failure(new AuthorizationFailure("Unable to handle principal " + principal));

                    return AuthorizationResult.authorized();
                });
    }
}
