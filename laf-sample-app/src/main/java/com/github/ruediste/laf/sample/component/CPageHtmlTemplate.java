package com.github.ruediste.laf.sample.component;

import java.io.IOException;

import javax.inject.Inject;

import org.rendersnake.HtmlCanvas;

import com.github.ruediste.laf.component.ComponentUtil;
import com.github.ruediste.laf.component.web.components.CLink;
import com.github.ruediste.laf.component.web.components.template.CWTemplateBase;

public class CPageHtmlTemplate extends CWTemplateBase<CLink> {

	@Inject
	ComponentUtil util;

	@Override
	public void render(CLink component, HtmlCanvas html) throws IOException {
		html.html().head()._head().body()
				.render(util.components(component.getChildren()))._body()
				._html();
	}
}
