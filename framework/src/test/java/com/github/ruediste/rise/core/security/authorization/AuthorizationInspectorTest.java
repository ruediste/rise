package com.github.ruediste.rise.core.security.authorization;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.github.ruediste.c3java.invocationRecording.MethodInvocationRecorder;

public class AuthorizationInspectorTest {

    interface IA {
        default void ia() {
            Authz.doAuthChecks(() -> {
            });
        }
    }

    class A implements IA {
    }

    @Test
    public void defaultMethodSupported() {
        assertTrue(
                AuthorizationInspector.callsDoAuthChecks(A.class,
                        MethodInvocationRecorder
                                .getLastInvocation(IA.class, x -> x.ia())
                                .getMethod()));
    }
}
