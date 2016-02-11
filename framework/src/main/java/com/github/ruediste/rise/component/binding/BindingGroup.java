package com.github.ruediste.rise.component.binding;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.stream.Stream;

import javax.validation.ConstraintViolation;

import com.github.ruediste.attachedProperties4J.AttachedProperty;
import com.github.ruediste.attachedProperties4J.AttachedPropertyBearer;
import com.github.ruediste.rise.component.tree.ComponentBase;
import com.github.ruediste.rise.component.tree.RelationsComponent;
import com.github.ruediste.rise.component.validation.ViolationStatusBearer;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.reflect.TypeToken;

/**
 * A group of bindings managed by a controller.
 * 
 * <p>
 * <img src="doc-files/bindingOverview.png" alt="">
 * </p>
 * 
 * <p>
 * The binding is triggered explicitly. The controller instantiates a
 * {@link BindingGroup} and references it in an instance variable. If the data
 * is ready, the controller uses the {@link #pullUp()} method to fill the
 * components with data. To retrieve the data from the components, it uses
 * {@link #pushDown()}. To the view the binding group is exposed via a function
 * returning {@link #proxy()}.
 * </p>
 * 
 * <p>
 * Note that when {@link BindingGroup}s are injected, if the value type can be
 * created by the injector, an instance is created and the group initialized
 * with it. Otherwise the group is initialized with the class only and the value
 * remains null.
 *
 * <p>
 * The component classes have a
 * {@link ComponentBase#bind(java.util.function.Consumer)} method which accepts
 * a lambda expression. The lambda expression has the component as parameter and
 * sets a property of the component to some value retrieved via a binding group
 * exposed by the controller.
 * </p>
 *
 * <p>
 * While establishing the binding, the lambda expression is called with a
 * dynamic proxy of the component class as parameter.
 * {@link BindingGroup#proxy()} returns a dynamic proxy. The two dynamic proxies
 * record the methods invoked on themselves. This information is used to
 * determine which properties are accessed.
 * </p>
 * 
 * <img src="doc-files/bindingPropertyAccessRecording.png" alt="">
 *
 * <p>
 * When the involved properties are known, it is determined if they are readable
 * and writeable. If both are, a two-way binding is set up. Otherwise an
 * appropriate one-way binding is used.
 * </p>
 *
 * <p>
 * When the binding is established, the lambda expression is not called again.
 * The properties are read and written using reflection. Thus any processing
 * logic contained in the expression does not affect the binding.
 * </p>
 *
 * <p>
 * <strong> One Way Bindings </strong><br>
 * Using {@link RelationsComponent#bindOneWay(java.util.function.Consumer)} the
 * check if a binding could be two way is suppressed and the binding always
 * takes the direction as specified by the lambda expression.
 * </p>
 *
 * <p>
 * <strong> Explicit Bindings </strong><br>
 * Using
 * {@link RelationsComponent#bind(java.util.function.Supplier, java.util.function.BiConsumer, java.util.function.BiConsumer)}
 * an explicit binding is established. The two lambda expressions are always
 * invoked with the real objects. This can be used to easily define
 * one-of-a-kind transformations in place. Either expression can be null, in
 * which case it is ignored.
 * </p>
 *
 * <p>
 * <strong> Transformers </strong><br>
 * Transformers allow a value to be transformed. When a lambda expression is
 * executed to establish a binding, the usage of any transformer is recorded and
 * integrated in the binding. Transformers can be one-way (implementing only
 * {@link BindingTransformer}) or two-way (implementing
 * {@link TwoWayBindingTransformer}). If it is attempted to establish a two-way
 * binding using an one-way transformer, an exception is raised.
 * </p>
 */
