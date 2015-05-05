package com.github.ruediste.laf.component.web.components.template;

import static org.rendersnake.HtmlAttributesFactory.class_;

import java.io.IOException;

import javax.inject.Inject;

import org.rendersnake.HtmlCanvas;

import com.github.ruediste.laf.component.web.*;
import com.github.ruediste.laf.component.web.components.CReload;

public class CReloadHtmlTemplate extends CWTemplateBase<CReload> {
	@Inject
	CWRenderUtil util;

	@Override
	public void render(CReload component, HtmlCanvas html) throws IOException {
		html.form(class_("c_reload").data("c-component-nr",
				String.valueOf(util.getComponentNr())).data("lwf-reload-count",
				String.valueOf(component.getReloadCount())));
		super.render(component, html);
		html._form();
	}

	@Override
	public void applyValues(CReload component, ApplyValuesUtil util) {
		component.setReloadCount(component.getReloadCount() + 1);
	}
}
