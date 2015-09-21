package com.github.ruediste.rise.core.security.authorization;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

import net.sf.cglib.proxy.Enhancer;

import com.github.ruediste.rise.core.aop.AopUtil;
import com.github.ruediste.salta.jsr330.JSR330InjectorConfiguration;
import com.github.ruediste.salta.standard.config.StandardInjectorConfiguration;

/**
 * Manages all authorization rules
 */
public class AuthorizationManager {

    private Consumer<List<Object>> rightChecker;

    interface AuthorizationRule {
        void checkAuthorized(Object target, Method method, Object[] args);
    }

    private static class RuleEntry {
        AuthorizationRule rule;
        Predicate<Class<?>> typeMatcher;
        BiPredicate<Class<?>, Method> methodMatcher;
    }

    private List<RuleEntry> entries = new ArrayList<>();

    public AuthorizationManager() {
        addRule(t -> true, (t, m) -> {
            for (Annotation annotation : m.getDeclaredAnnotations()) {
                if (annotation.annotationType()
                        .isAnnotationPresent(MetaRequiresRight.class))
                    return true;
            }
            return false;
        } , new AuthorizationRule() {

            @Override
            public void checkAuthorized(Object target, Method method,
                    Object[] args) {
                ArrayList<Object> requiredRights = new ArrayList<>();
                for (Annotation annotation : method.getAnnotations()) {
                    if (annotation.annotationType()
                            .isAnnotationPresent(MetaRequiresRight.class)) {
                        try {
                            extractRights(requiredRights, annotation);
                        } catch (Exception e) {
                            throw new RuntimeException(
                                    "error while reading value of annotation "
                                            + annotation + " on method "
                                            + method);
                        }
                    }
                }
                getRightChecker().accept(requiredRights);
            }

            private void extractRights(ArrayList<Object> requiredRights,
                    Annotation annotation) throws NoSuchMethodException,
                            IllegalAccessException, InvocationTargetException {
                Method value = annotation.annotationType().getMethod("value");
                Object right = value.invoke(annotation);
                if (value.getReturnType().isArray()) {
                    if (Annotation.class.isAssignableFrom(
                            value.getReturnType().getComponentType())) {
                        for (int i = 0; i < Array.getLength(right); i++) {
                            extractRights(requiredRights,
                                    (Annotation) Array.get(right, i));
                        }

                    } else
                        for (int i = 0; i < Array.getLength(right); i++) {
                            requiredRights.add(Array.get(right, i));
                        }
                } else
                    requiredRights.add(right);
            }
        });
    }

    public void addRule(Predicate<Class<?>> typeMatcher,
            BiPredicate<Class<?>, Method> methodMatcher,
            AuthorizationRule rule) {
        RuleEntry entry = new RuleEntry();
        entry.methodMatcher = methodMatcher;
        entry.typeMatcher = typeMatcher;
        entry.rule = rule;
        entries.add(entry);
    }

    public void register(JSR330InjectorConfiguration config) {
        register(config.standardConfig);
    }

    public void register(StandardInjectorConfiguration config) {
        AopUtil.registerSubclass(config, t -> {
            Class<?> cls = t.getRawType();
            return entries.stream().anyMatch(e -> e.typeMatcher.test(cls));
        } , (t, m) -> entries.stream().anyMatch(e -> {
            Class<?> cls = t.getRawType();
            return e.methodMatcher.test(cls, m);
        }), i -> {
            Object target = i.getTarget();
            Method method = i.getMethod();
            Object[] arguments = i.getArguments();
            entries.forEach(
                    r -> r.rule.checkAuthorized(target, method, arguments));
            return i.proceed();
        });
    }

    public void checkAuthorized(Object target, Method m, Object[] args) {
        Class<? extends Object> cls = target.getClass();
        if (Enhancer.isEnhanced(cls))
            cls = cls.getSuperclass();

        if (AuthorizationInspector.callsDoAuthChecks(cls, m)) {
            // invoke the method, which will cause the authorization advices to
            // be run, followed
            // the the authorization of the method
            AuthzHelper.withIsAuthorizing(true, () -> {
                try {
                    try {
                        m.setAccessible(true);
                        m.invoke(target, args);
                    } catch (InvocationTargetException e1) {
                        Throwable cause = e1.getCause();
                        if (cause instanceof RuntimeException)
                            throw (RuntimeException) cause;
                        if (cause instanceof Error)
                            throw (Error) cause;
                        throw new RuntimeException(cause);
                    } catch (Exception e1) {
                        throw new RuntimeException(e1);
                    }
                    throw new RuntimeException(
                            "Authorization check did not invoke authorize");
                } catch (AuthorizationIntrospectionCompleted e11) {
                    // swallow
                }
            });
        } else {
            // no authorization in the method, just evaluate the authorization
            // rules
            for (RuleEntry entry : entries) {
                if (entry.typeMatcher.test(cls)
                        && entry.methodMatcher.test(cls, m))
                    entry.rule.checkAuthorized(target, m, args);
            }
        }
    }

    public Consumer<List<Object>> getRightChecker() {
        return rightChecker;
    }

    public void setRightChecker(Consumer<List<Object>> rightChecker) {
        this.rightChecker = rightChecker;
    }
}
