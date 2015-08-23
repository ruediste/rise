package com.github.ruediste.rise.core.security;

import java.util.Optional;
import java.util.function.Supplier;

import javax.inject.Singleton;

import com.github.ruediste.rise.core.security.authentication.AuthenticationSuccess;

@Singleton
public class DefaultAuthenticationHolder implements AuthenticationHolder {

    private ThreadLocal<AuthenticationSuccess> currentAuthentication = new ThreadLocal<>();

    @Override
    public AuthenticationSuccess getCurrentAuthentication() {
        AuthenticationSuccess auth = currentAuthentication.get();
        if (auth == null)
            throw new NoAuthenticationException();
        return auth;
    }

    @Override
    public void checkAutheticationPresetn() {
        getCurrentAuthentication();
    }

    @Override
    public Optional<AuthenticationSuccess> tryGetCurrentAuthentication() {
        return Optional.ofNullable(currentAuthentication.get());
    }

    @Override
    public void withAuthentication(AuthenticationSuccess authentication,
            Runnable action) {
        withAuthentication(authentication, () -> {
            action.run();
            return null;
        });
    }

    @Override
    public <T> T withAuthentication(AuthenticationSuccess authentication,
            Supplier<T> action) {
        AuthenticationSuccess old = currentAuthentication.get();
        try {
            currentAuthentication.set(authentication);
            return action.get();
        } finally {
            currentAuthentication.set(old);
        }
    }

    @Override
    public Subject getCurrentSubject() {
        return getCurrentAuthentication().getSubject();
    }

    @Override
    public Optional<Subject> tryGetCurrentSubject() {
        return tryGetCurrentAuthentication().map(x -> x.getSubject());
    }

}
