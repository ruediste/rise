package com.github.ruediste.rise.crud;

import static org.junit.Assert.assertSame;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.junit.Before;
import org.junit.Test;

import com.github.ruediste.rise.core.strategy.Strategy;
import com.github.ruediste.rise.core.strategy.UseStrategy;
import com.github.ruediste.rise.crud.annotations.CrudStrategy;
import com.github.ruediste.salta.jsr330.ImplementedBy;
import com.github.ruediste.salta.jsr330.Injector;
import com.github.ruediste.salta.jsr330.Salta;

public class CrudUtilTest {

    @Inject
    Injector injector;

    @Inject
    CrudUtil util;

    @Inject
    TestFactoryImpl1 impl1;

    @Inject
    TestFactoryImpl2 impl2;

    @Before
    public void before() {
        injector = Salta.createInjector();
        Salta.createInjector().injectMembers(this);
    }

    private class A {
    }

    @UseStrategy(TestFactoryImpl2.class)
    @CrudStrategy(type = TestFactory.class, implementation = TestFactoryImpl2.class)
    private class B {
    }

    @ImplementedBy(TestFactoryImpl1.class)
    private interface TestFactory extends Strategy {

    }

    @Singleton
    private static class TestFactoryImpl1 implements TestFactory {

    }

    @Singleton
    private static class TestFactoryImpl2 implements TestFactory {

    }

    @Test
    public void testGetStrategy() throws Exception {
        assertSame(impl1, util.getStrategy(TestFactory.class, A.class));
        assertSame(impl2, util.getStrategy(TestFactory.class, B.class));
    }
}
