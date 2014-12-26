package laf.core.translation.labels;

import java.lang.annotation.*;

/**
 * Indicates that a type (class, interface or enum) is labeled
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.TYPE })
@Repeatable(Labels.class)
@Documented
public @interface Label {
	String value();

	String variant() default "";
}
