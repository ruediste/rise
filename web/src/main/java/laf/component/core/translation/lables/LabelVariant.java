package laf.component.core.translation.lables;

import java.lang.annotation.*;

/**
 * Indicates that an annotation represents a variant. The annotation may have
 * exactly the value attribute of string type.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface LabelVariant {
	/**
	 * name of the represented variant
	 */
	String value();
}
