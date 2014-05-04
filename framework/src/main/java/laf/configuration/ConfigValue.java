package laf.configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
@Qualifier
public @interface ConfigValue {
	/**
	 * Specifies the default value of this configuration parameter.
	 */
	@Nonbinding
	public String value() default "";
}
