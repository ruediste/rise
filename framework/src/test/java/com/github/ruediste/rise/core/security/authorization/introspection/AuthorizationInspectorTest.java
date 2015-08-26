package com.github.ruediste.rise.core.security.authorization.introspection;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.github.ruediste.rise.core.security.authorization.AuthorizationException;

public class AuthorizationInspectorTest {

    boolean checkSuccessCalled;

    private void checkSuccess() {
        checkSuccessCalled = true;
    }

    private void checkFail() {
        throw new AuthorizationException();
    }

    boolean executed;

    @Before
    public void before() {
        executed = false;
        checkSuccessCalled = true;
    }

    void completingMethod() {
        AuthorizationInspector.authorize(() -> {
            checkSuccess();
        });
        executed = true;
    }

    void failingMethod() {
        AuthorizationInspector.authorize(() -> {
            checkFail();
        });
        executed = true;
    }

    void noAuthorization() {
        executed = true;
    }

    @Test
    public void normalExecution() {
        completingMethod();
        assertTrue(checkSuccessCalled);
        assertTrue(executed);
    }

    @Test
    public void normalAuthorization() {
        assertTrue(AuthorizationInspector
                .isAuthorized(() -> completingMethod()));
        assertTrue(checkSuccessCalled);
        assertFalse(executed);
    }

    @Test(expected = AuthorizationException.class)
    public void failingExecution() {
        failingMethod();
    }

    @Test
    public void failingAuthorization() {
        assertFalse(AuthorizationInspector.isAuthorized(() -> failingMethod()));
        assertFalse(executed);
    }

    @Test
    public void noAuthorizationExecution() {
        noAuthorization();
        assertTrue(executed);
    }

    @Test
    public void noAuthorizationAuthorization() {
        assertTrue(AuthorizationInspector.isAuthorized(this,
                x -> x.noAuthorization()));
        assertFalse(executed);
    }

    static class Base {

        public void m() {

        }
    }

    static class Derived {
        public void m() {

        }
    }
}
