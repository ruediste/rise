package laf.component.web;

import java.io.IOException;

import javax.inject.Inject;

import laf.component.core.tree.Component;

import org.rendersnake.HtmlCanvas;

public class CWTemplateBase<T extends Component> implements CWTemplate<T> {

	@Inject
	protected HtmlTemplateService htmlTemplateService;

	@Inject
	CWRenderUtil util;

	@Override
	public void render(T component, HtmlCanvas html) throws IOException {
		for (Component child : component.getChildren()) {
			util.render(html, child);
		}
	}

	@Override
	public void applyValues(T component, ApplyValuesUtil util) {

	}

	@Override
	public void raiseEvents(T component, CWRaiseEventsUtil util) {

	}

}
