package com.github.ruediste.laf.component.web.components.template;

import static org.rendersnake.HtmlAttributesFactory.type;

import java.io.IOException;

import javax.inject.Inject;

import org.rendersnake.HtmlCanvas;

import com.github.ruediste.laf.component.web.ApplyValuesUtil;
import com.github.ruediste.laf.component.web.CWRenderUtil;
import com.github.ruediste.laf.component.web.components.CTextFieldFormGroup;

public class CTextFieldFormGroupHtmlTemplate extends
		CFormGroupHtmlTemplate<CTextFieldFormGroup> {
	@Inject
	CWRenderUtil util;

	@Override
	public void applyValues(CTextFieldFormGroup component, ApplyValuesUtil util) {
		component.setText(util.getValue("value"));
	}

	@Override
	public void innerRender(CTextFieldFormGroup component, HtmlCanvas html)
			throws IOException {
		html.input(type("text")
				.class_(util.combineClasses("form-control", component.tag()))
				.value(component.getText()).name(util.getKey("value"))
				.id(util.getComponentId()));
	}
}
