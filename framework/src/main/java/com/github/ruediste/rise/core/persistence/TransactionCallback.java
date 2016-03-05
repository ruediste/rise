package com.github.ruediste.rise.core.persistence;

import java.util.function.Supplier;

/**
 * Callback for transactional code. Typically used as argument to
 * {@link TransactionControl#execute(TransactionCallback)}
 */
@FunctionalInterface
public interface TransactionCallback<T> extends Supplier<T> {

    T doInTransaction();

    @Override
    default T get() {
        return doInTransaction();
    }
}
