package com.github.ruediste.rise.core.persistence;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Supplier;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.InvalidTransactionException;
import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.slf4j.Logger;

import com.github.ruediste.rise.core.persistence.em.EntityManagerHolder;
import com.github.ruediste.rise.core.persistence.em.EntityManagerSet;
import com.github.ruediste.rise.mvc.MvcRequestInfo;
import com.github.ruediste.rise.mvc.TransactionException;
import com.github.ruediste.rise.nonReloadable.persistence.IsolationLevel;
import com.github.ruediste.rise.nonReloadable.persistence.TransactionProperties;

/**
 * Programmatically controls a transaction.
 * <p>
 * 
 */
@Singleton
public class TransactionControl implements ITransactionControl {

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

    public class TransactionControlImpl implements ITransactionControl {
        private boolean forceNewEntityManagerSet = false;
        private boolean updating = false;
        private IsolationLevel isolationLevel = IsolationLevel.DEFAULT;
        private int timeout;
        private Propagation propagation = Propagation.REQUIRED;
        private boolean noAutoJoin;

        private ArrayList<Class<? extends Throwable>> noRollbackFor = new ArrayList<>();
        private ArrayList<Class<? extends Throwable>> rollbackFor = new ArrayList<>();

        @Override
        public ITransactionControl propagation(Propagation propagation) {
            this.propagation = propagation;
            return this;
        }

        @Override
        public ITransactionControl timeout(int seconds) {
            timeout = seconds;
            return this;
        }

        @Override
        public ITransactionControl forceNewEntityManagerSet() {
            forceNewEntityManagerSet = true;
            return this;
        }

        @Override
        public ITransactionControl noAutoJoin() {
            noAutoJoin = true;
            return this;
        }

        @Override
        public ITransactionControl updating() {
            updating = true;
            return this;
        }

