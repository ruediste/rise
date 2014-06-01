package laf.component.html.template;

import java.io.IOException;

import javax.inject.Inject;

import laf.component.core.Component;
import laf.component.html.ApplyValuesUtil;
import laf.component.html.RenderUtil;

import org.rendersnake.HtmlCanvas;

public class HtmlTemplateBase<T extends Component> implements HtmlTemplate<T> {

	@Inject
	protected HtmlTemplateService htmlTemplateService;

	@Override
	public void render(T component, HtmlCanvas html, RenderUtil util)
			throws IOException {
		for (Component child : component.getChildren()) {
			util.render(html, child);
		}
	}

	@Override
	public void applyValues(T component, ApplyValuesUtil util) {

	}

	@Override
	public void raiseEvents(T component, RaiseEventsUtil util) {

	}

}
