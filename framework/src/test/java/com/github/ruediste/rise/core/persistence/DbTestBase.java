package com.github.ruediste.rise.core.persistence;

import org.junit.After;
import org.junit.Before;

public class DbTestBase {
    protected PersistenceTestHelper helper = new PersistenceTestHelper(this);

    @Before
    final public void beforeDbTestBase() {
        helper.before();
    }

    @After
    final public void afterDbTestBase() {
        helper.after();
    }
}