public class BindingGroup<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Class<T> tClass;
    private TypeToken<T> tTypeToken;

    private final AttachedProperty<AttachedPropertyBearer, Set<Binding<T>>> bindings = new AttachedProperty<>();

    private final WeakHashMap<AttachedPropertyBearer, Object> components = new WeakHashMap<>();

    private T data;

    @SuppressWarnings({ "unchecked" })
    public void initialize(T data) {
        initialize((Class<T>) data.getClass());
        this.data = data;
    }

    public void initialize(T data, Class<T> cls) {
        initialize(cls);
        this.data = data;
    }

    public void initialize(T data, TypeToken<T> token) {
        initialize(token);
        this.data = data;
    }

    public void initialize(Class<T> cls) {
        tClass = cls;
        tTypeToken = TypeToken.of(cls);
    }

    @SuppressWarnings("unchecked")
    public void initialize(TypeToken<T> token) {
        tClass = (Class<T>) token.getRawType();
        tTypeToken = token;
    }

    public Stream<Binding<T>> getBindings() {
        return components.keySet().stream()
                .flatMap(c -> bindings.get(c).stream());
    }

    /**
     * Pull the data of all bindings up from the model to the view
     */
    public void pullUp() {
        getBindings().filter(b -> b.getPullUp() != null)
                .forEach(b -> b.getPullUp().accept(data));
    }

    public interface SuccessActions<T> {
        /**
         * Return true if the action was successful
         */
        boolean success();

        /**
         * Return true if the action was a failure
         */
        boolean failure();

        /**
         * When the action was successful, run the provided runnable
         */
        T onSuccess(Runnable r);

        /**
         * When the action was a failure, run the provided runnable
         */
        T onFailure(Runnable r);

    }

    public interface PullUpActions extends SuccessActions<PullUpActions> {
    }

    public PullUpActions tryPullUp() {
        return null;
    }

    public interface ValidateActions extends SuccessActions<ValidateActions> {

    }

    /**
     * Validate the value of this group and update the validation state of the
     * components
     */
    public ValidateActions validate() {
        return null;

    }

    /**
     * Validate the value of this group and update the validation state of the
     * components
     */
    public ValidateActions validate(Class<?>... groups) {
        return null;
    }

    public interface PushDownActions extends SuccessActions<PushDownActions> {

        /**
         * Validate the value of this group, both if the push down was
         * successful or failed. The success state value represents both the
         * push down and the validation.
         */
        ValidateActions validate();

        /**
         * Validate the value of this group, both if the push down was
         * successful or failed. The success state value represents both the
         * push down and the validation.
         */
        ValidateActions validate(Class<?>... groups);
    }

    /**
     * Push down but do not show errors in the components
     */
    public boolean silentPushDown() {
        return false;
    }

    /**
     * Push down and show errors in the components. The returned value can be
     * used to determine if the attempt was successful or if there were errors.
     */
    public PushDownActions tryPushDown() {
        return null;
    }

    /**
     * Push the data of all bindings down from the view to the model
     */
    public void pushDown() {
        getBindings().filter(b -> b.getPushDown() != null)
                .forEach(b -> b.getPushDown().accept(data));
    }

    /**
     * Get the data object. The bindings push/pull to/from this object;
     */
    public T get() {
        return data;
    }

    /**
     * Set the data object. The bindings push/pull to/from this object;
     */
    public void set(T data) {
        this.data = data;
    }

    /**
     * While evaluating a binding expression, return a recording proxy.
     * Otherwise return {@link #data}.
     */
    public T proxy() {
        BindingExpressionExecutionRecord log = BindingExpressionExecutionRecorder
                .getCurrentLog();
        if (log == null) {
            return data;
        }
        log.setInvolvedBindingGroup(this);
        return createModelProxy(tClass);
    }

    @SuppressWarnings("unchecked")
    private <TModel> TModel createModelProxy(Class<?> modelClass) {
        return (TModel) BindingExpressionExecutionRecorder
                .getCurrentLog().modelRecorder.getProxy(tTypeToken);

    }

    @SuppressWarnings({ "unchecked" })
    void addBindingUntyped(Binding<?> binding) {
        addBinding((Binding<T>) binding);
    }

    void addBinding(Binding<T> binding) {
        AttachedPropertyBearer component = binding.getComponent();
        Set<Binding<T>> set = bindings.get(component);
        if (set == null) {
            set = new HashSet<>();
            bindings.set(component, set);
            components.put(component, null);
        }

        set.add(binding);

        if (binding.getPullUp() != null) {
            binding.getPullUp().accept(data);
        }
    }

    /**
     * Set the constraint violations
     */
    public void applyConstraintViolations(
            Set<ConstraintViolation<T>> violations) {

        Multimap<String, ConstraintViolation<?>> violationMap = MultimapBuilder
                .hashKeys().arrayListValues().build();

        for (ConstraintViolation<?> v : violations) {
            violationMap.put(
                    ValidationPathUtil.toPathString(v.getPropertyPath()), v);
        }

        getBindings().forEach(b -> {

            if (b.getComponent() instanceof ViolationStatusBearer) {

                ViolationStatusBearer aware = (ViolationStatusBearer) b
                        .getComponent();
                aware.getViolationStatus().setConstraintViolations(
                        violationMap.get(b.modelPath.getPath()));
                aware.getViolationStatus().setValidated(true);
            }
        });
    }
}
