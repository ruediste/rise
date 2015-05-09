package com.github.ruediste.rise.component.web.components.template;

import static org.rendersnake.HtmlAttributesFactory.href;

import java.io.IOException;

import javax.inject.Inject;

import org.rendersnake.HtmlCanvas;

import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.component.web.components.CLink;

public class CLinkHtmlTemplate extends CWTemplateBase<CLink> {
	@Inject
	ComponentUtil util;

	@Override
	public void render(CLink component, HtmlCanvas html) throws IOException {
		html.a(href(util.url(component.getDestination())));
		super.render(component, html);
		html._a();
	}
}
