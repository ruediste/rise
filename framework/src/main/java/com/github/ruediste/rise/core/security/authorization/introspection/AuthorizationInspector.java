package com.github.ruediste.rise.core.security.authorization.introspection;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.github.ruediste.c3java.invocationRecording.MethodInvocation;
import com.github.ruediste.c3java.invocationRecording.MethodInvocationRecorder;
import com.github.ruediste.rise.core.security.authorization.AuthorizationException;
import com.github.ruediste.rise.util.Pair;
import com.github.ruediste.salta.standard.util.MethodOverrideIndex;
import com.google.common.collect.MapMaker;

/**
 * Determine if the current principal is allowed to execute a method without
 * executing it
 */
public class AuthorizationInspector {
    private static ThreadLocal<Boolean> isAuthorizing = new ThreadLocal<>();

    private static void withIsAuthorizing(boolean value, Runnable run) {
        withIsAuthorizing(value, () -> {
            run.run();
            return null;
        });
    }

    private static <T> T withIsAuthorizing(boolean value, Supplier<T> run) {
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

    static public boolean isAuthorized(Object target, Method m, Object[] args) {
        try {
            checkAuthorized(target, m, args);
        } catch (AuthorizationException e) {
            return false;
        }
        return true;
    }

    static public void checkAuthorized(Object target, Method m, Object[] args) {
        if (callsAuthorize(target.getClass(), m)) {
            checkAuthorized(() -> {
                try {
                    m.setAccessible(true);
                    m.invoke(target, args);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private static class AuthorizeCallsVisitor extends ClassVisitor {

        Set<Pair<String, String>> authorizeCallingMethods = new HashSet<>();

        private static String OWNER = Type
                .getInternalName(AuthorizationInspector.class);
        private static String NAME = "authorize";

        public AuthorizeCallsVisitor() {
            super(Opcodes.ASM5);
        }

        @Override
        public MethodVisitor visitMethod(int access, String methodName,
                String methodDesc, String signature, String[] exceptions) {
            return new MethodVisitor(Opcodes.ASM5) {
                @Override
                public void visitMethodInsn(int opcode, String owner,
                        String name, String desc, boolean itf) {
                    if (OWNER.equals(owner) && NAME.equals(name)) {
                        authorizeCallingMethods.add(Pair.of(methodName,
                                methodDesc));
                    }
                }
            };
        }

    }

    static boolean callsAuthorize(Class<?> clazz, Method method) {
        Class<?> c = clazz;
        while (c != null) {
            for (Method m : c.getDeclaredMethods()) {
                if (m.getName().equals(method.getName())
                        && Arrays.equals(m.getParameterTypes(),
                                method.getParameterTypes())) {
                    if (c.equals(clazz)
                            || MethodOverrideIndex.doesOverride(method, m)) {
                        return getAuthorizeCallingMethods(c).contains(
                                Pair.of(method.getName(),
                                        Type.getMethodDescriptor(method)));
                    }
                }
            }
            c = c.getSuperclass();
        }
        throw new RuntimeException("No declaration of " + method + " found on "
                + clazz + " or ancestors thereof");
    }

    private static ConcurrentMap<Class<?>, Set<Pair<String, String>>> cache = new MapMaker()
            .weakKeys().makeMap();

    private static Set<Pair<String, String>> getAuthorizeCallingMethods(
            Class<?> clazz) {
        return cache
                .computeIfAbsent(
                        clazz,
                        x -> {
                            AuthorizeCallsVisitor cv;
                            try (InputStream is = clazz.getClassLoader()
                                    .getResourceAsStream(
                                            Type.getInternalName(clazz)
                                                    + ".class")) {
                                ClassReader cr = new ClassReader(is);
                                cv = new AuthorizeCallsVisitor();
                                cr.accept(cv, ClassReader.SKIP_DEBUG
                                        + ClassReader.SKIP_FRAMES);
                            } catch (IOException e) {
                                throw new RuntimeException(
                                        "error while reading class "
                                                + clazz.getName());
                            }
                            Set<Pair<String, String>> authorizeCallingMethods = cv.authorizeCallingMethods;
                            return authorizeCallingMethods;
                        });
    }

    static public boolean isAuthorized(Runnable run) {
        try {
            checkAuthorized(run);
        } catch (AuthorizationException e) {
            return false;
        }
        return true;
    }

    static public void checkAuthorized(Runnable run) {
        withIsAuthorizing(true, () -> {
            try {
                run.run();
                throw new RuntimeException(
                        "Authorization check did not invoke authorize");
            } catch (AuthorizationIntrospectionCompleted e) {
                // swallow
            }
        });

    }

    static public void authorize(Runnable check) {
        if (Objects.equals(true, isAuthorizing.get())) {
            withIsAuthorizing(false, check);
            throw new AuthorizationIntrospectionCompleted();
        } else
            check.run();
    }
}
