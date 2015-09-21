package com.github.ruediste.rise.core.security.authorization;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.github.ruediste.salta.standard.util.MethodOverrideIndex;
import com.google.common.collect.Iterables;

/**
 * Helper class to find the implementation of a given method on a class.
 *
 */
public class MethodImplementationFinder {

    private MethodImplementationFinder() {
    }

    private static class MethodImplementation {
        Method method;
        Set<Method> overriddenMethods = new HashSet<>();

        public MethodImplementation(Method method,
                Set<Method> overriddenMethods) {
            super();
            this.method = method;
            this.overriddenMethods = overriddenMethods;
        }

    }

    /**
     * Find the implementation which would get invoked if the given method is
     * invoked on an instance of the given class
     */
    public static Method findImplementation(Class<?> cls, Method m) {
        return getImpl(cls, m).method;
    }

    private static MethodImplementation getImpl(Class<?> cls, Method m) {
        if (cls == null)
            return null;

        Method declaredMethod = getDeclaredMethod(cls, m);
        if (!cls.isInterface()) {
            if (declaredMethod != null)
                return new MethodImplementation(declaredMethod,
                        new HashSet<>());
            MethodImplementation result = getImpl(cls.getSuperclass(), m);
            if (result != null)
                return result;
        }

        Map<Method, Set<Method>> methods = new HashMap<>();
        for (Class<?> itf : cls.getInterfaces()) {
            MethodImplementation impl = getImpl(itf, m);
            if (impl != null)
                methods.put(impl.method, impl.overriddenMethods);
        }

        for (Entry<Method, Set<Method>> entry : new ArrayList<>(
                methods.entrySet())) {
            for (Method x : entry.getValue()) {
                methods.remove(x);
            }
        }
        if (methods.size() == 0) {
            if (declaredMethod != null)
                return new MethodImplementation(declaredMethod,
                        new HashSet<>());
            else
                return null;
        }
        if (methods.size() == 1) {
            Entry<Method, Set<Method>> entry = Iterables
                    .getOnlyElement(methods.entrySet());
            if (declaredMethod != null) {
                entry.getValue().add(entry.getKey());
                return new MethodImplementation(declaredMethod,
                        entry.getValue());
            } else
                return new MethodImplementation(entry.getKey(),
                        entry.getValue());
        } else
            return null;
    }

    private static Method getDeclaredMethod(Class<?> cls, Method method) {
        if (cls.equals(method.getDeclaringClass()))
            return method;
        Class<?>[] parameterTypes = method.getParameterTypes();
        String name = method.getName();
        for (Method m : cls.getDeclaredMethods()) {
            if (m.getName().equals(name)
                    && Arrays.equals(m.getParameterTypes(), parameterTypes)
                    && MethodOverrideIndex.doesOverride(m, method)) {

                return m;
            }
        }
        return null;
    }
}
