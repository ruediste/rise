package com.github.ruediste.rise.component.components.template;

import static org.rendersnake.HtmlAttributesFactory.href;

import java.io.IOException;

import javax.inject.Inject;

import org.rendersnake.HtmlCanvas;

import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.component.components.CLink;

public class CLinkHtmlTemplate extends ComponentTemplateBase<CLink> {
	@Inject
	ComponentUtil util;

	@Override
	public void doRender(CLink component, HtmlCanvas html) throws IOException {
		html.a(href(util.url(component.getDestination())));
		super.doRender(component, html);
		html._a();
	}
}
