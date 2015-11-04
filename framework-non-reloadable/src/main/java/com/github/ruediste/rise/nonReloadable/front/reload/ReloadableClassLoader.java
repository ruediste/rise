package com.github.ruediste.rise.nonReloadable.front.reload;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteStreams;

/**
 * Class loader loading all {@link Reloadable @Reloadable} classes by itself and
 * delegates to the parent class loader for other classes
 */
public class ReloadableClassLoader extends ClassLoader {
    public static Logger log = LoggerFactory
            .getLogger(ReloadableClassLoader.class);

    private ReloadableClassesIndex index;

    public ReloadableClassLoader(ClassLoader parent,
            ReloadableClassesIndex index) {
        super(parent);
        this.index = index;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve)
            throws ClassNotFoundException {
        {
            Class<?> result = findLoadedClass(name);
            if (result != null) {
                if (resolve)
                    resolveClass(result);
                return result;
            }
        }
        if (!name.startsWith("java.")) {
            if (index.isReloadable(name)) {
                log.debug("Class {} is reloadable", name);
                synchronized (getClassLoadingLock(name)) {
                    Class<?> result = findLoadedClass(name);
                    if (result == null) {
                        String resouceName = name.replace('.', '/') + ".class";
                        try (InputStream in = getResourceAsStream(
                                resouceName)) {
                            if (in == null)
                                throw new ClassNotFoundException(
                                        "Unable to locate resource "
                                                + resouceName
                                                + " for loading class " + name);
                            byte[] bb = ByteStreams.toByteArray(in);
                            result = defineClass(name, bb, 0, bb.length);
                        } catch (ClassNotFoundException e) {
                            throw e;
                        } catch (Exception e) {
                            throw new RuntimeException(
                                    "Error while loading " + resouceName, e);
                        }
                    }
                    if (resolve) {
                        resolveClass(result);
                    }
                    return result;
                }
            }

        }

        Class<?> result = getParent().loadClass(name);
        if (resolve) {
            resolveClass(result);
        }
        return result;
    }
}
