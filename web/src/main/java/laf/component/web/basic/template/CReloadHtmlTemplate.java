package laf.component.web.basic.template;

import static org.rendersnake.HtmlAttributesFactory.class_;

import java.io.IOException;

import javax.inject.Inject;

import laf.component.core.basic.CReload;
import laf.component.web.*;

import org.rendersnake.HtmlCanvas;

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
