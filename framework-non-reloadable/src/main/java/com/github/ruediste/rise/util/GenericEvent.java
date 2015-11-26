package com.github.ruediste.rise.util;

import java.util.function.Consumer;

/**
 * An event to which listeners can register themselves. No guarantee about the
 * order of firing is made.
 */
public class GenericEvent<T> {

    final private GenericEventManager<T> manager;

    public GenericEvent(GenericEventManager<T> manager) {
        this.manager = manager;
    }

    public boolean addListener(Runnable listener) {
        return manager.addListener(listener);
    }

    public boolean addListener(Consumer<T> listener) {
        return manager.addListener(listener);
    }

    public boolean removeListener(Runnable listener) {
        return manager.removeListener(listener);
    }

    public boolean removeListener(Consumer<T> listener) {
        return manager.removeListener(listener);
    }

}
