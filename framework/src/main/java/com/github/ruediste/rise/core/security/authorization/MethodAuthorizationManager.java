package com.github.ruediste.rise.core.security.authorization;

import static java.util.stream.Collectors.toSet;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.inject.Inject;

import com.github.ruediste.attachedProperties4J.AttachedProperty;
import com.github.ruediste.attachedProperties4J.AttachedPropertyBearer;
import com.github.ruediste.rise.core.aop.AopUtil;
import com.github.ruediste.salta.jsr330.binder.Binder;
import com.google.common.annotations.VisibleForTesting;

import net.sf.cglib.proxy.Enhancer;

/**
 * Manages rules determining the rights required to call a method.
 * 
 * <p>
 * The manager works through subclass AOP, therefore the {@link Right}
 * extraction rules have to be initialized during the Salta module
 * initialization.
 */
public class MethodAuthorizationManager {

    @Inject
    AuthorizationDecisionManager authorizationManager;

    @Inject
    IsAuthorizingHelper authzHelper;

    interface MethodAuthorizationRule {
        /**
         * Return the rights required to call the given method
         */
        Set<? extends Right> getRequiredRights(Object target, Method method,
                Object[] args);
    }

    @VisibleForTesting
    static class RuleEntry {
        MethodAuthorizationRule rule;
        /**
         * can be null
         */
        Predicate<Class<?>> typeMatcher;
        /**
         * can be null
         */
        BiPredicate<Class<?>, Method> methodMatcher;

    }

    @VisibleForTesting
    List<RuleEntry> entries = new ArrayList<>();

    public MethodAuthorizationManager() {
    }

    private static final AttachedProperty<AttachedPropertyBearer, MethodAuthorizationManager> managerProperty = new AttachedProperty<>(
            MethodAuthorizationManager.class.getSimpleName());

    /**
     * Add a {@link MethodAuthorizationRule} for the given binder
     */
    public static void addRule(Binder binder, MethodAuthorizationRule rule,
            Predicate<Class<?>> typeMatcher,
            BiPredicate<Class<?>, Method> methodMatcher) {
        get(binder).addRule(typeMatcher, methodMatcher, rule);
    }

    /**
     * Retrieve the manager instance associated with the given binder. If no
     * manager is associated yet, create a new one and register it.
     */
    public static MethodAuthorizationManager get(Binder binder) {
        MethodAuthorizationManager result = managerProperty
                .get(binder.config());
        if (result == null) {
            result = new MethodAuthorizationManager();
            result.register(binder);
        }
        return result;
    }

    /**
     * Add a rule to extract rights from a certain annotation
     * 
     * @param extractor
     *            extractor of rights from a single occurrence of the
     *            annotation. If the annotation is repeated, the extractor is
     *            invoked multiple times. The return value may be null
     */
    public static <T extends Annotation> void addRule(Binder binder,
            Class<T> annotationClass,
            Function<T, Collection<Right>> extractor) {
        get(binder).addRule(annotationClass, extractor);
    }

    /**
     * Add a rule to extract rights from a certain annotation
     * 
     * @param extractor
     *            extractor of rights from a single occurrence of the
     *            annotation. If the annotation is repeated, the extractor is
     *            invoked multiple times. The return value may be null
     * @return
     */
    public <T extends Annotation> MethodAuthorizationManager addRule(
            Class<T> annotationClass,
            Function<T, Collection<Right>> extractor) {
        addRule(t -> true,
                (t, m) -> m.getDeclaredAnnotationsByType(
                        annotationClass).length > 0,
                new MethodAuthorizationRule() {

                    @Override
                    public Set<? extends Right> getRequiredRights(Object target,
                            Method method, Object[] args) {
                        HashSet<Right> result = new HashSet<>();
                        for (T annotation : method.getDeclaredAnnotationsByType(
                                annotationClass)) {
                            Collection<Right> tmp = extractor.apply(annotation);
                            if (tmp != null)
                                result.addAll(tmp);
                        }
                        return result;
                    }
                });
        return this;
    }

    public MethodAuthorizationManager addRule(Predicate<Class<?>> typeMatcher,
            BiPredicate<Class<?>, Method> methodMatcher,
            MethodAuthorizationRule rule) {
        RuleEntry entry = new RuleEntry();
        entry.methodMatcher = methodMatcher;
        entry.typeMatcher = typeMatcher;
        entry.rule = rule;
        entries.add(entry);
        return this;
    }

    private MethodAuthorizationManager register(Binder binder) {
        managerProperty.set(binder.config(), this);
        AopUtil.registerSubclass(binder.config().standardConfig, t -> {
            Class<?> cls = t.getRawType();
            return entries.stream().anyMatch(
                    e -> e.typeMatcher == null || e.typeMatcher.test(cls));
        } , (t, m) -> entries.stream().anyMatch(e -> {
            Class<?> cls = t.getRawType();
            return e.methodMatcher == null || e.methodMatcher.test(cls, m);
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
        return this;
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
