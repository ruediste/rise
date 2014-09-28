package laf.component.web.basic.template;

import static org.rendersnake.HtmlAttributesFactory.type;

import java.io.IOException;

import javax.inject.Inject;

import laf.component.core.basic.CTextFieldFormGroup;
import laf.component.web.ApplyValuesUtil;
import laf.component.web.CWRenderUtil;

import org.rendersnake.HtmlCanvas;

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
