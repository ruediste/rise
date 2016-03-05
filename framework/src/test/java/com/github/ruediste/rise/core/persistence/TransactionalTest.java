package com.github.ruediste.rise.core.persistence;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.ruediste.rise.nonReloadable.persistence.IsolationLevel;
import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.Provides;
import com.github.ruediste.salta.jsr330.SaltaModule;

@RunWith(MockitoJUnitRunner.class)
public class TransactionalTest extends DbTestBase {

    @Mock
    TransactionControl trx;

    @Mock
    ITransactionControl trxe;

    @Override
    protected SaltaModule additionalRestartableModule() {
        return new AbstractModule() {

            @Override
            protected void configure() throws Exception {

            }

            @Provides
            @Singleton
            TransactionControl template() {
                return trx;
            }

        };
    }

    @Before
    public void before() {
        when(trx.executor()).thenReturn(trxe);
        when(trxe.propagation(any(Propagation.class))).thenReturn(trxe);
        when(trxe.isolation(any(IsolationLevel.class))).thenReturn(trxe);
        when(trxe.timeout(any(Integer.class))).thenReturn(trxe);
        when(trxe.updating(any(Boolean.class))).thenReturn(trxe);
        when(trxe.rollbackFor()).thenReturn(trxe);
        when(trxe.noRollbackFor()).thenReturn(trxe);
    }

    @Transactional
    public static class A {

        public void test(Runnable run) {
            run.run();
        }

    }

    public static class B {

        @Transactional(isolation = IsolationLevel.SERIALIZABLE)
        public void test(Runnable run) {
            run.run();
        }

    }

    @Inject
    A a;

    @Inject
    B b;

    @Test
    public void testClassAnnotated() {
        a.test(() -> {
        });
        verify(trxe).execute(Mockito.<TransactionCallback<Object>> any());
    }

    @Test
    public void testMethodAnnotated() {
        b.test(() -> {
        });
        verify(trxe).isolation(IsolationLevel.SERIALIZABLE);
        verify(trxe).execute(Mockito.<TransactionCallback<Object>> any());
    }
}
