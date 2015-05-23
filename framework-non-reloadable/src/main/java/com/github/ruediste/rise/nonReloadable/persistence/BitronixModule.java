package com.github.ruediste.rise.nonReloadable.persistence;

import javax.inject.Singleton;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;

import org.eclipse.persistence.sessions.ExternalTransactionController;

import bitronix.tm.TransactionManagerServices;

import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.Provides;

public class BitronixModule extends AbstractModule {
    public BitronixModule() {

    }

    @Override
    protected void configure() throws Exception {

    }

    @Provides
    @Singleton
    TransactionManager transactionManager() {
        return TransactionManagerServices.getTransactionManager();
    }

    @Provides
    @Singleton
    TransactionSynchronizationRegistry transactionSynchronizationRegistry() {
        return TransactionManagerServices
                .getTransactionSynchronizationRegistry();
    }

    @Provides
    @Singleton
    TransactionIntegrationInfo transactionIntegrationInfo() {
        return new TransactionIntegrationInfo() {

            @Override
            public Class<? extends ExternalTransactionController> getEclipseLinkExternalTransactionController() {
                return BitronixTransactionController.class;
            }
        };
    }
}
