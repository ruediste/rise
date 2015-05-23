package com.github.ruediste.rise.core.aop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.github.ruediste.attachedProperties4J.AttachedProperty;
import com.github.ruediste.attachedProperties4J.AttachedPropertyBearer;
import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.Injector;
import com.github.ruediste.salta.jsr330.Salta;

public class AopUtilTest {

    static class A {
        boolean invoked;

        int foo(int i) {
            invoked = true;
            return i;
        }

    }

    static class B {

        int foo(int i) {
            return i;
        }

        int bar(int i) {
            return i;
        }
    }

    @Test
    public void simpleSubclass() {
        A a = Salta.createInjector(new AbstractModule() {

            @Override
            protected void configure() throws Exception {
                AopUtil.registerSubclass(config().standardConfig, (t) -> t
                        .getRawType().equals(A.class), (t, m) -> true,
                        intercepted -> {
                            intercepted.proceed(intercepted.getArguments());
                            return 4;
                        });
            }
        }).getInstance(A.class);
        assertFalse(a.invoked);
        assertEquals(4, a.foo(1));
        assertTrue(a.invoked);
    }

    @Test
    public void simpleSubclassModifyArgs() {
        A a = Salta.createInjector(new AbstractModule() {

            @Override
            protected void configure() throws Exception {
                AopUtil.registerSubclass(config().standardConfig, (t) -> t
                        .getRawType().equals(A.class), (t, m) -> true,
                        intercepted -> {
                            return intercepted.proceed(2);
                        });
            }
        }).getInstance(A.class);
        assertEquals(2, a.foo(1));
    }

    @Test
    public void simpleSubclassTwo() {
        A a = Salta.createInjector(new AbstractModule() {

            @Override
            protected void configure() throws Exception {
                AopUtil.registerSubclass(config().standardConfig, (t) -> true,
                        (t, m) -> true, intercepted -> 1 + (int) intercepted
                                .proceed(intercepted.getArguments()));
                AopUtil.registerSubclass(config().standardConfig, (t) -> true,
                        (t, m) -> true, intercepted -> 2 * (int) intercepted
                                .proceed(intercepted.getArguments()));
            }
        }).getInstance(A.class);
        assertEquals(11, a.foo(5));
    }

    @Test
    public void simpleSubclassTwoSeparate() {
        B b = Salta.createInjector(new AbstractModule() {

            @Override
            protected void configure() throws Exception {
                AopUtil.registerSubclass(config().standardConfig, (t) -> true,
                        (t, m) -> "foo".equals(m.getName()),
                        intercepted -> 1 + (int) intercepted
                                .proceed(intercepted.getArguments()));
                AopUtil.registerSubclass(config().standardConfig, (t) -> true,
                        (t, m) -> "bar".equals(m.getName()),
                        intercepted -> 2 * (int) intercepted
                                .proceed(intercepted.getArguments()));
            }
        }).getInstance(B.class);
        assertEquals(6, b.foo(5));
        assertEquals(10, b.bar(5));
    }

    @Test
    public void simpleProxy() {
        A a = Salta.createInjector(new AbstractModule() {

            @Override
            protected void configure() throws Exception {
                AopUtil.registerProxy(config().standardConfig, (t) -> true, (t,
                        m) -> true, intercepted -> {
                    intercepted.proceed(intercepted.getArguments());
                    return 4;
                });
            }
        }).getInstance(A.class);
        assertFalse(a.invoked);
        assertEquals(4, a.foo(1));

        // fields do not work on proxies...
        assertFalse(a.invoked);
    }

    @Test
    public void simpleProxyModifyArgs() {
        A a = Salta.createInjector(new AbstractModule() {

            @Override
            protected void configure() throws Exception {
                AopUtil.registerProxy(config().standardConfig, (t) -> t
                        .getRawType().equals(A.class), (t, m) -> true,
                        intercepted -> {
                            return intercepted.proceed(3);
                        });
            }
        }).getInstance(A.class);
        assertEquals(3, a.foo(1));
    }

    @Test
    public void simpleProxyTwo() {
        A a = Salta.createInjector(new AbstractModule() {

            @Override
            protected void configure() throws Exception {
                AopUtil.registerProxy(config().standardConfig, (t) -> t
                        .getRawType().equals(A.class), (t, m) -> true,
                        intercepted -> 1 + (int) intercepted
                                .proceed(intercepted.getArguments()));
                AopUtil.registerProxy(config().standardConfig, (t) -> t
                        .getRawType().equals(A.class), (t, m) -> true,
                        intercepted -> 2 * (int) intercepted
                                .proceed(intercepted.getArguments()));
            }
        }).getInstance(A.class);
        assertEquals(11, a.foo(5));
    }

    @Test
    public void simpleProxyTwoSeparate() {
        B b = Salta.createInjector(new AbstractModule() {

            @Override
            protected void configure() throws Exception {
                AopUtil.registerProxy(config().standardConfig, (t) -> true, (t,
                        m) -> "foo".equals(m.getName()),
                        intercepted -> 1 + (int) intercepted
                                .proceed(intercepted.getArguments()));
                AopUtil.registerProxy(config().standardConfig, (t) -> true, (t,
                        m) -> "bar".equals(m.getName()),
                        intercepted -> 2 * (int) intercepted
                                .proceed(intercepted.getArguments()));
            }
        }).getInstance(B.class);
        assertEquals(6, b.foo(5));
        assertEquals(10, b.bar(5));
    }

    @Test
    public void testAttachedPropertyBearer() {
        AttachedProperty<AttachedPropertyBearer, Integer> p = new AttachedProperty<>();
        Injector injector = Salta.createInjector(new AbstractModule() {

            @Override
            protected void configure() throws Exception {
                AopUtil.registerSubclass(
                        config().standardConfig,
                        (t) -> t.getRawType().equals(A.class),
                        (t, m) -> true,
                        intercepted -> {
                            if (p.isSet(intercepted.getPropertyBearer())) {
                                return p.get(intercepted.getPropertyBearer());
                            } else {
                                p.set(intercepted.getPropertyBearer(),
                                        (Integer) intercepted.getArguments()[0]);
                                return 0;
                            }
                        });
            }
        });
        A a1 = injector.getInstance(A.class);
        A a2 = injector.getInstance(A.class);

        a1.foo(4);
        assertEquals(4, a1.foo(2));
        a2.foo(5);
        assertEquals(5, a2.foo(2));
    }
}
