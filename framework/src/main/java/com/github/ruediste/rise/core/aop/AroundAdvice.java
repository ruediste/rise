package com.github.ruediste.rise.core.aop;

/**
 * Interface for around advices
 *
 * @see AopUtil
 */
public interface AroundAdvice {

    Object intercept(InterceptedInvocation invocation) throws Throwable;
}
