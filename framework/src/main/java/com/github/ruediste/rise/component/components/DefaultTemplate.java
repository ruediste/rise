package com.github.ruediste.rise.component.components;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.ruediste.rise.component.ComponentTemplateIndex;
import com.github.ruediste.rise.component.tree.Component;

/**
 * Defines the default template for a {@link Component}. Used by the
 * {@link ComponentTemplateIndex}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DefaultTemplate {
    @SuppressWarnings("rawtypes")
    Class<? extends IComponentTemplate> value();
}
