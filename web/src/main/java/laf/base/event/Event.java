package laf.base.event;

import java.util.ArrayList;

public class Event<T> {

	private ArrayList<EventHandler<T>> listeners = new ArrayList<>();

	public void register(EventHandler<T> listener) {
		listeners.add(listener);
	}

	public boolean remove(EventHandler<T> listener) {
		return listeners.remove(listener);
	}

	public void raise(T arg) {
		for (EventHandler<T> listener : listeners) {
			listener.handle(arg);
		}
	}
}
