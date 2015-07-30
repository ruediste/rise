package com.github.ruediste.rise.component.components;

import java.util.function.Function;

import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.RelationsComponent;
import com.github.ruediste.rise.component.tree.SingleChildRelation;
import com.google.common.base.Supplier;

/**
 * Component containing a value and updating it's child whenever the value
 * changes. Allows binding a value and modifying the value from view code.
 * Usage:
 * 
 * <pre>
 * {@code
 * new CValue(v->toComponent(html->html.write(v.toString()))
 *   .bindValue(()->controller.data().getValue());;
 * }
 * </pre>
 */
@DefaultTemplate(RenderChildrenTemplate.class)
public class CValue<T> extends RelationsComponent<CValue<T>> {

    private SingleChildRelation<Component, CValue<T>> child = new SingleChildRelation<>(
            this);
    private Function<T, Component> childFactory;
    private T value;

    public CValue() {
    }

    public CValue(Function<T, Component> childFactory) {
        setChildFactory(childFactory);
    }

    public T getValue() {
        return value;
    }

    /**
     * Update the value. Updates the child as well, if the
     * {@link #getChildFactory()} is set.
     */
    public CValue<T> setValue(T value) {
        this.value = value;
        updateChild();
        return this;
    }

    public void updateChild() {
        if (childFactory != null)
            child.setChild(childFactory.apply(value));
    }

    public Function<T, Component> getChildFactory() {
        return childFactory;
    }

    public CValue<T> setChildFactory(Function<T, Component> childFactory) {
        this.childFactory = childFactory;
        return this;
    }

    public CValue<T> bindValue(Supplier<T> accessor) {
        return bind(c -> c.setValue(accessor.get()));
    }
}
