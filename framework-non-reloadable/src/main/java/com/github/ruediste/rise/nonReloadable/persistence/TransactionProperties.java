package com.github.ruediste.rise.nonReloadable.persistence;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.TransactionSynchronizationRegistry;

import com.github.ruediste.rise.nonReloadable.NonRestartable;

@NonRestartable
@Singleton
public class TransactionProperties {

    @Inject
    TransactionSynchronizationRegistry registry;

    private Object isolationLevel = new Object();
    private Object forceRollback = new Object();
    private Object updating = new Object();

    public void setUpdating(boolean updating) {
        registry.putResource(this.updating, updating);
    }

    public boolean isUpdating() {
        return Boolean.TRUE.equals(registry.getResource(updating));
    }

    public void setIsolationLevel(IsolationLevel level) {
        registry.putResource(isolationLevel, level);
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
    public IsolationLevel getIsolationLevel() {
        return (IsolationLevel) registry.getResource(isolationLevel);
    }
}
