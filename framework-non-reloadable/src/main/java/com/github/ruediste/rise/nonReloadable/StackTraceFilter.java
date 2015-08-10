package com.github.ruediste.rise.nonReloadable;

/**
 * Filters the stack trace of a {@link Throwable}
 * 
 * <p>
 * Often stack traces contain many stack frames from framework code. These stack
 * traces distract from the application code. This filter takes a
 * {@link Throwable} and removes unwanted stack frames.
 */
public interface StackTraceFilter {

    void filter(Throwable input);

}