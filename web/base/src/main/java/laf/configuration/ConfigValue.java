package laf.configuration;

import java.lang.annotation.*;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

/**
 * Marks a configuration value. If neither {@link #value()} of {@link #key()}
 * are given, the key for the value is the fully qualified name of the injection
 * point for primitive values, and the name of the to be injected class (or the
 * element type for collections) for other config values. If {@link #value()} is
 * given, the fully qualified name of the specified type is used. If
 * {@link #key()} is given, the specified key is used. It is an error to specify
 * both {@link #key()} and {@link #value()}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
@Qualifier
public @interface ConfigValue {
	/**
	 * Specifies the identifier of this configuration parameter.
	 */
	@Nonbinding
	// public Class<?> value() default Object.class;
	public String value() default "";

	/**
	 * Specifies the identifier of this configuration parameter.
	 */
	@Nonbinding
	public String key() default "";
}
