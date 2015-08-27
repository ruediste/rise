package com.github.ruediste.rise.core.security.authorization.introspection;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;

import com.github.ruediste.c3java.invocationRecording.MethodInvocationRecorder;
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
        checkSuccessCalled = false;
        executedDelegate = false;
    }

    int completingMethod() {
        AuthorizationInspector.authorize(() -> {
            checkSuccess();
        });
        executed = true;
        return 4;
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

    static class Derived1 extends Base {
        @Override
        public void m() {

            AuthorizationInspector.authorize(() -> {
            });
        }
    }

    static class Derived2 extends Derived1 {
    }

    @Test
    public void authorizationCall_inheritanceTest() {
        Method mBase = MethodInvocationRecorder.getLastInvocation(Base.class,
                x -> x.m()).getMethod();
        Method mDerived = MethodInvocationRecorder.getLastInvocation(
                Derived2.class, x -> x.m()).getMethod();
        assertFalse(AuthorizationInspector.callsAuthorize(Base.class, mBase));
        assertTrue(AuthorizationInspector.callsAuthorize(Derived1.class, mBase));
        assertTrue(AuthorizationInspector.callsAuthorize(Derived2.class, mBase));
        assertFalse(AuthorizationInspector.callsAuthorize(Base.class, mDerived));
        assertTrue(AuthorizationInspector.callsAuthorize(Derived1.class,
                mDerived));
        assertTrue(AuthorizationInspector.callsAuthorize(Derived2.class,
                mDerived));
    }

    boolean executedDelegate;

    void authorizeInvokingMethod() {
        AuthorizationInspector.authorize(() -> {
            completingMethod();
            executedDelegate = true;
        });
    }

    void authorizeDelegating() {
        AuthorizationInspector.authorize(() -> {
            AuthorizationInspector.checkAuthorized(this::completingMethod);
            executedDelegate = true;
        });
    }

    @Test
    public void callFromAuthorize() {
        assertTrue(AuthorizationInspector
                .isAuthorized(this::authorizeInvokingMethod));
        assertTrue(executedDelegate);
        assertTrue(checkSuccessCalled);
        assertTrue(executed);
    }

    @Test
    public void delegatingFromAuthorize() {
        assertTrue(AuthorizationInspector
                .isAuthorized(this::authorizeDelegating));
        assertTrue(executedDelegate);
        assertTrue(checkSuccessCalled);
        assertFalse(executed);
    }
}
