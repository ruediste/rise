package com.github.ruediste.rise.component.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import com.github.ruediste.rendersnakeXT.canvas.HtmlConsumer;
import com.github.ruediste.rendersnakeXT.canvas.HtmlProducer;
import com.github.ruediste.rise.api.SubControllerComponent;
import com.github.ruediste.rise.component.binding.BindingInfo;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.rise.core.web.HttpRenderResult;
import com.github.ruediste.rise.util.Pair;
import com.github.ruediste1.i18n.lString.LString;

/**
 * A HTML fragment. Examples: ifFragment, forEachFragment, mixedFragment
 */
public class HtmlFragment {

    private HtmlFragment parent;
    private boolean rendered;
    private HtmlProducer producer = (c) -> {
        rendered = true;
        produceHtml(c);
    };
    private long fragmentNr = -1;
    private boolean isValidationPresenter;
    private List<LString> labels = new ArrayList<>();

    private List<Pair<SubControllerComponent, BindingInfo<?>>> bindinginfos = new ArrayList<>();
    private final ValidationStateBearer validationStateBearer = new ValidationStateBearer();

    /**
     * List of bindings associated with this fragment. Required since the
     * controllers reference the bindings only weakly.
     */
    public List<Pair<SubControllerComponent, BindingInfo<?>>> getBindingInfos() {
        return bindinginfos;
    }

    public void addLabel(LString label) {
        labels.add(label);
    }

    /**
     * get all labels, recursively
     */
    public List<LString> getLabels() {
        ArrayList<LString> result = new ArrayList<>();
        forSubTree(f -> result.addAll(f.labels));
        return labels;
    }

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
     * Process actions for this fragment
     */
    public void processActions() {
        // NOP
    }

    /**
     * Handle an ajax request targeted at the given component.
     * 
     * <p>
     * To create the corresponding URL use
     * {@link HtmlFragmentBase#getAjaxUrl(Component)} . Anything you append to
     * the url (prefixed with a "/") will be available as suffix.
     * 
     * <p>
     * To handle the request you can either return a {@link HttpRenderResult} or
     * handle the request in the method by using
     * {@link CoreRequestInfo#getServletResponse()} and returning null
     * 
     * @return a {@link HttpRenderResult} which will be used to send the
     *         response, or null if the response has already be sent.
     * 
     */

    public HttpRenderResult handleAjaxRequest(String suffix) throws Throwable {
        throw new UnsupportedOperationException();
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
        Function<T, EventHandlingOutcome> handler;
        boolean handlesToo;

        public EventRegistration(Class<T> eventType, Function<T, EventHandlingOutcome> handler, boolean handlesToo) {
            super();
            this.eventType = eventType;
            this.handler = handler;
            this.handlesToo = handlesToo;
        }

    }

    public enum EventHandlingOutcome {
        HANDLED, NOT_HANDLED
    }

    ArrayList<EventRegistration<?>> eventRegistrations = new ArrayList<>();

    private <T> Function<T, EventHandlingOutcome> toHandler(Consumer<T> handler) {
        return e -> {
            handler.accept(e);
            return EventHandlingOutcome.HANDLED;
        };
    }

    /**
     * Register an event handler. Does not handle already handled events
     */
    public <T> void register(Class<T> eventType, Consumer<T> handler) {
        register(eventType, toHandler(handler), false);
    }

    /**
     * Register an event handler. Does not handle already handled events
     */
    public <T> void register(Class<T> eventType, Function<T, EventHandlingOutcome> handler) {
        register(eventType, handler, false);
    }

    public <T> void register(Class<T> eventType, Consumer<T> handler, boolean handlesToo) {
        eventRegistrations.add(new EventRegistration<>(eventType, toHandler(handler), handlesToo));
    }

    public <T> void register(Class<T> eventType, Function<T, EventHandlingOutcome> handler, boolean handlesToo) {
        eventRegistrations.add(new EventRegistration<>(eventType, handler, handlesToo));
    }

    /**
     * Raise an event starting with this fragment, continuing towards the root
     */
    public void raiseEventBubbling(Object event) {
        raiseEvents(parents(), event);
    }

    /**
     * Raise an event starting with the root, continuing towards and ending with
     * this
     */
    public void raiseEventTunneling(Object event) {
        raiseEvents(pathFromRoot(), event);
    }

    /**
     * Raise an event directly and only on this
     */
    public void raiseEventDirect(Object event) {
        raiseEvents(Collections.singletonList(this), event);
    }

    @SuppressWarnings("unchecked")
    private void raiseEvents(List<HtmlFragment> htmlFragments, Object event) {
        boolean handled = false;
        for (HtmlFragment fragment : htmlFragments) {
            for (EventRegistration<?> registration : fragment.eventRegistrations) {
                if (!registration.eventType.isAssignableFrom(event.getClass()))
                    continue;
                if (handled && !registration.handlesToo)
                    continue;
                EventHandlingOutcome outcome = ((Function<Object, EventHandlingOutcome>) registration.handler)
                        .apply(event);
                if (outcome == null)
                    throw new RuntimeException("Event handler may not return null");
                handled |= (outcome == EventHandlingOutcome.HANDLED);
            }

        }
    }

    /**
     * Return the path of a fragment, starting with the root and ending with the
     * target fragment
     */
    public List<HtmlFragment> pathFromRoot() {
        List<HtmlFragment> result = parents();
        Collections.reverse(result);
        return result;
    }

    /**
     * Return the start fragments followed by all ancestors, ending with the
     * root fragment
     */
    public List<HtmlFragment> parents() {
        ArrayList<HtmlFragment> result = new ArrayList<>();
        HtmlFragment f = this;
        while (f != null) {
            result.add(f);
            f = f.getParent();
        }
        return result;
    }

    public List<HtmlFragment> subTree() {
        ArrayList<HtmlFragment> result = new ArrayList<>();
        subTree(result, this);
        return result;
    }

    private static void subTree(ArrayList<HtmlFragment> result, HtmlFragment fragment) {
        result.add(fragment);
        fragment.getChildren().forEach(child -> subTree(result, child));
    }

    public void forSubTree(Consumer<HtmlFragment> consumer) {
        forSubTree(this, consumer);
    }

    private static void forSubTree(HtmlFragment fragment, Consumer<HtmlFragment> consumer) {
        consumer.accept(fragment);
        fragment.getChildren().forEach(x -> forSubTree(x, consumer));
    }

    long getFragmentNr() {
        return fragmentNr;
    }

    void setFragmentNr(long fragmentNr) {
        this.fragmentNr = fragmentNr;
    }

    public void forEachNonValidationPresenterInSubTree(Consumer<HtmlFragment> consumer) {
        consumer.accept(this);
        for (HtmlFragment child : getChildren()) {
            if (!child.isValidationPresenter())
                child.forEachNonValidationPresenterInSubTree(consumer);
        }
    }

    public boolean isValidationPresenter() {
        return isValidationPresenter;
    }

    public void setValidationPresenter(boolean isValidationPresenter) {
        this.isValidationPresenter = isValidationPresenter;
    }

    public ValidationStateBearer getValidationStateBearer() {
        return validationStateBearer;
    }

}
