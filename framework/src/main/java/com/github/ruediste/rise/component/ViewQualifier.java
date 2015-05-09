package com.github.ruediste.rise.component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines the {@link IViewQualifier view qualifier} of a view. The view
 * qualifier is used to distinguish between multiple views.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ViewQualifier {
	Class<? extends IViewQualifier> value();
}
