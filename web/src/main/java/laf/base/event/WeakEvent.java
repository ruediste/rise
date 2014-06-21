package laf.base.event;

import java.util.*;

import laf.attachedProperties.AttachedProperty;
import laf.attachedProperties.AttachedPropertyBearer;

/**
 * Event implementation using weak keys to reference the event handlers.
 *
 * <p>
 * Traditional event implementations keep a strong reference to the registered
 * event handlers. This causes memory leaks if the event lives longer than the
 * event subscribers. This implementation requires the handlers to implement
 * {@link AttachedPropertyBearer}. Using a private attached property, a strong
 * reference is established from the subscribers to the event handlers. The
 * event only keeps a weak reference to the subscriber.
 * </p>
 * <img src="doc-files/weakEvent.png" />
 */
public class WeakEvent<T> {
	private Object lock = new Object();
	private WeakHashMap<AttachedPropertyBearer, Object> subscribers = new WeakHashMap<>();
	private AttachedProperty<AttachedPropertyBearer, Set<EventHandler<T>>> handlers = new AttachedProperty<>();

	public void register(AttachedPropertyBearer subscriber,
			EventHandler<T> handler) {

		synchronized (lock) {

			subscribers.put(subscriber, null);

			handlers.setIfAbsent(subscriber, new HashSet<EventHandler<T>>())
					.add(handler);
		}
	}

	public boolean remove(AttachedPropertyBearer subscriber,
			EventHandler<T> handler) {

		synchronized (lock) {
			return handlers.get(subscriber).remove(handler);
		}
	}

	public void raise(T arg) {
		Set<AttachedPropertyBearer> set;
		synchronized (lock) {
			set = new HashSet<>(subscribers.keySet());
		}
		for (AttachedPropertyBearer subscriber : set) {
			for (EventHandler<T> handler : handlers.get(subscriber)) {
				handler.handle(arg);
			}
		}
	}
}
