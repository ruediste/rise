package com.github.ruediste.rise.core.security.authentication.core;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

import com.github.ruediste.rise.core.security.authentication.AuthenticationProviderKnownPrincipal;
import com.github.ruediste.rise.util.GenericEvent;
import com.github.ruediste.rise.util.GenericEventManager;
import com.github.ruediste.rise.util.Pair;
import com.google.common.reflect.TypeToken;

/**
 * {@link AuthenticationManager} implementation using
 * {@link AuthenticationProvider}s to perform the actual authentications.
 * 
 * <p>
 * <img src="doc-files/authenticationCoreOverview.png" alt="">
 */
@Singleton
public class DefaultAuthenticationManager implements AuthenticationManager {

    private final Deque<Pair<Class<?>, AuthenticationProvider<?>>> providers = new ArrayDeque<>();
    private GenericEventManager<AuthenticationRequest> preAuthenticationEvent = new GenericEventManager<>();
    private GenericEventManager<Pair<AuthenticationRequest, AuthenticationResult>> postAuthenticationEvent = new GenericEventManager<>();

    @PostConstruct
    public void postConstruct(AuthenticationProviderKnownPrincipal provider) {
        addProvider(provider);
    }

    /**
     * Authenticate an authentication request. Throws an exception if the
     * authentication is not successful
     */
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public AuthenticationResult authenticate(AuthenticationRequest request) {
        preAuthenticationEvent.fire(request);
        AuthenticationResult result = null;
        ArrayList<AuthenticationFailure> failures = new ArrayList<>();
        for (Pair<Class<?>, AuthenticationProvider<?>> pair : getProviders()) {
            if (pair.getA().isAssignableFrom(request.getClass())) {
                result = ((AuthenticationProvider) pair.getB()).authenticate(request);
                if (result.isSuccess())
                    break;
                else
                    failures.addAll(result.getFailures());
            }
        }

        if (result == null)
            result = AuthenticationResult.failure(failures);

        postAuthenticationEvent.fire(Pair.of(request, result));
        return result;
    }

    public Deque<Pair<Class<?>, AuthenticationProvider<?>>> getProviders() {
        return providers;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addProvider(AuthenticationProvider<? extends AuthenticationRequest> provider) {
        Class type = TypeToken.of(provider.getClass()).resolveType(AuthenticationProvider.class.getTypeParameters()[0])
                .getRawType();
        addProvider(type, (AuthenticationProvider) provider);
    }

    public <T extends AuthenticationRequest> void addProvider(Class<T> requestClass,
            AuthenticationProvider<? super T> provider) {
        providers.add(Pair.of(requestClass, provider));
        provider.initialize(this);
    }

    @Override
    public GenericEvent<AuthenticationRequest> preAuthenticationEvent() {
        return preAuthenticationEvent.event();
    }

    @Override
    public GenericEvent<Pair<AuthenticationRequest, AuthenticationResult>> postAuthenticationEvent() {
        return postAuthenticationEvent.event();
    }

}
