package com.github.ruediste.rise.nonReloadable.lambda;

import java.io.IOException;
import java.io.InputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import com.github.ruediste.rise.nonReloadable.lambda.expression.LambdaInformationWeaver;

class WeavingClassLoader extends ClassLoader {
    private Class<?> cls;

    public WeavingClassLoader(Class<?> cls, ClassLoader parent) {
        super(parent);
        this.cls = cls;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        if (name.startsWith(cls.getName())) {
            Class<?> result = findLoadedClass(name);
            if (result == null) {
                try (InputStream is = getResourceAsStream(name.replace('.', '/') + ".class")) {
                    ClassReader reader = new ClassReader(is);
                    ClassWriter cw = new ClassWriter(reader, 0);
                    LambdaInformationWeaver weaver = new LambdaInformationWeaver(cw);
                    reader.accept(weaver, 0);
                    byte[] bb = cw.toByteArray();
                    result = defineClass(name, bb, 0, bb.length);
                    System.out.println("Weaved " + name + " for lambda capturing");
                } catch (IOException e) {
                    throw new ClassNotFoundException("error while loading class " + name, e);
                }
            }
            return result;
        }
        return super.loadClass(name);
    }
}