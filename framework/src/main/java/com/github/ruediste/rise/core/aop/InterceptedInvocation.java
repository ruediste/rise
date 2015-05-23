package com.github.ruediste.rise.core.aop;

import java.lang.reflect.Method;

import com.github.ruediste.attachedProperties4J.AttachedPropertyBearer;

/**
 * Represents a method invocation intercepted by an {@link AroundAdvice}
 */
public interface InterceptedInvocation {

    /**
     * Object the intercepted method was invoked on
     */
    Object getTarget();

    /**
     * the intercepted method
     */
    Method getMethod();

    /**
     * arguments of the intercepted invocation
     */
    Object[] getArguments();

    /**
     * invoke the original method with the original arguments
     */
    Object proceed() throws Throwable;

    /**
     * invoke the original method with modified arguments
     */
    Object proceed(Object... args) throws Throwable;

    /**
     * Return an {@link AttachedPropertyBearer} for the intercepted instance.
     * Only available for
     * {@link AopUtil#registerSubclass(com.github.ruediste.salta.standard.config.StandardInjectorConfiguration, java.util.function.Predicate, java.util.function.BiPredicate, AroundAdvice)
     * subclass advices}
     */
    AttachedPropertyBearer getPropertyBearer();
}
