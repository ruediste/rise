package com.github.ruediste.rise.nonReloadable.lambda;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;

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
                    ClassVisitor cv = cw;
                    if (false)
                        cv = new CheckClassAdapter(cv);
                    if (false)
                        cv = new TraceClassVisitor(cv, new PrintWriter(System.out, true));
                    cv = new LambdaInformationWeaver(cv);
                    reader.accept(cv, 0);
                    byte[] bb = cw.toByteArray();
                    result = defineClass(name, bb, 0, bb.length);
                    resolveClass(result);
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