        @Override
        public ITransactionControl updating(boolean value) {
            updating = value;
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public ITransactionControl noRollbackFor(Class<? extends Throwable>... exceptions) {
            noRollbackFor.addAll(Arrays.asList(exceptions));
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public ITransactionControl rollbackFor(Class<? extends Throwable>... exceptions) {
            rollbackFor.addAll(Arrays.asList(exceptions));
            return this;
        }

        @Override
        public ITransactionControl isolation(IsolationLevel level) {
            this.isolationLevel = level;
            return this;
        }

        @Override
        public void execute(TransactionCallbackNoResult action) {
            execute(new TransactionCallback<Object>() {

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
                    if (!isMatchingTransactionActive()) {
                        throw new RuntimeException(
                                "Entered block with MANDATORY transaction, but no matching transaction was active");
                    }
                    return executeWithNewEntityManagerSetIfForcedOrNonePresent(() -> {
                        if (!noAutoJoin)
                            holder.joinTransaction();
                        return action.doInTransaction();
                    });
                case NEVER:
                    // make sure no transaction is in porgress
                    if (txm.getStatus() == Status.STATUS_ACTIVE) {
                        throw new RuntimeException("Entered block with NEVER transaction, but transaction was active");
                    }
                    return executeWithNewEntityManagerSetIfForcedOrNonePresent(action);
                case REQUIRED: {
                    if (isMatchingTransactionActive())
                        return executeWithNewEntityManagerSetIfForcedOrNonePresent(action);
                    else if (txm.getStatus() == Status.STATUS_ACTIVE) {
                        // current transaction does not match => suspend and run
                        // new one
                        return executeWithNewEntityManagerSetIfForcedOrNonePresent(
                                () -> suspendTransactionAndExecute(() -> executeInNewTransaction(action)));
                    } else
                        // no transaction running, start a new one
                        return executeWithNewEntityManagerSetIfForcedOrNonePresent(
                                () -> executeInNewTransaction(action));
                }
                case REQUIRE_NEW: {
                    if (txm.getStatus() == Status.STATUS_ACTIVE) {
                        // suspend and run new one
                        return executeWithNewEntityManagerSetIfForcedOrNonePresent(
                                () -> suspendTransactionAndExecute(() -> executeInNewTransaction(action)));
                    } else
                        // no transaction running, start a new one
                        return executeWithNewEntityManagerSetIfForcedOrNonePresent(
                                () -> executeInNewTransaction(action));
                }
                default:
                    throw new IllegalArgumentException("Unknown propagation " + propagation);
                }
            } catch (SystemException e) {
                throw new RuntimeException(e);
            }
        }

        private boolean isMatchingTransactionActive() throws SystemException {
            boolean matchingTransactionActive = txm.getStatus() == Status.STATUS_ACTIVE
                    && (!transactionProperties.getIsolationLevel().isLowerThan(getActualIsolationLevel()))
                    && transactionProperties.isUpdating() == updating;
            return matchingTransactionActive;
        }

        private <T> T executeWithNewEntityManagerSetIfForcedOrNonePresent(Supplier<T> supplier) {
            if (forceNewEntityManagerSet || holder.getCurrentEntityManagerSet() == null) {
                return holder.withNewEntityManagerSet(supplier);
            } else {
                return supplier.get();
            }
        }

        private <T> T suspendTransactionAndExecute(Supplier<T> supplier) {
            Transaction old;
            try {
                old = txm.suspend();
                try {
                    return supplier.get();
                } finally {
                    txm.resume(old);
                }
            } catch (InvalidTransactionException | IllegalStateException | SystemException e) {
                throw new TransactionException("Error while suspending and resuming transaction", e);
            }
        }

        private <T> T executeInNewTransaction(Supplier<T> supplier) {

            try {
                txm.begin();

                transactionProperties.setIsolationLevel(getActualIsolationLevel());
                transactionProperties.setUpdating(updating);
                txm.setTransactionTimeout(timeout);

                if (!noAutoJoin)
                    holder.joinTransaction();

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
                throw new RuntimeException("Error while running transaction", t.getTargetException());
            } catch (Exception e) {
                throw new TransactionException("Transaction error occured", e);
            } finally {
                Integer status;
                try {
                    status = txm.getStatus();
                    try {
                        if (status != Status.STATUS_NO_TRANSACTION)
                            txm.rollback();
                    } catch (IllegalStateException | SecurityException | SystemException e) {
                        log.error("Error during transaction rollback. Status was " + status, e);
                    }
                } catch (SystemException e) {
                    log.error("Unable to get transaction status", e);
                }

            }
        }

        private IsolationLevel getActualIsolationLevel() {
            IsolationLevel actualIsolationLevel = isolationLevel;

            if (isolationLevel == null || isolationLevel == IsolationLevel.DEFAULT)
                actualIsolationLevel = updating ? IsolationLevel.SERIALIZABLE : IsolationLevel.REPEATABLE_READ;
            return actualIsolationLevel;
        }
    }

    /**
     * Create a new {@link ITransactionControl} with default settings (
     * non-updating, using a fresh {@link EntityManagerSet})
     */
    public ITransactionControl executor() {
        return new TransactionControlImpl();
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
    public ITransactionControl isolation(IsolationLevel level) {
        return executor().isolation(level);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ITransactionControl rollbackFor(Class<? extends Throwable>... exceptions) {
        return executor().rollbackFor(exceptions);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ITransactionControl noRollbackFor(Class<? extends Throwable>... exceptions) {
        return executor().noRollbackFor(exceptions);
    }

    @Override
    public ITransactionControl updating(boolean value) {
        return executor().updating(value);
    }

    @Override
    public ITransactionControl updating() {
        return executor().updating();
    }

    @Override
    public ITransactionControl forceNewEntityManagerSet() {
        return executor().forceNewEntityManagerSet();
    }

    @Override
    public ITransactionControl timeout(int seconds) {
        return executor().timeout(seconds);
    }

    @Override
    public ITransactionControl propagation(Propagation propagation) {
        return executor().propagation(propagation);
    }

    @Override
    public ITransactionControl noAutoJoin() {
        return executor().noAutoJoin();
    }

    public static abstract class TransactionListener {
        /**
         * invoked before starting the commit
         */
        public void beforeCompletion() {
        }

        /**
         * invoked after a successful commit, before
         * {@link TransactionListener#after()}
         */
        public void afterSuccess(int status) {
        }

        /**
         * invoked after a failed commit, before
         * {@link TransactionListener#after()}
         */
        public void afterFailure(int status) {
        }

        /**
         * invoked after a commit, after
         * {@link TransactionListener#afterSuccess()} or
         * {@link TransactionListener#afterFailure()}
         */
        public void after(int status) {
        }
    }

    public void registerSynchronization(TransactionListener listener) {

        try {
            txm.getTransaction().registerSynchronization(new Synchronization() {

                @Override
                public void beforeCompletion() {
                    listener.beforeCompletion();
                }

                @Override
                public void afterCompletion(int status) {
                    try {
                        if (status == Status.STATUS_COMMITTED) {
                            listener.afterSuccess(status);
                        } else
                            listener.afterFailure(status);
                    } catch (Throwable t) {
                        log.error("Error occurred in after method", t);
                    }
                    try {
                        listener.after(status);
                    } catch (Throwable t) {
                        log.error("Error occurred in after method", t);
                    }
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
