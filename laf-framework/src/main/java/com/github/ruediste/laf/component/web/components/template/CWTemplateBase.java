package com.github.ruediste.laf.component.web.components.template;

import java.io.IOException;

import javax.inject.Inject;

import org.rendersnake.HtmlCanvas;

import com.github.ruediste.laf.component.ComponentUtil;
import com.github.ruediste.laf.component.tree.Component;

public class CWTemplateBase<T extends Component> implements CWTemplate<T> {

	@Inject
	ComponentUtil util;

	@Override
	public void render(T component, HtmlCanvas html) throws IOException {
		for (Component child : component.getChildren()) {
			util.render(child, html);
		}
	}

	@Override
	public void applyValues(T component) {

	}

	@Override
	public void raiseEvents(T component) {

	}

}
