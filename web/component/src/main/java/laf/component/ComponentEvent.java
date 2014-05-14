package laf.component;

import java.util.*;
import java.util.Map.Entry;

import laf.attachedProperties.AttachedProperty;

/**
 * Represents an event which can be delivered to {@link Component}s
 */
public class ComponentEvent<Data> {

	private static final AttachedProperty<Component, Map<ComponentEvent<?>, Map<ComponentEventListener<?>, Registration>>> eventsProperty = new AttachedProperty<>();

	private static class Registration {
		public Registration(boolean handlesToo) {
			this.handlesToo = handlesToo;
		}

		boolean handlesToo;
	}

	private final EventRouting routing;

	public ComponentEvent(EventRouting routing) {
		this.routing = routing;
	}

	public void send(Component target, Data data) {
		for (Component component : routing.getCandidateComponents(target)) {
			sendToComponent(target, data, false);
		}
	}

	private void sendToComponent(Component target, Data data, boolean handled) {

		Map<ComponentEventListener<Data>, Registration> innerMap = getMap(target);
		if (innerMap == null) {
			return;
		}

		for (Entry<ComponentEventListener<Data>, Registration> entry : innerMap
				.entrySet()) {
			if (!handled || entry.getValue().handlesToo) {
				handled |= entry.getKey().handle(data);
			}
		}

	}

	@SuppressWarnings("unchecked")
	private Map<ComponentEventListener<Data>, Registration> getMap(
			Component target) {
		Map<ComponentEvent<?>, Map<ComponentEventListener<?>, Registration>> outerMap = eventsProperty
				.get(target);
		if (outerMap == null) {
			return null;
		}

		return (Map) outerMap.get(this);
	}

	public void register(Component component,
			ComponentEventListener<Data> listener) {
		register(component, false, listener);
	}

	/**
	 *
	 * @param component
	 * @param handlesToo
	 *            if set to true, events which are already handled by other
	 *            listeners will still be delivered
	 * @param listener
	 */
	public void register(Component component, boolean handlesToo,
			ComponentEventListener<Data> listener) {
		Map<ComponentEventListener<?>, Registration> map = getOrCreateEventMap(component);

		map.put(listener, new Registration(handlesToo));
	}

	private Map<ComponentEventListener<?>, Registration> getOrCreateEventMap(
			Component component) {
		Map<ComponentEvent<?>, Map<ComponentEventListener<?>, Registration>> map = eventsProperty
				.get(component);
		if (map == null) {
			map = new LinkedHashMap<>();
			eventsProperty.set(component, map);
		}

		Map<ComponentEventListener<?>, Registration> result = map.get(this);
		if (result == null) {
			result = new LinkedHashMap<>();
			map.put(this, result);
		}

		return result;
	}

	public EventRouting getRouting() {
		return routing;
	}
}
