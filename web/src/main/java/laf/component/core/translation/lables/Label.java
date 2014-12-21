package laf.component.core.translation.lables;

import java.lang.annotation.*;

/**
 * Indicates that a type (class, interface or enum) is labeled
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
@Repeatable(Labels.class)
public @interface Label {
	String value();

	String variant() default "";
}
