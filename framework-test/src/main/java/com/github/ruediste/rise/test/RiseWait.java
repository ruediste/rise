package com.github.ruediste.rise.test;

import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.Clock;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Sleeper;

/**
 * Helper to poll for a condition to become true.
 * 
 * <p>
 * This is a replacement for {@link FluentWait} with an API optimized for use
 * with Java 8 Lambdas.
 */
public class RiseWait<T> {
    FluentWait<T> fluentWait;

    /**
     * @param input
     *            The input value to pass to the evaluated conditions.
     */
    public RiseWait(T input) {
        fluentWait = new FluentWait<>(input);
    }

    /**
     * @param input
     *            The input value to pass to the evaluated conditions.
     * @param clock
     *            The clock to use when measuring the timeout.
     * @param sleeper
     *            Used to put the thread to sleep between evaluation loops.
     */
    public RiseWait(T input, Clock clock, Sleeper sleeper) {
        fluentWait = new FluentWait<>(input, clock, sleeper);
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
    public RiseWait<T> withTimeout(java.time.Duration duration) {
        fluentWait.withTimeout(duration.toMillis(), TimeUnit.MILLISECONDS);
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
        fluentWait.withMessage(message);
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
    public RiseWait<T> pollingEvery(java.time.Duration duration) {
        fluentWait.pollingEvery(duration.toMillis(), TimeUnit.MILLISECONDS);
        return this;
    }

    /**
     * Repeatedly applies this instance's input value to the given consumer
     * until the timeout expires or the consumer runs without exception.
     **/
    public void untilPassing(final Consumer<T> passes) {
        untilTrue(new Predicate<T>() {
            @Override
            public boolean test(T i) {
                passes.accept(i);
                return true;
            }

            @Override
            public String toString() {
                return passes.toString();
            }
        });
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
        fluentWait.until(new com.google.common.base.Predicate<T>() {
            @Override
            public boolean apply(T input) {
                try {
                    return isTrue.test(input);
                } catch (Throwable t) {
                    return false;
                }
            }

            @Override
            public String toString() {
                return isTrue.toString();
            }
        });
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
        return fluentWait.until(new com.google.common.base.Function<T, V>() {

            @Override
            public V apply(T input) {
                try {
                    return isTrue.apply(input);
                } catch (Throwable t) {
                    return null;
                }
            }

            @Override
            public String toString() {
                return isTrue.toString();
            }
        });
    }

}
