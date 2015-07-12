package com.github.ruediste.rise.nonReloadable;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CoreConfigurationNonRestartableTest {

    @Test
    public void testShouldBeScanned() throws Exception {
        CoreConfigurationNonRestartable config = new CoreConfigurationNonRestartable();
        assertTrue(config.shouldBeScanned("com.github.ruediste.Test"));
        assertFalse(config.shouldBeScanned("org.eclipse.Test"));
    }

}
