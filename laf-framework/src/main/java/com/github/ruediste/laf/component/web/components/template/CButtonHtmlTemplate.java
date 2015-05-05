package com.github.ruediste.laf.component.web.components.template;

import static org.rendersnake.HtmlAttributesFactory.class_;

import java.io.IOException;

import javax.inject.Inject;

import org.rendersnake.HtmlCanvas;

import com.github.ruediste.laf.component.web.*;
import com.github.ruediste.laf.component.web.components.CButton;

public class CButtonHtmlTemplate extends CWTemplateBase<CButton> {
	@Inject
	CWRenderUtil util;

	@Override
	public void render(CButton component, HtmlCanvas html) throws IOException {
		html.button(class_(util.combineClasses("c_button", component.tag()))
				.add("data-c-component-nr",
						String.valueOf(util.getComponentNr())));
		super.render(component, html);
		html._button();
	}

	@Override
	public void raiseEvents(CButton component, CWRaiseEventsUtil util) {
		if (util.isDefined("clicked") && component.getHandler() != null) {
			component.getHandler().run();
		}
	}
}
