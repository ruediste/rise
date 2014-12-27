package laf.core.translation.label;

import java.lang.annotation.*;

/**
 * Indicates that a type (class, interface or enum) is labeled
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Labeled {
	/**
	 * Variants of the label, in addition to the variants specified by the
	 * {@link Label} annotations.
	 */
	String[] variants() default {};
}
