package com.github.ruediste.rise.core.security.authorization;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.Collections;
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

public class MethodAuthorizationManagerTest {

    @Inject
    AuthorizationDecisionManager mgr;

    @Inject
    Authz authz;

    @Before
    public void before() {
        allowedRights = new HashSet<>();
        Injector injector = Salta.createInjector(new AbstractModule() {

            @Override
            protected void configure() throws Exception {
                MethodAuthorizationManager.get(binder()).addRule(RequiresRight.class,
                        r -> Collections.singleton(r.value()));
            }
        });
        injector.injectMembers(this);
        mgr.setPerformer((rights, auth) -> {
            AuthorizationResultBuilder builder = new AuthorizationResultBuilder();
            for (com.github.ruediste.rise.core.security.authorization.Right right : rights) {
                if (allowedRights.contains(right))
                    continue;
                builder.add(new AuthorizationFailure("Right " + right + " not allowed"));
            }
            return builder.build();
        });
        InjectorsHolder.setInjectors(null, injector);
    }

    @After
    public void after() {
        InjectorsHolder.restoreInjectors(null);
    }

    Set<Object> allowedRights;

    enum TestRightRight implements Right {
        TEST_RIGHT, OTHER_RIGHT
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Repeatable(RepeatRequiresRight.class)
    @Target(ElementType.METHOD)
    @interface RequiresRight {
        TestRightRight value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface RepeatRequiresRight {
        RequiresRight[] value();
    }

    static class Service {
        boolean executed;

        @RequiresRight(TestRightRight.TEST_RIGHT)
        void protectedMethod() {
            executed = true;
        }

        void nonProtectedMethod() {
            executed = true;

        }

        @RequiresRight(TestRightRight.TEST_RIGHT)
        @RequiresRight(TestRightRight.OTHER_RIGHT)
        void protectedMultiple() {

        }
    }

    @Inject
    Service service;

    @Test
    public void protectedMethod_isAuthorized() {
        assertFalse(authz.isAuthorized(service, x -> x.protectedMethod()));
        assertFalse(service.executed);
        allowedRights.add(TestRightRight.TEST_RIGHT);
        assertTrue(authz.isAuthorized(service, x -> x.protectedMethod()));
        assertFalse(service.executed);
    }

    @Test
    public void protectedMethod_checkAuthorized_passes() {
        allowedRights.add(TestRightRight.TEST_RIGHT);
        authz.checkAuthorized(service, x -> x.protectedMethod());
        assertFalse(service.executed);
    }

    @Test(expected = AuthorizationException.class)
    public void protectedMethod_checkAuthorized_notAuthorized() {
        authz.checkAuthorized(service, x -> x.protectedMethod());
    }

    @Test(expected = AuthorizationException.class)
    public void protectedMethod_invocation_notAuthorized() {
        service.protectedMethod();
    }

    @Test
    public void protectedMultiple() {
        System.out.println(Arrays.toString(MethodInvocationRecorder
                .getLastInvocation(Service.class, x -> x.protectedMultiple()).getMethod().getAnnotations()));
        assertFalse(authz.isAuthorized(service, x -> x.protectedMultiple()));
        allowedRights.add(TestRightRight.TEST_RIGHT);
        assertFalse(authz.isAuthorized(service, x -> x.protectedMultiple()));
        allowedRights.add(TestRightRight.OTHER_RIGHT);
        assertTrue(authz.isAuthorized(service, x -> x.protectedMultiple()));

    }

    @Test
    public void protectedMethod_invocation_authorized() {
        allowedRights.add(TestRightRight.TEST_RIGHT);
        service.protectedMethod();
        assertTrue(service.executed);
    }

    @Test
    public void testNonProtectedMethod() {
        assertTrue(authz.isAuthorized(service, x -> x.nonProtectedMethod()));
        assertFalse(service.executed);
        authz.checkAuthorized(service, x -> x.nonProtectedMethod());
        assertFalse(service.executed);
        service.nonProtectedMethod();
        assertTrue(service.executed);
    }
}
