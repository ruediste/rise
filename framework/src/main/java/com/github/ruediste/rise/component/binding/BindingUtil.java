package com.github.ruediste.rise.component.binding;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.github.ruediste.attachedProperties4J.AttachedPropertyBearer;
import com.github.ruediste.c3java.properties.PropertyAccessor.AccessorType;
import com.github.ruediste.c3java.properties.PropertyUtil;
import com.github.ruediste.rise.util.Pair;

/**
 * Interface to define bindings.
 *
 * @see BindingGroup
 */
public class BindingUtil {

    /**
     * Establish a one-way or two-way binding, depending on the properties
     * involved
     */
    static public <TView extends AttachedPropertyBearer> Pair<BindingGroup<?>, Binding<?>> bind(
            TView view, Consumer<TView> expression) {
        return bind(view, expression, false);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    static public <TComponent extends AttachedPropertyBearer> Pair<BindingGroup<?>, Binding<?>> bind(
            TComponent component, Consumer<TComponent> expression,
            boolean oneWay) {
        BindingExpressionExecutionRecord info = BindingExpressionExecutionRecorder
                .collectBindingExpressionLog(() -> {
                    expression.accept(BindingUtil
                            .<TComponent> createComponentProxy(component
                                    .getClass()));
                });

        if (info.getInvolvedBindingGroup() == null) {
            throw new RuntimeException(
                    "No binding group was involved in the expression. "
                            + "Make sure you accsss BindingGroup::proxy() during the execution of the binding expression");
        }

        Binding<?> binding = new Binding<>();
        binding.setComponent(component);
        binding.componentPath = PropertyUtil.toPath(info.componentRecorder);
        binding.modelPath = PropertyUtil.toPath(info.modelRecorder);

        boolean isModelRead = PropertyUtil.getAccessor(
                info.modelRecorder.getLastInvocation().getMethod()).getType() == AccessorType.GETTER;

        boolean doPullUp;
        boolean doPushDown;

        // determine binding direction
        if (oneWay) {
            doPullUp = isModelRead;
            doPushDown = !doPullUp;
        } else {
            doPushDown = binding.componentPath.getAccessedProperty()
                    .isReadable()
                    && binding.modelPath.getAccessedProperty().isWriteable();
            doPullUp = binding.componentPath.getAccessedProperty()
                    .isWriteable()
                    && binding.modelPath.getAccessedProperty().isReadable();
        }
        // set push down lambda if possible
        if (doPushDown) {

            binding.setPushDown(model -> {
                Object value = binding.componentPath.evaluate(component);
                if (info.transformer != null) {
                    if (isModelRead && !info.transformInv) {
                        value = ((TwoWayBindingTransformer) info.transformer)
                                .transformInv(value);
                    } else {
                        value = ((BindingTransformer) info.transformer)
                                .transform(value);
                    }
                }
                binding.modelPath.set(model, value);
            });
        }

        // set pull up lambda if possible
        if (doPullUp) {

            binding.setPullUp(model -> {
                Object value;
                if (model == null)
                    value = null;
                else
                    value = binding.modelPath.evaluate(model);
                if (info.transformer != null) {
                    if (isModelRead && !info.transformInv) {
                        value = ((BindingTransformer) info.transformer)
                                .transform(value);
                    } else {
                        value = ((TwoWayBindingTransformer) info.transformer)
                                .transformInv(value);
                    }
                }
                binding.componentPath.set(component, value);
            });
        }

        // register binding
        info.getInvolvedBindingGroup().addBindingUntyped(binding);
        return Pair.of(info.getInvolvedBindingGroup(), binding);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static <TView> TView createComponentProxy(Class<?> viewClass) {
        return (TView) BindingExpressionExecutionRecorder.getCurrentLog().componentRecorder
                .getProxy((Class) viewClass);
    }

    /**
     * Establish a one-way binding accoding to the given expression
     */
    static public <TView extends AttachedPropertyBearer> void bindOneWay(
            TView view, Consumer<TView> expression) {
        bind(view, expression, true);
    }

    /**
     * Add a binding to a binding group, sepecified by an accessor
     * 
     * @param bindingGroupAccessor
     *            access {@link BindingGroup#proxy()} of the binding group to
     *            add the binding to
     * @param binding
     *            the binding to add to the {@link BindingGroup}
     */
    public static <T> void bind(Supplier<T> bindingGroupAccessor,
            Binding<T> binding) {
        BindingExpressionExecutionRecord info = BindingExpressionExecutionRecorder
                .collectBindingExpressionLog(() -> {
                    bindingGroupAccessor.get();
                });

        info.getInvolvedBindingGroup().addBindingUntyped(binding);
    }

    /**
     * Add a binding to a binding group sepecified by an accessor.
     * 
     * @param <T>
     *            type of the {@link BindingGroup}
     * @param component
     *            component determining the life cycle of the component
     * @param bindingGroupAccessor
     *            access {@link BindingGroup#proxy()} of the binding group to
     *            add the binding to
     */

    static public <T> void bind(AttachedPropertyBearer component,
            Supplier<T> bindingGroupAccessor, Consumer<T> pullUp,
            Consumer<T> pushDown) {

        Binding<T> binding = new Binding<>();
        binding.setComponent(component);
        binding.setPullUp(pullUp);
        binding.setPushDown(pushDown);

        bind(bindingGroupAccessor, binding);
    }
}
