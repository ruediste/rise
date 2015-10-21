package com.github.ruediste.rise.core.persistence;

import org.junit.After;
import org.junit.Before;

import com.github.ruediste.salta.jsr330.SaltaModule;

public class DbTestBase {
    protected PersistenceTestHelper helper = new PersistenceTestHelper(this);

    @Before
    final public void beforeDbTestBase() {
        SaltaModule module = additionalRestartableModule();
        if (module != null)
            helper.setAdditionalRestartableModule(module);

        helper.before();
    }

    protected SaltaModule additionalRestartableModule() {
        return null;
    }

    @After
    final public void afterDbTestBase() {
        helper.after();
    }
}
