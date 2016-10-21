package com.github.ruediste.rise.util;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Optional which can contain null values
 */
public class NOptional<T> {

    private static NOptional<?> empty = new NOptional<Object>(null);

    private T value;

    private NOptional(T value) {
        this.value = value;
    }

    public static <T> NOptional<T> of(T value) {
        return new NOptional<>(value);
    }

    @SuppressWarnings("unchecked")
    public static <T> NOptional<T> empty() {
        return (NOptional<T>) empty;
    }

    public <P> NOptional<P> map(Function<T, P> func) {
        if (this == empty)
            return empty();
        return NOptional.of(func.apply(value));
    }

    public <P> NOptional<P> flatMap(Function<T, NOptional<P>> func) {
        if (this == empty)
            return empty();
        return func.apply(value);
    }

    public T get() {
        if (this == empty)
            throw new NoSuchElementException("No value present");
        return value;
    }

    public T orElse(T fallback) {
        if (this == empty)
            return fallback;
        return value;
    }

    public T orElseGet(Supplier<T> fallback) {
        if (this == empty)
            return fallback.get();
        return value;
    }

    public <E extends Throwable> T orElseThrow(Supplier<E> throwableSupplier) throws E {
        if (this == empty)
            throw throwableSupplier.get();
        return value;

    }

    public boolean isPresent() {
        return this != empty;
    }

    public void ifPresent(Consumer<T> consumer) {
        if (this != empty)
            consumer.accept(value);
    }

    public void ifAbsent(Runnable runnable) {
        if (this == empty)
            runnable.run();
    }

    @Override
    public int hashCode() {
        if (this == empty)
            return 0;
        if (value == null)
            return 1;
        return Objects.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        NOptional<?> other = (NOptional<?>) obj;
        return Objects.equals(value, other.value);
    }

    @Override
    public String toString() {
        return isPresent() ? String.format("NOptional[%s]", value) : "NOptional.empty";
    }

}
