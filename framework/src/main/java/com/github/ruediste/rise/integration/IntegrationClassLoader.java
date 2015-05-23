package com.github.ruediste.rise.integration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeSet;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;

public class IntegrationClassLoader extends ClassLoader {
    private static final String EXT = ".rsc";

    HashSet<String> notDelegated;

    static {
        registerAsParallelCapable();
    }

    public static <T> T loadAndInstantiate(Class<T> interfaceClass,
            Class<? extends T> implementationClass,
            Class<?>... notDelegatedClasses) {

        String prefix = interfaceClass.getPackage().getName().replace('.', '/');

        Class<?>[] tmp = Arrays.copyOf(notDelegatedClasses,
                notDelegatedClasses.length + 1);
        tmp[tmp.length - 1] = implementationClass;

        IntegrationClassLoader cl = new IntegrationClassLoader(Thread
                .currentThread().getContextClassLoader(), prefix, tmp);

        try {
            return interfaceClass.cast(cl.loadClass(
                    implementationClass.getName()).newInstance());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Map from the name a resource is available under to the name the resource
     * can be loaded from the parent classloader
     */
    public HashMap<String, String> resources = new HashMap<>();

    public IntegrationClassLoader(ClassLoader parent, String prefix,
            Class<?>... notDelegated) {
        super(parent);
        this.notDelegated = new HashSet<>();
        for (Class<?> cls : notDelegated) {
            this.notDelegated.add(cls.getName());
        }

        try {
            TreeSet<String> rootPaths = new TreeSet<>();
            Properties deps = new Properties();
            {
                String name = prefix + "/dependencies.properties";

                try (InputStream in = getParent().getResourceAsStream(name)) {
                    if (in == null) {
                        throw new RuntimeException("Unable to load " + name);
                    }
                    deps.load(in);
                }
            }

            for (Entry<Object, Object> entry : deps.entrySet()) {
                String key = (String) entry.getKey();
                if (key.endsWith("/version")) {
                    key = key.substring(0, key.length() - "/version".length());
                    key = key.replace('.', '/');
                    key = key + "/" + entry.getValue() + "/";
                    rootPaths.add("/" + key);
                }
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    getClass().getResourceAsStream("resourceFiles.list"),
                    Charsets.UTF_8))) {
                String name;
                while ((name = br.readLine()) != null) {
                    String root = rootPaths.floor(name);
                    if (root != null && name.startsWith(root)) {
                        String s = name.substring(root.length());
                        s = s.substring(0, s.length() - EXT.length());
                        resources.put(s, prefix + "/resources" + name);
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve)
            throws ClassNotFoundException {
        if (notDelegated.contains(name)) {
            synchronized (getClassLoadingLock(name)) {
                try (InputStream in = getParent().getResourceAsStream(
                        name.replace('.', '/') + ".class")) {
                    byte[] bb = ByteStreams.toByteArray(in);
                    Class<?> result = defineClass(name, bb, 0, bb.length);
                    if (resolve)
                        resolveClass(result);
                    return result;

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        }

        return super.loadClass(name, resolve);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        String s = name.replace('.', '/') + ".class";
        String resource = resources.get(s);
        if (resource != null) {
            InputStream in = getParent().getResourceAsStream(resource);
            if (in != null) {
                try {
                    byte[] bb = ByteStreams.toByteArray(in);
                    return defineClass(name, bb, 0, bb.length);
                } catch (IOException e) {
                    throw new RuntimeException("Error reading class", e);
                } finally {
                    try {
                        in.close();
                    } catch (IOException e) {
                        throw new RuntimeException("Error closing stream", e);
                    }
                }
            }
        }
        return super.findClass(name);
    }
}
