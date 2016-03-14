package com.github.ruediste.rise.core.security.authorization;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.github.ruediste.rise.util.Pair;
import com.google.common.collect.MapMaker;

/**
 * Checks if a method calls {@link Authz#doAuthChecks(Runnable)}
 */
public class AuthorizationInspector {
    private static class AuthorizeCallsVisitor extends ClassVisitor {

        Set<Pair<String, String>> authorizeCallingMethods = new HashSet<>();

        private static String OWNER = Type.getInternalName(Authz.class);
        private static String NAME = "doAuthChecks";

        public AuthorizeCallsVisitor() {
            super(Opcodes.ASM5);
        }

        @Override
        public MethodVisitor visitMethod(int access, String methodName, String methodDesc, String signature,
                String[] exceptions) {
            return new MethodVisitor(Opcodes.ASM5) {
                @Override
                public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                    if (OWNER.equals(owner) && NAME.equals(name)) {
                        authorizeCallingMethods.add(Pair.of(methodName, methodDesc));
                    }
                }
            };
        }

    }

    public static boolean callsDoAuthChecks(Class<?> clazz, Method method) {
        Method impl = MethodImplementationFinder.findImplementation(clazz, method);
        if (impl == null)
            throw new RuntimeException(
                    "No implementation of " + method + " found on " + clazz + " or ancestors thereof");

        return getAuthorizeCallingMethods(impl.getDeclaringClass())
                .contains(Pair.of(method.getName(), Type.getMethodDescriptor(method)));
    }

    private static ConcurrentMap<Class<?>, Set<Pair<String, String>>> cache = new MapMaker().weakKeys().makeMap();

    private static Set<Pair<String, String>> getAuthorizeCallingMethods(Class<?> clazz) {
        return cache.computeIfAbsent(clazz, x -> {
            AuthorizeCallsVisitor cv;
            try (InputStream is = clazz.getClassLoader().getResourceAsStream(Type.getInternalName(clazz) + ".class")) {
                ClassReader cr = new ClassReader(is);
                cv = new AuthorizeCallsVisitor();
                cr.accept(cv, ClassReader.SKIP_DEBUG + ClassReader.SKIP_FRAMES);
            } catch (IOException e) {
                throw new RuntimeException("error while reading class " + clazz.getName());
            }
            Set<Pair<String, String>> authorizeCallingMethods = cv.authorizeCallingMethods;
            return authorizeCallingMethods;
        });
    }
}
