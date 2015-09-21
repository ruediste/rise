package com.github.ruediste.rise.nonReloadable;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class DefaultStrackTraceFilterTest {

    private DefaultStrackTraceFilter filter;

    @Before
    public void before() {
        filter = new DefaultStrackTraceFilter(
                e -> e.getClassName().startsWith("com.github.ruediste.rise")
                        && !(e.getMethodName().startsWith("t")
                                && e.getMethodName().length() == 2));
    }

    @Test
    public void testSimple() {
        RuntimeException t = new RuntimeException("Hello World");
        filter.filter(t);
        assertEquals("Hello World", t.getMessage());
        assertEquals(4, t.getStackTrace().length);
    }

    @Test
    public void testNested() throws Throwable {
        RuntimeException t = new RuntimeException(t5());
        filter.filter(t);
        assertEquals(7, t.getCause().getStackTrace().length);
    }

    @Test
    public void testWithSuppression() throws Throwable {
        Throwable t = t0();
        filter.filter(t);
        assertEquals(5, t.getStackTrace().length);

        t = t1();
        filter.filter(t);
        assertEquals(6, t.getStackTrace().length);

        t = t2();
        filter.filter(t);
        assertEquals(7, t.getStackTrace().length);

        t = t3();
        filter.filter(t);
        assertEquals(7, t.getStackTrace().length);

        t = t4();
        filter.filter(t);
        assertEquals(7, t.getStackTrace().length);

        t = t5();
        filter.filter(t);
        assertEquals(7, t.getStackTrace().length);
    }

    private Throwable t0() {
        return new Throwable("Hello World");
    }

    private Throwable t1() {
        return t0();
    }

    private Throwable t2() {
        return t1();
    }

    private Throwable t3() {
        return t2();
    }

    private Throwable t4() {
        return t3();
    }

    private Throwable t5() {
        return t4();
    }

}
