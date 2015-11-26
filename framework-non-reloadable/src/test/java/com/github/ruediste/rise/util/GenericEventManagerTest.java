package com.github.ruediste.rise.util;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;

import com.github.ruediste.attachedProperties4J.AttachedPropertyBearerBase;

public class GenericEventManagerTest {

    GenericEventManager<String> mgr;

    List<String> received;
    AttachedPropertyBearerBase bearer;

    @Before
    public void before() {
        mgr = new GenericEventManager<>();
        received = new ArrayList<>();
        bearer = new AttachedPropertyBearerBase();
    }

    @Test
    public void happyFlowWeakEvent() {
        mgr.addListener(received::add, bearer);
        mgr.fire("foo");
        assertThat(received, contains("foo"));
    }

    @Test
    public void weakEvent_can_collect() {
        Consumer<String> listener = received::add;
        mgr.addListener(listener, bearer);
        mgr.fire("foo");
        assertThat(received, contains("foo"));

        WeakReference<Consumer<String>> listenerRef = new WeakReference<Consumer<String>>(
                listener);
        WeakReference<AttachedPropertyBearerBase> bearerRef = new WeakReference<AttachedPropertyBearerBase>(
                bearer);
        assertThat(listenerRef.get(), equalTo(listener));
        assertThat(bearerRef.get(), equalTo(bearer));
        listener = null;
        bearer = null;
        System.gc();
        assertThat(listenerRef.get(), equalTo(null));
        assertThat(bearerRef.get(), equalTo(null));
    }
}
