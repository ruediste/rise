package com.github.ruediste.rise.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import com.github.ruediste.attachedProperties4J.AttachedProperty;
import com.github.ruediste.attachedProperties4J.AttachedPropertyBearer;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

/**
 * Manager of a {@link GenericEvent}.
 * <p>
 * The {@link GenericEvent} only allows addition and removal of listeners and is
 * intended to publish publically. To fire a reference to the
 * {@link GenericEventManager} must be kept. Typical code:
 * 
 * <pre>
 * {@code
 * private GenericEventManager<MyObj> myEventMgr=new GenericEventManager<>();
 * public GenericEvent<MyObj> getMyEvent(); { return myEventMgr.event();}
 * public void fireMyEvent(){ myEventMgr.fire(this); }
 * </pre>
 * 
 * <p>
 * <b> Weak Listeners </b> <br>
 * A normal event creates a reference from the event to the listener. This makes
 * impossible to garbage collect the listener before the event. To overcome
 * this, a {@link AttachedPropertyBearer} can be specified. The property bearer
 * will be weakly referenced, and a strong reference is established from the
 * bearer to the listener.
 */
public class GenericEventManager<T> {

    GenericEvent<T> event = new GenericEvent<>(this);

    private Set<Runnable> listeningRunnables = new LinkedHashSet<>();
    private Set<Consumer<T>> listeningConsumers = new LinkedHashSet<>();

    private Object placeHolder = new Object();
    private Map<AttachedPropertyBearer, Object> listeningBearers = new MapMaker()
            .weakKeys().makeMap();
    private static AttachedProperty<AttachedPropertyBearer, Multimap<GenericEventManager<?>, Consumer<?>>> consumersProperty = new AttachedProperty<>(
            "WeakConsumers");
    private static AttachedProperty<AttachedPropertyBearer, Multimap<GenericEventManager<?>, Runnable>> runnablesProperty = new AttachedProperty<>(
            "WeakRunnables");

    public GenericEvent<T> event() {
        return event;
    }

    /**
     * @return true if the listener was not yet registered
     */
    public synchronized boolean addListener(Runnable listener) {
        return listeningRunnables.add(listener);
    }

    public synchronized boolean addListener(Runnable listener,
            AttachedPropertyBearer bearer) {
        listeningBearers.put(bearer, placeHolder);

        Multimap<GenericEventManager<?>, Runnable> map = runnablesProperty
                .setIfAbsent(bearer, () -> MultimapBuilder.hashKeys()
                        .linkedHashSetValues().build());
        synchronized (map) {
            return map.put(this, listener);
        }
    }

    /**
     * @return true if the listener was not yet registered
     */
    public synchronized boolean addListener(Consumer<T> listener) {
        return listeningConsumers.add(listener);
    }

    public synchronized boolean addListener(Consumer<T> listener,
            AttachedPropertyBearer bearer) {
        listeningBearers.put(bearer, placeHolder);

        Multimap<GenericEventManager<?>, Consumer<?>> map = consumersProperty
                .setIfAbsent(bearer, () -> MultimapBuilder.hashKeys()
                        .linkedHashSetValues().build());
        synchronized (map) {
            return map.put(this, listener);
        }
    }

    /**
     * @return true if the listener was registered
     */
    public synchronized boolean removeListener(Runnable listener) {
        return listeningRunnables.remove(listener);
    }

    public synchronized boolean removeListener(Runnable listener,
            AttachedPropertyBearer bearer) {
        Multimap<GenericEventManager<?>, Runnable> map = runnablesProperty
                .get(bearer);
        if (map != null)
            return map.remove(this, listener);
        else
            return false;
    }

    /**
     * @return true if the listener was registered
     */
    public synchronized boolean removeListener(Consumer<T> listener) {
        return listeningConsumers.remove(listener);
    }

    public synchronized boolean removeListener(Consumer<T> listener,
            AttachedPropertyBearer bearer) {
        Multimap<GenericEventManager<?>, Consumer<?>> map = consumersProperty
                .get(bearer);
        if (map != null)
            return map.remove(this, listener);
        else
            return false;
    }

    public synchronized void clearListeners() {
        listeningRunnables.clear();
        listeningConsumers.clear();
    }

    /**
     * fire the event
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void fire(T arg) {
        List<Consumer<T>> listeningConsumersCopy;
        List<Runnable> listeningRunnablesCopy;
        synchronized (this) {
            listeningConsumersCopy = new ArrayList<>(listeningConsumers);
            listeningRunnablesCopy = new ArrayList<>(listeningRunnables);
            for (AttachedPropertyBearer bearer : listeningBearers.keySet()) {
                Multimap<GenericEventManager<?>, Consumer<?>> consumerMap = consumersProperty
                        .get(bearer);
                if (consumerMap != null) {
                    listeningConsumersCopy
                            .addAll((Collection) consumerMap.get(this));
                }
                Multimap<GenericEventManager<?>, Runnable> runnableMap = runnablesProperty
                        .get(bearer);
                if (runnableMap != null) {
                    listeningRunnablesCopy.addAll(runnableMap.get(this));
                }
            }
        }
        listeningConsumersCopy.forEach(x -> x.accept(arg));
        listeningRunnablesCopy.forEach(Runnable::run);
    }
}
