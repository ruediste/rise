package com.github.ruediste.rise.component.binding;

import java.util.function.Consumer;

import com.github.ruediste.attachedProperties4J.AttachedPropertyBearer;
import com.github.ruediste.c3java.properties.PropertyPath;

/**
 * A binding between a model and a component property
 * 
 * @see BindingGroup
 */
public class Binding<TData> {
    private AttachedPropertyBearer component;
    private Consumer<TData> pullUp;
    private Consumer<TData> pushDown;
    public PropertyPath componentPath;
    public PropertyPath modelPath;

    public Consumer<TData> getPullUp() {
        return pullUp;
    }

    public void setPullUp(Consumer<TData> pullUp) {
        this.pullUp = pullUp;
    }

    public Consumer<TData> getPushDown() {
        return pushDown;
    }

    public void setPushDown(Consumer<TData> pushDown) {
        this.pushDown = pushDown;
    }

    public AttachedPropertyBearer getComponent() {
        return component;
    }

    public void setComponent(AttachedPropertyBearer component) {
        this.component = component;
    }

}