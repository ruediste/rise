package com.github.ruediste.rise.component.binding;

import java.util.function.Consumer;

import com.github.ruediste.attachedProperties4J.AttachedPropertyBearer;

/**
 * A binding between a model and a component property
 * 
 * @see BindingGroup
 */
public class Binding<TData> {
    private AttachedPropertyBearer component;
    private Consumer<TData> pullUp;
    private Consumer<TData> pushDown;
    private String componentProperty;
    private String modelProperty;

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

    public String getComponentProperty() {
        return componentProperty;
    }

    public void setComponentProperty(String componentProperty) {
        this.componentProperty = componentProperty;
    }

    public String getModelProperty() {
        return modelProperty;
    }

    public void setModelProperty(String modelProperty) {
        this.modelProperty = modelProperty;
    }

    public AttachedPropertyBearer getComponent() {
        return component;
    }

    public void setComponent(AttachedPropertyBearer component) {
        this.component = component;
    }

}