package com.github.ruediste.rise.component.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.github.ruediste.rise.api.ViewComponentBase;
import com.github.ruediste.rise.component.binding.BindingInfo;
import com.github.ruediste.rise.component.binding.BindingUtil;
import com.github.ruediste.rise.component.render.ComponentState;
import com.github.ruediste.rise.nonReloadable.InjectorsHolder;
import com.github.ruediste.rise.nonReloadable.lambda.Capture;
import com.github.ruediste.rise.util.NOptional;
import com.github.ruediste.rise.util.Try;
import com.github.ruediste1.i18n.lString.LString;
import com.github.ruediste1.i18n.label.LabelUtil;
import com.google.common.base.Predicate;

/**
 * A component rendering some HTML
 * 
 * <p>
 * <b>Rendering Overview</b><br>
 * The basic idea is to freshly render the whole view upon each page reload,
 * based on the view state. Together with the HTML a component tree is
 * constructed. This tree is used during the reload to handle value updates and
 * events and to keep component state over reloads.
 * </p>
 * 
 * <p>
 * The following features are supported:
 * <ul>
 * <li><b>Component State:</b> Each component can have state which is kept over
 * page reloads. When a component is added to the tree during a page reload, all
 * fields marked with {@link ComponentState} are copied over from the
 * corresponding component of the previous render process. For details of the
 * matching process see below. If a field contains an {@link Optional}, it is
 * only copied if the target optional is {@link Optional#empty()}. The matching
 * happens immediatley after adding the component, before rendering. Otherwise
 * the state would not be available for rendering. *
 * <p>
 * Matching always happens among the children of the current parent component.
 * If a key is specified for a component, the old component with the same key is
 * used for matching. Otherwise the key is formed from the class of the
 * component and it's index. There is a separate index sequence for each
 * component class. Thus if the component sequence is A A B A C, the keys are
 * (A:1) (A:2) (B:1) (A:3) (C:1)
 * </p>
 * </li>
 * 
 * <li><b>Inspection of the Component Tree:</b> In many cases it is helpful to
 * be able to inspect the component tree to gain additional information or to
 * pass events. However, this is of little value if the rendering already
 * happened when such inspection becomes possible. Therefore a component can add
 * a placeholder during rendering which is evaluated during a second render
 * phase. There are two types of placeholders: attribute placeholders can only
 * add attributes to a single html tag. Tag placeholders can generate a whole
 * html tree, but have to start and end with a tag. All started tags have to be
 * closed. This property allows to check for well-fromedness of the generated
 * html during the initial rendering, as well as during the rendering of each
 * tag placeholder.</li>
 * 
 * <li><b>Validation:</b> Validation is always triggered by the controller. Both
 * the model and the components can be validated. The model will check domain
 * properties, the components the user input.
 * <p>
 * When a controller performs model validation, the validation failures are
 * stored in a field of the controller. The components inspect their bindings
 * and extract the applicable validation failures. The validation presenters
 * inspect a part of the component trees and display any validation failures
 * found. A validation presenter can also inspect the failures present on a
 * controller and display those not othewise displayed.
 * </p>
 * <p>
 * In addition, the controller can set a flag which causes the components to
 * perform their own validations which are displayed by the validation
 * presenters, too.
 * </p>
 * <li><b>Partial page reloads:</b></li> For fast page changes, pages can be
 * reloaded partially. This is achieved by implementing a part of the page
 * rendering as lambda function, which will be re-evaluated for the partial
 * rendering. All handlers (event, value, ajax) are associated with the
 * compoents, thus throwing away and recreating part of the page is sufficient.
 * <li><b>Ajax:</b> The client can send ajax requests, which can be handled by
 * individual components</li>
 * </ul>
 * </p>
 */
public class Component<TSelf> {

    private ViewComponentBase<?> view;
    private Component<?> parent;
    private List<Component<?>> children = new ArrayList<>();
    private long fragmentNr = -1;
    private boolean isValidationPresenter;
    private List<LString> labels = new ArrayList<>();

    private List<BindingInfo<?>> bindinginfos = new ArrayList<>();
    private final ValidationStateBearer validationStateBearer = new ValidationStateBearer();

    private String class_;
    private String testName;
    private Optional<Boolean> disabled = Optional.empty();
    private NOptional<Object> key = NOptional.empty();

    public void setKey(NOptional<Object> key) {
        this.key = key;
    }

    public TSelf key(Object key) {
        this.key = NOptional.of(key);
        return self();
    }

    public NOptional<Object> key() {
        return key;
    }

    @SuppressWarnings("unchecked")
    public TSelf self() {
        return (TSelf) this;
    }

    /**
     * Set the CSS-class to for to this component. It will generally be added to
     * the outermost HTML-element rendered for this component by the template.
     */
    public TSelf CLASS(String class_) {
        this.class_ = class_;
        return self();
    }

    public String CLASS() {
        return class_;
    }

    /**
     * Bind a property of this component. In addition, the {@link #TEST_NAME()}
     * is set to the name of the model property.
     * 
     * @see #bind(Consumer)
     */
    public TSelf TEST_NAME(@Capture Supplier<?> supplier) {
        BindingUtil.tryExtractBindingInfo(supplier).ifPresent(info -> TEST_NAME(info.modelProperty.getName()));
        return self();
    }

