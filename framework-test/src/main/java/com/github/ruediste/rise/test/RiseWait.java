package com.github.ruediste.rise.test;

import java.time.Duration;
import java.time.Instant;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.FluentWait;

/**
 * Helper to poll for a condition to become true.
 * 
 * <p>
 * This is a replacement for {@link FluentWait} with an API optimized for use
 * with Java 8 Lambdas.
 */
public class RiseWait<T> {
    private T input;
    private Duration timeout = Duration.ofSeconds(1);
    private Duration interval = Duration.ofMillis(500);
    private String message;

    /**
     * @param input
     *            The input value to pass to the evaluated conditions.
     */
    public RiseWait(T input) {
        this.input = input;
    }

    /**
     * Sets how long to wait for the evaluated condition to be true. The default
     * timeout is 0.5 seconds.
     *
     * @param duration
     *            The timeout duration.
     * 
     * @return A self reference.
     */
    public RiseWait<T> withTimeout(java.time.Duration timeout) {
        this.timeout = timeout;
        return this;
    }

    /**
     * Sets the message to be displayed when time expires.
     *
     * @param message
     *            to be appended to default.
     * @return A self reference.
     */
    public RiseWait<T> withMessage(String message) {
        this.message = message;
        return this;
    }

    /**
     * Sets how often the condition should be evaluated.
     *
     * <p>
     * In reality, the interval may be greater as the cost of actually
     * evaluating a condition function is not factored in. The default polling
     * interval is 500ms.
     *
     * @param duration
     *            The timeout duration.
     * @return A self reference.
     */
    public RiseWait<T> pollingEvery(java.time.Duration interval) {
        this.interval = interval;
        return this;
    }

    /**
     * Repeatedly applies this instance's input value to the given consumer
     * until the timeout expires or the consumer runs without exception.
     **/
    public void untilPassing(final Consumer<T> passes) {
        untilSucessful(() -> {
            passes.accept(input);
            return null;
        } , x -> true,
                lastThrowable -> new TimeoutException(
                        String.format(
                                "Timed out after waiting %.2f seconds for %s%s",
                                timeout.toMillis() / 1000.0,
                                message == null ? passes : message,
                                lastThrowable == null ? ""
                                        : ". Cause:\n"
                                                + lastThrowable.toString()),
                        lastThrowable));
    }

    private <V> V untilSucessful(final Supplier<V> operation,
            Predicate<V> isSuccessful,
            Function<Throwable, RuntimeException> exceptionProducer) {
        Instant end = Instant.now().plus(interval);
        Throwable lastThrowable;
        while (true) {
            try {
                V result = operation.get();
                if (isSuccessful.test(result))
                    return result;
                else
                    lastThrowable = null;
            } catch (Throwable t) {
                lastThrowable = t;
            }
            if (Instant.now().isAfter(end)) {
                throw exceptionProducer.apply(lastThrowable);
            }

            try {
                Thread.sleep(interval.toMillis());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new WebDriverException(e);
            }
        }
    }

    /**
     * Repeatedly runs the given runnable until the timeout expires or the
     * runable runs without exception.
     **/
    public void untilPassing(final Runnable passes) {
        untilPassing(new Consumer<T>() {
            @Override
            public void accept(T i) {
                passes.run();
            }

            @Override
            public String toString() {
                return passes.toString();
            }
        });
    }

    /**
     * Repeatedly tries the given supplier until the timeout expires or the
     * supplier evaluates to true.
     *
     * @param isTrue
     *            The predicate to wait on.
     * @throws TimeoutException
     *             If the timeout expires.
     */
    public void untilTrue(final BooleanSupplier isTrue) {
        untilTrue(x -> isTrue.getAsBoolean());
    }

    /**
     * Repeatedly applies this instance's input value to the given predicate
     * until the timeout expires or the predicate evaluates to true.
     *
     * @param isTrue
     *            The predicate to wait on.
     * @throws TimeoutException
     *             If the timeout expires.
     */
    public void untilTrue(final Predicate<T> isTrue) {
        untilSucessful(() -> isTrue.test(input), x -> x,
                lastThrowable -> new TimeoutException(
                        String.format(
                                "Timed out after waiting %d seconds for %s to return true",
                                timeout.toMillis() / 1000.0,
                                message == null ? isTrue : message),
                        lastThrowable));

    }

    /**
     * Repeatedly applies this instance's input value to the given function
     * until one of the following occurs:
     * <ol>
     * <li>the function returns a value other than null nor false,</li>
     * <li>the timeout expires,
     * <li>
     * <li>the current thread is interrupted</li>
     * </ol>
     *
     * @param isTrue
     *            The function to evaluate
     * @param <V>
     *            The function's expected return type.
     * @return The functions' return value if the function returned something
     *         different from null or false before the timeout expired.
     * @throws TimeoutException
     *             If the timeout expires.
     */
    public <V> V untilValue(Function<? super T, V> isTrue) {
        return untilSucessful(() -> isTrue.apply(input),
                x -> x != null && x != Boolean.FALSE,
                lastThrowable -> new TimeoutException(
                        String.format(
                                "Timed out after waiting %d seconds for %s to return a value other than null or false",
                                timeout.toMillis() / 1000.0,
                                message == null ? isTrue : message),
                        lastThrowable));
    }

}
