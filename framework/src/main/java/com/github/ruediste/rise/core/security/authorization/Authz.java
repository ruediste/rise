package com.github.ruediste.rise.core.security.authorization;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Consumer;

import com.github.ruediste.c3java.invocationRecording.MethodInvocation;
import com.github.ruediste.c3java.invocationRecording.MethodInvocationRecorder;
import com.github.ruediste.rise.nonReloadable.InjectorsHolder;

/**
 * Static methods for authorization.
 */
public class Authz {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    static public <T> boolean isAuthorized(T target, Consumer<T> invoker) {
        return isAuthorized(
                target,
                MethodInvocationRecorder.getLastInvocation(
                        (Class) target.getClass(), invoker));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    static public <T> void checkAuthorized(T target, Consumer<T> invoker) {
        checkAuthorized(
                target,
                MethodInvocationRecorder.getLastInvocation(
                        (Class) target.getClass(), invoker));
    }

    public static boolean isAuthorized(Object target,
            MethodInvocation<Object> lastInvocation) {
        return isAuthorized(target, lastInvocation.getMethod(), lastInvocation
                .getArguments().toArray());
    }

    public static void checkAuthorized(Object target,
            MethodInvocation<Object> lastInvocation) {
        checkAuthorized(target, lastInvocation.getMethod(), lastInvocation
                .getArguments().toArray());
    }

    static public boolean isAuthorized(Object target, Method m, List<?> args) {
        return isAuthorized(target, m, args.toArray());
    }

    static public boolean isAuthorized(Object target, Method m, Object[] args) {
        try {
            checkAuthorized(target, m, args);
        } catch (AuthorizationException e) {
            return false;
        }
        return true;
    }

    static public void checkAuthorized(Object target, Method m, Object[] args) {
        InjectorsHolder.getRestartableInjector()
                .getInstance(AuthorizationManager.class)
                .checkAuthorized(target, m, args);
    }

    /**
     * Perform the authorization checks contained in the check runnable.
     * <p>
     * Wrapping the checks in this method call allows to separate the
     * authorization logic from the execution logic, and thus do determine if a
     * method can be executed without actually executing it.
     */
    static public void doAuthChecks(Runnable check) {
        if (Authz.isAuthorizing()) {
            AuthzHelper.withIsAuthorizing(false, check);
            throw new AuthorizationIntrospectionCompleted();
        } else
            check.run();
    }

    public static boolean isAuthorizing() {
        return AuthzHelper.isAuthorizing();
    }
}
