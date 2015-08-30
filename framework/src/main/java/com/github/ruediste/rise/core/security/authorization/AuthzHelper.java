package com.github.ruediste.rise.core.security.authorization;

import java.util.Objects;
import java.util.function.Supplier;

public class AuthzHelper {

    public static ThreadLocal<Boolean> isAuthorizing = new ThreadLocal<>();

    private AuthzHelper() {
    }

    public static <T> T withIsAuthorizing(boolean value, Supplier<T> run) {
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

    public static void withIsAuthorizing(boolean value, Runnable run) {
        withIsAuthorizing(value, () -> {
            run.run();
            return null;
        });
    }

    public static boolean isAuthorizing() {
        return Objects.equals(true, AuthzHelper.isAuthorizing.get());
    }
}
