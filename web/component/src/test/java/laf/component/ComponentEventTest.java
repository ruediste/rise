package laf.component;

import static laf.MockitoExt.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import com.google.common.reflect.TypeToken;

public class ComponentEventTest {

	private ComponentEvent<Integer> event;
	private TestComponent component1;
	private ComponentEventListener<Integer> listener;
	private TestComponent component2;

	private static class TestComponent extends ComponentBase {
		public final SingleChildRelation child = new SingleChildRelation(this);
	}

	@Before
	public void setup() {
		event = new ComponentEvent<>(EventRouting.DIRECT);
		component1 = new TestComponent();
		component2 = new TestComponent();
		component1.child.setChild(component2);
		listener = mock(new TypeToken<ComponentEventListener<Integer>>() {
		});
	}

	@Test
	public void sendEventSimple() {
		event.register(component1, listener);
		event.send(component1, 3);
		verify(listener, times(1)).handle(3);
	}

	@Test
	public void sendEventNotRegistered() {
		event.send(component1, 3);
		verify(listener, times(0)).handle(3);
	}

	@Test
	public void sendEventBubble() {
		event = new ComponentEvent<>(EventRouting.BUBBLE);
		event.register(component1, listener);
		event.send(component2, 3);
		verify(listener, times(1)).handle(3);
	}
}
