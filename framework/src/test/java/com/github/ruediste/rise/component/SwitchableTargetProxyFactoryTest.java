package com.github.ruediste.rise.component;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.github.ruediste.c3java.invocationRecording.MethodInvocationRecorder;

public class SwitchableTargetProxyFactoryTest {

    static class A {
        int x() {
            return 5;
        }
    }

    A ctrl = new A();
    A proxy = SwitchableTargetProxyFactory.createProxy(ctrl);

    @Test
    public void noTarget() {
        assertEquals(5, proxy.x());
    }

    @Test
    public void withTarget() {
        assertEquals("x",
                MethodInvocationRecorder
                        .getLastInvocation(A.class,
                                a -> SwitchableTargetProxyFactory.withTarget(proxy, a, () -> proxy.x()))
                        .getMethod().getName());
    }
}
