package sampleApp.entities;

import laf.component.core.translation.Language;
import laf.component.core.translation.Variant;

@Language
public @interface En {
	String[] value();

	@Variant("short")
	String shrt() default "";
}
