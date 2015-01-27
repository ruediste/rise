package com.github.ruediste.laf.component.web.components.template;

import static org.rendersnake.HtmlAttributesFactory.class_;

import java.io.IOException;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;

import org.rendersnake.HtmlCanvas;

import com.github.ruediste.laf.component.core.ValidationState;
import com.github.ruediste.laf.component.web.CWRenderUtil;
import com.github.ruediste.laf.component.web.CWTemplateBase;
import com.github.ruediste.laf.component.web.components.CFormGroup;
import com.google.common.collect.Iterables;

public abstract class CFormGroupHtmlTemplate<T extends CFormGroup<T>> extends
		CWTemplateBase<T> {

	@Inject
	CWRenderUtil util;

	@Override
	final public void render(T component, HtmlCanvas html) throws IOException {
		String cls = "form-group";
		if (component.getValidationState() == ValidationState.SUCCESS) {
			cls += " has-success";
		}

		if (component.getValidationState() == ValidationState.ERROR) {
			cls += " has-error";
		}

		// @formatter:off
		html.div(class_(cls))
			.label(class_("control-label").for_(util.getComponentId())).content(component.getLabel());
			innerRender(component, html);
			if (!component.getConstraintViolations().isEmpty()){
				if (component.getConstraintViolations().size()==1){
					html.span(class_("help-block")).content(Iterables.getOnlyElement(component.getConstraintViolations()).getMessage());
				}
				if (component.getConstraintViolations().size()>1){
					html.ul(class_("help-block"));
					for (ConstraintViolation<?> v: component.getConstraintViolations()){
						html.li().content(v.getMessage());
					}
					html._ul();
				}
			}
		html._div();
	}

	abstract public void innerRender(T component, HtmlCanvas html)
			throws IOException;
}
