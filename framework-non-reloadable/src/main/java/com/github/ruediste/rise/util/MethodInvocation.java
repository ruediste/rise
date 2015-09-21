package com.github.ruediste.rise.util;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

import com.google.common.base.MoreObjects;

/**
 * An invocation of an method
 */
public class MethodInvocation<T> {
    /**
     * When comparing two {@link MethodInvocation}s with different argument
     * representations using
     * {@link MethodInvocation#isCallToSameMethod(MethodInvocation, ParameterValueEquality)}
     * , providing an implementation of this interface allows to specify a
     * strategy to compare the arguments.
     */
    public interface ParameterValueEquality<A, B> {
        public boolean equals(A a, B b);
    }

    private final List<T> arguments = new ArrayList<>();
    final private Class<?> instanceClass;
    final private Method method;

    public MethodInvocation(Class<?> instanceClass, Method method) {
        this.instanceClass = instanceClass;
        this.method = method;

    }

    /**
     * Create a copy of this invocation, not including the argument list
     */
    public MethodInvocation(MethodInvocation<?> invocation) {
        instanceClass = invocation.instanceClass;
        method = invocation.method;
    }

    public List<T> getArguments() {
        return arguments;
    }

    /**
     * Determine if the same method is called with the same arguments (compared
     * via {@link Object#equals(Object)}
     */
    public boolean isCallToSameMethod(MethodInvocation<T> other) {
        return isCallToSameMethod(other, Objects::equals);
    }

    public <O> boolean isCallToSameMethod(MethodInvocation<O> other,
            MethodInvocation.ParameterValueEquality<? super T, ? super O> equality) {
        if (!Objects.equals(method, other.method)) {
            return false;
        }
        if (arguments.size() != other.getArguments().size()) {
            return false;
        }

        Iterator<T> it = arguments.iterator();
        Iterator<O> oit = other.getArguments().iterator();
        while (it.hasNext() && oit.hasNext()) {
            if (!equality.equals(it.next(), oit.next())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("method", method)
                .add("arguments", arguments).toString();
    }

    public Method getMethod() {
        return method;
    }

    public Class<?> getInstanceClass() {
        return instanceClass;
    }

    public <R> MethodInvocation<R> map(
            BiFunction<AnnotatedType, ? super T, R> func) {
        MethodInvocation<R> invocation = new MethodInvocation<>(this);
        Iterator<AnnotatedType> pit = Arrays
                .asList(getMethod().getAnnotatedParameterTypes()).iterator();
        Iterator<T> ait = getArguments().iterator();
        while (pit.hasNext() && ait.hasNext()) {
            invocation.getArguments().add(func.apply(pit.next(), ait.next()));
        }
        return invocation;
    }
}
