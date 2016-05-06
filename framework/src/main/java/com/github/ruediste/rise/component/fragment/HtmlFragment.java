package com.github.ruediste.rise.component.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Consumer;

import com.github.ruediste.rendersnakeXT.canvas.HtmlConsumer;
import com.github.ruediste.rendersnakeXT.canvas.HtmlProducer;

/**
 * A HTML fragment. Examples: ifFragment, forEachFragment, mixedFragment
 */
public abstract class HtmlFragment {

    private HtmlFragment parent;
    private boolean rendered;
    private HtmlProducer producer = this::produceHtml;

    public HtmlFragment() {
    }

    public HtmlFragment(HtmlFragment parent) {
        setParent(parent);
    }

    public Iterable<HtmlFragment> getChildren() {
        return Collections.emptyList();
    }

    protected void produceHtml(HtmlConsumer consumer) {
        // NOP
    }

    public final HtmlProducer getHtmlProducer() {
        return producer;
    }

    public void setParent(HtmlFragment parent) {
        if (this.parent != null)
            this.parent.childRemoved(this);
        this.parent = parent;
        if (parent != null)
            parent.childAdded(this);
    }

    public void childAdded(HtmlFragment child) {
    }

    public void childRemoved(HtmlFragment child) {
    }

    public HtmlFragment getParent() {
        return parent;
    }

    /**
     * Raise the events of this fragment only
     */
    public void applyValues() {
        // NOP
    }

    /**
     * Raise the events of this fragment only
     */
    public void raiseEvents() {
        // NOP
    }

    public interface UpdateStructureArg {
        /**
         * When called from
         * {@link HtmlFragment#updateStructure(UpdateStructureArg)}, declares
         * that the structure has been updated and that interested fragments
         * need to be updated again. Repeated calls have no effect
         */
        void structureUpdated();

        /**
         * When called from
         * {@link HtmlFragment#updateStructure(UpdateStructureArg)}, declares
         * that this fragment needs to be notified of structural changes of
         * other fragment, thus updateStructure() will be called again if any
         * other fragment called {@link #structureUpdated()}.
         * 
         * Repeated calls have no effect
         */
        void callOnFurtherStructureUpdates();
    }

    /**
     * Update this component to match the current view state. T
     */
    public void updateStructure(UpdateStructureArg arg) {
    }

    /**
     * true if the fragment is currently rendered on screen. Set to true while
     * rendering and cleared again after {@link #raiseEvents()}.
     */
    public boolean isRendered() {
        return rendered;
    }

    public void setRendered(boolean rendered) {
        this.rendered = rendered;

    }

    public final void render(HtmlConsumer consumer) {
        getHtmlProducer().produce(consumer);
    }

    private static class EventRegistration<T> {
        Class<T> eventType;
        Consumer<T> handler;

        public EventRegistration(Class<T> eventType, Consumer<T> handler) {
            super();
            this.eventType = eventType;
            this.handler = handler;
        }

    }

    ArrayList<EventRegistration<?>> eventRegistrations = new ArrayList<>();

    public <T> void register(Class<T> eventType, Consumer<T> handler) {
        eventRegistrations.add(new EventRegistration<>(eventType, handler));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void raiseEvent(Object event) {
        Class<? extends Object> eventType = event.getClass();
        eventRegistrations.stream().filter(r -> r.eventType.isAssignableFrom(eventType)).map(r -> r.handler)
                .forEach((Consumer handler) -> handler.accept(event));
    }
}
