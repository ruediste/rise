package com.github.ruediste.rise.util;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * An event to which listeners can register themselves. No guarantee about the
 * order of firing is made.
 */
public class GenericEvent<T> {

    private Set<Runnable> listeningRunnables = new LinkedHashSet<>();
    private Set<Consumer<T>> listeningConsumers = new LinkedHashSet<>();

    /**
     * @return true if the listener was not yet registered
     */
    public synchronized boolean addListener(Runnable listener) {
        return listeningRunnables.add(listener);
    }

    /**
     * @return true if the listener was not yet registered
     */
    public synchronized boolean addListener(Consumer<T> listener) {
        return listeningConsumers.add(listener);
    }

    /**
     * @return true if the listener was registered
     */
    public synchronized boolean removeListener(Runnable listener) {
        return listeningRunnables.remove(listener);
    }

    /**
     * @return true if the listener was registered
     */
    public synchronized boolean removeListener(Consumer<T> listener) {
        return listeningConsumers.remove(listener);
    }

    /**
     * fire the event
     */
    public void fire(T arg) {
        List<Consumer<T>> listeningConsumersCopy;
        List<Runnable> listeningRunnablesCopy;
        synchronized (this) {
            listeningConsumersCopy = new ArrayList<>(listeningConsumers);
            listeningRunnablesCopy = new ArrayList<>(listeningRunnables);
        }
        listeningConsumersCopy.forEach(x -> x.accept(arg));
        listeningRunnablesCopy.forEach(Runnable::run);
    }
}
