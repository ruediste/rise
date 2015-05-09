package com.github.ruediste.rise.component.web.components.template;

import static org.rendersnake.HtmlAttributesFactory.type;

import java.io.IOException;

import javax.inject.Inject;

import org.rendersnake.HtmlCanvas;

import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.component.web.components.CTextFieldFormGroup;

public class CTextFieldFormGroupHtmlTemplate extends
		CFormGroupHtmlTemplate<CTextFieldFormGroup> {
	@Inject
	ComponentUtil util;

	@Override
	public void applyValues(CTextFieldFormGroup component) {
		component.setText(util.getParameterValue(component, "value"));
	}

	@Override
	public void innerRender(CTextFieldFormGroup component, HtmlCanvas html)
			throws IOException {
		html.input(type("text")
				.class_(util.combineCssClasses("form-control", component.tag()))
				.value(component.getText())
				.name(util.getKey(component, "value"))
				.id(util.getComponentId(component)));
	}
}
