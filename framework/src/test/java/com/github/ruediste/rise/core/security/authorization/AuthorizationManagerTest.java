package com.github.ruediste.rise.core.security.authorization;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.ruediste.c3java.invocationRecording.MethodInvocationRecorder;
import com.github.ruediste.rise.nonReloadable.InjectorsHolder;
import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.Injector;
import com.github.ruediste.salta.jsr330.Salta;

public class AuthorizationManagerTest {

    @Inject
    AuthorizationManager mgr;

    @Before
    public void before() {
        allowedRights = new HashSet<>();
        Injector injector = Salta.createInjector(new AbstractModule() {

            @Override
            protected void configure() throws Exception {
                AuthorizationManager authorizationManager = new AuthorizationManager();
                authorizationManager.register(config());
                bind(AuthorizationManager.class)
                        .toInstance(authorizationManager);
            }
        });
        injector.injectMembers(this);
        mgr.setRightChecker(rights -> {
            for (Object right : rights) {
                if (!allowedRights.contains(right))
                    throw new AuthorizationException();
            }
        });
        InjectorsHolder.setInjectors(null, injector);
    }

    @After
    public void after() {
        InjectorsHolder.restoreInjectors(null);
    }

    Set<Object> allowedRights;

    enum Right {
        TEST_RIGHT, OTHER_RIGHT
    }

    @MetaRequiresRight
    @Retention(RetentionPolicy.RUNTIME)
    @Repeatable(RepeatRequiresRight.class)
    @interface RequiresRight {
        Right value();
    }

    @MetaRequiresRight
    @Retention(RetentionPolicy.RUNTIME)
    @interface RepeatRequiresRight {
        RequiresRight[]value();
    }

    static class Service {
        boolean executed;

        @RequiresRight(Right.TEST_RIGHT)
        void protectedMethod() {
            executed = true;
        }

        void nonProtectedMethod() {
            executed = true;

        }

        @RequiresRight(Right.TEST_RIGHT)
        @RequiresRight(Right.OTHER_RIGHT)
        void protectedMultiple() {

        }
    }

    @Inject
    Service service;

    @Test
    public void protectedMethod_isAuthorized() {
        assertFalse(Authz.isAuthorized(service, x -> x.protectedMethod()));
        assertFalse(service.executed);
        allowedRights.add(Right.TEST_RIGHT);
        assertTrue(Authz.isAuthorized(service, x -> x.protectedMethod()));
        assertFalse(service.executed);
    }

    @Test
    public void protectedMethod_checkAuthorized_passes() {
        allowedRights.add(Right.TEST_RIGHT);
        Authz.checkAuthorized(service, x -> x.protectedMethod());
        assertFalse(service.executed);
    }

    @Test(expected = AuthorizationException.class)
    public void protectedMethod_checkAuthorized_notAuthorized() {
        Authz.checkAuthorized(service, x -> x.protectedMethod());
    }

    @Test(expected = AuthorizationException.class)
    public void protectedMethod_invocation_notAuthorized() {
        service.protectedMethod();
    }

    @Test
    public void protectedMultiple() {
        System.out
                .println(Arrays.toString(MethodInvocationRecorder
                        .getLastInvocation(Service.class,
                                x -> x.protectedMultiple())
                        .getMethod().getAnnotations()));
        assertFalse(Authz.isAuthorized(service, x -> x.protectedMultiple()));
        allowedRights.add(Right.TEST_RIGHT);
        assertFalse(Authz.isAuthorized(service, x -> x.protectedMultiple()));
        allowedRights.add(Right.OTHER_RIGHT);
        assertTrue(Authz.isAuthorized(service, x -> x.protectedMultiple()));

    }

    @Test
    public void protectedMethod_invocation_authorized() {
        allowedRights.add(Right.TEST_RIGHT);
        service.protectedMethod();
        assertTrue(service.executed);
    }

    @Test
    public void testNonProtectedMethod() {
        assertTrue(Authz.isAuthorized(service, x -> x.nonProtectedMethod()));
        assertFalse(service.executed);
        Authz.checkAuthorized(service, x -> x.nonProtectedMethod());
        assertFalse(service.executed);
        service.nonProtectedMethod();
        assertTrue(service.executed);
    }
}
