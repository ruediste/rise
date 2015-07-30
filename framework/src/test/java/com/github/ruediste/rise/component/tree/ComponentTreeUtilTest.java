package com.github.ruediste.rise.component.tree;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.github.ruediste.rise.component.components.CGroup;
import com.github.ruediste.rise.component.tree.ComponentEvent.ComponentEventType;

@RunWith(MockitoJUnitRunner.class)
public class ComponentTreeUtilTest {

    Component parent;
    Component child;
    Component grandChild;

    @Mock
    Consumer<TestEvent> parentListener;
    @Mock
    Consumer<TestEvent> childListener;
    @Mock
    Consumer<TestEvent> grandChildListener;
    @Mock
    Consumer<TestEvent> parentListenerToo;
    @Mock
    Consumer<TestEvent> childListenerToo;
    @Mock
    Consumer<TestEvent> grandChildListenerToo;
    private Object[] listeners;
    private Answer<Object> cancelAnswer;

    @Before
    public void before() {
        grandChild = new CGroup();
        child = new CGroup().add(grandChild);
        parent = new CGroup().add(child);
        ComponentTreeUtil.registerEventListener(parent, TestEvent.class,
                parentListener);
        ComponentTreeUtil.registerEventListener(TestEvent.class, parent,
                parentListenerToo, true);

        ComponentTreeUtil.registerEventListener(child, TestEvent.class,
                childListener);
        ComponentTreeUtil.registerEventListener(TestEvent.class, child,
                childListenerToo, true);

        ComponentTreeUtil.registerEventListener(grandChild, TestEvent.class,
                grandChildListener);
        ComponentTreeUtil.registerEventListener(TestEvent.class, grandChild,
                grandChildListenerToo, true);
        listeners = new Object[] { parentListener, parentListenerToo,
                childListener, childListenerToo, grandChildListener,
                grandChildListenerToo };
        cancelAnswer = i -> {
            ((ComponentEvent) i.getArguments()[0]).cancel();
            return null;
        };
    }

    private class TestEvent extends ComponentEventBase {

        protected TestEvent(ComponentEventType type) {
            super(type);
        }

    }

    @Test
    public void simple() {
        TestEvent event = new TestEvent(ComponentEventType.TUNNEL);
        ComponentTreeUtil.raiseEvent(grandChild, event);
        InOrder order = inOrder(listeners);
        order.verify(parentListener).accept(event);
        order.verify(parentListenerToo).accept(event);
        order.verify(childListener).accept(event);
        order.verify(childListenerToo).accept(event);
        order.verify(grandChildListener).accept(event);
        order.verify(grandChildListenerToo).accept(event);
        noMoreInteractions();
    }

    @Test
    public void direct() {
        TestEvent event = new TestEvent(ComponentEventType.DIRECT);
        ComponentTreeUtil.raiseEvent(grandChild, event);
        InOrder order = inOrder(listeners);
        order.verify(grandChildListener).accept(event);
        order.verify(grandChildListenerToo).accept(event);
        noMoreInteractions();
    }

    @Test
    public void withCancels() {
        TestEvent event = new TestEvent(ComponentEventType.TUNNEL);

        doAnswer(cancelAnswer).when(childListener).accept(event);

        ComponentTreeUtil.raiseEvent(grandChild, event);
        InOrder order = inOrder(listeners);
        order.verify(parentListener).accept(event);
        order.verify(parentListenerToo).accept(event);
        order.verify(childListener).accept(event);
        order.verify(childListenerToo).accept(event);
        order.verify(grandChildListenerToo).accept(event);
        noMoreInteractions();
    }

    private void noMoreInteractions() {
        verifyNoMoreInteractions(listeners);
    }
}
