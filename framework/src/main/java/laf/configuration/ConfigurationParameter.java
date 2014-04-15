package laf.configuration;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

/**
 * Defines a configuration parameter. Must be declared as public field in a
 * singleton.
 */
public class ConfigurationParameter<T> {

	private boolean valueSet;
	private T value;
	final Supplier<T> defaultValueSupplier;

	/**
	 * The identifier is set to "declaring class"."field name" during
	 * initialization.
	 */
	String identifier;

	/**
	 * Create a parameter with the given default value
	 */
	public ConfigurationParameter(T defaultValue) {
		this(Suppliers.ofInstance(defaultValue));
	}

	/**
	 * Create a parameter with the given defaultValueSupplier. The Supplier is
	 * invoked only if no explicit value is set ({@link #setValue(Object)}. The
	 * supplier is invoked at most once.
	 */
	public ConfigurationParameter(Supplier<T> defaultValueSupplier) {
		this.defaultValueSupplier = defaultValueSupplier;
	}

	/**
	 * Get the value of this parameter. If no value is set explicitely, the
	 * default value provider is used.
	 */
	public T getValue() {
		if (!valueSet) {
			setValue(defaultValueSupplier.get());
		}
		return value;
	}

	/**
	 * Set the value of this parameter.
	 */
	public void setValue(T value) {
		this.value = value;
		valueSet = true;
	}
}
