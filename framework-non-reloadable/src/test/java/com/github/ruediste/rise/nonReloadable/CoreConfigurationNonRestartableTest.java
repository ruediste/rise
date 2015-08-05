package com.github.ruediste.rise.nonReloadable;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.common.reflect.Reflection;

public class CoreConfigurationNonRestartableTest {

    @Test
    public void testShouldBeScanned() throws Exception {
        CoreConfigurationNonRestartable config = new CoreConfigurationNonRestartable();
        config.scannedPrefixes.add(Reflection.getPackageName(getClass()));
        assertTrue(config.shouldBeScanned(getClass().getName()));
        assertFalse(config.shouldBeScanned("org.eclipse.Test"));
    }

}
