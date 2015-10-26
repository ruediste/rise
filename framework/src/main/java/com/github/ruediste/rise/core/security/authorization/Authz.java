package com.github.ruediste.rise.core.security.authorization;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Consumer;

import javax.inject.Inject;

import com.github.ruediste.c3java.invocationRecording.MethodInvocation;
import com.github.ruediste.c3java.invocationRecording.MethodInvocationRecorder;
import com.github.ruediste.rise.nonReloadable.InjectorsHolder;

/**
 * Facade for the authorization and authorization introspection subsystem.
 */
public class Authz {
    @Inject
    AuthorizationManager authorizationManager;

    @Inject
    public MethodAuthorizationManager methodAuthorizationManager;

    @Inject
    AuthzHelper helper;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T> AuthorizationResult performAuthorization(T target,
            Consumer<T> invoker) {
        return performAuthorization(target, MethodInvocationRecorder
                .getLastInvocation((Class) target.getClass(), invoker));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T> boolean isAuthorized(T target, Consumer<T> invoker) {
        return isAuthorized(target, MethodInvocationRecorder
                .getLastInvocation((Class) target.getClass(), invoker));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T> void checkAuthorized(T target, Consumer<T> invoker) {
        checkAuthorized(target, MethodInvocationRecorder
                .getLastInvocation((Class) target.getClass(), invoker));
    }

    public boolean isAuthorized(Object target,
            MethodInvocation<Object> lastInvocation) {
        return isAuthorized(target, lastInvocation.getMethod(),
                lastInvocation.getArguments().toArray());
    }

    public void checkAuthorized(Object target,
            MethodInvocation<Object> lastInvocation) {
        checkAuthorized(target, lastInvocation.getMethod(),
                lastInvocation.getArguments().toArray());
    }

    public AuthorizationResult performAuthorization(Object target,
            MethodInvocation<Object> lastInvocation) {
        return performAuthorization(target, lastInvocation.getMethod(),
                lastInvocation.getArguments().toArray());
    }

    public boolean isAuthorized(Object target, Method m, List<?> args) {
        return isAuthorized(target, m, args.toArray());
    }

    public AuthorizationResult performAuthorization(Object target, Method m,
            Object[] args) {
        try {
            checkAuthorized(target, m, args);
        } catch (AuthorizationException e) {
            return e.toAuthorizationResult();
        }
        return AuthorizationResult.authorized();
    }

    public boolean isAuthorized(Object target, Method m, Object[] args) {
        try {
            checkAuthorized(target, m, args);
        } catch (AuthorizationException e) {
            return false;
        }
        return true;
    }

    public void checkAuthorized(Object target, Method m, Object[] args) {
        InjectorsHolder.getRestartableInjector()
                .getInstance(MethodAuthorizationManager.class)
                .checkAuthorized(target, m, args);
    }

    /**
     * Perform the authorization checks contained in the check runnable.
     * <p>
     * Wrapping the checks in this method call allows to separate the
     * authorization logic from the execution logic, and thus do determine if a
     * method can be executed without actually executing it.
     */
    public void doAuthChecks(Runnable check) {
        if (helper.isAuthorizing()) {
            helper.withIsAuthorizing(false, check);
            throw new AuthorizationIntrospectionCompleted();
        } else
            check.run();
    }

    /**
     * Determine if the caller is currently performing an authorization check,
     * or really executing.
     */
    public boolean isAuthorizing() {
        return helper.isAuthorizing();
    }
}
