package laf.component.web.api;

import java.io.IOException;

import javax.inject.Inject;

import laf.component.core.tree.Component;
import laf.component.web.ApplyValuesUtil;
import laf.component.web.template.HtmlTemplateService;

import org.rendersnake.HtmlCanvas;

public class CWTemplateBase<T extends Component> implements CWTemplate<T> {

	@Inject
	protected HtmlTemplateService htmlTemplateService;

	@Override
	public void render(T component, HtmlCanvas html, CWRenderUtil util)
			throws IOException {
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
