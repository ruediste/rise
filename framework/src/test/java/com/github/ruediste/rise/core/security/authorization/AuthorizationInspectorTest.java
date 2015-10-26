package com.github.ruediste.rise.core.security.authorization;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.github.ruediste.c3java.invocationRecording.MethodInvocationRecorder;

public class AuthorizationInspectorTest {

    interface IA {
        Authz getAuthz();

        default void ia() {
            getAuthz().doAuthChecks(() -> {
            });
        }
    }

    class A implements IA {

        @Override
        public Authz getAuthz() {
            return null;
        }
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
