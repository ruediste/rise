package laf.core.translation.label;

import java.lang.annotation.*;

/**
 * Indicates that the properties of a type are labeled
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface PropertiesLabeled {
	/**
	 * Available variants of the member labels.
	 */
	String[] variants() default {};
}
