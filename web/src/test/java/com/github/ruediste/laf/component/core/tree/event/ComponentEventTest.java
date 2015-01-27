package com.github.ruediste.laf.component.core.tree.event;

import laf.test.MockitoExt;

import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import com.github.ruediste.laf.component.core.tree.*;
import com.github.ruediste.laf.component.core.tree.event.*;
import com.google.common.reflect.TypeToken;

public class ComponentEventTest {

	private ComponentEvent<Integer> event;
	private TestComponent component1;
	private TestComponent component2;
	private TestComponent component3;
	private ComponentEventListener<Integer> listener1;
	private ComponentEventListener<Integer> listener2;
	private ComponentEventListener<Integer> listener3;
	private InOrder order;

	private static class TestComponent extends ComponentBase<TestComponent> {
		public final MultiChildrenRelation<Component, TestComponent> children = new MultiChildrenRelation<>(
				this);

	}

	@SuppressWarnings("serial")
	public void setup(EventRouting routing) {
		event = new ComponentEvent<>(routing);
		component1 = new TestComponent();
		component2 = new TestComponent();
		component3 = new TestComponent();
		component1.children.add(component2);
		component1.children.add(component3);
		listener1 = MockitoExt.mock(
				new TypeToken<ComponentEventListener<Integer>>() {
				}, "listener1");
		listener2 = MockitoExt.mock(
				new TypeToken<ComponentEventListener<Integer>>() {
				}, "listener2");
		listener3 = MockitoExt.mock(
				new TypeToken<ComponentEventListener<Integer>>() {
				}, "listener3");
		event.register(component1, listener1);
		event.register(component2, listener2);
		event.register(component3, listener3);
		order = Mockito.inOrder(listener1, listener2, listener3);
	}

	@Test
	public void sendEventSimple() {
		setup(EventRouting.DIRECT);
		event.send(component1, 3);
		Mockito.verify(listener1, Mockito.times(1)).handle(3);
		Mockito.verify(listener2, Mockito.times(0)).handle(3);
	}

	@Test
	public void sendEventUnRegister() {
		setup(EventRouting.DIRECT);
		event.unregister(component1, listener1);
		event.send(component1, 3);

		Mockito.verify(listener1, Mockito.times(0)).handle(3);
		Mockito.verify(listener2, Mockito.times(0)).handle(3);
	}

	@Test
	public void sendEventBubble() {
		setup(EventRouting.BUBBLE);
		event.send(component2, 3);
		order.verify(listener2, Mockito.times(1)).handle(3);
		order.verify(listener1, Mockito.times(1)).handle(3);
	}

	@Test
	public void sendEventHandledSuppresses() {
		setup(EventRouting.BUBBLE);
		Mockito.when(listener2.handle(3)).thenReturn(true);
		event.send(component2, 3);
		order.verify(listener2, Mockito.times(1)).handle(3);
		order.verify(listener1, Mockito.times(0)).handle(3);
	}

	@Test
	public void sendEventHandlesTooWorks() {
		setup(EventRouting.BUBBLE);
		Mockito.when(listener2.handle(3)).thenReturn(true);
		event.register(component1, listener1, true);
		event.send(component2, 3);
		order.verify(listener2, Mockito.times(1)).handle(3);
		order.verify(listener1, Mockito.times(1)).handle(3);
	}

	@Test
	public void sendEventTunnel() {
		setup(EventRouting.TUNNEL);
		event.send(component2, 3);
		order.verify(listener1, Mockito.times(1)).handle(3);
		order.verify(listener2, Mockito.times(1)).handle(3);
	}

	@Test
	public void sendEventBroadcast() {
		setup(EventRouting.BROADCAST);
		event.send(component1, 3);
		order.verify(listener1, Mockito.times(1)).handle(3);
		order.verify(listener2, Mockito.times(1)).handle(3);
		order.verify(listener3, Mockito.times(1)).handle(3);
	}
}
