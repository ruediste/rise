package com.github.ruediste.laf.component.web.components;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.ruediste.laf.component.TemplateIndex;
import com.github.ruediste.laf.component.tree.Component;
import com.github.ruediste.laf.component.web.components.template.CWTemplate;
import com.github.ruediste.laf.core.front.reload.PermanentSpace;

/**
 * Defines the default template for a {@link Component}. Used by the
 * {@link TemplateIndex}
 */
@PermanentSpace
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DefaultTemplate {
	Class<? extends CWTemplate<?>> value();
}