    /**
     * Set the "data-test-name" attribute to for this component. It will
     * generally be added to the outermost HTML-element rendered for this
     * component by the template.
     */
    public TSelf TEST_NAME(String testName) {
        this.testName = testName;
        return self();
    }

    public String TEST_NAME() {
        return testName;
    }

    public TSelf apply(Consumer<TSelf> consumer) {
        TSelf self = self();
        consumer.accept(self);
        return self;
    }

    /**
     * Return true if this component is disabled, either directly or through
     * inheritance
     */
    public boolean isDisabled() {
        for (Component<?> f : parents(true))
            if (f.disabled.isPresent())
                return f.disabled.get();

        return false;
    }

    /**
     * Return the disabled flag of this component without taking inheritance
     * into account
     */
    public Optional<Boolean> getDisabled() {
        return disabled;
    }

    public TSelf setDisabled(Optional<Boolean> disabled) {
        this.disabled = disabled;
        return self();
    }

    public TSelf setDisabled(Boolean disabled) {

        if (disabled)
            this.disabled = Optional.of(true);
        else
            this.disabled = Optional.empty();
        return self();
    }

    public TSelf disable() {
        this.disabled = Optional.of(true);
        return self();
    }

    /**
     * List of bindings associated with this fragment. Required since the
     * controllers reference the bindings only weakly.
     */
    public List<BindingInfo<?>> getBindingInfos() {
        return bindinginfos;
    }

    public TSelf addLabel(LString label) {
        labels.add(label);
        return self();
    }

    /**
     * get all labels, recursively
     */
    public List<LString> getLabels() {
        ArrayList<LString> result = new ArrayList<>();
        forSubTree(f -> result.addAll(f.labels));
        return result;
    }

    public Component() {
    }

    final public List<Component<?>> getChildren() {
        return children;
    }

    final public Component<?> getParent() {
        return parent;
    }

    public void setParent(Component<?> parent) {
        this.parent = parent;
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
        raiseEvents(parents(true), event);
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
    private void raiseEvents(List<Component<?>> htmlFragments, Object event) {
        boolean handled = false;
        for (Component<?> fragment : htmlFragments) {
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
    public List<Component<?>> pathFromRoot() {
        List<Component<?>> result = parents(true);
        Collections.reverse(result);
        return result;
    }

    /**
     * Return the start fragments followed by all ancestors, ending with the
     * root fragment
     */
    public List<Component<?>> parents() {
        return parents(false);
    }

    /**
     * Return the start fragments followed by all ancestors, ending with the
     * root fragment
     */
    public List<Component<?>> parents(boolean includeThis) {
        ArrayList<Component<?>> result = new ArrayList<>();
        Component<?> f;
        if (includeThis)
            f = this;
        else
            f = getParent();
        while (f != null) {
            result.add(f);
            f = f.getParent();
        }
        return result;
    }

    /**
     * Return all children, including this component
     */
    public List<Component<?>> subTree() {
        return subTree(x -> true);
    }

    /**
     * Return all children, including this component
     */
    public List<Component<?>> subTree(Predicate<Component<?>> includeChildrenOf) {
        ArrayList<Component<?>> result = new ArrayList<>();
        subTree(result, this, includeChildrenOf);
        return result;
    }

    private static void subTree(ArrayList<Component<?>> result, Component<?> fragment,
            Predicate<Component<?>> includeChildrenOf) {
        result.add(fragment);
        if (includeChildrenOf.apply(fragment))
            fragment.getChildren().forEach(child -> subTree(result, child, includeChildrenOf));
    }

    public void forSubTree(Consumer<Component<?>> consumer) {
        forSubTree(this, consumer);
    }

    private static void forSubTree(Component<?> fragment, Consumer<Component<?>> consumer) {
        consumer.accept(fragment);
        fragment.getChildren().forEach(x -> forSubTree(x, consumer));
    }

    long getFragmentNr() {
        return fragmentNr;
    }

    void setFragmentNr(long fragmentNr) {
        this.fragmentNr = fragmentNr;
    }

    public void forEachNonValidationPresenterInSubTree(Consumer<Component<?>> consumer) {
        consumer.accept(this);
        for (Component<?> child : getChildren()) {
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

    public <T> ValueHandle<T> createValueHandle(T value, boolean isLabelProperty) {
        return new ValueHandleImpl<>(value);
    }

    public <T> ValueHandle<T> createValueHandle(Supplier<T> accessor, boolean isLabelProperty) {
        Try<BindingInfo<T>> infoTry = BindingUtil.tryExtractBindingInfo(accessor);
        if (infoTry.isPresent()) {
            BindingInfo<T> info = infoTry.get();
            if (isLabelProperty && info.modelProperty != null) {
                InjectorsHolder.getInstance(LabelUtil.class).property(info.modelProperty).tryLabel()
                        .ifPresent(label -> addLabel(label));
            }
            getBindingInfos().add(info);
            return new ValueHandle<T>() {

                @Override
                public T get() {
                    return accessor.get();
                }

                @Override
                public void set(T value) {
                    info.setModelProperty(value);
                }
            };

        } else {
            return new ValueHandle<T>() {

                @Override
                public T get() {
                    return accessor.get();
                }

                @Override
                public void set(T value) {
                    throw new UnsupportedOperationException("unable to set value, could not parse accessor",
                            infoTry.getFailure());
                }
            };
        }
    }

    public ViewComponentBase<?> getView() {
        return view;
    }

    public void setView(ViewComponentBase<?> view) {
        this.view = view;
    }
}
