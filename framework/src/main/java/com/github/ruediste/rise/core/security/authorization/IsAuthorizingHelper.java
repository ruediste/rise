package com.github.ruediste.rise.core.security.authorization;

import java.util.Objects;
import java.util.function.Supplier;

import javax.inject.Singleton;

/**
 * Helper used by {@link Authz} to perform method authorization introspection.
 */
@Singleton
public class IsAuthorizingHelper {

    public ThreadLocal<Boolean> isAuthorizing = new ThreadLocal<>();

    public <T> T withIsAuthorizing(boolean value, Supplier<T> run) {
        Boolean old = isAuthorizing.get();
        try {
            isAuthorizing.set(value);
            return run.get();
        } finally {
            if (old == null)
                isAuthorizing.remove();
            else
                isAuthorizing.set(old);
        }
    }

    public void withIsAuthorizing(boolean value, Runnable run) {
        withIsAuthorizing(value, () -> {
            run.run();
            return null;
        });
    }

    public boolean isAuthorizing() {
        return Objects.equals(true, isAuthorizing.get());
    }
}
