package com.github.ruediste.rise.core.persistence;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Supplier;

import javax.inject.Inject;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

import org.slf4j.Logger;

import com.github.ruediste.rise.core.persistence.em.EntityManagerHolder;
import com.github.ruediste.rise.core.persistence.em.EntityManagerSet;
import com.github.ruediste.rise.mvc.MvcRequestInfo;
import com.github.ruediste.rise.mvc.TransactionException;
import com.github.ruediste.rise.nonReloadable.persistence.IsolationLevel;
import com.github.ruediste.rise.nonReloadable.persistence.TransactionProperties;

public class TransactionTemplate implements TransactionExecutor {

    @Inject
    Logger log;

    @Inject
    TransactionManager txm;

    @Inject
    EntityManagerHolder holder;

    @Inject
    MvcRequestInfo info;

    @Inject
    TransactionProperties transactionProperties;

    public class TransactionExecutorImpl implements TransactionExecutor {
        private boolean forceNewEntityManagerSet = false;
        private boolean updating = false;
        private IsolationLevel isolationLevel = IsolationLevel.DEFAULT;
        private int timeout;
        private Propagation propagation = Propagation.REQUIRED;

        private ArrayList<Class<? extends Throwable>> noRollbackFor = new ArrayList<>();
        private ArrayList<Class<? extends Throwable>> rollbackFor = new ArrayList<>();

        @Override
        public TransactionExecutor propagation(Propagation propagation) {
            this.propagation = propagation;
            return this;
        }

        @Override
        public TransactionExecutor timeout(int seconds) {
            timeout = seconds;
            return this;
        }

        @Override
        public TransactionExecutor forceNewEntityManagerSet() {
            forceNewEntityManagerSet = true;
            return this;
        }

        @Override
        public TransactionExecutor updating() {
            updating = true;
            return this;
        }

        @Override
        public TransactionExecutor updating(boolean value) {
            updating = value;
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public TransactionExecutor noRollbackFor(
                Class<? extends Throwable>... exceptions) {
            noRollbackFor.addAll(Arrays.asList(exceptions));
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public TransactionExecutor rollbackFor(
                Class<? extends Throwable>... exceptions) {
            rollbackFor.addAll(Arrays.asList(exceptions));
            return this;
        }

        @Override
        public TransactionExecutor isolation(IsolationLevel level) {
            this.isolationLevel = level;
            return this;
        }

        @Override
        public void execute(TransactionCallbackNoResult action) {
            execute(new TransactionCallback<Object>() {

                @Override
                public void beforeEntityManagerSetCreated() {
                    action.beforeEntityManagerSetCreated();
                }

                @Override
                public Object doInTransaction() {
                    action.doInTransaction();
                    return null;
                }
            });
        }

        @Override
        public <T> T execute(TransactionCallback<T> action) {
            try {
                switch (propagation) {
                case MANDATORY:
                    // check that a transaction is in progress
                    if (txm.getStatus() != Status.STATUS_ACTIVE) {
                        throw new RuntimeException(
                                "Entered block with MANDATORY transaction, but no transaction was active");
                    }
                    return executeWithNewEntityManagerSetIfForcedOrNonePresent(() -> action
                            .doInTransaction());
                case NEVER:
                    // make sure no transaction is in porgress
                    if (txm.getStatus() != Status.STATUS_NO_TRANSACTION) {
                        throw new RuntimeException(
                                "Entered block with NEVER transaction, but transaction was active");
                    }
                    if (forceNewEntityManagerSet) {
                        return holder.withNewEntityManagerSet(() -> action
                                .doInTransaction());
                    } else {
                        return action.doInTransaction();
                    }
                case REQUIRED: {
                    Supplier<T> supplier = () -> executeWithNewEntityManagerSetIfForcedOrNonePresent(() -> action
                            .doInTransaction());
                    if (txm.getStatus() == Status.STATUS_NO_TRANSACTION)
                        return executeInNewTransaction(supplier);
                    else
                        return supplier.get();
                }
                default:
                    throw new IllegalArgumentException();
                }
            } catch (SystemException e) {
                throw new RuntimeException(e);
            }
        }

        private <T> T executeWithNewEntityManagerSetIfForcedOrNonePresent(
                Supplier<T> supplier) {
            if (forceNewEntityManagerSet
                    || holder.getCurrentEntityManagerSet() == null) {
                return holder.withNewEntityManagerSet(supplier);
            } else {
                return supplier.get();
            }
        }

        private <T> T executeInNewTransaction(Supplier<T> supplier) {

            try {
                txm.begin();

                if (isolationLevel == null
                        || isolationLevel == IsolationLevel.DEFAULT)
                    transactionProperties
                            .setDefaultIsolationLevel(updating ? IsolationLevel.SERIALIZABLE
                                    : IsolationLevel.REPEATABLE_READ);
                else
                    transactionProperties
                            .setDefaultIsolationLevel(isolationLevel);

                txm.setTransactionTimeout(timeout);

                T result;
                try {
                    result = supplier.get();
                } catch (Throwable t) {
                    throw new InvocationTargetException(t);
                }
                if (updating && !transactionProperties.isForceRollback()) {
                    txm.commit();
                }
                return result;

            } catch (InvocationTargetException t) {
                throw new RuntimeException("Error while running transaction",
                        t.getTargetException());
            } catch (Exception e) {
                throw new TransactionException("Transaction error occured", e);
            } finally {
                Integer status;
                try {
                    status = txm.getStatus();
                    try {
                        if (status != Status.STATUS_NO_TRANSACTION)
                            txm.rollback();
                    } catch (IllegalStateException | SecurityException
                            | SystemException e) {
                        log.error(
                                "Error during transaction rollback. Status was "
                                        + status, e);
                    }
                } catch (SystemException e) {
                    log.error("Unable to get transaction status", e);
                }

            }
        }
    }

    /**
     * Create a new {@link TransactionExecutor} with default settings (
     * non-updating, using a fresh {@link EntityManagerSet})
     */
    public TransactionExecutor executor() {
        return new TransactionExecutorImpl();
    }

    @Override
    public <T> T execute(TransactionCallback<T> action) {
        return executor().execute(action);
    }

    @Override
    public void execute(TransactionCallbackNoResult action) {
        executor().execute(action);
    }

    @Override
    public TransactionExecutor isolation(IsolationLevel level) {
        return executor().isolation(level);
    }

    @SuppressWarnings("unchecked")
    @Override
    public TransactionExecutor rollbackFor(
            Class<? extends Throwable>... exceptions) {
        return executor().rollbackFor(exceptions);
    }

    @SuppressWarnings("unchecked")
    @Override
    public TransactionExecutor noRollbackFor(
            Class<? extends Throwable>... exceptions) {
        return executor().noRollbackFor(exceptions);
    }

    @Override
    public TransactionExecutor updating(boolean value) {
        return executor().updating(value);
    }

    @Override
    public TransactionExecutor updating() {
        return executor().updating();
    }

    @Override
    public TransactionExecutor forceNewEntityManagerSet() {
        return executor().forceNewEntityManagerSet();
    }

    @Override
    public TransactionExecutor timeout(int seconds) {
        return executor().timeout(seconds);
    }

    @Override
    public TransactionExecutor propagation(Propagation propagation) {
        return executor().propagation(propagation);
    }

}
