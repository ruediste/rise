package com.github.ruediste.rise.nonReloadable.persistence;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.sql.DataSource;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

import org.slf4j.Logger;

/**
 * {@link DataSource} delegating to a registered delegate based on the
 * {@link IsolationLevel} of the current transaction
 * 
 * @see TransactionProperties#getIsolationLevel()
 */
public class IsolationLevelDataSourceRouter extends DelegatingDataSource {

    @Inject
    Logger log;

    @Inject
    TransactionProperties transactionProperties;

    @Inject
    TransactionManager txm;

    private IsolationLevel defaultLevel = IsolationLevel.SERIALIZABLE;

    private Class<?> qualifier;
    private Map<IsolationLevel, DataSource> dataSources = new HashMap<>();

    @Override
    protected DataSource getDelegate() {
        DataSource result;
        try {
            if (txm.getStatus() != Status.STATUS_NO_TRANSACTION) {
                IsolationLevel level = transactionProperties
                        .getIsolationLevel();
                result = dataSources.get(level);
                if (result == null) {
                    result = dataSources.get(defaultLevel);
                }
                if (result == null) {
                    throw new RuntimeException(
                            "No DataSource registered for level " + level
                                    + "or default level " + defaultLevel);
                }
            } else {
                result = dataSources.get(defaultLevel);
                if (result == null) {
                    throw new RuntimeException(
                            "No DataSource registered for default level "
                                    + defaultLevel);
                }
            }
        } catch (SystemException e) {
            throw new RuntimeException("Error while getting transaction status",
                    e);
        }
        return result;
    }

    public void setDataSource(IsolationLevel level, DataSource dataSource) {
        dataSources.put(level, dataSource);
        log.debug("Registered DataSource for isolation level " + level + ": "
                + dataSource);
    }

    public Class<?> getQualifier() {
        return qualifier;
    }

    /**
     * Set the {@link DataSource} qualifier of this router
     */
    public void setQualifier(Class<?> qualifier) {
        this.qualifier = qualifier;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + qualifier == null ? "null"
                : qualifier.getClass().getName() + "]";
    }

    public IsolationLevel getDefaultLevel() {
        return defaultLevel;
    }

    public void setDefaultLevel(IsolationLevel defaultLevel) {
        this.defaultLevel = defaultLevel;
    }
}
