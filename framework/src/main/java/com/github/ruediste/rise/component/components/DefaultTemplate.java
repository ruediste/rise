package com.github.ruediste.rise.component.components;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.ruediste.rise.component.TemplateIndex;
import com.github.ruediste.rise.component.components.template.CWTemplate;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.front.reload.Permanent;

/**
 * Defines the default template for a {@link Component}. Used by the
 * {@link TemplateIndex}
 */
@Permanent
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DefaultTemplate {
	Class<? extends CWTemplate<?>> value();
}
