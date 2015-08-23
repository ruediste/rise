package com.github.ruediste.rise.core.security.authentication;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

import javax.inject.Singleton;

import com.github.ruediste.rise.util.GenericEvent;
import com.github.ruediste.rise.util.Pair;
import com.google.common.reflect.TypeToken;

@Singleton
public class DefaultAuthenticationManager implements AuthenticationManager {

    private final Deque<Pair<Class<?>, AuthenticationProvider<?>>> providers = new ArrayDeque<>();
    private GenericEvent<AuthenticationRequest> preAuthenticationEvent = new GenericEvent<>();
    private GenericEvent<Pair<AuthenticationRequest, AuthenticationResult>> postAuthenticationEvent = new GenericEvent<>();

    /**
     * Authenticate an authentication request. Throws an exception if the
     * authentication is not successful
     * 
     * @return
     */
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public AuthenticationResult authenticate(AuthenticationRequest request) {
        preAuthenticationEvent.fire(request);
        AuthenticationResult result = null;
        ArrayList<AuthenticationFailure> failures = new ArrayList<>();
        for (Pair<Class<?>, AuthenticationProvider<?>> pair : getProviders()) {
            if (pair.getA().isAssignableFrom(request.getClass())) {
                result = ((AuthenticationProvider) pair.getB())
                        .tryAuthenticate(request);
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
    public void addProvider(
            AuthenticationProvider<? extends AuthenticationRequest> provider) {
        Class type = TypeToken
                .of(provider.getClass())
                .resolveType(
                        AuthenticationProvider.class.getTypeParameters()[0])
                .getRawType();
        addProvider(type, provider);
    }

    public <T extends AuthenticationRequest> void addProvider(
            Class<T> requestClass, AuthenticationProvider<? super T> provider) {
        providers.add(Pair.of(requestClass, provider));
    }

    @Override
    public GenericEvent<AuthenticationRequest> preAuthenticationEvent() {
        return preAuthenticationEvent;
    }

    @Override
    public GenericEvent<Pair<AuthenticationRequest, AuthenticationResult>> postAuthenticationEvent() {
        return postAuthenticationEvent;
    }
}
