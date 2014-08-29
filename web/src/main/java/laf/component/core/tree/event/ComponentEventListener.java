package laf.component.core.tree.event;

/**
 * Event handler for {@link ComponentEvent}
 */
public interface ComponentEventListener<T> {

	/**
	 * Handle an event
	 *
	 * @param data
	 *            data sent along with the event
	 * @return true if the event has been handled and the other listeners should
	 *         not be called, false if the other listeners schould be called
	 */
	boolean handle(T data);
}
