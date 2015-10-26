package com.github.ruediste.rise.core.security.authorization;

import static java.util.stream.Collectors.toSet;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.ruediste.attachedProperties4J.AttachedProperty;
import com.github.ruediste.attachedProperties4J.AttachedPropertyBearer;
import com.github.ruediste.rise.core.CoreRestartableModule;
import com.github.ruediste.rise.core.aop.AopUtil;
import com.github.ruediste.salta.jsr330.binder.Binder;
import com.google.common.annotations.VisibleForTesting;

import net.sf.cglib.proxy.Enhancer;

/**
 * Manages rules determining the rights are reqired to call a method.
 */
@Singleton
public class MethodAuthorizationManager {

    @Inject
    AuthorizationManager authorizationManager;

    @Inject
    AuthzHelper authzHelper;

    interface MethodAuthorizationRule {
        Set<? extends Right> getRequiredRights(Object target, Method method,
                Object[] args);
    }

    @VisibleForTesting
    static class RuleEntry {
        MethodAuthorizationRule rule;
        Predicate<Class<?>> typeMatcher;
        BiPredicate<Class<?>, Method> methodMatcher;

    }

    @VisibleForTesting
    List<RuleEntry> entries = new ArrayList<>();
    private boolean defaultRuleDisabled;

    public MethodAuthorizationManager() {
        addDefaultRule();
    }

    private void addDefaultRule() {
        addRule(t -> !defaultRuleDisabled, (t, m) -> {
            for (Annotation annotation : m.getDeclaredAnnotations()) {
                if (annotation.annotationType()
                        .isAnnotationPresent(MetaRequiresRight.class))
                    return true;
            }
            return false;
        } , new MethodAuthorizationRule() {

            @Override
            public Set<? extends Right> getRequiredRights(Object target,
                    Method method, Object[] args) {
                Set<Right> requiredRights = new HashSet<>();
                for (Annotation annotation : method.getAnnotations()) {
                    if (annotation.annotationType()
                            .isAnnotationPresent(MetaRequiresRight.class)) {
                        try {
                            extractRights(requiredRights, annotation, target,
                                    method);
                        } catch (Exception e) {
                            throw new RuntimeException(
                                    "error while reading value of annotation "
                                            + annotation + " on method "
                                            + method);
                        }
                    }
                }
                return requiredRights;
            }

            private void extractRights(Set<Right> requiredRights,
                    Annotation annotation, Object target, Method method)
                            throws NoSuchMethodException,
                            IllegalAccessException, InvocationTargetException {
                Method valueMethod = annotation.annotationType()
                        .getMethod("value");
                Object value = valueMethod.invoke(annotation);
                if (valueMethod.getReturnType().isArray()) {
                    if (Annotation.class.isAssignableFrom(
                            valueMethod.getReturnType().getComponentType())) {
                        for (int i = 0; i < Array.getLength(value); i++) {
                            extractRights(requiredRights,
                                    (Annotation) Array.get(value, i), target,
                                    method);
                        }

                    } else
                        for (int i = 0; i < Array.getLength(value); i++) {
                            Object right = Array.get(value, i);
                            requiredRights.add(new RequiresRightAnnotationRight(
                                    right, method, annotation));
                        }
                } else
                    requiredRights.add(new RequiresRightAnnotationRight(value,
                            method, annotation));
            }
        });
    }

    private static final AttachedProperty<AttachedPropertyBearer, MethodAuthorizationManager> managerProperty = new AttachedProperty<>(
            MethodAuthorizationManager.class.getSimpleName());

    /**
     * Disable the default rule which checks for {@link MetaRequiresRight}
     * -Annotations
     */
    public static void disableDefaultRule(Binder binder) {
        MethodAuthorizationManager mgr = managerProperty.get(binder.config());
        mgr.defaultRuleDisabled = true;
    }

    /**
     * Add a {@link MethodAuthorizationRule} for the given binder
     */
    public static void addRule(Binder binder, Predicate<Class<?>> typeMatcher,
            BiPredicate<Class<?>, Method> methodMatcher,
            MethodAuthorizationRule rule) {
        MethodAuthorizationManager mgr = managerProperty.get(binder.config());
        mgr.addRule(typeMatcher, methodMatcher, rule);
    }

    public void addRule(Predicate<Class<?>> typeMatcher,
            BiPredicate<Class<?>, Method> methodMatcher,
            MethodAuthorizationRule rule) {
        RuleEntry entry = new RuleEntry();
        entry.methodMatcher = methodMatcher;
        entry.typeMatcher = typeMatcher;
        entry.rule = rule;
        entries.add(entry);
    }

    /**
     * Called from the {@link CoreRestartableModule}
     */
    public void register(Binder binder) {
        managerProperty.set(binder.config(), this);
        AopUtil.registerSubclass(binder.config().standardConfig, t -> {
            Class<?> cls = t.getRawType();
            return entries.stream().anyMatch(e -> e.typeMatcher.test(cls));
        } , (t, m) -> entries.stream().anyMatch(e -> {
            Class<?> cls = t.getRawType();
            return e.methodMatcher.test(cls, m);
        }), i -> {
            Object target = i.getTarget();
            if (target != null) {
                Class<? extends Object> targetClass = target.getClass();
                Method method = i.getMethod();
                Object[] arguments = i.getArguments();
                Set<? extends Right> requiredRights = entries.stream()
                        .filter(e -> {
                    return e.typeMatcher.test(targetClass)
                            && e.methodMatcher.test(targetClass, method);
                }).flatMap(r -> r.rule
                        .getRequiredRights(target, method, arguments).stream())
                        .collect(toSet());
                authorizationManager.checkAuthorization(requiredRights);
            }
            return i.proceed();
        });

        binder.bind(MethodAuthorizationManager.class).toInstance(this);
    }

    public void checkAuthorized(Object target, Method m, Object[] args) {
        Class<? extends Object> cls = target.getClass();
        if (Enhancer.isEnhanced(cls))
            cls = cls.getSuperclass();

        if (AuthorizationInspector.callsDoAuthChecks(cls, m)) {
            // invoke the method, which will cause the authorization advices to
            // be run, followed
            // the the authorization of the method
            authzHelper.withIsAuthorizing(true, () -> {
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
            Class<? extends Object> clsFinal = cls;
            Set<? extends Right> requiredRights = entries.stream()
                    .filter(e -> e.typeMatcher.test(clsFinal)
                            && e.methodMatcher.test(clsFinal, m))
                    .flatMap(e -> e.rule.getRequiredRights(target, m, args)
                            .stream())
                    .collect(toSet());
            authorizationManager.checkAuthorization(requiredRights);
        }
    }
}
