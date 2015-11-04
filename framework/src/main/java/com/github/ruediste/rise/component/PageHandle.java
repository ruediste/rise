package com.github.ruediste.rise.component;

import java.time.Instant;
import java.util.function.Function;

import com.github.ruediste.salta.standard.util.SimpleScopeManagerBase.ScopeState;

/**
 * Handle for a {@link ComponentPage}.
 */
public class PageHandle {
    /**
     * Objects in the page scope. Includes the {@link ComponentPage} instance.
     */
    public ScopeState pageScopeState;

    /**
     * Lock used to guarantee single threaded access to a page
     */
    public final Object lock = new Object();

    public long id;

    private Object endOfLifeLock = new Object();
    private Instant endOfLife;

    public Instant getEndOfLife() {
        synchronized (endOfLifeLock) {
            return endOfLife;
        }
    }

    /**
     * Update the end of life atomically
     */
    public void setEndOfLife(Function<Instant, Instant> updater) {
        synchronized (endOfLifeLock) {
            this.endOfLife = updater.apply(endOfLife);
        }
    }

}
