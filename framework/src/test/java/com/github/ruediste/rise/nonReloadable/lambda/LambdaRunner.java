package com.github.ruediste.rise.nonReloadable.lambda;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

/**
 * JUnit Runner weaving the test class for lambda expression extraction
 */
public class LambdaRunner extends BlockJUnit4ClassRunner {

    public LambdaRunner(Class<?> klass) throws InitializationError {
        super(weave(klass));
    }

    private static Class<?> weave(Class<?> klass) {
        try {
            return new WeavingClassLoader(klass, klass.getClassLoader()).loadClass(klass.getName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Error while weaving test class", e);
        }
    }

}
