package com.github.ruediste.rise.nonReloadable.persistence;

import java.util.function.Supplier;

import javax.inject.Singleton;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;

import org.eclipse.persistence.sessions.ExternalTransactionController;

import bitronix.tm.TransactionManagerServices;

import com.github.ruediste.rise.nonReloadable.front.StartupTimeLogger;
import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.Provides;
import com.google.common.base.Stopwatch;

public class BitronixModule extends AbstractModule {
    public BitronixModule() {

    }

    @Override
    protected void configure() throws Exception {

    }

    private <T> T withStartupTimeLogging(Supplier<T> sup) {
        if (TransactionManagerServices.isTransactionManagerRunning())
            return sup.get();
        Stopwatch watch = Stopwatch.createStarted();
        T result = sup.get();
        StartupTimeLogger.stopAndLog("Bitronix startup", watch);
        return result;
    }

    @Provides
    @Singleton
    TransactionManager transactionManager() {
        return withStartupTimeLogging(() -> TransactionManagerServices
                .getTransactionManager());
    }

    @Provides
    @Singleton
    TransactionSynchronizationRegistry transactionSynchronizationRegistry() {
        return withStartupTimeLogging(() -> TransactionManagerServices
                .getTransactionSynchronizationRegistry());
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
