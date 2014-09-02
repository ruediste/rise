package laf.component.web.basic.template;

import static org.rendersnake.HtmlAttributesFactory.class_;

import java.io.IOException;

import javax.inject.Inject;

import laf.component.core.basic.CButton;
import laf.component.web.*;

import org.rendersnake.HtmlCanvas;

public class CButtonHtmlTemplate extends CWTemplateBase<CButton> {
	@Inject
	CWRenderUtil util;

	@Override
	public void render(CButton component, HtmlCanvas html) throws IOException {
		html.button(class_("c_button")).span(class_("_componentId c_hidden"))
				.content(String.valueOf(util.getComponentId()));
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
