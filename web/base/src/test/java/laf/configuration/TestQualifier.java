package laf.configuration;

import java.lang.annotation.*;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.TYPE })
@Qualifier
public @interface TestQualifier {
	/**
	 * Specifies the default value of this configuration parameter.
	 */
	@Nonbinding
	public String value() default "";
}
