package com.github.ruediste.rise.component.binding;

import java.util.function.Consumer;

import com.github.ruediste.attachedProperties4J.AttachedPropertyBearer;

/**
 * @see BindingGroup
 */
public class Binding<T> {
	private AttachedPropertyBearer component;
	private Consumer<T> pullUp;
	private Consumer<T> pushDown;
	private String componentProperty;
	private String modelProperty;

	public Consumer<T> getPullUp() {
		return pullUp;
	}

	public void setPullUp(Consumer<T> pullUp) {
		this.pullUp = pullUp;
	}

	public Consumer<T> getPushDown() {
		return pushDown;
	}

	public void setPushDown(Consumer<T> pushDown) {
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