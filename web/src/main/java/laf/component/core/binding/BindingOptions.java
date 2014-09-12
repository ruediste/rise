package laf.component.core.binding;

import laf.core.base.attachedProperties.AttachedProperty;
import laf.core.base.attachedProperties.AttachedPropertyBearerBase;

public class BindingOptions extends AttachedPropertyBearerBase {

	boolean forceOneWay;

	/**
	 * Set an attached property on this {@link BindingOptions}
	 */
	public <T> BindingOptions withProperty(
			AttachedProperty<? super BindingOptions, T> property, T value) {
		property.set(this, value);
		return this;
	}
}
