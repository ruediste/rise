package com.github.ruediste.rise.nonReloadable.persistence;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.TransactionSynchronizationRegistry;

import com.github.ruediste.rise.util.Pair;

@Singleton
public class TransactionProperties {

    @Inject
    TransactionSynchronizationRegistry registry;

    private Object isolationLevel = new Object();
    private Object defaultIsolationLevel = new Object();
    private Object forceRollback = new Object();

    /**
     * Set the isolation level of the current transaction for the given data
     * source
     */
    public void setIsolationLevel(Class<?> qualifier, IsolationLevel level) {
        registry.putResource(Pair.of(isolationLevel, qualifier), level);
    }

    public void setDefaultIsolationLevel(IsolationLevel level) {
        registry.putResource(defaultIsolationLevel, level);
    }

    public void forceRollback() {
        registry.putResource(forceRollback, true);
    }

    public boolean isForceRollback() {
        return registry.getResource(forceRollback) != null;
    }

    /**
     * Get the isolation level of the current transaction
     */
    public IsolationLevel getIsolationLevel(Class<?> qualifier) {
        Object result = registry
                .getResource(Pair.of(isolationLevel, qualifier));
        if (result == null)
            result = registry.getResource(defaultIsolationLevel);
        return (IsolationLevel) result;
    }
}
