package com.github.ruediste.rise.nonReloadable;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.common.reflect.Reflection;

public class CoreConfigurationNonRestartableTest {

    @Test
    public void testShouldBeScanned() throws Exception {
        CoreConfigurationNonRestartable config = new CoreConfigurationNonRestartable();
        config.scannedPrefixes.add(Reflection.getPackageName(getClass()).replace('.', '/'));
        assertTrue(config.shouldClasspathResourceBeScanned(getClass().getName().replace('.', '/') + ".class"));
        assertFalse(config.shouldClasspathResourceBeScanned("org/eclipse/Test"));
    }

}
