package com.github.ruediste.rise.util;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Monad containing a value or the reason there is no value as an exception
 */
public class Try<T> {

    RuntimeException exception;
    T value;

    private Try(RuntimeException exception, T value) {
        super();
        this.exception = exception;
        this.value = value;
    }

    public static <T> Try<T> failure(RuntimeException exception) {
        return new Try<>(exception, null);
    }

    public static <T> Try<T> of(T value) {
        return new Try<>(null, value);
    }

    public T get() {
        if (exception != null)
            throw exception;
        return value;
    }

    @SuppressWarnings("unchecked")
    public <P> Try<P> map(Function<T, P> func) {
        if (exception != null)
            return (Try<P>) this;
        return Try.of(func.apply(value));
    }

    @SuppressWarnings("unchecked")
    public <P> Try<P> flatMap(Function<T, Try<P>> func) {
        if (exception != null)
            return (Try<P>) this;
        return func.apply(value);
    }

    public boolean isPresent() {
        return exception == null;
    }

    public void ifPresent(Consumer<T> consumer) {
        if (exception == null)
            consumer.accept(value);
    }

    public void ifFailure(Consumer<RuntimeException> consumer) {
        if (exception != null)
            consumer.accept(exception);
    }

    public static <T> Try<T> of(Optional<T> optional, Supplier<RuntimeException> failureSupplier) {
        if (optional.isPresent())
            return Try.of(optional.get());
        else
            return Try.failure(failureSupplier.get());
    }

    @Override
    public int hashCode() {
        return Objects.hash(exception, value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Try<?> other = (Try<?>) obj;
        return Objects.equals(exception, other.exception) && Objects.equals(value, other.value);
    }

}
