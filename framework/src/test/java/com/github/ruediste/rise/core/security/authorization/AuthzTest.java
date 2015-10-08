package com.github.ruediste.rise.core.security.authorization;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.ruediste.c3java.invocationRecording.MethodInvocationRecorder;
import com.github.ruediste.rise.core.aop.AopUtil;
import com.github.ruediste.rise.core.security.authorization.MethodAuthorizationManager.MethodAuthorizationRule;
import com.github.ruediste.rise.nonReloadable.InjectorsHolder;
import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.Injector;
import com.github.ruediste.salta.jsr330.Salta;

public class AuthzTest {

    boolean checkSuccessCalled;

    private void checkSuccess() {
        checkSuccessCalled = true;
    }

    private void checkFail() {
        throw new AuthorizationException();
    }

    boolean executed;

    @Inject
    Service service;

    @Inject
    MethodAuthorizationManager mgr;

    @Before
    public void before() {
        Injector injector = Salta.createInjector(new AbstractModule() {

            @Override
            protected void configure() throws Exception {
                MethodAuthorizationManager mgr = new MethodAuthorizationManager();
                AopUtil.registerSubclass(config().standardConfig, t1 -> {
                    Class<?> cls = t1.getRawType();
                    return mgr.entries.stream().anyMatch(e -> e.typeMatcher.test(cls));
                } , (t2, m1) -> mgr.entries.stream().anyMatch(e -> {
                    Class<?> cls = t2.getRawType();
                    return e.methodMatcher.test(cls, m1);
                }), i -> {
                    Object target1 = i.getTarget();
                    Method method1 = i.getMethod();
                    Object[] arguments = i.getArguments();
                    mgr.entries.forEach(
                            r -> r.rule.getRequiredRights(target1, method1, arguments));
                    return i.proceed();
                });
                mgr.addRule(t -> true,
                        (t, m) -> m.isAnnotationPresent(RequireRight.class),
                        new MethodAuthorizationRule() {

                    @Override
                    public void getRequiredRights(Object target, Method method,
                            Object[] args) {
                        if (!rightPresent)
                            throw new AuthorizationException();
                    }
                });
                bind(MethodAuthorizationManager.class).toInstance(mgr);
            }
        });
        injector.injectMembers(this);
        InjectorsHolder.setInjectors(null, injector);
        executed = false;
        checkSuccessCalled = false;
        executedDelegate = false;
        rightPresent = false;
    }

    @After
    public void after() {
        InjectorsHolder.restoreInjectors(null);
    }

    int completingMethod() {
        Authz.doAuthChecks(() -> {
            checkSuccess();
        });
        executed = true;
        return 4;
    }

    void failingMethod() {
        Authz.doAuthChecks(() -> {
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
        assertTrue(Authz.isAuthorized(this, x -> x.completingMethod()));
        assertTrue(checkSuccessCalled);
        assertFalse(executed);
    }

    @Test(expected = AuthorizationException.class)
    public void failingExecution() {
        failingMethod();
    }

    @Test
    public void failingAuthorization() {
        assertFalse(Authz.isAuthorized(this, x -> x.failingMethod()));
        assertFalse(executed);
    }

    @Test
    public void noAuthorizationExecution() {
        noAuthorization();
        assertTrue(executed);
    }

    @Test
    public void noAuthorizationAuthorization() {
        assertTrue(Authz.isAuthorized(this, x -> x.noAuthorization()));
        assertFalse(executed);
    }

    static class Base {

        public void m() {
        }
    }

    static class Derived1 extends Base {
        @Override
        public void m() {

            Authz.doAuthChecks(() -> {
            });
        }
    }

    static class Derived2 extends Derived1 {
    }

    @Test
    public void authorizationCall_inheritanceTest() {
        Method mBase = MethodInvocationRecorder
                .getLastInvocation(Base.class, x -> x.m()).getMethod();
        Method mDerived = MethodInvocationRecorder
                .getLastInvocation(Derived2.class, x -> x.m()).getMethod();
        assertFalse(
                AuthorizationInspector.callsDoAuthChecks(Base.class, mBase));
        assertTrue(AuthorizationInspector.callsDoAuthChecks(Derived1.class,
                mBase));
        assertTrue(AuthorizationInspector.callsDoAuthChecks(Derived2.class,
                mBase));
        assertFalse(
                AuthorizationInspector.callsDoAuthChecks(Base.class, mDerived));
        assertTrue(AuthorizationInspector.callsDoAuthChecks(Derived1.class,
                mDerived));
        assertTrue(AuthorizationInspector.callsDoAuthChecks(Derived2.class,
                mDerived));
    }

    boolean executedDelegate;

    void authorizeInvokingMethod() {
        Authz.doAuthChecks(() -> {
            completingMethod();
            executedDelegate = true;
        });
    }

    void authorizeDelegating() {
        Authz.doAuthChecks(() -> {
            Authz.checkAuthorized(this, x -> x.completingMethod());
            executedDelegate = true;
        });
    }

    @Test
    public void callFromAuthorize() {
        assertTrue(Authz.isAuthorized(this, x -> x.authorizeInvokingMethod()));
        assertTrue(executedDelegate);
        assertTrue(checkSuccessCalled);
        assertTrue(executed);
    }

    @Test
    public void delegatingFromAuthorize() {
        assertTrue(Authz.isAuthorized(this, x -> x.authorizeDelegating()));
        assertTrue(executedDelegate);
        assertTrue(checkSuccessCalled);
        assertFalse(executed);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface RequireRight {
    }

    boolean rightPresent;

    static class Service {

        public void noCheck() {

        }

        @RequireRight
        public void checkRightOnly() {

        }
    }

    @Test
    public void plainServiceCall() {
        service.noCheck();
    }

    @Test
    public void isAuthorized_rightIsRespected() {
        assertFalse(Authz.isAuthorized(service, x -> x.checkRightOnly()));
        rightPresent = true;
        assertTrue(Authz.isAuthorized(service, x -> x.checkRightOnly()));
    }
}
