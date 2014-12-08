package laf.component.core.translation;

import java.lang.annotation.*;

/**
 * Define the variant a member of a language annotation represents.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Variant {
	/**
	 * name of the represented variant
	 */
	String value();
}
