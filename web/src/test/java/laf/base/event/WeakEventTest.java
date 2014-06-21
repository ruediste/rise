package laf.base.event;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

import java.lang.ref.WeakReference;

import laf.attachedProperties.AttachedPropertyBearerBase;

import org.junit.Before;
import org.junit.Test;

public class WeakEventTest {

	private static class Handler implements EventHandler<String> {

		@Override
		public void handle(String arg) {
		}

	}

	Handler handler;
	WeakEvent<String> event;
	AttachedPropertyBearerBase subscriber;

	@Before
	public void setup() {
		handler = mock(Handler.class);
		event = new WeakEvent<>();
		subscriber = new AttachedPropertyBearerBase();
	}

	@Test
	public void testSimple() {
		event.register(subscriber, handler);
		event.raise("Hello");
		verify(handler).handle("Hello");
	}

	@Test
	public void testRemove() {
		event.register(subscriber, handler);
		event.remove(subscriber, handler);
		event.raise("Hello");
		verifyNoMoreInteractions(handler);
	}

	@Test
	public void testGC() {
		event.register(subscriber, handler);
		WeakReference<Object> ref = new WeakReference<Object>(subscriber);
		assertNotNull(ref.get());
		subscriber = null;
		System.gc();
		event.raise("Hello");
		verifyNoMoreInteractions(handler);
		assertNull(ref.get());
	}

	@Test
	public void testGCSurvie() {
		event.register(subscriber, handler);
		System.gc();
		event.raise("Hello");
		verify(handler).handle("Hello");
	}
}
